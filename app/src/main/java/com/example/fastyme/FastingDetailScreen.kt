package com.example.fastyme

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject

@Composable
fun FastingDetailScreen(navController: NavController, fastingId: String) {
    val db = FirebaseFirestore.getInstance()
    var fastingData by remember { mutableStateOf<Fasting?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isError by remember { mutableStateOf(false) }

    // Ambil data dari Firestore
    LaunchedEffect(fastingId) {
        db.collection("fasting_type")
            .document(fastingId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    fastingData = document.toObject(Fasting::class.java)
                    Log.d("FastingDetailScreen", "Data fetched successfully: $fastingData")
                } else {
                    Log.e("FastingDetailScreen", "Document not found.")
                }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                Log.e("FastingDetailScreen", "Error fetching data: ${exception.message}")
                isError = true
                isLoading = false
            }
    }

    // UI untuk Loading
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.Black)
        }
    } else if (isError) {
        // UI untuk Error
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Failed to load data. Please try again.", color = Color.Red)
        }
    } else {
        fastingData?.let { fasting ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Tombol Back
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Gambar Statis
                Image(
                    painter = painterResource(id = R.drawable.fasiting_image1),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Judul
                Text(
                    text = fasting.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Deskripsi
                Text(
                    text = fasting.description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Jadwal
                Text(
                    text = "Schedule",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                fasting.schedule.split(",").map { it.trim() }.forEach { schedule ->
                    Text(text = "- $schedule")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Manfaat
                Text(
                    text = "Benefits",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                fasting.benefits.forEach { benefit ->
                    Text(text = "• $benefit")
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tips
                Text(
                    text = "Tips",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                fasting.tips.split("\n").forEach { tip ->
                    Text(text = "• $tip")
                }
            }
        }
    }
}
