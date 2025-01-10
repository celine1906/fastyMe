package com.example.fastyme

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale



@Composable
fun PlanInputPage(navController: NavController, time: String?) {
    var startDateTime by remember { mutableStateOf("Today, 22:27") }
    var endDateTime by remember { mutableStateOf("Tomorrow, 10:27") }
    var isStartDateTimePickerVisible by remember { mutableStateOf(false) }
    var isEndDateTimePickerVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .background(Color(0xFFF8F8F8)) // Background utama
    ) {
        // Header Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE0F7FA), RoundedCornerShape(8.dp))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = time ?: "12:12",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4CAF50)
                )
                Text(
                    text = "Puasa ${time?.split(":")?.get(0)} jam dan makan ${time?.split(":")?.get(1)} jam",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Input Waktu
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                // Input Waktu Mulai
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Clock Icon",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Awal", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text(text = startDateTime, fontSize = 16.sp, color = Color.Black)
                    IconButton(onClick = { isStartDateTimePickerVisible = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit Start Time")
                    }
                }

                Divider()

                // Input Waktu Selesai
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Clock Icon",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Akhir", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text(text = endDateTime, fontSize = 16.sp, color = Color.Black)
                    IconButton(onClick = { isEndDateTimePickerVisible = true }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit End Time")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Persiapan Puasa Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "Persiapan Puasa",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(start = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Bullet Icon",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Makan cukup protein seperti daging, tahu, ikan, dan kacang-kacangan.",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Bullet Icon",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Minum banyak air.",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Bullet Icon",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Makan makanan berserat tinggi seperti buah-buahan dan sayuran.",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Bullet Icon",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Tidur yang cukup di malam hari.",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Bullet Icon",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Hindari makanan olahan dan makanan manis.",
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                val todayDate = LocalDate.now().toString()
                val fastingType = time ?: "12:12"
                val duration = fastingType.split(":")[0].toIntOrNull() ?: 0
                saveFastingPlanToFirebase(userId, todayDate, fastingType, startDateTime, endDateTime, duration, "Fasting schedule")
                navController.navigateUp()
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
        ) {
            Text("Mulai Puasa", color = Color.White)
        }

        // Menampilkan Dialog untuk Waktu Mulai
        if (isStartDateTimePickerVisible) {
            DateTimePickerDialog(
                initialDateTime = startDateTime,
                onDismiss = { isStartDateTimePickerVisible = false },
                onConfirm = { dateTime ->
                    startDateTime = dateTime
                    isStartDateTimePickerVisible = false
                }
            )
        }

        // Menampilkan Dialog untuk Waktu Selesai
        if (isEndDateTimePickerVisible) {
            DateTimePickerDialog(
                initialDateTime = endDateTime,
                onDismiss = { isEndDateTimePickerVisible = false },
                onConfirm = { dateTime ->
                    endDateTime = dateTime
                    isEndDateTimePickerVisible = false
                }
            )
        }
    }
}



// Picker and Dialog implementations remain the same as your previous ones.


@Composable
fun DateTimePickerDialog(
    initialDateTime: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedDate by remember { mutableStateOf("Today") }
    var selectedTime by remember { mutableStateOf(initialDateTime.split(", ")[1]) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        containerColor = Color.White, // Ubah warna latar belakang utama menjadi putih
        shape = RoundedCornerShape(
            topStart = 50.dp, // Top left corner
            topEnd = 50.dp,   // Top right corner
            bottomStart = 50.dp, // Bottom left corner
            bottomEnd = 50.dp  // Bottom right corner
        ),
        title = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Pilih Waktu",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Picker Tanggal
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tanggal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        DatePicker(
                            selectedDate = selectedDate,
                            onDateChange = { selectedDate = it }
                        )
                    }

                    // Picker Jam
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Jam",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        TimePicker(
                            selectedTime = selectedTime,
                            onTimeChange = { selectedTime = it }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = { onConfirm("$selectedDate, $selectedTime") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Text("Menyimpan", color = Color.White)
                }
            }
        },
        dismissButton = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.TopEnd
            ) {
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(24.dp)
                        .padding(end = 16.dp, top = 16.dp)
                ) {
                    Icon(Icons.Filled.Close, contentDescription = "Close Picker", tint = Color.Black)
                }
            }
        }
    )
}

@Composable
fun DatePicker(
    selectedDate: String,
    onDateChange: (String) -> Unit
) {
    val today = LocalDate.now()
    val dates = (0..30).map { today.plusDays(it.toLong()) } // Generate 30 hari ke depan
    val dateFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .height(150.dp)
            .background(Color.White)
    ) {
        items(dates.size) { index ->
            val date = dates[index]
            val displayDate = if (index == 0) "Today" else date.format(dateFormatter)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(
                        if (displayDate == selectedDate) Color(0xFFEDEDED) else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onDateChange(displayDate) }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = displayDate, fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color.Black)
            }
        }
    }
}

@Composable
fun TimePicker(
    selectedTime: String,
    onTimeChange: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .height(150.dp)
            .background(Color.White)
    ) {
        val times = (0..23).flatMap { hour ->
            listOf("$hour:00", "$hour:05", "$hour:10", "$hour:15").map {
                it.padStart(5, '0')
            }
        }
        items(times.size) { index ->
            val time = times[index]
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .background(
                        if (time == selectedTime) Color(0xFFEDEDED) else Color.Transparent,
                        RoundedCornerShape(8.dp)
                    )
                    .clickable { onTimeChange(time) }
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = time, fontSize = 16.sp, fontWeight = FontWeight.Normal, color = Color.Black)
            }
        }
    }
}



fun saveFastingPlanToFirebase(
    userId: String,
    date: String,
    fastingType: String,
    startTime: String,
    endTime: String,
    duration: Int,
    notes: String
) {
    val fastingData = mapOf(
        "fastingType" to fastingType,
        "startTime" to startTime,
        "endTime" to endTime,
        "duration" to duration,
        "notes" to notes
    )

    FirebaseFirestore.getInstance()
        .collection("FastingPlan")
        .document(userId)
        .collection("Dates")
        .document(date)
        .set(fastingData)
        .addOnSuccessListener {
            Log.d("Firebase", "Data berhasil disimpan.")
        }
        .addOnFailureListener { e ->
            Log.e("Firebase", "Gagal menyimpan data: ${e.message}")
        }
}



fun getNextDates(): List<String> {
    val dateList = mutableListOf("Today")
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("E, dd MMM", Locale.getDefault())

    for (i in 1..7) {
        calendar.add(Calendar.DATE, 1)
        dateList.add(formatter.format(calendar.time))
    }
    return dateList
}

fun getTimeIntervals(): List<String> {
    val timeList = mutableListOf<String>()
    for (hour in 0..23) {
        for (minute in 0..59 step 5) {
            timeList.add("${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}")
        }
    }
    return timeList
}