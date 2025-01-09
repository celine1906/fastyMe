package com.example.fastyme

import android.content.ContentValues.TAG
import android.util.Log
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
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.sin

val db = Firebase.firestore

var fillPercentage = 0f // Percentage of glass filled (0-100)
var totalIntake =0
val targetIntake = 2100
val today = LocalDate.now()
val todayString = today.format(DateTimeFormatter.ISO_DATE)


@Composable
fun fetchData() {
    LaunchedEffect(Unit) {
        db.collection("Water Intake")
            .document("${AuthViewModel.userId}_$todayString")
            .addSnapshotListener {
                    snapshot, e ->
                if (e != null) {
                    Log.w(TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    Log.d(TAG, "Current data: ${snapshot.data}")
                    totalIntake = snapshot.getLong("totalWaterIntake")?.toInt() ?: 0
                    fillPercentage = (totalIntake.toFloat() / targetIntake * 100).coerceAtMost(100f)
                } else {
                    Log.d(TAG, "Current data: null")
                }
            }
    }
}

@Composable
fun glass(sizes:Int) {

    val waveAnimation = rememberInfiniteTransition()
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
                .size(sizes.dp)
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaterIntake(userId: String, navController: NavController) {
    var activeButtonIndex by remember { mutableStateOf(-1) }
    var savedValue by remember { mutableStateOf(0) }
    var expanded by remember { mutableStateOf(false) }
    var customAmount by remember { mutableStateOf("") }




    fun updateDatabase(total: Int) {
        val data = hashMapOf(
            "totalWaterIntake" to total,
            "date" to todayString
        )
        db.collection("Water Intake")
            .document("${AuthViewModel.userId}_$todayString")
            .set(data)
            .addOnSuccessListener {
                Log.d("Firebase", "Data updated successfully")
            }
            .addOnFailureListener { exception ->
                Log.d("Firebase", "Error updating data: ${exception.message}")
            }
    }



    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.baseline_arrow_back_ios_24),
                            contentDescription = "Back",
                            tint = Color.White // Ensure the back icon is black
                        )
                    }
                },
                title = { Text("Water Intake", color = Color.White) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF673AB7))
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
            // Header
            val adviceText = when {
                totalIntake > targetIntake -> "Warning: You have exceeded your water intake limit for the day! " +
                        "Consider slowing down to avoid overhydration."
                totalIntake == targetIntake -> "Great job! You have reached your water intake goal for the day."
                totalIntake < targetIntake && totalIntake > 0 -> "You are on track with your water intake. " +
                        "Keep sipping to stay hydrated!"
                totalIntake == 0 -> "You haven't logged any water intake yet. Start drinking water to stay hydrated!"
                else -> "Keep an eye on your water intake and make sure to stay hydrated!"
            }

            val textColor = if (totalIntake > targetIntake) Color.Red else Color.Black

            Text(
                text = adviceText,
                color = textColor,
//                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
            )

            Spacer(modifier = Modifier.height(16.dp))



            glass(200)


            Spacer(modifier = Modifier.height(16.dp))

            // Intake Text
            Text(
                text = "You have drunk",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Text(
                text = buildAnnotatedString {
                    append("$totalIntake / $targetIntake ml")
                    if (totalIntake > targetIntake) {
                        addStyle(style = SpanStyle(color = Color.Red), start = 0, end = "$totalIntake".length)
                    } else {
                        addStyle(style = SpanStyle(color = Color.Black), start = 0, end = "$totalIntake".length)
                    }
                },
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
            val chunkedAmounts =
                amounts.chunked(3) // Mengelompokkan menjadi maksimal 3 tombol per baris

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
                                containerColor = if (activeButtonIndex == amounts.indexOf(amount)) Color(0xFF5624C4) else Color(0xFFD9C2EC),
                                contentColor = if (activeButtonIndex == amounts.indexOf(amount)) Color.White else Color.Black
                            )
                        ) {
                            Text(text = "$amount ml")
                        }
                    }

                    // Jika ini adalah baris terakhir, tambahkan tombol "Other"
                    if (rowIndex == chunkedAmounts.lastIndex && row.size < 3) {
                        repeat(2 - row.size) {
                            Spacer(
                                modifier = Modifier.weight(1f).padding(4.dp)
                            ) // Spacer untuk tombol yang kosong
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
                                containerColor = if (activeButtonIndex == amounts.size) Color(0xFF5624C4) else Color(0xFFD9C2EC),
                                contentColor = if (activeButtonIndex == amounts.size) Color.White else Color.Black
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
                            containerColor = if (activeButtonIndex == amounts.size) Color(0xFF5624C4) else Color(0xFFD9C2EC),
                            contentColor = if (activeButtonIndex == amounts.size) Color.White else Color.Black
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


            Button(
                onClick = {
                    if (expanded && customAmount.isNotEmpty()) {
                        totalIntake += customAmount.toInt()
                        customAmount = "" // Reset the input field
                    } else {
                        totalIntake += savedValue
                    }
                    updateDatabase(totalIntake)
                    // Reset active button after adding
                    activeButtonIndex = -1
                    fillPercentage = (totalIntake.toFloat() / targetIntake * 100).coerceAtMost(100f)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent, // Warna latar belakang menjadi transparan
                    contentColor = Color.White         // Warna teks atau ikon menjadi hitam
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp),
//                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color(0xFF5624C4))
                        .padding(16.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Show Dialog")
                }
            }
            }

        }
    }

}

