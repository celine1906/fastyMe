package com.example.fastyme

import CalorieIntake
import DetailCalorieScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.fastyme.ui.theme.FastyMeTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

const val HOME_ROUTE = "home"
const val FASTING_ROUTE = "fasting"
const val RECIPE_ROUTE = "recipe"
const val CALENDAR_ROUTE = "calendar"
const val PROFILE_ROUTE = "profile"
const val FASTING_DETAIL_ROUTE = "fasting_detail"
const val FASTING_PLAN = "fasting_plan"
const val LOGIN_ROUTE = "loginPage"
const val REGISTER_ROUTE = "registerPage"
const val FASTING_INPUT = "planInput/{time}"

val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

//val user = FirebaseAuth.getInstance().currentUser
//val userId = user?.uid ?: "guest"

@Composable
fun BottomNavBar(navController: NavHostController) {
    NavigationBar(containerColor = Color(0xFF5624C4)) {
        NavigationBar(containerColor = Color(0xFF5624C4)) {
            NavigationBarItem(
                selected = false,
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription = null,
                        modifier = Modifier.size(35.dp),
                        tint = Color.White // Ikon vector menjadi putih
                    )
                },
                label = { Text("Home", color = Color.White) }, // Teks menjadi putih
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
                    Image(
                        painter = painterResource(R.drawable.fasting),
                        contentDescription = null,
                        modifier = Modifier.size(35.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White) // Gambar bitmap menjadi putih
                    )
                },
                label = { Text("Fasting", color = Color.White) }, // Teks menjadi putih
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
                    Image(
                        painter = painterResource(R.drawable.recipe),
                        contentDescription = null,
                        modifier = Modifier.size(35.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(Color.White) // Gambar bitmap menjadi putih
                    )
                },
                label = { Text("Recipe", color = Color.White) }, // Teks menjadi putih
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
                        modifier = Modifier.size(32.dp),
                        tint = Color.White // Ikon vector menjadi putih
                    )
                },
                label = { Text("Calendar", color = Color.White) }, // Teks menjadi putih
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
                        modifier = Modifier.size(35.dp),
                        tint = Color.White // Ikon vector menjadi putih
                    )
                },
                label = { Text("Profile", color = Color.White) }, // Teks menjadi putih
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
}

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        WindowCompat.setDecorFitsSystemWindows(window, false) // Opsional jika ingin immersive mode
        actionBar?.hide()
        setContent {
            FastyMeTheme {
                val navController = rememberNavController()
                // Periksa rute aktif
                val currentRoute = navController.currentBackStackEntryFlow
                    .collectAsState(initial = navController.currentBackStackEntry)
                    .value?.destination?.route

                Scaffold(
                    bottomBar = {
                        // Sembunyikan BottomNavBar di halaman login dan register
                        if (currentRoute != LOGIN_ROUTE && currentRoute != REGISTER_ROUTE) {
                            BottomNavBar(navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = if (FirebaseAuth.getInstance().currentUser == null) LOGIN_ROUTE else HOME_ROUTE,
                        modifier = Modifier.padding()
                    ) {
                        composable(LOGIN_ROUTE) {
                            val authViewModel: AuthViewModel = viewModel()
                            LoginPage(
                                modifier = Modifier.fillMaxSize(),
                                navController = navController,
                                authViewModel = authViewModel
                            )
                        }
                        composable(REGISTER_ROUTE) {
                            val authViewModel: AuthViewModel = viewModel()
                            RegisterPage(
                                modifier = Modifier.fillMaxSize(),
                                navController = navController,
                                authViewModel = authViewModel
                            )
                        }
                        composable(HOME_ROUTE) {
                            FastingAppUI(navController = navController)
                        }
                        composable(FASTING_ROUTE) { FastingPage(navController) }
                        composable(RECIPE_ROUTE) { RecipeApp() }
                        composable(CALENDAR_ROUTE) { CalendarPage(navController) }
                        composable(PROFILE_ROUTE) { ProfilePage(navController) }
                        composable("waterIntake") { WaterIntake(navController) }
                        composable("calorie") { CalorieIntake(navController) }
                        composable("detailCalorie/{mealType}") { backStackEntry ->
                            val mealType = backStackEntry.arguments?.getString("mealType") ?: ""
                            DetailCalorieScreen(name = mealType, navController)
                        }
                        composable(
                            route = "$FASTING_DETAIL_ROUTE/{fastingId}",
                            arguments = listOf(navArgument("fastingId") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val fastingId = backStackEntry.arguments?.getString("fastingId") ?: ""
                            FastingDetailScreen(navController = navController, fastingId = fastingId)
                        }

                        composable(FASTING_PLAN) { PlanPage(navController) }
                        composable(
                            route = "planInput/{time}",
                            arguments = listOf(navArgument("time") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val time = backStackEntry.arguments?.getString("time") ?: "12:12"
                            PlanInputPage(navController, time)
                        }
                    }
                }
            }
        }
    }
}