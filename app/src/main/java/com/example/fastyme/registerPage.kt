package com.example.fastyme

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RegisterPage(
    modifier: Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel
) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Register",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Join Us Now!",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            val name = remember { mutableStateOf("") }
            TextField(
                value = name.value,
                onValueChange = { name.value = it },
                label = { Text("Nama") },
                modifier = Modifier.width(300.dp).fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color(0xFF5624C4),
                    unfocusedIndicatorColor = Color(0xFF5624C4)
                )
            )
            Spacer(modifier = Modifier.height(25.dp))

            val email = remember { mutableStateOf("") }
            TextField(
                value = email.value,
                onValueChange = { email.value = it },
                label = { Text("Email") },
                modifier = Modifier.width(300.dp).fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color(0xFF5624C4),
                    unfocusedIndicatorColor = Color(0xFF5624C4)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(25.dp))

            val password = remember { mutableStateOf("") }
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Password") },
                modifier = Modifier.width(300.dp).fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color(0xFF5624C4),
                    unfocusedIndicatorColor = Color(0xFF5624C4)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(25.dp))

            val passwordConfirmation = remember { mutableStateOf("") }

            TextField(
                value = passwordConfirmation.value,
                onValueChange = { passwordConfirmation.value = it },
                label = { Text("Password Confirmation") },
                modifier = Modifier.width(300.dp).fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color(0xFF5624C4),
                    unfocusedIndicatorColor = Color(0xFF5624C4)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(30.dp))

            val authState = authViewModel.authState.observeAsState()
            authState.value?.let {
                when (it) {
                    is AuthState.Loading -> {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    }
                    is AuthState.Error -> {
                        Text(
                            text = it.message,
                            color = Color.Red,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    else -> {}
                }
            }

            Button(
                onClick = {
                    when {
                        name.value.isEmpty() || email.value.isEmpty() || password.value.isEmpty() -> {
                            authViewModel.setAuthStateError("All fields must be filled")
                        }
                        password.value != passwordConfirmation.value -> {
                            authViewModel.setAuthStateError("Passwords do not match")
                        }
                        else -> {
                            authViewModel.register(email.value, password.value, name.value) // Tambahkan name.value di sini
                            navController.navigate("questionPage") {
                                popUpTo("registerPage") { inclusive = true }
                            }
                        }
                    }
                },
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text(text = "Register", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = { /* Implement Google Sign-In logic */ },
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Register using Google", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Already have account? ")
                Text(
                    text = "Login",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE),
                    modifier = Modifier.clickable {
                        navController.navigate("loginPage")
                    }
                )
            }
        }
    }
}
