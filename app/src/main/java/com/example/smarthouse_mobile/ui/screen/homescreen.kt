package com.example.smarthouse_mobile.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smarthouse_mobile.R
import com.example.smarthouse_mobile.data.model.HomeModel
import com.example.smarthouse_mobile.data.model.UserModel
import com.example.smarthouse_mobile.data.repository.RemoteRepository
import com.example.smarthouse_mobile.data.repository.RemoteRepository.sessionToken
import kotlinx.coroutines.launch


@Composable
fun HomeScreen(
    user: UserModel,
    onHouseClicked: (HomeModel) -> Unit
) {
    var houses by remember { mutableStateOf<List<HomeModel>>(emptyList()) }
    var showAddDialog by remember { mutableStateOf(false) }
    var houseToDelete by remember { mutableStateOf<HomeModel?>(null) }
    val coroutineScope = rememberCoroutineScope()


    LaunchedEffect(sessionToken) {
        coroutineScope.launch {
            houses = RemoteRepository.getAllHomes(sessionToken)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF222222)) // Dark background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Text(
                text = "Hi, ${user.name} 👋",
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp),
                color = Color(0xFFFFC107) // Yellow for greeting text
            )

            Text(
                text = "Your Homes",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, color = Color(0xFF757575)),
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            if (houses.isEmpty()) {
                Text("No homes found. Add your first home!", fontSize = 16.sp, color = Color.Gray)
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(houses.size) { index ->
                        HouseCard(
                            house = houses[index],
                            onHouseClicked = onHouseClicked,
                            onDeleteHouse = { houseToDelete = houses[index] }
                        )
                    }
                }
            }
        }

        // Floating Add House Button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            shape = CircleShape,
            containerColor = Color(0xFFFFC107), // Yellow button color
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.Black)
        }
    }

    /*
        // Add House Dialog
        if (showAddDialog) {
            AddHouseDialog(
                onDismiss = { showAddDialog = false },
                onAddHouse = { houseName ->
                    houses = houses + HomeModel(homeName = houseName, id = "house " + (user.homeId.length +1), rooms = emptyList(), address = , unassignedDevices = emptyList())
                    showAddDialog = false
                }
            )
        }
    */

    // Delete Confirmation Dialog
    houseToDelete?.let { house ->
        DeleteHouseDialog(
            house = house,
            onConfirmDelete = {
                houses = houses - house
                houseToDelete = null
            },
            onDismiss = { houseToDelete = null }
        )
    }
}

@Composable
fun HouseCard(house: HomeModel, onHouseClicked: (HomeModel) -> Unit, onDeleteHouse: () -> Unit) {
    Card(
        onClick = { onHouseClicked(house) },
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333)) // Dark card color
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF444444), Color(0xFF222222)) // Dark gradient background
                    )
                )
                .padding(12.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_home),
                        contentDescription = "House Icon",
                        modifier = Modifier.size(36.dp)
                    )

                    IconButton(onClick = onDeleteHouse) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                    }
                }

                Column {
                    Text(
                        text = house.homeName,
                        fontSize = 18.sp,
                        color = Color(0xFFFFC107), // Yellow text for house name
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text("${house.rooms.size} rooms", fontSize = 14.sp, color = Color.Gray)
                    Text("${house.unassignedDevices.size} devices", fontSize = 14.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(6.dp))
                    val statusColor = if (house.rooms.isEmpty()) Color.Red else Color(0xFF4CAF50)
                    Text(
                        text = if (house.rooms.isEmpty()) "No rooms added" else "Active",
                        fontSize = 12.sp,
                        color = statusColor
                    )
                }
            }
        }
    }
}

@Composable
fun AddHouseDialog(onDismiss: () -> Unit, onAddHouse: (String) -> Unit) {
    var houseName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = { if (houseName.isNotBlank()) onAddHouse(houseName) }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Add New House") },
        text = {
            OutlinedTextField(
                value = houseName,
                onValueChange = { houseName = it },
                label = { Text("House Name") }
            )
        }
    )
}

@Composable
fun DeleteHouseDialog(house: HomeModel, onConfirmDelete: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onConfirmDelete, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                Text("Delete", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text("Delete House?") },
        text = { Text("Are you sure you want to delete ${house.homeName}? This action cannot be undone.") }
    )
}
