package com.example.fastyme

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.serialization.Serializable

@Serializable
object Calendar

@Composable
fun CalendarPage() {
    Text("ini kalender")
}