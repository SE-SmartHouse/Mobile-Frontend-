package com.example.smarthouse_mobile.ui.screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smarthouse_mobile.data.model.RoomModel
import com.example.smarthouse_mobile.data.repository.RemoteRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RoomsScreen(homeId: String, navController: NavController) {
    val scope = rememberCoroutineScope()
    var rooms by remember { mutableStateOf<List<RoomModel>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newRoomName by remember { mutableStateOf("") }
    var temperature by remember { mutableStateOf("Loading...") }
    var humidity by remember { mutableStateOf("Loading...") }

    fun fetchRooms() {
        scope.launch {
            loading = true
            error = null
            try {
                rooms = RemoteRepository.getRoomsForHome(homeId)
            } catch (e: Exception) {
                error = e.message ?: "Unknown error"
            } finally {
                loading = false
            }
        }
    }

    fun fetchSensorData() {
        scope.launch {
            try {
                val sensors = RemoteRepository.getSensorsForHome(homeId)
                val tempSensor = sensors.find { it.unit.equals("C", ignoreCase = true) }
                val humSensor =  sensors.find { it.unit == "%" }
                temperature = tempSensor?.reading ?: "N/A"
                humidity = humSensor?.reading ?: "N/A"
            } catch (e: Exception) {
                temperature = "Error"
                humidity = "Error"
            }
        }
    }

    fun addRoom() {
        scope.launch {
            if (newRoomName.isNotBlank()) {
                val success = RemoteRepository.createRoom(homeId, newRoomName.trim())
                if (success) {
                    fetchRooms()
                    showAddDialog = false
                    newRoomName = ""
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchRooms()
        fetchSensorData()
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Text(
                        text = "Rooms",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color(0xFFFFC107)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFFFFC107),
                contentColor = Color.Black
            ) {
                Text("+", fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFF0E0E0E)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {

            // DASHBOARD FOR TEMP + HUMIDITY
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("Home Environment", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("Temperature", color = Color.Gray, fontSize = 14.sp)
                            Text(temperature, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Column {
                            Text("Humidity", color = Color.Gray, fontSize = 14.sp)
                            Text(humidity, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // ROOMS GRID
            when {
                loading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
                error != null -> {
                    Text(error ?: "Error", color = Color.Red)
                }
                rooms.isEmpty() -> {
                    Text("No rooms found.", color = Color.Gray)
                }
                else -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(rooms) { room ->
                            RoomCardMinimal(room = room) {
                                navController.navigate("devices/${room.id}/$homeId")
                            }
                        }
                    }
                }
            }
        }

        // DIALOG FOR NEW ROOM
        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add New Room", color = Color.White) },
                text = {
                    OutlinedTextField(
                        value = newRoomName,
                        onValueChange = { newRoomName = it },
                        label = { Text("Room Name", color = Color.White) },
                        textStyle = LocalTextStyle.current.copy(color = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    TextButton(onClick = { addRoom() }) {
                        Text("Add", color = Color(0xFFFFC107))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel", color = Color.White)
                    }
                },
                containerColor = Color(0xFF333333)
            )
        }
    }
}

@Composable
fun RoomCardMinimal(room: RoomModel, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .padding(horizontal = 4.dp)
            .height(140.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2C2C2C)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = room.name.split(" ")
                    .joinToString(" ") { it.replaceFirstChar { c -> c.uppercaseChar() } },
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                text = "Floor ${room.floorNumber}",
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}
