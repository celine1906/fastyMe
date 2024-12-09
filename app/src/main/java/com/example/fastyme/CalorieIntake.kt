package com.example.fastyme

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CalorieIntake() {
    var totalIntake by remember { mutableStateOf(0) }
    val targetIntake = 2100
    val progress = (totalIntake.toFloat() / targetIntake) * 100

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "You are on the way!",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress Circle
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = progress / 100,
                strokeWidth = 10.dp,
                color = Color.Blue
            )
            Text(
                text = "${progress.toInt()}%",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Blue
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Intake Text
        Text(
            text = "You have drunk",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "$totalIntake ml / $targetIntake ml",
            fontSize = 30.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = "today",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons for Water Amounts
        Text(text = "Enter Drinks You Just Had", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))

        val amounts = listOf(50, 100, 150, 200, 250)

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            amounts.forEach { amount ->
                Button(
                    onClick = { totalIntake += amount },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD1C4E9), // Background color of the button
                        contentColor = Color.White // Text/Icon color
                    )
                ) {
                    Text(text = "$amount ml", color = Color.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Other Input
        var customAmount by remember { mutableStateOf("") }
        OutlinedTextField(
            value = customAmount,
            onValueChange = { customAmount = it },
            label = { Text("Other") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                customAmount.toIntOrNull()?.let {
                    totalIntake += it
                    customAmount = ""
                }
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "+", fontSize = 24.sp)
        }
    }
}