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
import com.example.smarthouse_mobile.data.repository.RemoteRepository
import kotlinx.coroutines.launch

@Composable
fun DeviceScreen(roomId: String, roomName: String) {
    val scope = rememberCoroutineScope()
    var devices by remember { mutableStateOf<List<DeviceModel>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    var toggling by remember { mutableStateOf<String?>(null) }

    fun fetchDevices() {
        scope.launch {
            loading = true
            error = null
            try {
                devices = RemoteRepository.getDevicesForRoom(roomId)
            } catch (e: Exception) {
                error = e.message ?: "Error loading devices"
            } finally {
                loading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchDevices()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        Column {
            Text("Devices in $roomName", color = Color(0xFFFFC107), style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))

            when {
                loading -> CircularProgressIndicator(color = Color.White)
                error != null -> Text(error!!, color = Color.Red)
                devices.isEmpty() -> Text("No devices found.", color = Color.Gray)
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(devices) { device ->
                        DeviceCard(device, isToggling = toggling == device.id) {
                            toggling = device.id
                            scope.launch {
                                RemoteRepository.toggleDeviceStatus(device.id, roomId)
                                fetchDevices()
                                toggling = null
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceCard(device: DeviceModel, isToggling: Boolean, onToggle: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(device.deviceName, color = Color(0xFFFFC107), style = MaterialTheme.typography.titleMedium)
                Text("Status: ${device.status}", color = Color.Gray)
            }
            IconButton(
                onClick = onToggle,
                enabled = !isToggling
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = "Toggle",
                    tint = if (device.status.lowercase() == "on") Color.Red else Color.Green
                )
            }
        }
    }
}
