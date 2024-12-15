package com.example.fastyme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun FastingAppUI(modifier: Modifier = Modifier, navController: NavController) {
    // State for user data
    var userName by remember { mutableStateOf("User") }
    var beginTime by remember { mutableStateOf("08:00 PM") }
    var stopTime by remember { mutableStateOf("08:00 AM") }
    var waterIntake by remember { mutableStateOf("500/2000 ml") }
    var calorieIntake by remember { mutableStateOf("2550/2500 cal") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Greeting text
        Text(
            text = "Halo, $userName",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Fasting timer
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .background(
                        color = Color(0xFF6200EE).copy(alpha = 0.1f),
                        shape = CircleShape
                    )
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(
                        onClick = { /* Handle timer edit */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray.copy(alpha = 0.1f),
                            contentColor = Color(0xFF5624C4)
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .padding(8.dp)
                    ) {
                        Text(text = "12:12", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit", modifier = Modifier.size(20.dp))
                    }
                    Button(onClick = { /* Handle start */ }) {
                        Text(text = "Start", fontSize = 18.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Time settings (Begin & Stop)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
        ) {
            Row {
                TimeOption(label = "Begin", time = beginTime)
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit",
                    modifier = Modifier
                        .size(25.dp)
                        .clickable { beginTime = "09:00 PM" } // Example edit action
                )
            }
            TimeOption(label = "Stop", time = stopTime)
        }

        ReminderBox()

        Spacer(modifier = Modifier.height(16.dp))

        // Water and Calorie intake
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            IntakeBox(
                label = "Water Intake",
                value = waterIntake,
                color = Color(0xFF98E9FF),
                navController = navController,
                onEdit = { waterIntake = "1000/2000 ml" } // Example update
            )
            IntakeBox(
                label = "Calorie Intake",
                value = calorieIntake,
                color = Color(0XFFFFD0D0),
                navController = navController,
                onEdit = { calorieIntake = "2000/2500 cal" } // Example update
            )
        }
    }
}

@Composable
fun TimeOption(label: String, time: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text(
            text = time,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5624C4)
        )
    }
}

@Composable
fun ReminderBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0xFF5624C4))
            .padding(16.dp)
    ) {
        Column {
            Text(text = "You should start fasting now!", color = Color.White)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Drink water to fulfill daily intake of water!", color = Color.White)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Stop eating, you have exceeded your daily calorie limit", color = Color.White)
        }
    }
}

@Composable
fun IntakeBox(label: String, value: String, color: Color, navController: NavController, onEdit: () -> Unit) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(180.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(color)
            .padding(16.dp)
            .clickable { onEdit() }
    ) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Image(
            painter = painterResource(id = R.drawable.body),
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}
