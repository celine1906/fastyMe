package com.example.fastyme

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.util.Log
import co.yml.charts.common.model.Point
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import co.yml.charts.axis.AxisData
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
import com.example.fastyme.ui.theme.MontserratFamily
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@Serializable
object Profile

@Composable
fun ProfilePage(navController: NavController) {
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
                profile(userId.toString(), navController)
                recommendation(userId.toString(), navController)
                plan()
                achievement()
                calorieIntake()
                waterIntake()
                memberTeam()
            }
        }
    }
}

fun fetchUsersAnswer(userId: String, onComplete: (Map<String, String>) -> Unit) {
    db.collection("usersAnswers")
        .document(userId)
        .collection("answers")
        .get()
        .addOnSuccessListener { result ->
            val answers = mutableMapOf<String, String>()
            for (document in result) {
                val questionId = document.id
                val answer = document.getString("answer") ?: ""
                answers[questionId] = answer
            }
            onComplete(answers)
        }
        .addOnFailureListener { exception ->
            Log.e("FirestoreError", "Failed to fetch answers: ${exception.message}")
        }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun profile(userId: String, navController:NavController) {
    var userAnswers by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    var showEditDialog by remember { mutableStateOf(false) }

    // Fetch data on profile load
    LaunchedEffect(userId) {
        fetchUsersAnswer(userId) { answers ->
            userAnswers = answers
        }
    }

    // Recalculate age whenever userAnswers changes
    val countAge by derivedStateOf {
        userAnswers["age"]?.let { age ->
            try {
                val formatter = DateTimeFormatter.ofPattern("d/M/yyyy")
                val birthdayDate = LocalDate.parse(age, formatter) // Format: "d/M/yyyy"
                val currentDate = LocalDate.now()
                Period.between(birthdayDate, currentDate).years
            } catch (e: Exception) {
                Log.e("AgeCalculationError", "Invalid birthday format: $age")
                null
            }
        }
    }

    val (bmi, bmiCategory) = derivedStateOf {
        val weight = userAnswers["weight"]?.toDoubleOrNull()
        val height = userAnswers["height"]?.toDoubleOrNull()
        if (weight != null && height != null && weight > 0 && height > 0) {
            val heightInMeters = height / 100
            val calculatedBmi = weight / (heightInMeters * heightInMeters)
            val category = when {
                calculatedBmi < 18.5 -> "Underweight"
                calculatedBmi < 24.9 -> "Normal weight"
                calculatedBmi < 29.9 -> "Overweight"
                else -> "Obese"
            }
            calculatedBmi to category
        } else null to null
    }.value

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
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
            Button(onClick = { showEditDialog = true }) {
                Text("Edit")
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        CardBox(
            content = {
                Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Age: ${countAge ?: "N/A"}")
                        Text("Sex: ${userAnswers["gender"] ?: "N/A"}")
                        Text("BMI: ${bmi?.let { String.format("%.2f", it) } ?: "Calculating..."}")
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Weight: ${userAnswers["weight"] ?: "N/A"} kg")
                        Text("Height: ${userAnswers["height"] ?: "N/A"} cm")
                        Text("Category: ${bmiCategory ?: "Calculating..."}")
                    }
                }
            }
        )
    }

    if (showEditDialog) {
        EditProfileDialog(
            userId = userId,
            currentAnswers = userAnswers,
            onDismiss = { showEditDialog = false },
            onSave = { updatedAnswers ->
                saveAnswersToFirestore(userId, updatedAnswers) {
                    fetchUsersAnswer(userId) { answers ->
                        userAnswers = answers
                    }
                }
                showEditDialog = false
            }
        )
    }
    // Logout Button
    Button(
        onClick = {
            FirebaseAuth.getInstance().signOut()
            navController.navigate("loginPage") {
                popUpTo(0) { inclusive = true }
            }
        },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text(text = "Logout", color = Color.White)
    }
}

