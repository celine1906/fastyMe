package com.example.fastyme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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



@Composable
fun bottomNavBar(navHostController: NavHostController) {
    Scaffold (
        bottomBar = {
            NavigationBar(
                containerColor = Color(0xFF5624C4)
            ) {
                NavigationBarItem (
                    selected = false,
                    icon = { Icon(
                        imageVector = Icons.Outlined.Home,
                        contentDescription="",
                        modifier = Modifier
                            .size(35.dp)
                    ) },
                    label = { Text("Home") }, // Text under the icon
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White, // Icon color when selected
                        selectedTextColor = Color.White, // Text color when selected
                        unselectedIconColor = Color.Gray, // Icon color when unselected
                        unselectedTextColor = Color.Gray // Text color when unselected
                    ),
                    onClick = {
                        navHostController.navigate(Dashboard)
                    }
                )
                NavigationBarItem(
                    selected = false,
                    icon = { Icon(
                        bitmap = ImageBitmap.imageResource(R.drawable.fasting),
                        contentDescription="",
                        modifier = Modifier
                            .size(35.dp)
                    ) },
                    label = { Text("Fasting") }, // Text under the icon
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White, // Icon color when selected
                        selectedTextColor = Color.White, // Text color when selected
                        unselectedIconColor = Color.Gray, // Icon color when unselected
                        unselectedTextColor = Color.Gray // Text color when unselected
                    ),
                    onClick = {
                        navHostController.navigate(Fasting)
                    }
                )
                NavigationBarItem(
                    selected = false,
                    icon = { Icon(
                        bitmap = ImageBitmap.imageResource(R.drawable.recipe),
                        contentDescription="",
                        modifier = Modifier
                            .size(35.dp)
                    ) },
                    label = { Text("Recipe") }, // Text under the icon
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White, // Icon color when selected
                        selectedTextColor = Color.White, // Text color when selected
                        unselectedIconColor = Color.Gray, // Icon color when unselected
                        unselectedTextColor = Color.Gray // Text color when unselected
                    ),
                    onClick = {
                        navHostController.navigate(Recipe)
                    }
                )
                NavigationBarItem(
                    selected = false,
                    icon = { Icon(imageVector = Icons.Outlined.DateRange,
                        contentDescription="",
                        modifier = Modifier
                            .size(32.dp)
                    ) },
                    label = { Text("Calendar") }, // Text under the icon
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White, // Icon color when selected
                        selectedTextColor = Color.White, // Text color when selected
                        unselectedIconColor = Color.Gray, // Icon color when unselected
                        unselectedTextColor = Color.Gray // Text color when unselected
                    ),
                    onClick = {
                        navHostController.navigate(Calendar)
                    }
                )
                NavigationBarItem(
                    selected = false,
                    icon = { Icon(imageVector = Icons.Outlined.Person,
                        contentDescription="",
                        modifier = Modifier
                            .size(35.dp)
                        ) },
                    label = { Text("Profile") }, // Text under the icon
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White, // Icon color when selected
                        selectedTextColor = Color.White, // Text color when selected
                        unselectedIconColor = Color.Gray, // Icon color when unselected
                        unselectedTextColor = Color.Gray // Text color when unselected
                    ),
                    onClick = {
                        navHostController.navigate(Profile)
                    }
                )
            }
        }

    ) {
            innerPadding ->
        NavHost(
            navController = navHostController,
            startDestination = Dashboard,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<Dashboard> {
                FastingAppUI(navHostController)
            }
            composable<Fasting> {
                FastingPage()
            }
            composable<Recipe> {
                RecipePage()
            }
            composable<Calendar> {
                CalendarPage()
            }
            composable<Profile> {
                ProfilePage()
            }

        }
    }
}

// Call the UI function in your Activity
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            val navController = rememberNavController()
            bottomNavBar(navController)
        }
    }
}
