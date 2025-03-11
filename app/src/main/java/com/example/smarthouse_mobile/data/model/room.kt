package com.example.smarthouse_mobile.data.model

data class Room(
    val roomId: String,
    val name: String,
    val devices: List<Device>
)


data class Device(
    val name: String,
    var type: Boolean
)
