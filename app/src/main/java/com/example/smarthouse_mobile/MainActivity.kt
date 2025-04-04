package com.example.smarthouse_mobile

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.smarthouse_mobile.ui.screen.HomeScreen
import com.example.smarthouse_mobile.ui.screen.LandingScreen
import com.example.smarthouse_mobile.ui.screen.SignInScreen
import com.example.smarthouse_mobile.ui.theme.Smarthouse_mobileTheme
/*import com.example.smarthouse_mobile.data.model.User
import com.example.smarthouse_mobile.data.model.Home
import com.example.smarthouse_mobile.data.model.Room*/
import com.example.smarthouse_mobile.data.model.UserModel
import com.example.smarthouse_mobile.data.model.HomeModel
import com.example.smarthouse_mobile.data.repository.RemoteRepository
//import com.example.smarthouse_mobile.data.repository.MockRepository


import com.example.smarthouse_mobile.ui.screen.RoomsScreen
import com.example.smarthouse_mobile.ui.screen.authtoken

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
    var loggedInUser by remember { mutableStateOf<UserModel?>(null) }

    NavHost(
        navController = navController,
        startDestination = "landing"
    ) {
        composable("landing") {
            LandingScreen(navController)
        }
        composable("signin") {
            SignInScreen(navController) { sessionToken, user ->
                Log.d("SignIn", "Login successful, token: $sessionToken")
                loggedInUser = user
                navController.navigate("home") {
                    popUpTo("signin") { inclusive = true }
                }
            }
        }
        composable("home") {
            loggedInUser?.let { user ->
                HomeScreen(
                    user = user,
                    onHouseClicked = { home ->
                        navController.navigate("rooms/${user.homeId}")
                    }
                )
            } ?: navController.navigate("signin")
        }
        composable("rooms/{houseId}") { backStackEntry ->
            val houseId = backStackEntry.arguments?.getString("houseId")

            var house by remember {mutableStateOf<HomeModel?>(null) }

            LaunchedEffect(houseId) {
                houseId?.let {
                    house = RemoteRepository.getHomeById(it)
                }
            }

            house?.let {
                RoomsScreen(it, navController)
            } ?: navController.navigate("home") // Redirect to home if house not found
        }
    }
}