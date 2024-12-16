package com.example.fastyme

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Serializable

@Serializable
object Fasting

@Composable
fun FastingPage() {
    // Halaman Scrollable
    @Composable
    fun FastingSection(imageResId: Int, title: String, description: String) {
        Box(
            modifier = Modifier
                .padding(horizontal = 26.dp, vertical = 15.dp)
                .fillMaxWidth()
                .height(200.dp) // Ukuran section
                .clip(RoundedCornerShape(25.dp))  // Membuat seluruh box memiliki sudut membulat
                .background(
                    color = Color.Gray.copy(alpha = 0.1f)  // Latar belakang transparansi
                )
        ) {
            // Gambar sebagai background
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = title,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(25.dp))  // Membuat gambar juga mengikuti bentuk sudut membulat
                    .background(Color.Black.copy(alpha = 0.3f)) // Menambahkan transparansi pada gambar
            )

            // Konten Section
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.Start
            ) {
                // Teks Judul
                Text(
                    text = title,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                // Teks Deskripsi
                Text(
                    text = description,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 14.sp
                )

                // Teks "Click to continue" di posisi kanan bawah
                Text(
                    text = "Click to continue",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .align(Alignment.End)  // Memindahkan teks ke ujung kanan
                        .padding(top = 4.dp)    // Memberikan sedikit ruang dari teks sebelumnya
                )
            }
        }
    }

    // Halaman Scrollable
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState()) // Bikin scrollable
    ) {
        // Header atau Judul Halaman
        Text(
            text = "Fasting Type",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()  // Menyesuaikan lebar agar teks di tengah
                .padding(top = 32.dp, bottom = 18.dp),  // Menambahkan padding atas (top) untuk menurunkan teks
            textAlign = TextAlign.Center // Teks di tengah horizontal
        )

        // Divider antara header dan section
        HorizontalDivider(
            modifier = Modifier
                .fillMaxWidth() // Divider memenuhi lebar layar
                .padding(horizontal = 16.dp), // Hilangkan padding horizontal
            thickness = 1.dp, // Ketebalan divider
            color = Color.Gray.copy(alpha = 0.5f) // Warna divider
        )

        // Section dengan Background Gambar
        FastingSection(
            imageResId = R.drawable.fasiting_image1,
            title = "Leangains Protocol (16/8)",
            description = "Fleksibel, efektif, sederhana, dan teratur"
        )

        FastingSection(
            imageResId = R.drawable.fasiting_image1,
            title = "Warrior Diet (20/4)",
            description = "Puasa panjang diiringi makan besar"
        )

        FastingSection(
            imageResId = R.drawable.fasiting_image1,
            title = "Eat Stop Eat",
            description = "Puasa 24 jam seminggu sekali"
        )

        FastingSection(
            imageResId = R.drawable.fasiting_image1,
            title = "OMAD (One Meal A Day)",
            description = "Hanya makan sekali sehari"
        )
    }
}