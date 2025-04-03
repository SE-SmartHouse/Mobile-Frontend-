package com.example.smarthouse_mobile.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smarthouse_mobile.data.model.Devices
/*import com.example.smarthouse_mobile.data.model.Home
import com.example.smarthouse_mobile.data.model.Room
import com.example.smarthouse_mobile.data.model.Device*/
import com.example.smarthouse_mobile.data.model.HomeModel
import com.example.smarthouse_mobile.data.model.Rooms

@Composable
fun RoomsScreen(house: HomeModel, navController: NavController) {
    var selectedRoom by remember { mutableStateOf<Rooms?>(null) }
    var showAddDeviceDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF222222))
            .padding(16.dp)
    ) {
        Text(
            text = house.homeName,
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp),
            color = Color(0xFFFFC107)
        )

        Text(
            text = "Rooms",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, color = Color.Gray),
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        LazyColumn {
            items(house.rooms.size) { index ->
                RoomCard(room = house.rooms[index], onClick = { selectedRoom = house.rooms[index] })
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
        ) {
            Text("Back to Home", color = Color.Black)
        }
    }

    selectedRoom?.let { room ->
        DeviceGridScreen(
            room = room,
            onDismiss = { selectedRoom = null },
            onAddDeviceClick = { showAddDeviceDialog = true }
        )
    }

    if (showAddDeviceDialog) {
        AddDeviceDialog(
            onDismiss = { showAddDeviceDialog = false },
            onAddDevice = { /* TODO: Store device later */ }
        )
    }
}

@Composable
fun RoomCard(room: Rooms, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = room.name,
                fontSize = 18.sp,
                color = Color(0xFFFFC107),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text("${room.devices.size} devices", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun DeviceGridScreen(room: Rooms, onDismiss: () -> Unit, onAddDeviceClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF333333))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "${room.name} - Devices",
                color = Color(0xFFFFC107),
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(room.devices.size) { index ->
                    DeviceCard(device = room.devices[index])
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            FloatingActionButton(
                onClick = { onAddDeviceClick() },
                modifier = Modifier.align(Alignment.CenterHorizontally),
                containerColor = Color(0xFFFFC107)
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Device", tint = Color.Black)
            }
        }

        IconButton(
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .size(36.dp),
        ) {
            Icon(imageVector = Icons.Default.PowerSettingsNew, contentDescription = "Close", tint = Color.White)
        }
    }
}

@Composable
fun DeviceCard(device: Devices) {
    var isOn by remember { mutableStateOf(device.isOn) }

    val scale = animateFloatAsState(
        targetValue = if (isOn) 1.1f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    Card(
        modifier = Modifier
            .padding(8.dp)
            .scale(scale.value)
            .background(
                if (isOn) Color(0xFF4CAF50) else Color.Gray,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {
                isOn = !isOn
                device.isOn = isOn
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = if (isOn) Color(0xFF4CAF50) else Color.DarkGray)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = device.name,
                color = Color.White,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            IconButton(
                onClick = {
                    isOn = !isOn
                    device.isOn = isOn
                },
                modifier = Modifier
                    .size(48.dp)
                    .scale(scale.value)
                    .background(
                        if (isOn) Color(0xFFFFC107) else Color.Gray,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = "Toggle Device",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun AddDeviceDialog(onDismiss: () -> Unit, onAddDevice: (String) -> Unit) {
    var deviceName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Device", color = Color(0xFFFFC107)) },
        text = {
            Column {
                OutlinedTextField(
                    value = deviceName,
                    onValueChange = { deviceName = it },
                    label = { Text("Device Name") }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onAddDevice(deviceName); onDismiss() }) {
                Text("Add", color = Color.Black)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel", color = Color.Black)
            }
        }
    )
}
