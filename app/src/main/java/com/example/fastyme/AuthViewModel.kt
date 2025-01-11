package com.example.fastyme

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class AuthViewModel : ViewModel() {


    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()


    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
        userId = auth.currentUser?.uid.toString()
    }

    private fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _authState.value = AuthState.Unauthenticated
        } else {
            _authState.value = AuthState.Authenticated
        }
    }

    fun login(email: String, password: String) {
        Log.d("AuthViewModel", "Login attempt with Email: $email") // Tambahkan log di sini

        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty!")
            Log.d("AuthViewModel", "Login failed: Empty email or password")
            return
        }

        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    userId = auth.currentUser?.uid.toString()
                    fetchDataWater()
                    Log.d("AuthViewModel", "Login successful for Email: $email")
                    _authState.value = AuthState.Authenticated
                } else {
                    val errorMessage = task.exception?.message ?: "Unknown error"
                    Log.e("AuthViewModel", "Login failed: $errorMessage")
                    _authState.value = AuthState.Error(errorMessage)
                }
            }
    }

    fun register(email: String, password: String, name: String) {
        Log.d("AuthViewModel", "Registering with Email: $email, Name: $name") // Tambahkan log di sini

        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email or password can't be empty!")
            return
        }

        _authState.value = AuthState.Loading
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    fetchDataWater()
                    Log.d("AuthViewModel", "Registration successful for UID: $userId") // Tambahkan log di sini
                    saveUserName(userId, name) // Simpan nama pengguna ke Firestore
                    _authState.value = AuthState.Authenticated
                } else {
                    val errorMessage = task.exception?.message ?: "Unknown error"
                    Log.e("AuthViewModel", "Registration failed: $errorMessage")
                    _authState.value = AuthState.Error(errorMessage)
                }
            }
    }

    fun signOut() {
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
        Log.d("AuthViewModel", "User signed out")
    }

    fun setAuthStateError(message: String) {
        _authState.value = AuthState.Error(message)
    }

    fun saveUserName(userId: String, name: String) {
        val userDoc = hashMapOf(
            "name" to name,
            "email" to auth.currentUser?.email
        )
        db.collection("Users")
            .document(userId) // UID sebagai ID dokumen
            .set(userDoc, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Firebase", "User data saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Error saving user data: ${e.message}")
            }
    }

}

sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}
