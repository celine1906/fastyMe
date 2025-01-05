package com.example.fastyme

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.MutableState
import androidx.navigation.NavController
import com.google.firebase.firestore.SetOptions
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import fetchDataCalorie
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import progressCircle
import totalCalorie
import java.net.URLEncoder
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.cos
import kotlin.math.sin


@Serializable
object Dashboard

@Composable
fun FastingAppUI(navController: NavController) {
    val fastingState = remember { mutableStateOf(fastingData("",0,"",false, false,0,"","","")) }
    fetchDataFasting(fastingState, fastingState.value.startTime)

    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("12:12") }
    var valueSelected by remember { mutableStateOf(12) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    var currentTime by remember { mutableStateOf(valueSelected*60*60) }
    var isTimeRunning by remember { mutableStateOf(false) }
    var pickedTime by remember { mutableStateOf(LocalTime.now()) }
    var endTime by remember { mutableStateOf(System.currentTimeMillis()) }
    val formattedTime by remember {
        derivedStateOf {
            DateTimeFormatter.ofPattern("hh:mm a").format(pickedTime)
        }
    }

    val stopTime by remember {
        derivedStateOf { pickedTime.plusHours(valueSelected.toLong()) }
    }

    val formattedStopTime by remember {
        derivedStateOf {
            DateTimeFormatter.ofPattern("hh:mm a").format(stopTime)
        }
    }
    // Main background with a purple accent circle timer
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)

    ) {
        item {


        // Top greeting text
        Text(
            text = "Halo, (nama user)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.DarkGray,
            modifier = Modifier.padding(top = 16.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        val strokeWidth = 5.dp
        // Fasting timer (circular)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .background(
                        color = Color(0xFF6200EE).copy(alpha = 0.1f),
                        shape = CircleShape
                    )
                    .onSizeChanged {
                        size = it
                    },

                ) {
                Canvas(modifier=Modifier.fillMaxSize()) {
//                    val value = currentTime.toFloat()/(valueSelected*60*60)
                    val value = currentTime.toFloat()
                    val sweepAngle = 360f * value
                    drawArc(
                        color = Color.LightGray,
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        size = Size(size.width.toFloat(), size.height.toFloat()),
                        style = Stroke(width = 16.dp.toPx(), cap= StrokeCap.Round)
                    )
                    drawArc(
                        color = Color.Blue,
                        startAngle = -90f,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        size = Size(size.width.toFloat(), size.height.toFloat()),
                        style = Stroke(width = 16.dp.toPx(), cap= StrokeCap.Round)
                    )
                    val center = Offset(size.width / 2f, size.height / 2f) // Pusat lingkaran
                    val angleInRadians = Math.toRadians((sweepAngle - 90).toDouble()).toFloat() // Konversi derajat ke radian
                    val radius = size.width / 2f
                    val x = center.x + radius * cos(angleInRadians) // Posisi X titik
                    val y = center.y + radius * sin(angleInRadians) // Posisi Y titik

                    drawPoints(
                        points = listOf(Offset(x, y)),
                        pointMode = PointMode.Points,
                        color = Color.Cyan,
                        strokeWidth = (strokeWidth * 4f).toPx(),
                        cap = StrokeCap.Round
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                    // Timer countdown
                    var remainingTime by remember { mutableStateOf(0L) } // waktu tersisa
                    val predictedEndTime = fastingState.value.predictedEndTime ?: 0L


                    LaunchedEffect(fastingState.value.predictedEndTime) {
                        while (remainingTime > 0) {
                            delay(1000L)
                            remainingTime = maxOf(predictedEndTime - System.currentTimeMillis(), 0L)
                        }
                    }

                    val hours = (remainingTime / (1000 * 60 * 60)).toInt()
                    val minutes = ((remainingTime / (1000 * 60)) % 60).toInt()
                    val seconds = ((remainingTime / 1000) % 60).toInt()

                    val time = String.format("%02d:%02d:%02d", hours, minutes, seconds)



//                    if(fastingState.value.isWaiting) {
//                        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
//                        val startFasting = LocalTime.parse(fastingState.value.startTime, formatter) // Konversi String ke LocalTime
//                        val doneFasting = LocalTime.parse(fastingState.value.endTime, formatter)
//                        val rightNowTime = LocalTime.now()
//                        if (todayString==fastingState.value.startDate) {
//                            // Kasus tidak lintas hari
//                            if (rightNowTime >= startFasting && rightNowTime < doneFasting) {
//                                saveFastingData(fastingState.value.startTime, fastingState.value.duration, fastingState.value.endTime, true, false, fastingState.value.predictedEndTime, fastingState.value.startDate, "", "")
//                            } else if (rightNowTime >= doneFasting) {
//                                saveFastingData(fastingState.value.startTime, fastingState.value.duration, fastingState.value.endTime, false, false, fastingState.value.predictedEndTime, fastingState.value.startDate, todayString, fastingState.value.endTime)
//                            }
//                        } else {
//                            // Kasus lintas hari
//                            if (rightNowTime >= startFasting || rightNowTime < doneFasting) {
//                                saveFastingData(fastingState.value.startTime, fastingState.value.duration, fastingState.value.endTime, true, false, fastingState.value.predictedEndTime, fastingState.value.startDate, "", "")
//                            } else if (rightNowTime >= doneFasting && rightNowTime < startFasting) {
//                                saveFastingData(fastingState.value.startTime, fastingState.value.duration, fastingState.value.endTime, false, false, fastingState.value.predictedEndTime, fastingState.value.startDate, todayString, fastingState.value.endTime)
//                            }
//                        }
//                        fetchDataFasting(fastingState, fastingState.value.startTime)
//                    }

                    if(fastingState.value.isFasting) {
//                        val formatter = DateTimeFormatter.ofPattern("hh:mm a")
//                        val startFasting = LocalTime.parse(fastingState.value.startTime, formatter) // Konversi String ke LocalTime
//                        val doneFasting = LocalTime.parse(fastingState.value.endTime, formatter)
//                        val rightNowTime = LocalTime.now()
//                        if (todayString==fastingState.value.startDate) {
//                            // Kasus tidak lintas hari
//                            if (rightNowTime >= doneFasting) {
//                                saveFastingData(fastingState.value.startTime, fastingState.value.duration, fastingState.value.endTime, false, false, fastingState.value.predictedEndTime, fastingState.value.startDate, todayString, fastingState.value.endTime)
//                            }
//                        } else {
//                            // Kasus lintas hari
//                            if (rightNowTime >= doneFasting && rightNowTime < startFasting) {
//                                saveFastingData(fastingState.value.startTime, fastingState.value.duration, fastingState.value.endTime, false, false, fastingState.value.predictedEndTime, fastingState.value.startDate, todayString, fastingState.value.endTime)
//                            }
//
//                        }
//                        fetchDataFasting(fastingState, fastingState.value.startTime)
                        Text(text="Remaining", fontSize = 16.sp, color = Color(0xFF5624C4))
                        Text(
                            text = time,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5624C4)
                        )
                        Button(
                            onClick = {
                                fastingState.value.isFasting=false
                                val now: String = LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a"))
                                saveFastingData(fastingState.value.startTime, fastingState.value.duration, fastingState.value.endTime, false, false, fastingState.value.predictedEndTime, fastingState.value.startDate, todayString, now)
                            },
//                            colors = ButtonDefaults.buttonColors(
//                                backgroundColor = Color.Blue
//                            )
                        ){
                            Text(text="Stop", fontSize = 18.sp)
//                            fetchDataFasting(fastingState, fastingState.value.startTime)
                        }
                    }else {
                        val duration = valueSelected * 60 * 60 * 1000 // Durasi dalam milidetik
                        endTime = System.currentTimeMillis() + duration
                        remainingTime = duration.toLong()
                        Button(
                            onClick = {
                                if (isTimeRunning) {
                                    expanded=false
                                }else {
                                    expanded=true
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Gray.copy(alpha = 0.1f), // Background color of the button
                                contentColor = Color(0xFF5624C4) // Text/Icon color
                            ),
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp)) // Rounded corners
                                .padding(8.dp) // Inner padding within the button
                        ) {

                            Text(text = "${selectedItem}", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit", modifier = Modifier.size(20.dp))

                        }
                        Box(
                            modifier = Modifier.width(80.dp),

                            ) {
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = {expanded=false},
//                            modifier = Modifier.width(100.dp)
                            ) {
                                DropdownMenuItem(text = { Text("12:12", textAlign = TextAlign.Center) },
                                    onClick ={
                                        selectedItem = "12:12"
                                        valueSelected = 12
                                        expanded = false
                                    })
                                HorizontalDivider()
                                DropdownMenuItem(text = { Text("14:10", textAlign = TextAlign.Center) },
                                    onClick ={
                                        selectedItem = "14:10"
                                        valueSelected=14
                                        expanded = false
                                    })
                                HorizontalDivider()
                                DropdownMenuItem(text = { Text("16:8", textAlign = TextAlign.Center) },
                                    onClick ={
                                        selectedItem = "16:8"
                                        valueSelected=16
                                        expanded = false
                                    })
                            }
                        }

                        // Start button
                        Button(onClick = {
                            fastingState.value.isFasting = true
                            currentTime = valueSelected*60*60
                            pickedTime = LocalTime.now()
                            endTime = System.currentTimeMillis() + currentTime * 1000
                            addFastingData(formattedTime, valueSelected,formattedStopTime,true, false,endTime, todayString, "","")
                            fetchDataFasting(fastingState, fastingState.value.startTime)
                        }) {
                            Text(text = "Start", fontSize = 18.sp)
//                            fetchDataFasting(fastingState, fastingState.value.startTime)
                        }
                    }



                }
            }
        }


