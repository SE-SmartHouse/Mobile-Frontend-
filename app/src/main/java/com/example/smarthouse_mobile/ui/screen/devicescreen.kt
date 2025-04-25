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
import androidx.compose.ui.graphics.Brush
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
            if (success) fetchDevices() else error = "Failed to toggle ${device.deviceName}"
        }
    }

    LaunchedEffect(Unit) { fetchDevices() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Smart Devices", color = Color.White, fontSize = 22.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF1E1E1E))
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            when {
                loading -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFFFFC107)
                )
                error != null -> Text(
                    error ?: "Error",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(devices) { device ->
                        val isOn = device.status.equals("on", ignoreCase = true)
                        val statusColor = if (isOn) Color(0xFF00E676) else Color(0xFFE53935)
                        val gradient = Brush.horizontalGradient(
                            colors = if (isOn) listOf(Color(0xFF2E7D32), Color(0xFF66BB6A))
                            else listOf(Color(0xFFB71C1C), Color(0xFFEF5350))
                        )

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 100.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                            shape = MaterialTheme.shapes.large,
                            elevation = CardDefaults.cardElevation(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(gradient)
                                    .padding(16.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = device.deviceName,
                                            fontSize = 20.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Status: ${device.status}",
                                            color = Color.White.copy(alpha = 0.8f)
                                        )
                                    }

                                    Button(
                                        onClick = { toggleDeviceStatus(device) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White,
                                            contentColor = statusColor
                                        ),
                                        shape = MaterialTheme.shapes.medium
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.PowerSettingsNew,
                                            contentDescription = "Toggle Power",
                                            tint = statusColor
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(text = if (isOn) "Turn Off" else "Turn On")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
