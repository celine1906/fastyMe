package com.example.fastyme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
object Recipe

@Composable
fun RecipePage() {
    Text("ini resep")
}