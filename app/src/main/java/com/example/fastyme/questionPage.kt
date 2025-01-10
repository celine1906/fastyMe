package com.example.fastyme

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.util.Log
import android.widget.DatePicker
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.fastyme.ui.theme.MontserratFamily
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date

fun saveAnswerToFirestore(
    userId: String,
    questionId: String,
    question: String,
    answer: String
) {
    val db = Firebase.firestore
    val answerData = hashMapOf(
        "question" to question,
        "answer" to answer
    )

    db.collection("usersAnswers")
        .document(userId)
        .collection("answers")
        .document(questionId)
        .set(answerData)
        .addOnSuccessListener {
            println("Answer for $questionId saved successfully!")
        }
        .addOnFailureListener { e ->
            e.printStackTrace()
        }
}

@Composable
fun Page1(navController: androidx.navigation.NavHostController, userAnswers: MutableState<MutableList<String>>) {
    val selectedOption = remember { mutableStateOf("") }
    Box (
        modifier = Modifier
            .fillMaxSize()
            .background (
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5624C4),
                        Color(0xFF29115E)
                    )
                )
            )
    )
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.page1),
            contentDescription = null,
            modifier = Modifier.padding(16.dp).width(204.dp).height(204.dp)
        )
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color.White,
                        fontSize = 20.sp
                    )
                ) {
                    append("What is your ")
                }
                append("main goal")
                withStyle(
                    style = SpanStyle(
                        color = Color.White,
                        fontSize = 20.sp
                    )
                ) {
                    append(" for \n doing intermittent fasting?")
                }
            },
//            "What is your main goal for \n" +
//                    "doing intermittent fasting?",
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            color = Color(0xFFDB00FF),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = MontserratFamily,
            textAlign = TextAlign.Center,
        )
        val options = listOf("Weight loss", "Health maintenance", "Boosting energy", "Stay in shape")
        options.forEach { option ->
            OutlinedButton(
                onClick = { selectedOption.value = option },
                modifier = Modifier
                    .padding(8.dp)
                    .width(292.dp)
                    .height(64.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (selectedOption.value == option) Color(0xFFDB00FF) else Color.Transparent
                )
            ) {
                Text(
                    text = option,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = MontserratFamily
                )
            }
        }
        Button(
            onClick = {
                if (selectedOption.value.isNotEmpty()) {
                    // Simpan jawaban ke Firestore menggunakan userId
                    saveAnswerToFirestore(
                        userId = userId,
                        questionId = "goal",
                        question = "What is your main goal for doing intermittent fasting?",
                        answer = selectedOption.value
                    )
                    navController.navigate("page2")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .padding(16.dp)
                .width(222.dp)
                .height(58.dp)
        ) {
            Text(
                "Next",
                color = Color(0XFF663090),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = MontserratFamily
            )
        }


    }
}

@Composable
fun Page2 (navController: androidx.navigation.NavHostController, userAnswers: MutableState<MutableList<String>>){
    val selectedOption = remember { mutableStateOf("") }
    Box (
        modifier = Modifier
            .fillMaxSize()
            .background (
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5624C4),
                        Color(0xFF29115E)
                    )
                )
            )
    )
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.page2),
            contentDescription = null,
            modifier = Modifier.width(185.dp).height(277.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color.White,
                        fontSize = 20.sp
                    )
                ) {
                    append("How familiar are you \n with ")
                }
                append("fasting")
                withStyle(
                    style = SpanStyle(
                        color = Color.White,
                        fontSize = 20.sp
                    )
                ) {
                    append(" ?")
                }
            },
