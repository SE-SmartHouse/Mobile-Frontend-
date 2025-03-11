package com.example.smarthouse_mobile.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smarthouse_mobile.data.model.Home
import com.example.smarthouse_mobile.data.model.Room
import com.example.smarthouse_mobile.data.model.Device

@Composable
fun RoomsScreen(house: Home, navController: NavController) {
    var selectedRoom by remember { mutableStateOf<Room?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF222222)) // Dark background
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = house.name,
                style = MaterialTheme.typography.headlineLarge.copy(fontSize = 28.sp),
                color = Color(0xFFFFC107) // Yellow text
            )

            Text(
                text = "Rooms",
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 18.sp, color = Color.Gray),
                modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
            )

            LazyColumn {
                items(house.rooms.size) { index ->
                    RoomCard(room = house.rooms[index], onClick = {  selectedRoom = house.rooms[index] })
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
            DeviceControlDialog(room = room, onDismiss = { selectedRoom = null })
        }
    }
}

@Composable
fun RoomCard(room: Room, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF333333)) // Dark card
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = room.name,
                fontSize = 18.sp,
                color = Color(0xFFFFC107), // Yellow title
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text("${room.devices.size} devices", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun DeviceControlDialog(room: Room, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFC107))
            ) {
                Text("Close", color = Color.Black)
            }
        },
        title = {
            Text(text = room.name, color = Color(0xFFFFC107), fontSize = 22.sp)
        },
        text = {
            Column {
                if (room.devices.isEmpty()) {
                    Text("No devices found.", color = Color.Gray)
                } else {
                    room.devices.forEach { device ->
                        DeviceToggle(device = device)
                    }
                }
            }
        },
        containerColor = Color(0xFF333333), // Dark background
    )
}

@Composable
fun DeviceToggle(device: Device) {
    var isOn by remember { mutableStateOf(device.type) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = device.name,
            color = Color.White,
            fontSize = 16.sp
        )

        Switch(
            checked = isOn,
            onCheckedChange = {
                isOn = it
                device.type = it // Update device state
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFFFFC107), // Yellow toggle
                uncheckedThumbColor = Color.Gray
            )
        )
    }
}
