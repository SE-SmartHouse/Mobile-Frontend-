package com.example.smarthouse_mobile.ui.screen


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.example.smarthouse_mobile.R // Replace with your app’s package name

@Composable
fun LandingScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animation_main))
        LottieAnimation(composition, modifier = Modifier.size(200.dp))

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Welcome to Your Smart Home", fontSize = 24.sp)

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { navController.navigate("signin") }) {
            Text("Get Started")
        }
    }
}
