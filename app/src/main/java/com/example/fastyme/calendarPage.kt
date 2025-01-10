package com.example.fastyme

import android.util.Log
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.time.format.DateTimeFormatter

@Composable
fun CalendarPage(navController: NavController) {
    val fastingHistory = remember { mutableStateOf(mapOf<LocalDate, Pair<Color, String>>()) }
    val fastingPlans = remember { mutableStateListOf<FastingPlan>() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }

    LaunchedEffect(Unit) {
        fetchFastingScheduleFromFirebase(userId) { schedule ->
            fastingHistory.value = schedule
            Log.d("CalendarPage", "Fasting history: $schedule")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CalendarHeader { newMonth, newYear ->
            selectedDate = LocalDate.of(newYear, newMonth, selectedDate?.dayOfMonth ?: 1)
        }

        Spacer(modifier = Modifier.height(24.dp))

        CalendarGrid(
            selectedDate = selectedDate,
            fastingHistory = fastingHistory.value,
            onDateSelected = { newDate ->
                selectedDate = newDate
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        selectedDate?.let { date ->
            fastingHistory.value[date]?.let { (color, fastingType) ->
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
                            text = "${date.dayOfWeek.name}, ${date.dayOfMonth} ${date.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${date.year}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF673AB7),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Fasting Type: $fastingType",
                            fontSize = 14.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }?: run {
                Text(
                    text = "Tidak ada jadwal puasa untuk tanggal ini.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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

fun fetchFastingScheduleFromFirebase(
    userId: String,
    callback: (Map<LocalDate, Pair<Color, String>>) -> Unit
) {
    val fastingSchedule = mutableMapOf<LocalDate, Pair<Color, String>>()
    val today = LocalDate.now()

    FirebaseFirestore.getInstance()
        .collection("FastingPlan")
        .document(userId)
        .collection("Dates")
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                val startTime = document.getString("startTime")
                val fastingType = document.getString("fastingType") ?: "Unknown"
                Log.d("Firebase", "Fetched document: $document")

                if (startTime != null) {
                    try {
                        // Parsing tanggal
                        val dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM, HH:mm", Locale.getDefault())
                        val startDate = LocalDate.parse(startTime.split(",")[1].trim(), dateFormatter)

                        // Warna sesuai status
                        val color = when {
                            startDate.isBefore(today) -> Color(0xFF6200EE)
                            startDate.isAfter(today) -> Color.Green
                            else -> Color.Yellow
                        }

                        fastingSchedule[startDate] = Pair(color, fastingType)
                        Log.d("Firebase", "Parsed date: $startDate with type: $fastingType")
                    } catch (e: Exception) {
                        Log.e("Firebase", "Error parsing date: ${e.message}")
                    }
                }
            }
            callback(fastingSchedule)
        }
        .addOnFailureListener { exception ->
            Log.e("Firebase", "Error fetching fasting schedule: ${exception.message}")
        }
}


@Composable
fun CalendarGrid(
    selectedDate: LocalDate?,
    fastingHistory: Map<LocalDate, Pair<Color, String>>,
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
            .padding(horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                Text(
                    text = day,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        val totalCells = daysInMonth + firstDayOfWeek
        val rows = (totalCells / 7) + if (totalCells % 7 != 0) 1 else 0

        for (row in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    val date = if (cellIndex >= firstDayOfWeek && cellIndex < daysInMonth + firstDayOfWeek) {
                        cellIndex - firstDayOfWeek + 1
                    } else null

                    val currentDate = date?.let {
                        LocalDate.of(currentYear, currentMonth, it)
                    }

                    val fastingInfo = fastingHistory[currentDate]
                    val backgroundColor = fastingInfo?.first ?: Color.Transparent
                    val fastingType = fastingInfo?.second

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .padding(2.dp)
                            .background(
                                color = backgroundColor,
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
                            color = if (date != null && currentDate == today) Color.Red else Color.Black,
                            textAlign = TextAlign.Center
                        )
                        fastingType?.let {
                            Text(
                                text = it,
                                fontSize = 10.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
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
