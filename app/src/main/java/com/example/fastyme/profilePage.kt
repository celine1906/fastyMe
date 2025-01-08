package com.example.fastyme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.serialization.Serializable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import co.yml.charts.axis.AxisData
import co.yml.charts.common.model.Point
import co.yml.charts.ui.linechart.LineChart
import co.yml.charts.ui.linechart.model.GridLines
import co.yml.charts.ui.linechart.model.IntersectionPoint
import co.yml.charts.ui.linechart.model.Line
import co.yml.charts.ui.linechart.model.LineChartData
import co.yml.charts.ui.linechart.model.LinePlotData
import co.yml.charts.ui.linechart.model.LineStyle
import co.yml.charts.ui.linechart.model.LineType
import co.yml.charts.ui.linechart.model.SelectionHighlightPoint
import co.yml.charts.ui.linechart.model.SelectionHighlightPopUp
import co.yml.charts.ui.linechart.model.ShadowUnderLine
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Locale


@Serializable
object Profile

@Composable
fun ProfilePage() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                profile()
                recommendation()
                plan()
                achievement()
                calorieIntake()
                waterIntake()
            }
        }
    }

}

@Composable
fun profile() {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Profile Picture and Button
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFFD9C2EC))

            ) {
                Image(
                    painter = painterResource(id = R.drawable.profileicon),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            Button(onClick = {

            }) {
                Text("Edit")
            }
        }

        Spacer(modifier = Modifier.width(16.dp)) // Jarak antara Column

        // User Information
        CardBox(
            content = {

                Row(
//                    modifier = Modifier.padding(16.dp).clip(RoundedCornerShape(10.dp)),
                    horizontalArrangement = Arrangement.spacedBy(32.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Age : ")
                        Text("Sex : ")
                        Text("BMI : ")
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Weight : ")
                        Text("Height : ")
                        Text("BMI : ")
                    }
                }
            }
        )
    }
}

@Composable
fun title(str:String, imageResId:Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = str,
            style = TextStyle(
                color = Color.Black,
                textDecoration = TextDecoration.Underline
            ),
            fontWeight = FontWeight.Bold
        )
        Icon(
            bitmap = ImageBitmap.imageResource(id = imageResId),
            contentDescription="",
            modifier = Modifier
                .size(30.dp)
        )
    }
}

@Composable
fun recommendation() {
    CardBox(
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                title("Recommendation", R.drawable.plan)
                Button(onClick = {}) {
                    Text("See recommendation")
                }
            }
        }
    )
}

@Composable
fun plan() {
    CardBox(
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                title("My Plan", R.drawable.plan)
                Button(onClick = {}) {
                    Text("Upgrade")
                }
            }
        }
    )
}

@Composable
fun achievement() {
    CardBox(
        content = {
            title("Achievement", R.drawable.achievement)
        }
    )
}

@Composable
fun calorieIntake() {
    CardBox(
        content = {
            title("Calorie Intake", R.drawable.calorie_intake)
            LineChartScreen()
        }
    )
}

@Composable
fun waterIntake() {
    CardBox(
        content = {
            title("Water Intake", R.drawable.water_intake)
            LineChartScreen()
        }
    )
}

@Composable
fun CardBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
//            .height(120.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .background(Color(0xFFD9C2EC), RoundedCornerShape(20.dp))
            .padding(16.dp),
//        contentAlignment = Alignment.CenterStart
    ) {
        content()
    }
}

@Composable
fun EditProfilePage() {

}

