package com.example.fastyme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.sin

@Composable
fun WaterIntake() {
    var totalIntake by remember { mutableStateOf(0) }
    val targetIntake = 2100
    val progress = (totalIntake.toFloat() / targetIntake) * 100
    var fillPercentage by remember { mutableStateOf(0f) } // Percentage of glass filled (0-100)
    val waveAnimation = rememberInfiniteTransition()
    var activeButtonIndex by remember { mutableStateOf(-1) }
    var savedValue by remember { mutableStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    var customAmount by remember { mutableStateOf("") }

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

        // Wave offset animation for wave movement
        val waveOffset by waveAnimation.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing)
            )
        )


        Column(
            modifier = Modifier,
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Draw Glass
            Canvas(
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp)
            ) {
                // Draw the glass outline
                val glassWidth = size.width * 0.6f
                val glassHeight = size.height * 0.8f
                val glassX = (size.width - glassWidth) / 2
                val glassY = (size.height - glassHeight) / 2

                drawRoundRect(
                    color = Color.Black,
                    topLeft = Offset(glassX, glassY),
                    size = Size(glassWidth, glassHeight),
                    cornerRadius = CornerRadius(20f, 20f),
                    style = Stroke(width = 5f)
                )

                // Draw the water inside the glass
                val waterHeight = fillPercentage / 100f * glassHeight
                val waterY = glassY + (glassHeight - waterHeight)

                val path = Path()
                val waveAmplitude = 20f // Height of wave
                val waveLength = glassWidth / 4 // Length of wave

                path.moveTo(glassX, waterY)
                for (x in 0..glassWidth.toInt() step 10) {
                    val y =
                        waterY + waveAmplitude * sin((x + waveOffset) * Math.PI / waveLength).toFloat()
                    path.lineTo(glassX + x, y)
                }
                path.lineTo(glassX + glassWidth, glassY + glassHeight)
                path.lineTo(glassX, glassY + glassHeight)
                path.close()

                drawPath(path, color = Color.Blue)
            }
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
        val chunkedAmounts = amounts.chunked(3) // Mengelompokkan menjadi maksimal 3 tombol per baris

        chunkedAmounts.forEachIndexed { rowIndex, row ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Loop untuk membuat tombol pada baris
                row.forEach { amount ->
                    Button(
                        onClick = {
                            activeButtonIndex = amounts.indexOf(amount)
                            savedValue = amount
                            expanded = false // Tutup input field lain
                        },
                        modifier = Modifier
                            .weight(1f) // Pastikan ukuran tombol proporsional
                            .padding(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeButtonIndex == amounts.indexOf(amount)) Color.Blue else Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "$amount ml")
                    }
                }

                // Jika ini adalah baris terakhir, tambahkan tombol "Other"
                if (rowIndex == chunkedAmounts.lastIndex && row.size < 3) {
                    repeat(2 - row.size) {
                        Spacer(modifier = Modifier.weight(1f).padding(4.dp)) // Spacer untuk tombol yang kosong
                    }
                    Button(
                        onClick = {
                            activeButtonIndex = amounts.size // "Other" button index
                            expanded = true // Tampilkan input untuk custom amount
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeButtonIndex == amounts.size) Color.Blue else Color.Gray,
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Other")
                    }
                }
            }
        }

// Jika semua baris penuh (contoh 6 tombol), tambahkan tombol "Other" di baris baru
        if (amounts.size % 3 == 0) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = {
                        activeButtonIndex = amounts.size
                        expanded = true
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(4.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (activeButtonIndex == amounts.size) Color.Blue else Color.Gray,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Other")
                }
            }
        }






        Spacer(modifier = Modifier.height(16.dp))

        // Input field for "Other" value
        if (expanded) {
            OutlinedTextField(
                value = customAmount,
                onValueChange = { customAmount = it },
                label = { Text("Enter Amount") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to add water intake
        Button(
            onClick = {
                if (expanded && customAmount.isNotEmpty()) {
                    totalIntake += customAmount.toInt()
                    customAmount = "" // Reset the input field
                } else {
                    totalIntake += savedValue
                }
                // Reset active button after adding
                activeButtonIndex = -1
                fillPercentage = (totalIntake.toFloat() / targetIntake * 100).coerceAtMost(100f)
            },
            shape = RoundedCornerShape(200.dp),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Text(text = "+", fontSize = 24.sp)
        }
    }
}