//            "What is your main goal for \n" +
//                    "doing intermittent fasting?",
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            color = Color(0xFFDB00FF),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = MontserratFamily,
            textAlign = TextAlign.Center,
        )
        val options = listOf("I'm new to fasting and need guidance", "I've tried it a few times but \n" +
                " need more information", "I'm experienced with fasting and \n" +
                " just need tracking tools")
        options.forEach { option ->
            OutlinedButton(
                onClick = { selectedOption.value = option },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(90.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (selectedOption.value == option) Color(0xFFDB00FF) else Color.Transparent
                )
            ) {
                Text(
                    text = option,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = MontserratFamily,
                    textAlign = TextAlign.Center
                )
            }
        }
        Button(
            onClick = {
                if (selectedOption.value.isNotEmpty()) {
                    saveAnswerToFirestore(
                        userId = userId,
                        questionId = "fastingFamiliarity",
                        question = "How familiar are you with fasting?",
                        answer = selectedOption.value
                    )
                    navController.navigate("page3")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .padding(16.dp)
                .width(222.dp)
                .height(58.dp)
        ) {
            Text(
                "Next",
                color = Color(0XFF663090),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = MontserratFamily
            )
        }

    }
}

@Composable
fun Page3 (navController: androidx.navigation.NavHostController, userAnswers: MutableState<MutableList<String>>){
    val selectedOption = remember { mutableStateOf("") }
    Box (
        modifier = Modifier
            .fillMaxSize()
            .background (
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5624C4),
                        Color(0xFF29115E)
                    )
                )
            )
    )
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.page3),
            contentDescription = null,
            modifier = Modifier.width(254.dp).height(254.dp)
        )
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color.White,
                        fontSize = 20.sp
                    )
                ) {
                    append("How many ")
                }
                append("meals")
                withStyle(
                    style = SpanStyle(
                        color = Color.White,
                        fontSize = 20.sp
                    )
                ) {
                    append(" do you \n usually eat in a day?")
                }
            },
//            "What is your main goal for \n" +
//                    "doing intermittent fasting?",
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            color = Color(0xFFDB00FF),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = MontserratFamily,
            textAlign = TextAlign.Center,
        )
        val options = listOf("1 meal", "2 meals", "3 meals", "4 or more meals")
        options.forEach { option ->
            OutlinedButton(
                onClick = { selectedOption.value = option },
                modifier = Modifier
                    .padding(8.dp)
                    .width(292.dp)
                    .height(64.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (selectedOption.value == option) Color(0xFFDB00FF) else Color.Transparent
                )
            ) {
                Text(
                    text = option,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = MontserratFamily,
                    textAlign = TextAlign.Center
                )
            }
        }
        Button(
            onClick = {
                if (selectedOption.value.isNotEmpty()) {
                    saveAnswerToFirestore(
                        userId = userId,
                        questionId = "mealsPerDay",
                        question = "How many meals do you usually eat in a day?",
                        answer = selectedOption.value
                    )
                    navController.navigate("page4")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .padding(16.dp)
                .width(222.dp)
                .height(58.dp)
        ) {
            Text(
                "Next",
                color = Color(0XFF663090),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = MontserratFamily
            )
        }

    }
}

@Composable
fun Page4 (navController: androidx.navigation.NavHostController, userAnswers: MutableState<MutableList<String>>){
    val selectedOption = remember { mutableStateOf("") }
    Box (
        modifier = Modifier
            .fillMaxSize()
            .background (
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF5624C4),
                        Color(0xFF29115E)
                    )
                )
            )
    )
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.page4),
            contentDescription = null,
            modifier = Modifier.width(254.dp).height(254.dp)
        )
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color.White,
                        fontSize = 20.sp
                    )
                ) {
                    append("How would you describe your daily ")
                }
                append("lifestyle")
                withStyle(
                    style = SpanStyle(
                        color = Color.White,
                        fontSize = 20.sp
                    )
                ) {
                    append(" ?")
                }
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            color = Color(0xFFDB00FF),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = MontserratFamily,
            textAlign = TextAlign.Center,
        )
        val options = listOf("Mostly sedentary (desk job, minimal movement)", "Moderately active (frequent standing, occasional movement)", "Highly active (lots of movement or standing throughout the day)")
        options.forEach { option ->
            OutlinedButton(
                onClick = { selectedOption.value = option },
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .height(90.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = if (selectedOption.value == option) Color(0xFFDB00FF) else Color.Transparent
                )
            ) {
                Text(
                    text = option,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = MontserratFamily,
                    textAlign = TextAlign.Center
                )
            }
        }
        Button(
            onClick = {
                if (selectedOption.value.isNotEmpty()) {
                    saveAnswerToFirestore(
                        userId = userId,
                        questionId = "activityLevel",
                        question = "How would you describe your daily lifestyle?",
                        answer = selectedOption.value
                    )
                    navController.navigate("page5")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .padding(16.dp)
                .width(222.dp)
                .height(58.dp)
        ) {
            Text(
                "Next",
                color = Color(0XFF663090),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = MontserratFamily
            )
        }

    }
}

