package com.example.fastyme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier,authViewModel: AuthViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "loginPage", builder={
//        composable<Dashboard> {
//            FastingAppUI(navHostController)
//        }
//        composable<Fasting> {
//            FastingPage()
//        }
//        composable<Recipe> {
//            RecipePage()
//        }
//        composable<Calendar> {
//            CalendarPage()
//        }
//        composable<Profile> {
//            ProfilePage()
//        }
        composable("loginPage") {
            LoginPage(modifier, navController,authViewModel)
        }
        composable("registerPage") {
            RegisterPage(modifier, navController,authViewModel)
        }
//        composable("homePage") {
//            FastingAppUI(navController)
//        }
    })
}