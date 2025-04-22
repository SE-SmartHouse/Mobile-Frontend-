package com.example.smarthouse_mobile.data.repository

import android.util.Log
import com.example.smarthouse_mobile.data.model.*
import com.example.smarthouse_mobile.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object RemoteRepository {
    private val api = RetrofitClient.instance
    var sessionToken: String = ""
    var userId: String = ""

    suspend fun authenticateUser(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(AuthRequest(email, password))
                if (response.isSuccessful) {
                    val rawCookie = response.headers()["Set-Cookie"]
                    val extractedToken =
                        rawCookie?.substringAfter("token=")?.substringBefore(";")?.trim()

                    return@withContext if (!extractedToken.isNullOrEmpty()) {
                        sessionToken = "token=$extractedToken"
                        Log.d("AUTH", "Login successful. Token extracted: $sessionToken")
                        true
                    } else {
                        Log.e("AUTH", "Login failed: Token missing in Set-Cookie header.")
                        false
                    }
                } else {
                    Log.e("AUTH", "Login failed: ${response.errorBody()?.string()}")
                    false
                }
            } catch (e: Exception) {
                Log.e("AUTH", "Exception during login: ${e.message}", e)
                false
            }
        }
    }

    suspend fun getAllHomes(): List<HomeModel> {
        return withContext(Dispatchers.IO) {
            val response = api.getHomes(sessionToken)
            if (response.isSuccessful) response.body() ?: emptyList()
            else {
                Log.e("HOMES", "Failed to fetch homes: ${response.code()} ${response.message()}")
                emptyList()
            }
        }
    }

    suspend fun createHome(homeId: String): HomeModel? {
        return withContext(Dispatchers.IO) {
            val response = api.addHome(sessionToken, AddHomeRequest(homeId))
            if (response.isSuccessful) response.body() else null
        }
    }

    suspend fun getRoomsForHome(homeId: String): List<RoomModel> {
        return withContext(Dispatchers.IO) {
            val response = api.getRooms(homeId, sessionToken)
            if (response.isSuccessful) response.body() ?: emptyList()
            else {
                Log.e("Rooms", "Error: ${response.code()}")
                emptyList()
            }
        }
    }
    suspend fun getDevicesForRoom(roomId: String): List<DeviceModel> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getDevices(roomId, sessionToken)
                if (response.isSuccessful) {
                    response.body() ?: emptyList()
                } else {
                    Log.e("Devices", "Error: ${response.code()} ${response.message()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("Devices", "Exception: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun createRoom(homeId: String, roomName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val request = AddRoomRequest(newRoomName = roomName)
                val response = api.addRoom(homeId, sessionToken, request)
                Log.d("AddRoom", "Status: ${response.code()}, Success: ${response.isSuccessful}")
                response.isSuccessful
            } catch (e: Exception) {
                Log.e("AddRoom", "Error: ${e.message}")
                false
            }
        }
    }

    suspend fun toggleDeviceStatus(deviceId: String, homeId: String, currentStatus: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                //val newStatus = if (currentStatus.equals("on", true)) "off" else "on"

                val request = DeviceToggleRequest(
                    houseId = homeId,
                    deviceStatus = currentStatus
                )
                Log.d("TOGGLE_DEVICE", " Sending toggle request:")
                Log.d("TOGGLE_DEVICE", " Device ID: $deviceId")
                Log.d("TOGGLE_DEVICE", " Home ID: $homeId")
                Log.d("TOGGLE_DEVICE", " New Status: $currentStatus")
                Log.d("TOGGLE_DEVICE", " Body: $request")

                val response = api.toggleDeviceStatus(deviceId, sessionToken, request)


                if (!response.isSuccessful) {
                    Log.e("TOGGLE_DEVICE", " Failed with code: ${response.code()}")
                } else {
                    Log.d("TOGGLE_DEVICE", " Toggle successful!")
                }

                response.isSuccessful
            } catch (e: Exception) {
                Log.e("TOGGLE_DEVICE", "🔥 Exception: ${e.message}")
                false
            }
        }
    }

}
