package com.example.smarthouse_mobile.data.model

data class Home(
    val houseId: String,
    val name: String,
    val rooms: List<Room>,
    val unassignedDevices: List<Device>
)