@Composable
fun EditProfileDialog(
    userId: String,
    currentAnswers: Map<String, String>,
    onDismiss: () -> Unit,
    onSave: (Map<String, String>) -> Unit
) {
    val weight = remember { mutableStateOf(currentAnswers["weight"] ?: "") }
    val height = remember { mutableStateOf(currentAnswers["height"] ?: "") }
    val goal = remember { mutableStateOf(currentAnswers["goal"] ?: "") }

    val goalOptions = listOf("Weight loss", "Health maintenance", "Boosting energy", "Stay in shape")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column {
                TextField(
                    value = weight.value,
                    onValueChange = { weight.value = it },
                    label = { Text("Weight (kg)") }
                )

                TextField(
                    value = height.value,
                    onValueChange = { height.value = it },
                    label = { Text("Height (cm)") }
                )

                Text("Goal:")
                goalOptions.forEach { option ->
                    Button(
                        onClick = { goal.value = option },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (goal.value == option) Color.Gray else Color.White
                        ),
                        modifier = Modifier.padding(4.dp)
                    ) {
                        Text(option)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                val updatedAnswers = mapOf(
                    "weight" to weight.value,
                    "height" to height.value,
                    "goal" to goal.value
                )
                saveAnswersToFirestore(userId, updatedAnswers) {
                    onSave(updatedAnswers)
                }
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

fun saveAnswersToFirestore(userId: String, answers: Map<String, String>, onComplete: () -> Unit) {
    db.collection("usersAnswers")
        .document(userId)
        .collection("answers")
        .get()
        .addOnSuccessListener { result ->
            for ((key, value) in answers) {
                db.collection("usersAnswers")
                    .document(userId)
                    .collection("answers")
                    .document(key)
                    .set(mapOf("answer" to value))
            }
            onComplete()
        }
        .addOnFailureListener { exception ->
            Log.e("FirestoreError", "Failed to save answers: ${exception.message}")
        }
}

@Composable
fun title(str: String, imageResId: Int) {
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
            contentDescription = "",
            modifier = Modifier
                .size(30.dp)
        )
    }
}

@Composable
fun recommendation(userId: String, navController:NavController) {
    CardBox(
        content = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                title("Recommendation", R.drawable.plan)
                Button(onClick = {navController.navigate("geminipage")}) {
                    Text("See recommendation")
                }
            }
        }
    )
}

fun fetchRecommendation(userId: String, onSuccess: (Recommendation?) -> Unit, onFailure: (String) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    // Mendapatkan dokumen berdasarkan userId dari koleksi "recommendations"
    db.collection("recommendations")
        .document(userId)
        .get()
        .addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                // Ambil data rekomendasi dari Firestore
                val fastingType = documentSnapshot.getString("fastingType")
                val fastingDescription = documentSnapshot.getString("fastingDescription")
                val calorieIntake = documentSnapshot.getLong("calorieIntake")?.toInt()
                val waterIntake = documentSnapshot.getLong("waterIntake")?.toInt()
                val fastingSchedule = documentSnapshot.getString("fastingSchedule")
                val eatingSchedule = documentSnapshot.getString("eatingSchedule")

                // Membuat objek Recommendation dari data yang diambil
                val recommendation = Recommendation(
                    fastingType = fastingType ?: "",
                    fastingDescription = fastingDescription ?: "",
                    calorieIntake = calorieIntake ?: 0,
                    waterIntake = waterIntake ?: 0,
                    fastingSchedule = fastingSchedule ?: "",
                    eatingSchedule = eatingSchedule ?: ""
                )

                // Memanggil onSuccess callback dengan data rekomendasi yang diambil
                onSuccess(recommendation)
            } else {
                // Jika dokumen tidak ada
                onFailure("Recommendation not found")
            }
        }
        .addOnFailureListener { exception ->
            // Jika ada error saat mengambil data
            onFailure("Error fetching recommendation: ${exception.message}")
        }
}

@Composable
fun geminipage (userId: String, navController:NavController) {
    var recommendations by remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Recommendation Program",
            style = TextStyle(
                fontFamily = MontserratFamily,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFFFFF),
                letterSpacing = 1.5.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "Fasting Type: ${recommendations["fastingType"] ?: "N/A"}",
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Fasting Description: ${recommendations["fastingDescription"] ?: "N/A"}",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Calorie Intake: ${recommendations["calorieIntake"] ?: "N/A"} kcal/day",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Water Intake: ${recommendations["waterIntake"] ?: "N/A"} ml/day",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Fasting Schedule: ${recommendations["fastingSchedule"] ?: "N/A"}",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    ),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Eating Schedule: ${recommendations["eatingSchedule"] ?: "N/A"}",
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                navController.navigate("ProfilePage")
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(text = "Back", fontSize = 18.sp, color = Color.White)
        }
    }
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
fun memberTeam() {
    CardBoxTeam(
        content = {
            Column() {
                teamMemberPage()
            }
        }
    )
}

@Composable
fun teamMemberPage() {
    val teamMembers = listOf(
        R.drawable.team1,
        R.drawable.team2,
        R.drawable.team3,
        R.drawable.team4
    )
    val pagerState = rememberPagerState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5624C4),
                        Color(0xFF29115E)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Meet Our Team!",
                color = Color.White,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = MontserratFamily,
                modifier = Modifier.padding(top = 25.dp)
            )

            // Horizontal Pager for Team Members
            HorizontalPager(
                count = teamMembers.size,
                modifier = Modifier.weight(1f)
            )
            { page ->
                Image(
                    painter = painterResource(id = teamMembers[page]),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 32.dp)
                        .fillMaxWidth()
                        .aspectRatio(0.883f),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun CardBoxTeam(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .background(Color(0xFFD9C2EC), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun CardBox(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(20.dp))
            .background(Color(0xFFD9C2EC), RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        content()
    }
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