//        Spacer(modifier = Modifier.height(24.dp))



        val timeDialogState = rememberMaterialDialogState()

        // Time settings (Begin & Stop)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
        ) {
            Row() {
                var beginTime = ""
                if (fastingState.value.isFasting || fastingState.value.isWaiting) {
                    beginTime = fastingState.value.startTime
                } else {
                    beginTime = formattedTime
                }

                TimeOption(label = "Begin", time = beginTime)
                IconButton(onClick = {
                    if(fastingState.value.isFasting) {
                        timeDialogState.hide()
                    }else {
                        timeDialogState.show()
                    }
                }) {
                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit", modifier = Modifier.size(25.dp))
                }
            }
            var stopTimeLocal = ""
            if (fastingState.value.isFasting || fastingState.value.isWaiting) {
                stopTimeLocal = fastingState.value.endTime
            } else {
                stopTimeLocal = formattedStopTime
            }
            TimeOption(label = "Stop", time = stopTimeLocal)
        }
        val context = LocalContext.current
        MaterialDialog(
            dialogState = timeDialogState,
            buttons = {
                positiveButton(text="Ok") {
                    Toast.makeText(
                        context,
                        "Selected ${formattedTime}",
                        Toast.LENGTH_LONG
                    ).show()
                    val currentDate = java.time.LocalDate.now() // Tanggal hari ini
                    val dateTime = LocalDateTime.of(currentDate, pickedTime)
                    val timeMillis = dateTime.toInstant(ZoneOffset.UTC).toEpochMilli()
                    val endTime = timeMillis + currentTime * 1000
                    addFastingData(formattedTime, valueSelected,formattedStopTime,false, true, endTime, todayString, "", "")
                }
                negativeButton(text="Cancel") {

                }
            }
        ) {
            timepicker(
                initialTime = LocalTime.now(),
                title = "When do you want to start fasting?",
                timeRange = LocalTime.MIN..LocalTime.MAX
            ) {
                pickedTime = it

            }
        }



        ReminderBox()

        Spacer(modifier = Modifier.height(16.dp))


        // Water and Calorie intake
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(180.dp)
                    .clip(RoundedCornerShape(30.dp, 30.dp, 30.dp, 30.dp))
                    .background(Color(0xFF98E9FF))
                    .padding(16.dp)
                    .clickable { navController.navigate("waterIntake") }

            ) {
                Text(text = "Water Intake", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                glass(130)
                Text(
                    text = "${totalIntake.toString()} / ${targetIntake.toString()} ml",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }


            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .width(180.dp)
                    .clip(RoundedCornerShape(30.dp, 30.dp, 30.dp, 30.dp))
                    .background(Color(0XFFFFD0D0))
                    .padding(16.dp)
                    .clickable { navController.navigate("calorie") }

            ) {
                val calorieState = remember { mutableStateOf(totalCalorie(0,0,0,0,0)) }
                fetchDataCalorie(calorieState)
                val targetCalorie = 2000
                val progress = (calorieState.value.totalCalories.toFloat() / targetCalorie) * 100
                Text(text = "Calorie Intake", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(0.dp))
                progressCircle(100, progress)
                Spacer(modifier = Modifier.height(0.dp))
                Text(
                    text = "${calorieState.value.totalCalories} / 2000 kcal",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            }
        }
    }
}



