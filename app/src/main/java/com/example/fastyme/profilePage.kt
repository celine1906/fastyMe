package com.example.fastyme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import java.text.SimpleDateFormat
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
        }
    )
}

@Composable
fun waterIntake() {
    CardBox(
        content = {
            title("Water Intake", R.drawable.water_intake)
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