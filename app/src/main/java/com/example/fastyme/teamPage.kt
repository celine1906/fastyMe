package com.example.fastyme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fastyme.R
import com.example.fastyme.ui.theme.MontserratFamily

@Composable
fun TeamPage() {
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
            Button(
                onClick = {},
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .padding(16.dp)
                    .width(222.dp)
                    .height(58.dp)
            ) {
                Text(
                    text = "Next",
                    color = Color(0XFF663090),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = MontserratFamily
                )
            }
        }
    }
}