package com.example.fastyme

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.widget.DatePicker
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar
import java.util.Date

val MontserratFamily = FontFamily(
    Font(R.font.montserrat_black, FontWeight.Black),
    Font(R.font.montserrat_bold, FontWeight.Bold),
    Font(R.font.montserrat_extrabold, FontWeight.ExtraBold),
    Font(R.font.montserrat_extralight, FontWeight.ExtraLight),
    Font(R.font.montserrat_light, FontWeight.Light),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold),
    Font(R.font.montserrat_thin, FontWeight.Thin)
)

@Composable
fun Page1 (){
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
            onClick = {},
            //colors = ButtonDefaults.buttonColors(),
            modifier = Modifier
                .padding(16.dp)
                .width(222.dp)
                .height(58.dp)
        ) { Text(
            "Next",
            color = Color(0XFF663090),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = MontserratFamily
        ) }

    }
}

@Composable
fun Page2 (){
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
            onClick = {},
            //colors = ButtonDefaults.buttonColors(),
            modifier = Modifier
                .padding(16.dp)
                .width(222.dp)
                .height(58.dp)
        ) { Text(
            "Next",
            color = Color(0XFF663090),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = MontserratFamily
        ) }

    }
}

@Composable
fun Page3 (){
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
            onClick = {},
            //colors = ButtonDefaults.buttonColors(),
            modifier = Modifier
                .padding(16.dp)
                .width(222.dp)
                .height(58.dp)
        ) { Text(
            "Next",
            color = Color(0XFF663090),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = MontserratFamily
        ) }

    }
}

@Composable
fun Page4() {
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
                painter = painterResource(id = R.drawable.page4),
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

            // Add a spacer of 100dp
            Spacer(modifier = Modifier.size(10.dp))

            Button(
                onClick = {},
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
fun Page5() {
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
                painter = painterResource(id = R.drawable.page5),
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
                onClick = {},
                //colors = ButtonDefaults.buttonColors(),
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
fun Page6() {
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
                onClick = {},
                //colors = ButtonDefaults.buttonColors(),
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
fun Page7() {
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
                painter = painterResource(id = R.drawable.page7),
                contentDescription = null,
                modifier = Modifier.padding(35.dp).width(338.dp).height(248.dp)
            )
            // Creating a button that on
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

            // Adding a space of 100dp height
            Spacer(modifier = Modifier.size(100.dp))
            Button(
                onClick = {},
                //colors = ButtonDefaults.buttonColors(),
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
fun Page8(){
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
            painter = painterResource(id = R.drawable.page8),
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
            onClick = {},
            //colors = ButtonDefaults.buttonColors(),
            modifier = Modifier
                .padding(16.dp)
                .width(222.dp)
                .height(58.dp)
        ) { Text(
            "Next",
            color = Color(0XFF663090),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = MontserratFamily
        ) }

    }
}

@Composable
fun Page9(){
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
            onClick = {},
            //colors = ButtonDefaults.buttonColors(),
            modifier = Modifier
                .padding(16.dp)
                .width(222.dp)
                .height(58.dp)
        ) { Text(
            "Next",
            color = Color(0XFF663090),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = MontserratFamily
        ) }

    }
}