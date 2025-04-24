package com.example.smarthouse_mobile.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarthouse_mobile.data.model.HomeModel
import com.example.smarthouse_mobile.data.model.UserModel
import com.example.smarthouse_mobile.data.repository.RemoteRepository
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    user: UserModel,
    onHouseClicked: (HomeModel) -> Unit
) {
    var homes by remember { mutableStateOf<List<HomeModel>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        homes = RemoteRepository.getAllHomes()
        Log.d("HomeScreen", "Fetched homes: $homes")
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF222222))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Hi, ${user.name} 👋",
                fontSize = 28.sp,
                color = Color(0xFFFFC107),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (homes.isEmpty()) {
                Text("No homes found. Add your first home!", color = Color.Gray)
            } else {
                LazyColumn {
                    items(homes) { home ->
                        HomeCard(home = home, onClick = { onHouseClicked(home) })
                    }
                }
            }
        }

        // Floating button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = Color(0xFFFFC107),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
        }

        if (showAddDialog) {
            AddHomeDialog(
                onDismiss = { showAddDialog = false },
                onAddHome = { name ->
                    scope.launch {
                        val newHome = RemoteRepository.createHome(name)
                        if (newHome != null) {
                            homes = homes + newHome
                        }
                        showAddDialog = false
                    }
                }
            )
        }
    }
}

@Composable
fun HomeCard(home: HomeModel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(home.homeName, color = Color(0xFFFFC107), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = home.address ?: "No address",
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun AddHomeDialog(onDismiss: () -> Unit, onAddHome: (String) -> Unit) {
    var home_id by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Home") },
        text = {
            Column {
                OutlinedTextField(
                    value = home_id,
                    onValueChange = { home_id = it },
                    label = { Text("Home ID") },
                    modifier = Modifier.fillMaxWidth()
                )

            }
        },
        confirmButton = {
            TextButton(onClick = { onAddHome(home_id) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
