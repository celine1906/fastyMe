package com.example.fastyme

import android.content.ContentValues.TAG
import android.util.Log
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
import androidx.compose.runtime.mutableStateListOf
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
import com.google.firebase.firestore.FieldPath
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
            Column() {


            title("Calorie Intake", R.drawable.calorie_intake)
//            Spacer(modifier = Modifier.width(16.dp))
            LineChartScreen("Calorie Intake", "totalCalorieIntake", 2000.toFloat())
            }
        }
    )
}

@Composable
fun waterIntake() {
    CardBox(
        content = {
            Column() {

            title("Water Intake", R.drawable.water_intake)
//            Spacer(modifier = Modifier.height(10.dp))
            LineChartScreen("Water Intake", "totalWaterIntake", 2100.toFloat())
            }
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

@Composable
fun LineChartScreen(category:String, fieldName: String, limit:Float) {
    val currentMonth = remember { LocalDate.now().monthValue }
    val currentYear = remember { LocalDate.now().year }
    var selectedMonth by remember { mutableStateOf(currentMonth) }
    var selectedYear by remember { mutableStateOf(currentYear) }
    var isMonthDropdownExpanded by remember { mutableStateOf(false) }
    var isYearDropdownExpanded by remember { mutableStateOf(false) }

    val months = (1..12).toList()
    val years = (2024..2025).toList()

    val pointsData = remember { mutableStateListOf<Point>() }

    // Fetch data when month or year changes
    LaunchedEffect(selectedMonth, selectedYear) {
        fetchDataChart(selectedYear, selectedMonth, pointsData, category, fieldName)
    }

    val xAxisSteps = if (pointsData.isNotEmpty()) pointsData.size - 1 else 0

    val xAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .backgroundColor(Color.Transparent)
        .steps(xAxisSteps) // Ensure steps are not negative
        .labelData { i -> "${i + 1}" }
        .labelAndAxisLinePadding(15.dp)
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()

    val yAxisSteps = if (pointsData.isNotEmpty()) 5 else 1 // Adjust steps based on data availability

    val maxYValue = pointsData.maxOfOrNull { it.y } ?: 0f
    val minYValue = pointsData.minOfOrNull { it.y } ?: 0f
    val yScale = if (maxYValue > 0) (maxYValue - minYValue) / yAxisSteps else 0f

    val yAxisData = AxisData.Builder()
        .steps(yAxisSteps)
        .backgroundColor(Color.Transparent)
        .labelAndAxisLinePadding(50.dp)
        .labelData { i ->
            val yValue = minYValue + i * yScale
            yValue.toString().substringBefore(".")
        }
        .axisLineColor(MaterialTheme.colorScheme.tertiary)
        .axisLabelColor(MaterialTheme.colorScheme.tertiary)
        .build()




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

        val linePlotData = if (pointsData.isNotEmpty()) {
            LinePlotData(
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
            )
        } else {
            LinePlotData(lines = listOf()) // Provide empty lines for no data case
        }

        val lineChartData = LineChartData(
            linePlotData = linePlotData,
            backgroundColor = MaterialTheme.colorScheme.surface,
            xAxisData = xAxisData,
            yAxisData = yAxisData,
            gridLines = GridLines(color = MaterialTheme.colorScheme.outlineVariant)
        )


        LineChart(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp),
            lineChartData = lineChartData
        )
    }
}




fun fetchDataChart(selectedYear: Int, selectedMonth: Int, pointsData: MutableList<Point>, category:String, fieldName:String) {
    val startDate = LocalDate.of(selectedYear, selectedMonth, 1)
    val endDate = startDate.withDayOfMonth(startDate.lengthOfMonth())

    val defaultData = mutableMapOf<Int, Float>().apply {
        for (day in 1..endDate.dayOfMonth) {
            this[day] = 0f
        }
    }

    db.collection("${category}")
        .whereGreaterThanOrEqualTo(FieldPath.documentId(), "${userId}_$selectedYear-${selectedMonth.toString().padStart(2, '0')}-01")
        .whereLessThanOrEqualTo(FieldPath.documentId(), "${userId}_$selectedYear-${selectedMonth.toString().padStart(2, '0')}-${endDate.dayOfMonth.toString().padStart(2, '0')}")
        .addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            pointsData.clear()

            snapshots?.forEach { snapshot ->
                val documentId = snapshot.id
                val datePart = documentId.substringAfter("_")
                val localDate = LocalDate.parse(datePart)

                if (localDate.year == selectedYear && localDate.monthValue == selectedMonth) {
                    val day = localDate.dayOfMonth
                    val totalIntake = snapshot.getLong("${fieldName}")?.toFloat() ?: 0f
                    defaultData[day] = totalIntake
                }
            }

            // Convert map to pointsData list
            defaultData.forEach { (day, totalIntake) ->
                pointsData.add(Point(day.toFloat(), totalIntake))
            }

            // Check if pointsData is empty and add a default value
            if (pointsData.isEmpty()) {
                pointsData.add(Point(0f, 0f))
            }
        }
}