@Composable
fun Page5(navController: androidx.navigation.NavHostController, userAnswers: MutableState<MutableList<String>>) {
    // Fetching local context
    val mContext = LocalContext.current

    // Declaring and initializing a calendar
    val mCalendar = Calendar.getInstance()
    val mHour = mCalendar[Calendar.HOUR_OF_DAY]
    val mMinute = mCalendar[Calendar.MINUTE]

    // Values for storing time as strings
    val firstMealTime = remember { mutableStateOf("") }
    val lastMealTime = remember { mutableStateOf("") }

    // Creating TimePicker dialogs
    val firstMealPickerDialog = TimePickerDialog(
        mContext,
        { _, hour: Int, minute: Int ->
            firstMealTime.value = String.format("%02d:%02d", hour, minute)
        },
        mHour,
        mMinute,
        false
    )

    val lastMealPickerDialog = TimePickerDialog(
        mContext,
        { _, hour: Int, minute: Int ->
            lastMealTime.value = String.format("%02d:%02d", hour, minute)
        },
        mHour,
        mMinute,
        false
    )

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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.page5),
                contentDescription = null,
                modifier = Modifier.width(335.dp).height(184.dp)
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    ) {
                        append("At what time do you usually \n have your ")
                    }
                    append("first and last \n meal")
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    ) {
                        append(" of the day?")
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                color = Color(0xFFDB00FF),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = MontserratFamily,
                textAlign = TextAlign.Center,
            )
            // Display selected times
            // Buttons to pick time
            Button(
                onClick = { firstMealPickerDialog.show() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(text = "First Meal = ${firstMealTime.value}",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = MontserratFamily
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            Button(
                onClick = { lastMealPickerDialog.show() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(text = "Last Meal = ${lastMealTime.value}",
                    color = Color.Black,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = MontserratFamily
                )
            }

            Spacer(modifier = Modifier.size(10.dp))

            Button(
                onClick = {
                    if (firstMealTime.value.isNotEmpty() && lastMealTime.value.isNotEmpty()) {
                        val mealtimeAnswer = "First Meal: ${firstMealTime.value}, Last Meal: ${lastMealTime.value}"
                        saveAnswerToFirestore(
                            userId = userId, // Variabel dari MainActivity
                            questionId = "mealTime",
                            question = "At what time do you usually have your first and last meal of the day?",
                            answer = mealtimeAnswer
                        )

                        navController.navigate("page6")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .padding(16.dp)
                    .width(222.dp)
                    .height(58.dp)
            ) {
                Text(
                    "Next",
                    color = Color(0XFF663090),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = MontserratFamily
                )
            }
        }
    }
}

@Composable
fun Page6(navController: androidx.navigation.NavHostController, userAnswers: MutableState<MutableList<String>>) {
    val selectedOption = remember { mutableStateOf("") }
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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.page6),
                contentDescription = null,
                modifier = Modifier.width(254.dp).height(254.dp)
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    ) {
                        append("What is your dietary ")
                    }
                    append("\npreference")
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    ) {
                        append(" ?")
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                color = Color(0xFFDB00FF),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = MontserratFamily,
                textAlign = TextAlign.Center,
            )
            val options = listOf("Vegetarian", "Vegan", "Pescatarian", "Omnivore")
            options.forEach { option ->
                OutlinedButton(
                    onClick = { selectedOption.value = option },
                    modifier = Modifier
                        .padding(8.dp)
                        .width(292.dp)
                        .height(64.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = if (selectedOption.value == option) Color(0xFFDB00FF) else Color.Transparent
                    )
                ) {
                    Text(
                        text = option,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = MontserratFamily,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Button(
                onClick = {
                    if (selectedOption.value.isNotEmpty()) {
                        saveAnswerToFirestore(
                            userId = userId,
                            questionId = "dietaryPreference",
                            question = "What is your dietary preference?",
                            answer = selectedOption.value
                        )
                        navController.navigate("page7")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .padding(16.dp)
                    .width(222.dp)
                    .height(58.dp)
            ) {
                Text(
                    "Next",
                    color = Color(0XFF663090),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = MontserratFamily
                )
            }

        }
    }
}

@Composable
fun Page7(navController: androidx.navigation.NavHostController, userAnswers: MutableState<MutableList<String>>) {
    val selectedOption = remember { mutableStateOf("") }
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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.page7),
                contentDescription = null,
                modifier = Modifier.width(373.dp).height(286.dp)
            )
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    ) {
                        append("What's your ")
                    }
                    append("gender")
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    ) {
                        append(" ?")
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                color = Color(0xFFDB00FF),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = MontserratFamily,
                textAlign = TextAlign.Center,
            )
            val options = listOf("Male", "Female")
            options.forEach { option ->
                OutlinedButton(
                    onClick = { selectedOption.value = option },
                    modifier = Modifier
                        .padding(8.dp)
                        .width(292.dp)
                        .height(64.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = if (selectedOption.value == option) Color(0xFFDB00FF) else Color.Transparent
                    )
                ) {
                    Text(
                        text = option,
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = MontserratFamily,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Button(
                onClick = {
                    if (selectedOption.value.isNotEmpty()) {
                        saveAnswerToFirestore(
                            userId = userId,
                            questionId = "gender",
                            question = "What's your gender?",
                            answer = selectedOption.value
                        )
                        navController.navigate("page8")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .padding(16.dp)
                    .width(222.dp)
                    .height(58.dp)
            ) {
                Text(
                    "Next",
                    color = Color(0XFF663090),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = MontserratFamily
                )
            }

        }
    }
}

@Composable
fun Page8(navController: androidx.navigation.NavHostController, userAnswers: MutableState<MutableList<String>>) {
    // Fetching the Local Context
    val mContext = LocalContext.current

    // Declaring integer values
    // for year, month and day
    val mYear: Int
    val mMonth: Int
    val mDay: Int

    // Initializing a Calendar
    val mCalendar = Calendar.getInstance()

    // Fetching current year, month and day
    mYear = mCalendar.get(Calendar.YEAR)
    mMonth = mCalendar.get(Calendar.MONTH)
    mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

    mCalendar.time = Date()

    // Declaring a string value to
    // store date in string format
    val mDate = remember { mutableStateOf("") }

    // Declaring DatePickerDialog and setting
    // initial values as current values (present year, month and day)
    val mDatePickerDialog = DatePickerDialog(
        mContext,
        { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
            mDate.value = "$mDayOfMonth/${mMonth+1}/$mYear"
        }, mYear, mMonth, mDay
    )
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
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    ) {
                        append("When is your ")
                    }
                    append("birthday")
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontSize = 20.sp
                        )
                    ) {
                        append(" ?")
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                color = Color(0xFFDB00FF),
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                fontFamily = MontserratFamily,
                textAlign = TextAlign.Center,
            )
            Image(
                painter = painterResource(id = R.drawable.page8),
                contentDescription = null,
                modifier = Modifier.padding(35.dp).width(338.dp).height(248.dp)
            )
            // click displays/shows the DatePickerDialog
            // Displaying the mDate value in the Text
            Button(
                onClick = { mDatePickerDialog.show() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Text(text = "Birthday on ${mDate.value}",
                    color = Color.Black,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = MontserratFamily
                )
            }
            Spacer(modifier = Modifier.size(100.dp))
            Button(
                onClick = {
                    if (mDate.value.isNotEmpty()) {
                        saveAnswerToFirestore(
                            userId = userId,
                            questionId = "age",
                            question = "When is your birthday?",
                            answer = mDate.value
                        )
                        navController.navigate("page9")
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .padding(16.dp)
                    .width(222.dp)
                    .height(58.dp)
            ) {
                Text(
                    "Next",
                    color = Color(0XFF663090),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = MontserratFamily
                )
            }

        }
    }
}

