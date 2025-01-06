package com.example.fastyme

import CalorieIntake
import DetailCalorieScreen
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.snap
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.fastyme.ui.theme.FastyMeTheme
import com.google.firebase.auth.FirebaseAuth

// Define route constants
const val HOME_ROUTE = "home"
const val FASTING_ROUTE = "fasting"
const val RECIPE_ROUTE = "recipe"
const val CALENDAR_ROUTE = "calendar"
const val PROFILE_ROUTE = "profile"
//const val CALORIE_ROUTE = "calorie"
val user = FirebaseAuth.getInstance().currentUser
val userId = user?.uid ?: "guest"

// Main UI
@Composable
fun BottomNavBar(navController: NavHostController) {

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color(0xFF5624C4)) {
                NavigationBarItem(
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Home,
                            contentDescription = null,
                            modifier = Modifier.size(35.dp)
                        )
                    },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    onClick = { navController.navigate(HOME_ROUTE) }
                )
                NavigationBarItem(
                    selected = false,
                    icon = {
                        Icon(
                            bitmap = ImageBitmap.imageResource(R.drawable.fasting),
                            contentDescription = null,
                            modifier = Modifier.size(35.dp)
                        )
                    },
                    label = { Text("Fasting") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    onClick = { navController.navigate(FASTING_ROUTE) }
                )
                NavigationBarItem(
                    selected = false,
                    icon = {
                        Icon(
                            bitmap = ImageBitmap.imageResource(R.drawable.recipe),
                            contentDescription = null,
                            modifier = Modifier.size(35.dp)
                        )
                    },
                    label = { Text("Recipe") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    onClick = { navController.navigate(RECIPE_ROUTE) }
                )
                NavigationBarItem(
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.DateRange,
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                    },
                    label = { Text("Calendar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    onClick = { navController.navigate(CALENDAR_ROUTE) }
                )
                NavigationBarItem(
                    selected = false,
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            modifier = Modifier.size(35.dp)
                        )
                    },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = Color.White,
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray
                    ),
                    onClick = { navController.navigate(PROFILE_ROUTE) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HOME_ROUTE,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(HOME_ROUTE) {
                // Panggil UI Home di sini
                FastingAppUI(
                    navController = navController,
                )
            }
            composable(FASTING_ROUTE) { FastingPage() }
            composable(RECIPE_ROUTE) { RecipeApp() }
            composable(CALENDAR_ROUTE) { CalendarPage() }
            composable(PROFILE_ROUTE) { ProfilePage() }
            composable("waterIntake") { WaterIntake(userId, navController) }
            composable("calorie") { CalorieIntake(userId, navController) }
            composable("detailCalorie/{mealType}") { backStackEntry ->
                val mealType = backStackEntry.arguments?.getString("mealType") ?: ""
                DetailCalorieScreen(name = mealType, navController)
            }
        }

    }
}



// Main Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            FastyMeTheme {
                // Fetch initial data on load
                fetchData()
                val navController = rememberNavController()
                BottomNavBar(navController)
            }
        }
    }
}