@Composable
fun TimeOption(label: String, time: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Text(
            text = time,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5624C4)
        )
    }
}

@Composable
fun ReminderBox() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0xFF5624C4))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(text = "You should start fasting now!", color = Color.White)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Drink water to fulfill daily intake of water!", color = Color.White)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "Stop eating, you have exceeded your daily calorie limit", color = Color.White)
        }
    }
}

data class fastingData (
    var startTime: String,
    var duration: Int,
    var endTime:String,
    var isFasting:Boolean,
    var isWaiting:Boolean,
    var predictedEndTime: Long,
    var startDate:String,
    var endDate:String,
    var actualEndTime:String
)

fun addFastingData(
    startTime: String,
    duration: Int,
    endTime:String,
    isFasting:Boolean,
    isWaiting: Boolean,
    predictedEndTime: Long,
    startDate:String,
    endDate:String,
    actualEndTime: String
) {
    val formattedStartTime = startTime.replace(":", "").replace(" am", "").replace(" pm", "")
    val documentId = "${todayString}_$formattedStartTime"
    // Dokumen untuk tanggal startTime
    val fastingStartDoc = hashMapOf(
        "startTime" to startTime,
        "duration" to duration,
        "isFasting" to isFasting,
        "isWaiting" to isWaiting,
        "predictedEndTime" to predictedEndTime,
        "endTime" to endTime,
        "startDate" to startDate,
        "endDate" to endDate,
        "actualEndTime" to actualEndTime
    )
    db.collection("Fasting Data")
        .document("${userId}")
        .collection("$todayString")
        .document("$documentId")
        .set(fastingStartDoc)
        .addOnSuccessListener {
            Log.d("Firebase Fasting", "Fasting data saved")
        }
        .addOnFailureListener { exception ->
            Log.d("Firebase Fasting", "Error adding entry: ${exception.message}")
        }
}

