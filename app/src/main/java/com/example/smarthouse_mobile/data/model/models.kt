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
    @Json(name = "devices") val devices: List<Devices>
)

@JsonClass(generateAdapter = true)
data class Devices(
    @Json(name = "device_name") val name: String,
    @Json(name = "is_on") var isOn: Boolean
)

data class StatusUpdate(
    val status: Boolean
)

@JsonClass(generateAdapter = true)
data class NewRoomRequest(
    @Json(name = "newRoomId") val newRoomId: String
)

@JsonClass(generateAdapter = true)
data class RoomNameRequest(
    @Json(name = "newRoomName") val newRoomName: String
)
/*package com.example.smarthouse_mobile.data.model

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
    @Json(name = "address") val address: String,
    //@Json(name = "owner_id") val ownerId: String,
    //@Json(name = "emergency_mode") val emergencyMode: EmergencyMode,
    @Json(name = "rooms") val rooms: List<RoomModel> = emptyList(),
    //@Json(name = "devices") val devices: List<Devices>,
    @Json(name = "unassigned_devices") val unassignedDevices: List<Devices>,
    //@Json(name = "created_at") val createdAt: String
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
    @Json(name = "devices") val devices: List<Devices>
)

@JsonClass(generateAdapter = true)
data class Devices(
    @Json(name = "device_name") val name: String,
    @Json(name = "is_on") var isOn: Boolean
)
@JsonClass(generateAdapter = true)
data class RoomModel(
    @Json(name = "_id") val id: String,
    @Json(name = "room_name") val roomName: String,
    @Json(name = "floor_number") val floorNumber: Int
)
@JsonClass(generateAdapter = true)
data class DeviceModel(
    @Json(name = "_id") val id: String,
    @Json(name = "device_name") val deviceName: String,
    @Json(name = "status") val status: String
)
data class LoginRequest(
    val email: String,
    val password: String
)

data class TokenResponse(
    val token: String
)
@JsonClass(generateAdapter = true)
data class NewRoomRequest(
    @Json(name = "newRoomId") val newRoomId: String
)

@JsonClass(generateAdapter = true)
data class RoomNameRequest(
    @Json(name = "newRoomName") val newRoomName: String
)

data class StatusUpdate(
    val status: Boolean
)
*/