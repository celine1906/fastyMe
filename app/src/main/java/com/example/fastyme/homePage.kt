package com.example.fastyme

import android.R.attr.radius
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import com.vanpra.composematerialdialogs.MaterialDialog
import com.vanpra.composematerialdialogs.datetime.time.timepicker
import com.vanpra.composematerialdialogs.rememberMaterialDialogState
import kotlinx.coroutines.delay
import kotlinx.serialization.Serializable
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin


@Serializable
object Dashboard

@Composable
fun FastingAppUI() {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("12:12") }
    var valueSelected by remember { mutableStateOf(12) }
    var size by remember { mutableStateOf(IntSize.Zero) }
    var value by remember { mutableStateOf(1f) }
    var currentTime by remember { mutableStateOf(valueSelected*60*60) }
    var isTimeRunning by remember { mutableStateOf(false) }
    var pickedTime by remember { mutableStateOf(LocalTime.now()) }
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)

    ) {
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
                    val progress = currentTime.toFloat()/(valueSelected*60*60)
                    val sweepAngle = 360 * (currentTime / progress )
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
                        sweepAngle = 360*progress,
                        useCenter = false,
                        size = Size(size.width.toFloat(), size.height.toFloat()),
                        style = Stroke(width = 16.dp.toPx(), cap= StrokeCap.Round)
                    )
                    val center = Offset(size.width /2f, size.height /2f)
                    val beta = (250f * value + 145f) * (PI / 180f).toFloat()
                    val r = size.width / 2f
                    val s = cos(beta) * r
                    val b = sin(beta) * r

                    drawPoints(
                        listOf(Offset(center.x + s, center.y + b)),
                        pointMode = PointMode.Points,
                        color = Color.Cyan,
                        strokeWidth = (strokeWidth * 3f).toPx(),
                        cap = StrokeCap.Round
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Timer countdown
                    val hours = (currentTime / (60 * 60 ))
                    val minutes = ((currentTime % (60 * 60)) / (60))
                    if(isTimeRunning) {
                        Text(
                            text = String.format("%02d:%02d", hours, minutes),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Button(
                            onClick = {
                                isTimeRunning=!isTimeRunning
                            },
//                            colors = ButtonDefaults.buttonColors(
//                                backgroundColor = Color.Blue
//                            )
                        ){
                            Text(text="STOP", fontSize = 18.sp)
                        }
                    }else {
                        Button(
                            onClick = {expanded=true},
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
                                        currentTime = valueSelected*60*60
                                        expanded = false
                                    })
                                HorizontalDivider()
                                DropdownMenuItem(text = { Text("14:10", textAlign = TextAlign.Center) },
                                    onClick ={
                                        selectedItem = "14:10"
                                        valueSelected=14
                                        currentTime = valueSelected*60*60
                                        expanded = false
                                    })
                                HorizontalDivider()
                                DropdownMenuItem(text = { Text("16:8", textAlign = TextAlign.Center) },
                                    onClick ={
                                        selectedItem = "16:8"
                                        valueSelected=16
                                        currentTime = valueSelected*60*60
                                        expanded = false
                                    })
                            }
                        }



                        LaunchedEffect(key1=currentTime,key2=isTimeRunning) {
                            if(currentTime>0 && isTimeRunning) {
                                delay(1000L)
                                currentTime--
                                value=currentTime/valueSelected.toFloat()
                            }
                        }


                        // Start button
                        Button(onClick = {
                            isTimeRunning = true
                            pickedTime = LocalTime.now()
                        }) {
                            Text(text = "Start", fontSize = 18.sp)
                        }
                    }



                }
            }
        }


        Spacer(modifier = Modifier.height(24.dp))



        val timeDialogState = rememberMaterialDialogState()

        // Time settings (Begin & Stop)
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(30.dp)
        ) {
            Row() {

                TimeOption(label = "Begin", time = formattedTime)
                IconButton(onClick = {timeDialogState.show()}) {
                    Icon(imageVector = Icons.Outlined.Edit, contentDescription = "Edit", modifier = Modifier.size(25.dp))
                }
            }
            TimeOption(label = "Stop", time = formattedStopTime)
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

        Spacer(modifier = Modifier.height(16.dp))

        ReminderBox()

        Spacer(modifier = Modifier.height(24.dp))

        // Water and Calorie intake
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {

            IntakeBox(
                label = "Water Intake",
                value = "500/2000 ml",
                color = Color(0xFF98E9FF)
            )
            IntakeBox(
                label = "Calorie Intake",
                value = "2550/2500 cal",
                color = Color(0XFFFFD0D0)
            )

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

@Composable
fun IntakeBox(label: String, value: String, color: Color) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(180.dp)
            .clip(RoundedCornerShape(30.dp, 30.dp, 30.dp, 30.dp))
            .background(color)
            .padding(16.dp)

    ) {
        Text(text = label, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        Image(
            painter = painterResource(id = R.drawable.body), // Ganti dengan resource icon jam
            contentDescription = null,
            modifier = Modifier.size(64.dp)
        )
        Text(
            text = value,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
    }
}