fun saveFastingData(
    startTime: String,
    duration: Int,
    endTime:String,
    isFasting:Boolean,
    isWaiting: Boolean,
    predictedEndTime: Long,
    startDate:String,
    endDate:String,
    actualEndTime: String
) {
    val formattedStartTime = startTime.replace(":", "").replace(" am", "").replace(" pm", "")
    val documentId = "${todayString}_$formattedStartTime"
    db.collection("Fasting Data")
        .document("${userId}")
        .collection("$todayString")
        .whereEqualTo("isFasting", true)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                val documentRef = document.reference
                val fastingStartDoc = hashMapOf(
                    "startTime" to startTime,
                    "duration" to duration,
                    "isFasting" to isFasting,
                    "isWaiting" to isWaiting,
                    "predictedEndTime" to predictedEndTime,
                    "endTime" to endTime,
                    "startDate" to startDate,
                    "endDate" to endDate,
                    "actualEndTime" to actualEndTime
                )
                documentRef.update(fastingStartDoc as Map<String, Any>)
                    .addOnSuccessListener {
                        Log.d("Firebase Fasting", "Fasting data updated")
                    }
                    .addOnFailureListener { exception ->
                        Log.d("Firebase Fasting", "Error updating entry: ${exception.message}")
                    }
            }
        }
        .addOnFailureListener { exception ->
            Log.d(TAG, "Error fetching documents: ${exception.message}")
        }


}


//@Composable
fun fetchDataFasting(fastingState: MutableState<fastingData>, startTime: String) {
//    LaunchedEffect(Unit) {
    val formattedStartTime = startTime.replace(":", "").replace(" am", "").replace(" pm", "")
    val documentId = "${todayString}_$formattedStartTime"
    db.collection("Fasting Data")
        .document("${userId}")
        .collection("$todayString")
//        .whereEqualTo("isFasting", true)
        .get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                val data = document.data
                if (data != null && (data["isFasting"] == true)) {
                    // Proses setiap dokumen yang memenuhi syarat
                    val fastingStateData = fastingData(
                        startTime = data["startTime"] as? String ?: "",
                        duration = (data["duration"] as? Number)?.toInt() ?: 0,
                        endTime = data["endTime"] as? String ?: "",
                        isFasting = data["isFasting"] as? Boolean ?: false,
                        isWaiting = data["isWaiting"] as? Boolean ?: false,
                        predictedEndTime = (data["predictedEndTime"] as? Number)?.toLong() ?: 0L,
                        startDate = data["startDate"] as? String ?: "",
                        endDate = data["endDate"] as? String ?: "",
                        actualEndTime = data["actualEndTime"] as? String ?: ""
                    )
                    fastingState.value = fastingStateData
                }
            }
        }
        .addOnFailureListener { exception ->
            Log.d(TAG, "Current data: null", exception)
        }

}