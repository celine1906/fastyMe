package com.example.fastyme

data class Fasting(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val schedule: String = "",
    val benefits: List<String> = emptyList(),
    val tips: String = ""
)
