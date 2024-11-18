package com.example.fastyme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                Spacer(modifier = Modifier.height(16.dp))
                RecipeCategories()
                Spacer(modifier = Modifier.height(16.dp))
                PopularRecipes()
            }
        }
    }
}

@Composable
fun Header() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "FastyMe.",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Text(
            text = "Specially tailored for your fasting success",
            fontSize = 16.sp,
            color = Color.Gray
        )
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
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(categories.size) { index ->
                CategoryCard(category = categories[index])
            }
        }
    }
}

@Composable
fun CategoryCard(category: String) {
    Card(
        modifier = Modifier
            .padding(end = 8.dp)
            .size(120.dp, 60.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5E5F7)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
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
            title = "Chocolate Avocado Smoothie Bowl",
            description = "Satisfy your morning appetite with this chocolate temptation",
            tags = listOf("Vegetarian", "Gluten Free", "After Fasting", "Sweet")
        )
        Spacer(modifier = Modifier.height(8.dp))
        RecipeCard(
            title = "Oatmeal with Fresh Berries",
            description = "A healthy and tasty choice for your breakfast.",
            tags = listOf("Vegetarian", "Low Sugar", "Fiber Rich")
        )
    }
}

@Composable
fun RecipeCard(title: String, description: String, tags: List<String>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF7F6)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
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

@Composable
fun Tag(tag: String) {
    Surface(
        modifier = Modifier.wrapContentSize(),
        shape = RoundedCornerShape(50),
        color = Color(0xFFD1E8E2),
        tonalElevation = 2.dp
    ) {
        Text(
            text = tag,
            fontSize = 12.sp,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

val categories = listOf("Breakfast", "Main dishes", "Soup & Salads", "Desserts", "Snacks", "Favorites")
