package com.example.fastyme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.serialization.Serializable

@Serializable
object Dashboard

@Composable
fun FastingAppUI(navController: NavController) {
    // Main background with a purple accent circle timer
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)

    ) {
        // Top greeting text
        Text(
            text = "Halo, (nama user)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // Fasting timer (circular)
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
                    ),

            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Timer countdown
                    Button(
                        onClick = { /* handle click */ },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray.copy(alpha = 0.1f), // Background color of the button
                            contentColor = Color(0xFF5624C4) // Text/Icon color
                        ),
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp)) // Rounded corners
                            .padding(8.dp) // Inner padding within the button
                    ) {

                        Text(text = "12:12", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit", modifier = Modifier.size(20.dp))

                    }

                    // Start button
                    Button(onClick = { /* handle start click */ }) {
                        Text(text = "Start", fontSize = 18.sp)
                    }
                }
            }
        }


        Spacer(modifier = Modifier.height(24.dp))

        // Time settings (Begin & Stop)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(30.dp)
        ) {
            Row() {
                TimeOption(label = "Begin", time = "08:00 PM")
                Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit", modifier = Modifier.size(25.dp))
            }
            TimeOption(label = "Stop", time = "08:00 AM")
        }

        Spacer(modifier = Modifier.height(16.dp))

        ReminderBox()

        Spacer(modifier = Modifier.height(24.dp))

        // Water and Calorie intake
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            IntakeBox(
                label = "Water Intake",
                value = "500/2000 ml",
                color = Color(0xFF98E9FF)
            )
            IntakeBox(
                label = "Calorie Intake",
                value = "2550/2500 cal",
                color = Color(0XFFFFD0D0)
            )

        }
    }
}

//class CountDownTimer : ViewModel() {}


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
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "You should start fasting now!", color = Color.White)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Drink water to fulfill daily intake of water!", color = Color.White)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Stop eating, you have exceeded your daily calorie limit", color = Color.White)
        }
    }
}

@Composable
fun IntakeBox(label: String, value: String, color: Color) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(180.dp)
            .clip(RoundedCornerShape(30.dp, 30.dp, 30.dp, 30.dp))
            .background(color)
            .padding(16.dp)

    ) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Image(
            painter = painterResource(id = R.drawable.body), // Ganti dengan resource icon jam
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