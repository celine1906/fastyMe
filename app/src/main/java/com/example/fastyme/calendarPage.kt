package com.example.fastyme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
@Composable
fun CalendarPage(navController: NavController) {
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp) // Mengurangi padding agar kalender lebih lebar
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally // Pusatkan elemen dalam kolom
    ) {

        CalendarHeader { newMonth, newYear ->
            selectedDate = LocalDate.of(newYear, newMonth, selectedDate?.dayOfMonth ?: 1)
        }

        Spacer(modifier = Modifier.height(24.dp))

        CalendarGrid(selectedDate) { newDate ->
            selectedDate = newDate
        }

        Spacer(modifier = Modifier.height(16.dp))

        selectedDate?.let {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${it.dayOfWeek.name}, ${it.dayOfMonth} ${it.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${it.year}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF673AB7),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Type of Fasting: OMAD",
                        fontSize = 14.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Time: 12 Hours",
                        fontSize = 14.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        GoalAccomplishedSection(navController)
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
            .padding(top = 24.dp, bottom = 8.dp, start = 16.dp, end = 16.dp),
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
            fontWeight = FontWeight.Bold
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
    val firstDayOfWeek = LocalDate.of(currentYear, currentMonth, 1).dayOfWeek.value % 7

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp) // Mengurangi padding untuk membuat grid lebih lebar
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly // Atur jarak secara proporsional
        ) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(
                    text = day,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f) // Pastikan semua elemen mendapatkan lebar sama
                )
            }
        }

        val totalCells = daysInMonth + firstDayOfWeek
        val rows = (totalCells / 7) + if (totalCells % 7 != 0) 1 else 0

        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly // Elemen dalam setiap baris diratakan
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
                            .padding(2.dp) // Sesuaikan padding untuk memperlebar grid
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
                            else Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoalAccomplishedSection(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Start Your Fasting Plan",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        Button(
            onClick = { navController.navigate(FASTING_PLAN) },
            colors = ButtonDefaults.buttonColors(Color(0xFF6200EE)),
            modifier = Modifier
                .width(100.dp)
                .height(60.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(
                text = "+",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White

            )
        }
    }
}

@Composable
fun MyApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "calendar") {
        composable("calendar") { CalendarPage(navController) }
        composable("plan") {
            // Implementasi halaman "Plan"
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Plan Page", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
