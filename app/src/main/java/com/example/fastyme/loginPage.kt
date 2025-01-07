package com.example.fastyme

import android.annotation.SuppressLint
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
fun LoginPage(modifier: Modifier, navController: NavHostController, authViewModel: AuthViewModel) {
    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(140.dp))
            Text(
                text = "Login",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(30.dp))
            Text(
                text = "Welcome Back!",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Email Field
            val email = remember {
                mutableStateOf("")
            }
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

            // Password Field
            val password = remember {
                mutableStateOf("")
            }

//            val authState = authViewModel.authState.observeAsState()
//            val context = LocalContext.current
//
//            LaunchedEffect(authState.value) {
//                when(authState.value){
//                    is AuthState.Authenticated -> navController.navigate("homepage")
//                    is AuthState.Error -> Toast.makeText(context,
//                        (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
//                    else -> Unit
//                }
//            }
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

            // Register Button
            Button(
                onClick = {  },
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6200EE))
            ) {
                Text(text = "Login", color = Color.White, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login Using Google
            OutlinedButton(
                onClick = { /* Handle Login with Google */ },
                modifier = Modifier
                    .width(300.dp)
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Login using Google", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Already Have Account
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Don't have account yet? ")
                Text(
                    text = "Register", fontWeight = FontWeight.Bold,
                    color = Color(0xFF6200EE),
                    modifier = Modifier.clickable {
                        navController.navigate("registerPage")
                    }
                )
            }
        }
    }
}
