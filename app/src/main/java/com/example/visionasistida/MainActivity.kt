package com.example.visionasistida

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.visionasistida.ui.theme.VisionAsistidaTheme

import com.example.visionasistida.LoginScreen
import com.example.visionasistida.RegisterScreen
import com.example.visionasistida.ForgotPasswordScreen
import com.example.visionasistida.HomeScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VisionAsistidaTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VisionAsistidaApp(navController, Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun VisionAsistidaApp(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(navController = navController, startDestination = "login", modifier = modifier) {
        composable("login")          { LoginScreen(navController) }
        composable("register")       { RegisterScreen(navController) }
        composable("forgot_password"){ ForgotPasswordScreen(navController) }
        composable("home")           { HomeScreen(navController) }
        composable("features/location") { com.example.visionasistida.ui.location.LocationScreen() }
    }
}