@Composable
fun Page9(navController: androidx.navigation.NavHostController, userAnswers: MutableState<MutableList<String>>){
    var text by remember { mutableStateOf("") }
    Box (
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
    )
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.page9),
            contentDescription = null,
            modifier = Modifier
                .width(343.dp)
                .height(193.dp)
        )
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color.White,
                        fontSize = 20.sp
                    )
                ) {
                    append("What's your ")
                }
                append("height")
                withStyle(
                    style = SpanStyle(
                        color = Color.White,
                        fontSize = 20.sp
                    )
                ) {
                    append("?")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color(0xFFDB00FF),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = MontserratFamily,
            textAlign = TextAlign.Center,
        )
        Column {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(
                    "cm",
                    textAlign = TextAlign.Left,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = MontserratFamily
                ) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (text.isNotEmpty()) {
                    saveAnswerToFirestore(
                        userId =userId,
                        questionId = "height",
                        question = "What's your height?",
                        answer = text
                    )
                    navController.navigate("page10")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .padding(16.dp)
                .width(222.dp)
                .height(58.dp)
        ) {
            Text(
                "Next",
                color = Color(0XFF663090),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = MontserratFamily
            )
        }

    }
}

@Composable
fun Page10(navController: androidx.navigation.NavHostController, userAnswers: MutableState<MutableList<String>>){
    var text by remember { mutableStateOf("") }
    Box (
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
    )
    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.page10),
            contentDescription = null,
            modifier = Modifier
                .width(343.dp)
                .height(193.dp)
        )
        Text(
            text = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(
                        color = Color.White,
                        fontSize = 20.sp
                    )
                ) {
                    append("What's your current ")
                }
                append("weight")
                withStyle(
                    style = SpanStyle(
                        color = Color.White,
                        fontSize = 20.sp
                    )
                ) {
                    append("?")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = Color(0xFFDB00FF),
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = MontserratFamily,
            textAlign = TextAlign.Center,
        )
        Column {
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text(
                    "kg",
                    textAlign = TextAlign.Left,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = MontserratFamily
                ) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (text.isNotEmpty()) {
                    saveAnswerToFirestore(
                        userId = userId,
                        questionId = "weight",
                        question = "What's your current weight?",
                        answer = text
                    )
                    navController.navigate("recommendation")
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .padding(16.dp)
                .width(222.dp)
                .height(58.dp)
        ) {
            Text(
                "Submit",
                color = Color(0XFF663090),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = MontserratFamily
            )
        }

    }
}

