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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.serialization.Serializable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.font.FontWeight

@Serializable
object Profile

@Composable
fun ProfilePage() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        profile()
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
            Button(onClick = {}) {
                Text("Edit")
            }
        }

        Spacer(modifier = Modifier.width(16.dp)) // Jarak antara Column

        // User Information
        Column(
            modifier = Modifier.fillMaxWidth()
                .background(Color(0xFFD9C2EC)),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp).clip(RoundedCornerShape(10.dp)),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Age : ")
                    Text("Sex : ")
                }
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Weight : ")
                    Text("Height : ")
                    Text("BMI : ")
                }
            }
        }
    }
}

@Composable
fun plan() {
    Column(

    ) {
        title("My Plan")
    }
}

@Composable
fun title(str:String) {
    Text(
        text = str,
        fontWeight = FontWeight.Bold
    )
    Icon(
        bitmap = ImageBitmap.imageResource(R.drawable.recipe),
        contentDescription="",
        modifier = Modifier
            .size(35.dp)
    )
}

@Composable
fun calorieIntake() {

}

@Composable
fun waterIntake() {

}