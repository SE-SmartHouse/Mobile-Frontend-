package com.example.smarthouse_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*
import com.airbnb.lottie.compose.*
import androidx.compose.ui.tooling.preview.Preview
import com.example.smarthouse_mobile.ui.screen.LandingScreen
import com.example.smarthouse_mobile.ui.screen.SignInScreen
import com.example.smarthouse_mobile.ui.theme.Smarthouse_mobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Smarthouse_mobileTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                )

                {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "landing"
    ) {
        composable("landing") {
            LandingScreen(navController)
        }
        composable("signin") {
            SignInScreen(navController)
        }
    }
}