fun fetchAnswers(userId: String, onComplete: (Map<String, String>) -> Unit) {
    val db = Firebase.firestore
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

fun saveRecommendation(userId: String, recommendation: Recommendation) {
    val db = FirebaseFirestore.getInstance()
    val recommendationMap = hashMapOf(
        "fastingType" to recommendation.fastingType,
        "fastingDescription" to recommendation.fastingDescription,
        "calorieIntake" to recommendation.calorieIntake,
        "waterIntake" to recommendation.waterIntake,
        "fastingSchedule" to recommendation.fastingSchedule,
        "eatingSchedule" to recommendation.eatingSchedule,
        "timestamp" to System.currentTimeMillis()
    )

    db.collection("recommendations")
        .document(userId) // Dokumen berdasarkan userId
        .set(recommendationMap)
        .addOnSuccessListener {
            Log.d("Firestore", "Recommendation successfully saved!")
        }
        .addOnFailureListener { e ->
            Log.e("FirestoreError", "Error saving recommendation: ${e.message}")
        }
}

// Model data untuk rekomendasi
data class Recommendation(
    val fastingType: String,
    val fastingDescription: String,
    val calorieIntake: Int,
    val waterIntake: Int,
    val fastingSchedule: String,
    val eatingSchedule: String
)

@Composable
fun GeminiRecommendation(navController: androidx.navigation.NavHostController, userId: String) {
    val context = LocalContext.current
    var recommendation by remember { mutableStateOf("Fetching recommendation...") }
    var recommendationData by remember { mutableStateOf<Recommendation?>(null) }

    // Fungsi untuk memanggil Gemini dan mendapatkan rekomendasi
    fun fetchRecommendation() {
        fetchAnswers(userId) { answers ->
            val generativeModel = GenerativeModel(
                modelName = "gemini-1.5-flash",
                apiKey = "AIzaSyCnT1SCfEDNqAQnRcIVHVDnBsqSUXMd6tw"
            )

            val prompt = """
                Based on the following user profile:
                Goal: ${answers["goal"]}
                Fasting Familiarity: ${answers["fastingFamiliarity"]}
                Meals per Day: ${answers["mealsPerDay"]}
                First Meal and Last Meal Time: ${answers["mealTime"]}
                Activity Level/Daily Lifestyle: ${answers["activityLevel"]}
                Dietary Preference: ${answers["dietaryPreference"]}
                Gender: ${answers["gender"]}
                Count Age From Birthday: ${answers["age"]}
                Height (cm): ${answers["height"]}
                Weight (kg): ${answers["weight"]}

                Provide recommendations for a suitable type of intermittent fasting (IF) for the user (choose one from OMAD (One Meal A Day), Leangains Protocol (16/8), Eat Stop Eat (24 hours), Warrior Diet (20/4)).
                Also, provide the ideal calorie intake (kcal/day), water intake (ml/day), and the specific fasting and eating schedule by their usual meal time everyday.
                Format the response as a JSON object like this : {"fastingType":str, "fastingDescription":str, "calorieIntake":Int, "waterIntake":Int, "fastingSchedule":str, "eatingSchedule":str}.
                Ensure the response is a valid JSON object that matches the provided structure exactly, without any additional explanations or notes.
            """.trimIndent()

            // untuk mengupdate UI setelah data didapat
            MainScope().launch {
                try {
                    val response = generativeModel.generateContent(prompt)
                    recommendation = response.text ?: "No response"
                    Log.d("GeminiResponse", "Recommendation: $recommendation")

                    // Parsing JSON response ke dalam model data
                    val gson = Gson()

                    // Untuk membersihkan string JSON
                    val cleanedJson = recommendation.trim()
                        .removePrefix("```json")
                        .removeSuffix("```")
                        .trim() // Memastikan tidak ada spasi tambahan

                    val parsedRecommendationData: Recommendation? = try {
                        gson.fromJson(cleanedJson, Recommendation::class.java)
                    } catch (e: Exception) {
                        Log.e("ParsingError", "Failed to parse JSON: ${e.message}")
                        null
                    }

                    // Update state dengan hasil parsing data
                    recommendationData = parsedRecommendationData
                    // Menyimpan hasil rekomemdasi ke dalam database
                    if (recommendationData != null) {
                        saveRecommendation(userId, recommendationData!!)
                    }
                    Log.d("ParsedData", "Parsed Recommendation: $recommendationData")
                } catch (e: Exception) {
                    recommendation = "Failed to fetch recommendation: ${e.message}"
                    Log.e("GeminiError", e.message ?: "Unknown error")
                }
            }
        }
    }

    // Hanya panggil fetchRecommendation jika userId berubah
    LaunchedEffect(userId) {
        fetchRecommendation()
    }
    Box (
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
    )
    // Menampilkan loading jika rekomendasi data null
    if (recommendationData == null) {
        // Loading
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(48.dp)
                    .padding(16.dp)
            )
            Text(
                text = "Loading...",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    } else {
        // Display Recommendation ketika setelah mendapatkan data
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
                        text = "Fasting Type: ${recommendationData?.fastingType}",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Fasting Description: ${recommendationData?.fastingDescription}",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Calorie Intake: ${recommendationData?.calorieIntake} kcal/day",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Water Intake: ${recommendationData?.waterIntake} ml/day",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Fasting Schedule: ${recommendationData?.fastingSchedule}",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Text(
                        text = "Eating Schedule: ${recommendationData?.eatingSchedule}",
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
                    navController.navigate("home")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Start Journey", fontSize = 18.sp, color = Color.White)
            }
        }
    }
}