//@Composable
//fun LineChartScreen() {
//    val steps = 5
//    val pointsData = listOf(
//        Point(0f,40f),
//        Point(1f,90f),
//        Point(2f,0f),
//        Point(3f,60f),
//        Point(4f,10f),
//    )
//
//    val xAxisData = AxisData.Builder()
//        .axisStepSize(100.dp)
//        .backgroundColor(Color.Transparent)
//        .steps(pointsData.size - 1)
//        .labelData { i -> i.toString() }
//        .labelAndAxisLinePadding(15.dp)
//        .axisLineColor(MaterialTheme.colorScheme.tertiary)
//        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
//        .build()
//
//    val yAxisData = AxisData.Builder()
//        .steps(steps)
//        .backgroundColor(Color.Transparent)
//        .labelAndAxisLinePadding(20.dp)
//        .labelData { i ->
//            val yScale = 100/steps
//            (i*yScale).toString()
//        }
//        .axisLineColor(MaterialTheme.colorScheme.tertiary)
//        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
//        .build()
//
//    val lineChartData = LineChartData(
//        linePlotData = LinePlotData(
//            lines = listOf(
//                Line(
//                    dataPoints = pointsData,
//                    LineStyle(
//                        color = MaterialTheme.colorScheme.tertiary,
//                        lineType = LineType.Straight(isDotted = false)
//                    ),
//                    IntersectionPoint(
//                        color = MaterialTheme.colorScheme.tertiary
//                    ),
//                    SelectionHighlightPoint(color = MaterialTheme.colorScheme.primary),
//                    ShadowUnderLine(
//                        alpha = 0.5f,
//                        brush = Brush.verticalGradient(
//                            colors = listOf(
//                                MaterialTheme.colorScheme.inversePrimary,
//                                Color.Transparent
//                            )
//                        )
//                    ),
//                    SelectionHighlightPopUp()
//                )
//            ),
//        ),
//        backgroundColor = MaterialTheme.colorScheme.surface,
//        xAxisData = xAxisData,
//        yAxisData = yAxisData,
//        gridLines = GridLines(color = MaterialTheme.colorScheme.outlineVariant)
//    )
//
//    LineChart(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(400.dp),
//        lineChartData=lineChartData
//    )
//}

@Composable
fun LineChartScreen() {
    val currentMonth = remember { LocalDate.now().monthValue }
    val currentYear = remember { LocalDate.now().year }
    var selectedMonth by remember { mutableStateOf(currentMonth) }
    var selectedYear by remember { mutableStateOf(currentYear) }
    var isMonthDropdownExpanded by remember { mutableStateOf(false) }
    var isYearDropdownExpanded by remember { mutableStateOf(false) }

    val months = (1..12).toList()
    val years = (2020..2025).toList()

    val daysInMonth = LocalDate.of(selectedYear, selectedMonth, 1).lengthOfMonth()
    val pointsData = (0 until daysInMonth).map { Point(it.toFloat(), (0..100).random().toFloat()) }

    // X Axis Data
    val xAxisData = AxisData.Builder()
        .axisStepSize(100.dp)
        .backgroundColor(Color.Transparent)
        .steps(pointsData.size - 1)
        .labelData { i -> "${i + 1}" }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()

    // Y Axis Data
    val steps = 5
    val yAxisData = AxisData.Builder()
        .steps(steps)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(20.dp)
        .labelData { i ->
            val yScale = 100 / steps
            (i * yScale).toString()
        }
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val lineChartData = LineChartData(
        linePlotData = LinePlotData(
            lines = listOf(
                Line(
                    dataPoints = pointsData,
                    LineStyle(
                        color = MaterialTheme.colorScheme.tertiary,
                        lineType = LineType.Straight(isDotted = false)
                    ),
                    IntersectionPoint(
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    SelectionHighlightPoint(color = MaterialTheme.colorScheme.primary),
                    ShadowUnderLine(
                        alpha = 0.5f,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.inversePrimary,
                                Color.Transparent
                            )
                        )
                    ),
                    SelectionHighlightPopUp()
                )
            )
        ),
        backgroundColor = MaterialTheme.colorScheme.surface,
        xAxisData = xAxisData,
        yAxisData = yAxisData,
        gridLines = GridLines(color = MaterialTheme.colorScheme.outlineVariant)
    )

    Column {
        Row {
            // Dropdown for selecting month
            Box {
                Text(
                    text = "Month: $selectedMonth",
                    modifier = Modifier
                        .clickable { isMonthDropdownExpanded = true }
                        .padding(16.dp)
                )
                DropdownMenu (
                    expanded = isMonthDropdownExpanded,
                    onDismissRequest = { isMonthDropdownExpanded = false }
                ) {
                    months.forEach { month ->
                        DropdownMenuItem(
                            onClick = {
                                selectedMonth = month
                                isMonthDropdownExpanded = false
                            },
                            text = {
                                Text(text = month.toString())
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Dropdown for selecting year
            Box {
                Text(
                    text = "Year: $selectedYear",
                    modifier = Modifier
                        .clickable { isYearDropdownExpanded = true }
                        .padding(16.dp)
                )
                DropdownMenu(
                    expanded = isYearDropdownExpanded,
                    onDismissRequest = { isYearDropdownExpanded = false }
                ) {
                    years.forEach { year ->
                        DropdownMenuItem(
                            onClick = {
                                selectedYear = year
                                isYearDropdownExpanded = false
                            },
                            text = {
                                Text(text = year.toString())
                            }
                        )
                    }
                }
            }
        }

        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            lineChartData = lineChartData
        )
    }
}
