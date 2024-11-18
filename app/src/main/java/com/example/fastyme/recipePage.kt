package com.example.fastyme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
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
object Recipe

@Composable
fun RecipePage() {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Header()
                Spacer(modifier = Modifier.height(4.dp))
                RecipeCategories()
                Spacer(modifier = Modifier.height(8.dp)) // Kurangi jarak di sini
                PopularRecipes()
            }
        }
    }
}


@Composable
fun Header() {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Gambar latar belakang
        Image(
            painter = painterResource(id = R.drawable.header_background), // Ganti dengan nama file gambar Anda
            contentDescription = null,
            contentScale = ContentScale.Crop, // Crop gambar ke tengah
            modifier = Modifier
                .fillMaxWidth() // Menyesuaikan lebar layar
                .height(350.dp) // Atur tinggi header sesuai kebutuhan
        )
        // Teks di atas gambar
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = "FastyMe.",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black // Teks di atas gambar akan berwarna putih
            )
            Text(
                text = "Specially tailored for your fasting success",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}


@Composable
fun RecipeCategories() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Recipes Categories",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(3), // 3 elemen per baris
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp), // Memberikan tinggi pasti
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories.size) { index ->
                CategoryCard(category = categories[index], iconRes = categoryIcons[index])
            }
        }
    }
}



@Composable
fun CategoryCard(category: String, iconRes: Int) {
    Card(
        modifier = Modifier
            .width(100.dp) // Lebar kartu
            .height(100.dp), // Tinggi kartu
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5E5F7)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Gambar kategori berbentuk bulat
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                contentScale = ContentScale.Crop, // Crop gambar di tengah
                modifier = Modifier
                    .size(50.dp) // Ukuran lingkaran
                    .clip(CircleShape) // Membuat gambar berbentuk lingkaran
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Teks kategori
            Text(
                text = category,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}


@Composable
fun PopularRecipes() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Popular Recipes",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        RecipeCard(
            imageRes = R.drawable.chocolate_smoothie, // Tambahkan resource gambar
            title = "Chocolate Avocado Smoothie Bowl",
            description = "Satisfy your morning appetite with this chocolate temptation",
            tags = listOf("Vegetarian", "Gluten Free", "After Fasting", "Sweet")
        )
        Spacer(modifier = Modifier.height(8.dp))
        RecipeCard(
            imageRes = R.drawable.oatmeal_berries, // Tambahkan resource gambar
            title = "Oatmeal with Fresh Berries",
            description = "A healthy and tasty choice for your breakfast.",
            tags = listOf("Vegetarian", "Low Sugar", "Fiber Rich")
        )
    }
}

@Composable
fun RecipeCard(imageRes: Int, title: String, description: String, tags: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5E5F7)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Menampilkan gambar resep
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp) // Atur tinggi gambar
            )
            Spacer(modifier = Modifier.height(8.dp))
            // Teks informasi resep
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    tags.forEach { tag ->
                        Tag(tag)
                    }
                }
            }
        }
    }
}


@Composable
fun Tag(tag: String) {
    Surface(
        modifier = Modifier.wrapContentSize(),
        shape = RoundedCornerShape(50),
        color = Color(0xFF5624C4),
        tonalElevation = 2.dp
    ) {
        Text(
            text = tag,
            fontSize = 12.sp,
            color = Color.White,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

val categories = listOf("Breakfast", "Main dishes", "Soup & Salads", "Desserts", "Snacks", "Favorites")
val categoryIcons = listOf(
    R.drawable.ic_breakfast,  // Ganti dengan ikon kategori Anda
    R.drawable.ic_main_dishes,
    R.drawable.ic_soup_salads,
    R.drawable.ic_desserts,
    R.drawable.ic_snacks,
    R.drawable.ic_favorites
)
