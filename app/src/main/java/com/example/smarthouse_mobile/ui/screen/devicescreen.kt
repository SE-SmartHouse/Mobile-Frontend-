package com.example.smarthouse_mobile.ui.screen

import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smarthouse_mobile.data.model.DeviceModel
import com.example.smarthouse_mobile.data.repository.RemoteRepository
import kotlinx.coroutines.launch
import java.util.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.text.font.FontWeight


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DeviceScreen(roomId: String, homeId: String, navController: NavController) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
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

    fun toggleDeviceStatus(device: DeviceModel, desiredStatus: String? = null) {
        scope.launch {
            val newStatus = desiredStatus ?: when (device.type?.lowercase()) {
                "door", "window" -> if (device.status.equals("Open", true)) "Closed" else "Open"
                else -> if (device.status.equals("Off", true)) "On" else "Off"
            }

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

    val voiceLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val spokenText = result.data
            ?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            ?.get(0)

        spokenText?.let { command ->
            val matchedDevice = devices.find { device ->
                command.contains(device.deviceName, ignoreCase = true)
            }

            if (matchedDevice != null) {
                val turnOn = command.contains("turn on", ignoreCase = true)
                val turnOff = command.contains("turn off", ignoreCase = true)

                if (turnOn || turnOff) {
                    val newStatus = when (matchedDevice.type?.lowercase()) {
                        "door", "window" -> if (turnOn) "Open" else "Closed"
                        else -> if (turnOn) "On" else "Off"
                    }
                    toggleDeviceStatus(matchedDevice, newStatus)
                } else {
                    error = "Voice command must include 'turn on' or 'turn off'"
                }
            } else {
                error = "No matching device found for command: \"$command\""
            }
        }
    }

    LaunchedEffect(Unit) {
        fetchDevices()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Devices in Room", color = Color.Black) },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFFFFC107))
            )
        },
        containerColor = Color(0xFF121212)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Button(
                onClick = {
                    val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                        putExtra(
                            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                        )
                        putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
                        putExtra(RecognizerIntent.EXTRA_PROMPT, "Say a command like 'Turn on light'")
                    }
                    voiceLauncher.launch(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
            ) {
                Text(" Voice Command", color = Color.Black)
            }

            when {
                loading -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
                }
                error != null -> Text(error ?: "Error", color = Color.Red)
                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(devices) { device ->
                        val canToggle = isToggleSupported(device)
                        val buttonLabel = getActionLabel(device)

                        ElevatedCard(
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1A1A1A))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = device.deviceName,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                    Icon(
                                        imageVector = Icons.Default.PowerSettingsNew,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                }
                                if (canToggle) {
                                    Button(
                                        onClick = { toggleDeviceStatus(device) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (
                                                device.status.equals("Off", true)
                                                || device.status.equals("Closed", true)
                                            ) Color(0xFFD32F2F)
                                            else Color(0xFF388E3C),
                                            contentColor = Color.White
                                        ),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(text = buttonLabel)
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

fun isToggleSupported(device: DeviceModel): Boolean {
    return when (device.type?.lowercase()) {
        "light", "fan", "door", "window" -> true
        else -> false
    }
}

fun getActionLabel(device: DeviceModel): String {
    return when (device.type?.lowercase()) {
        "door", "window" -> if (device.status.equals("Open", true)) "Close" else "Open"
        else -> if (device.status.equals("On", true)) "Turn Off" else "Turn On"
    }
}
