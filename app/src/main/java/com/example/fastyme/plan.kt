package com.example.fastyme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlanPage(navController: NavController) {
    var selectedTab by remember { mutableStateOf("Daily") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigateUp() }) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Go Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Fasting Plan",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        // Tab Selector
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .background(Color(0xFFF5E5F7), RoundedCornerShape(30)),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            TabButton(
                title = "Daily",
                isSelected = selectedTab == "Daily",
                onClick = { selectedTab = "Daily" }
            )
            TabButton(
                title = "Weekly",
                isSelected = selectedTab == "Weekly",
                onClick = { selectedTab = "Weekly" }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        when (selectedTab) {
            "Daily" -> DailyPlans()
            "Weekly" -> WeeklyPlans()
        }
    }
}

@Composable
fun TabButton(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .background(if (isSelected) Color(0xFF6200EE) else Color.Transparent, RoundedCornerShape(30))
            .height(40.dp)
            .width(190.dp) // Atur lebar tombol sesuai kebutuhan
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = if (isSelected) Color.White else Color.Black,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun DailyPlans() {
    FastingSection("Basic Fasting", listOf(
        FastingPlan("12:12", "12h fasting", "12h eating", "Beginner"),
        FastingPlan("14:10", "14h fasting", "10h eating", null),
        FastingPlan("15:9", "15h fasting", "9h eating", null),
        FastingPlan("16:8", "16h fasting", "8h eating", "Popular")
    ))

    FastingSection("Advance Fasting", listOf(
        FastingPlan("17:7", "17h fasting", "7h eating", null),
        FastingPlan("18:6", "18h fasting", "6h eating", null),
        FastingPlan("19:5", "19h fasting", "5h eating", null),
        FastingPlan("20:4", "20h fasting", "4h eating", "Popular")
    ))

    FastingSection("Intermediate Fasting", listOf(
        FastingPlan("21:3", "21h fasting", "3h eating", null),
        FastingPlan("22:2", "22h fasting", "2h eating", null),
        FastingPlan("23:1", "23h fasting", "1h eating", "Popular, OMAD"),
        FastingPlan("24", "24h fasting", null, null)
    ))
}

@Composable
fun WeeklyPlans() {
    FastingSection("Weekly Fasting Plans", listOf(
        FastingPlan("3:1", "3 days fasting", "1 day eating", "Popular"),
        FastingPlan("5:2", "5 days normal", "2 days fasting", null)
    ))
}

@Composable
fun FastingSection(title: String, plans: List<FastingPlan>) {
    Text(
        text = title,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(vertical = 8.dp)
    )
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        plans.forEach { plan ->
            FastingPlanCard(plan)
        }
    }
}

@Composable
fun FastingPlanCard(plan: FastingPlan) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDE7F6)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(Color(0xFF6200EE), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = plan.time,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = plan.fasting,
                    fontSize = 14.sp,
                    color = Color.Black
                )
                plan.eating?.let {
                    Text(
                        text = it,
                        fontSize = 14.sp,
                        color = Color.Black
                    )
                }
            }

            plan.tag?.let {
                Text(
                    text = it,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier
                        .background(Color(0xFFBB86FC), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

data class FastingPlan(
    val time: String,
    val fasting: String,
    val eating: String?,
    val tag: String?
)
