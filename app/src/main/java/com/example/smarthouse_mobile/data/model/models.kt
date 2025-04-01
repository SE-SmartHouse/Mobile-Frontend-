package com.example.smarthouse_mobile.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserModel(
    @Json(name = "_id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String,
    @Json(name = "role") val role: String,
    @Json(name = "home_id") val homeId: String
)

@JsonClass(generateAdapter = true)
data class HomeModel(
    @Json(name = "_id") val id: String?,
    @Json(name = "home_name") val homeName: String,
    @Json(name = "address") val address: String,
    @Json(name = "owner_id") val ownerId: String,
    @Json(name = "emergency_mode") val emergencyMode: EmergencyMode,
    @Json(name = "rooms") val rooms: List<Rooms>,
    @Json(name = "devices") val devices: List<Devices>,
    @Json(name = "unassigned_devices") val unassignedDevices: List<Devices>,
    @Json(name = "created_at") val createdAt: String
)

@JsonClass(generateAdapter = true)
data class EmergencyMode(
    @Json(name = "status") val status: String,
    @Json(name = "last_activated") val lastActivated: String?,
    @Json(name = "action") val action: String?
)

@JsonClass(generateAdapter = true)
data class Rooms(
    @Json(name = "room_id") val id: String,
    @Json(name = "room_name") val name: String,
    @Json(name = "devices") val devices: List<Device>
)

@JsonClass(generateAdapter = true)
data class Devices(
    @Json(name = "device_name") val name: String,
    @Json(name = "is_on") val isOn: Boolean
)

data class StatusUpdate(
    val status: Boolean
)
