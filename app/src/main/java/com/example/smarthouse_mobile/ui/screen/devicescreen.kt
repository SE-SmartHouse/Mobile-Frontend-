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
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smarthouse_mobile.data.model.DeviceModel
import com.example.smarthouse_mobile.data.repository.RemoteRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceScreen(roomId: String, homeId: String, navController: NavController) {
    val scope = rememberCoroutineScope()
    var devices by remember { mutableStateOf<List<DeviceModel>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    fun fetchDevices() {
        scope.launch {
            loading = true
            try {
                devices = RemoteRepository.getDevicesForRoom(roomId)
            } catch (e: Exception) {
                error = e.message
            } finally {
                loading = false
            }
        }
    }

    fun toggleDeviceStatus(device: DeviceModel) {
        scope.launch {
            val newStatus = if (device.status.equals("off", ignoreCase = true)) "on" else "off"
            val success = RemoteRepository.toggleDeviceStatus(
                deviceId = device.id,
                homeId = homeId,
                currentStatus = newStatus
            )
            if (success) {
                fetchDevices()
            } else {
                error = "Failed to toggle ${device.deviceName}"
            }
        }
    }
    LaunchedEffect(Unit) {
        fetchDevices()
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Devices in Room", color = Color.White) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF333333))
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp)) {

            when {
                loading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                error != null -> Text(error ?: "Error", color = Color.Red)
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(devices) { device ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(device.deviceName, fontSize = 20.sp, color = Color(0xFFFFC107))
                                    Text("Status: ${device.status}", color = Color.Gray)
                                }
                                IconButton(onClick = { toggleDeviceStatus(device) }) {
                                    Icon(
                                        imageVector = Icons.Default.PowerSettingsNew,
                                        contentDescription = "Toggle",
                                        tint = if (device.status == "On") Color.Red else Color.Green
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
