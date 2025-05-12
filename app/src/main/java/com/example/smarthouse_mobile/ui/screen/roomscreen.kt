package com.example.smarthouse_mobile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smarthouse_mobile.data.model.RoomModel
import com.example.smarthouse_mobile.data.model.DeviceModel
import com.example.smarthouse_mobile.data.repository.RemoteRepository
import kotlinx.coroutines.launch

@Composable
fun RoomsScreen(homeId: String, navController: NavController) {
    val scope = rememberCoroutineScope()
    var rooms by remember { mutableStateOf<List<RoomModel>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var newRoomName by remember { mutableStateOf("") }
    var roomTemperatures by remember { mutableStateOf<Map<String, String>>(emptyMap()) }

    fun fetchRoomsAndTemperatures() {
        scope.launch {
            loading = true
            error = null
            try {
                rooms = RemoteRepository.getRoomsForHome(homeId)
                android.util.Log.d("TEMP_DEBUG", "Fetched Rooms: ${rooms.map { it.name to it.id }}")

                val sensors = RemoteRepository.getSensorsForHome(homeId)
                android.util.Log.d("TEMP_DEBUG", "Fetched Sensors: ${sensors.map { it.deviceName to it.type to it.status }}")

                val temperatureSensors = sensors.filter { it.type.equals("temperature", true) }
                android.util.Log.d("TEMP_DEBUG", "Temperature Sensors: ${temperatureSensors.map { it.deviceName to it.status }}")

                val tempMap = mutableMapOf<String, String>()
                rooms.forEach { room ->
                    val sensor = temperatureSensors.find {
                        it.deviceName.contains(room.name, ignoreCase = true)
                    }
                    android.util.Log.d("TEMP_DEBUG", "Matching sensor for room '${room.name}': ${sensor?.deviceName} -> ${sensor?.status}")
                    if (sensor != null) {
                        tempMap[room.id] = sensor.status
                    }
                }

                roomTemperatures = tempMap
                android.util.Log.d("TEMP_DEBUG", "Final Room Temperatures Map: $roomTemperatures")

            } catch (e: Exception) {
                error = e.message ?: "Unknown error"
                android.util.Log.e("TEMP_DEBUG", "Exception while fetching temps: ${e.message}")
            } finally {
                loading = false
            }
        }
    }


    fun addRoom() {
        scope.launch {
            if (newRoomName.isNotBlank()) {
                val success = RemoteRepository.createRoom(homeId, newRoomName.trim())
                if (success) {
                    fetchRoomsAndTemperatures()
                    showAddDialog = false
                    newRoomName = ""
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchRoomsAndTemperatures()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        Column {
            Text("Rooms", color = Color(0xFFFFC107), style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(16.dp))

            when {
                loading -> CircularProgressIndicator(color = Color.White)
                error != null -> Text(error ?: "Error", color = Color.Red)
                rooms.isEmpty() -> Text("No rooms found.", color = Color.Gray)
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(rooms) { room ->
                        val temp = roomTemperatures[room.id]
                        RoomCard(
                            room = room,
                            temperature = temp,
                            onClick = { navController.navigate("devices/${room.id}/$homeId") }
                        )
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = Color(0xFFFFC107),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("+", color = Color.Black)
        }

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
fun RoomCard(room: RoomModel, temperature: String?, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(room.name, color = Color.White, style = MaterialTheme.typography.titleLarge)
                    Text("Floor ${room.floorNumber}", color = Color.Gray)
                }
                temperature?.let {
                    Text(text = "$it°", color = Color.White, fontSize = 18.sp)
                }
            }
        }
    }
}
