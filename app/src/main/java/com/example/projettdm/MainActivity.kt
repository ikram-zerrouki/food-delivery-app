package com.example.projettdm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.projettdm.ui.theme.ProjetTDMTheme


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ProjetTDMTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                    }
                }
            }
        }
}

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val context = LocalContext.current
    NavHost(navController = navController, startDestination = "acceuil1") {
        composable("acceuil1") { FoodifyGoApp(navController) }
        composable("acceuil2") { Acceuil2(navController,context) }
        composable("pages") { PageScreen(navController) }
        composable("login") { LoginScreen(navController) }
        composable("create_account") { CreateAccountScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }
        composable("home"){ HomeScreen(navController,"") }
        composable("orderHistory/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") // Retrieve the userId from the route
            if (userId != null) {
                OrderHistoryScreen(navController, userId) // Pass the userId to the OrderHistoryScreen
            }
        }
        composable("homeScreen/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            HomeScreen(navController, userId)
        }
    }

}
