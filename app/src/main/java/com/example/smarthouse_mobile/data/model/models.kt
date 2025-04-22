package com.example.smarthouse_mobile.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserModel(
    @Json(name = "userid") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "role") val role: String,
    @Json(name = "home_id") val homeId: String

)
@JsonClass(generateAdapter = true)
data class HomeModel(
    @Json(name = "_id") val id: String,
    @Json(name = "home_name") val homeName: String,
    @Json(name = "address") val address: String? = null
)

@JsonClass(generateAdapter = true)
data class AddHomeRequest(
    @Json(name = "home_id") val houseId: String,

)
@JsonClass(generateAdapter = true)
data class AddRoomRequest(
    @Json(name = "newRoomName") val newRoomName: String
)

@JsonClass(generateAdapter = true)
data class DeviceToggleRequest(
    @Json(name = "home_id") val houseId: String,
    @Json(name = "status") val deviceStatus: String

    )

@JsonClass(generateAdapter = true)
data class EmergencyMode(
    @Json(name = "status") val status: String,
    @Json(name = "last_activated") val lastActivated: String?,
    @Json(name = "action") val action: String?
)

@JsonClass(generateAdapter = true)
data class RoomModel(
    @Json(name = "_id") val id: String,
    @Json(name = "room_name") val name: String,
    @Json(name = "floor_number") val floorNumber: Int = 1
)

@JsonClass(generateAdapter = true)
data class DeviceModel(
    @Json(name = "_id") val id: String,
    @Json(name = "device_name") val deviceName: String,
    val status: String
)

data class StatusUpdate(
    val status: Boolean
)

@JsonClass(generateAdapter = true)
data class ToggleDeviceRequest(
    val status: String,
    @Json(name = "home_id") val homeId: String
)
