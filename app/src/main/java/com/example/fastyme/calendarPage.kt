package com.example.fastyme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@Serializable
object Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarPage() {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header Kalender
        CalendarHeader { newMonth, newYear ->
            selectedDate = LocalDate.of(newYear, newMonth, selectedDate?.dayOfMonth ?: 1)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Kalender Utama
        CalendarGrid(selectedDate) { newDate ->
            selectedDate = newDate
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Informasi Tanggal yang Dipilih
        selectedDate?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${it.dayOfWeek.name}, ${it.dayOfMonth} ${it.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${it.year}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF673AB7)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Type of Fasting: OMAD",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Time: 12 Hours",
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }
        }

        // Bagian "Goal Accomplished"
        GoalAccomplishedSection()
    }
}

@Composable
fun CalendarHeader(onMonthChange: (Int, Int) -> Unit) {
    var currentMonth by remember { mutableStateOf(LocalDate.now().monthValue) }
    var currentYear by remember { mutableStateOf(LocalDate.now().year) }

    val monthName = LocalDate.of(currentYear, currentMonth, 1)
        .month
        .getDisplayName(TextStyle.FULL, Locale.getDefault())

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            if (currentMonth == 1) {
                currentMonth = 12
                currentYear--
            } else {
                currentMonth--
            }
            onMonthChange(currentMonth, currentYear)
        }) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Previous Month")
        }

        Text(
            text = "$monthName $currentYear",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        IconButton(onClick = {
            if (currentMonth == 12) {
                currentMonth = 1
                currentYear++
            } else {
                currentMonth++
            }
            onMonthChange(currentMonth, currentYear)
        }) {
            Icon(Icons.Filled.ArrowForward, contentDescription = "Next Month")
        }
    }
}

@Composable
fun CalendarGrid(
    selectedDate: LocalDate?,
    onDateSelected: (LocalDate) -> Unit
) {
    val today = LocalDate.now()
    val currentMonth = selectedDate?.monthValue ?: today.monthValue
    val currentYear = selectedDate?.year ?: today.year

    val daysInMonth = YearMonth.of(currentYear, currentMonth).lengthOfMonth()
    val firstDayOfWeek = LocalDate.of(currentYear, currentMonth, 1).dayOfWeek.value % 7 // 0 untuk Minggu

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Baris untuk nama hari
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(
                    text = day,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Grid tanggal
        val totalCells = daysInMonth + firstDayOfWeek
        val rows = (totalCells / 7) + if (totalCells % 7 != 0) 1 else 0

        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    val date = if (cellIndex >= firstDayOfWeek && cellIndex < daysInMonth + firstDayOfWeek) {
                        cellIndex - firstDayOfWeek + 1
                    } else null

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(4.dp)
                            .background(
                                color = if (date != null && selectedDate?.dayOfMonth == date) Color(0xFFBB86FC)
                                else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable(enabled = date != null) {
                                date?.let {
                                    onDateSelected(LocalDate.of(currentYear, currentMonth, it))
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = date?.toString() ?: "",
                            fontSize = 14.sp,
                            color = if (date != null && LocalDate.of(currentYear, currentMonth, date) == today) Color.Red
                            else Color.Black
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun GoalAccomplishedSection() {
    Text(
        text = "Goal Accomplished",
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        GoalCard(title = "Water Warrior", iconColor = Color(0xFF2196F3))
        GoalCard(title = "Time Heroes", iconColor = Color(0xFFFFC107))
        GoalCard(title = "Recipeess", iconColor = Color(0xFF4CAF50))
    }
}

@Composable
fun GoalCard(title: String, iconColor: Color) {
    Card(
        modifier = Modifier
            .size(150.dp)
            .padding(8.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(containerColor = iconColor.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(iconColor, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title.first().toString(),
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
        }
    }
}