package com.example.smarthouse_mobile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.smarthouse_mobile.data.model.DeviceModel
import com.example.smarthouse_mobile.data.model.RoomModel
import com.example.smarthouse_mobile.data.repository.RemoteRepository
import kotlinx.coroutines.launch

@Composable
fun RoomsScreen(homeId: String, navController: NavController) {
    val coroutineScope = rememberCoroutineScope()

    var rooms by remember { mutableStateOf<List<RoomModel>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    var newRoomName by remember { mutableStateOf("") }
    var newRoomFloor by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        fetchRooms(homeId) { r, e ->
            rooms = r
            error = e
            loading = false
        }
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
                        RoomCard(room) { deviceId ->
                            coroutineScope.launch {
                                RemoteRepository.toggleDeviceStatus(deviceId, room.id)
                                fetchRooms(homeId) { r, e ->
                                    rooms = r
                                    error = e
                                }
                            }
                        }
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
            AddRoomDialog(
                newRoomName,
                newRoomFloor,
                onNameChange = { newRoomName = it },
                onFloorChange = { newRoomFloor = it },
                onDismiss = { showAddDialog = false },
                onAddRoom = {
                    val floor = newRoomFloor.toIntOrNull() ?: 1
                    coroutineScope.launch {
                        val success = RemoteRepository.createRoom(homeId, newRoomName, floor)
                        if (success) {
                            fetchRooms(homeId) { r, e ->
                                rooms = r
                                error = e
                            }
                            showAddDialog = false
                            newRoomName = ""
                            newRoomFloor = ""
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun RoomCard(room: RoomModel, onToggleDevice: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(room.name, color = Color.White, style = MaterialTheme.typography.titleLarge)
            Text("Floor ${room.floorNumber}", color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            room.devices.forEach { device ->
                DeviceItem(device, onToggle = { onToggleDevice(device.id) })
            }
        }
    }
}

@Composable
fun DeviceItem(device: DeviceModel, onToggle: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Column(Modifier.weight(1f)) {
            Text(device.deviceName, color = Color(0xFFFFC107), style = MaterialTheme.typography.bodyLarge)
            Text("Status: ${device.status}", color = Color.Gray)
        }
        IconButton(onClick = onToggle) {
            Icon(
                imageVector = Icons.Default.PowerSettingsNew,
                contentDescription = "Toggle",
                tint = if (device.status == "On") Color.Red else Color.Green
            )
        }
    }
}

@Composable
fun AddRoomDialog(
    roomName: String,
    floor: String,
    onNameChange: (String) -> Unit,
    onFloorChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onAddRoom: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Room", color = Color.White) },
        text = {
            Column {
                OutlinedTextField(
                    value = roomName,
                    onValueChange = onNameChange,
                    label = { Text("Room Name") }
                )
                OutlinedTextField(
                    value = floor,
                    onValueChange = onFloorChange,
                    label = { Text("Floor Number") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onAddRoom) {
                Text("Add", color = Color(0xFFFFC107))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        containerColor = Color(0xFF333333)
    )
}

private suspend fun fetchRooms(
    homeId: String,
    onResult: (List<RoomModel>, String?) -> Unit
) {
    try {
        val rooms = RemoteRepository.getRoomsForHome(homeId)
        onResult(rooms, null)
    } catch (e: Exception) {
        onResult(emptyList(), "Failed to fetch rooms: ${e.message}")
    }
}
