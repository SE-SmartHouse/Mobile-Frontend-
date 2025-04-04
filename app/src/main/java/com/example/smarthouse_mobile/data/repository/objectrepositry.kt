package com.example.smarthouse_mobile.data.repository

import com.example.smarthouse_mobile.data.model.*
import com.example.smarthouse_mobile.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Log

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

    // ✅ These functions should be defined OUTSIDE authenticateUser()
    suspend fun getAllHomes(sessionToken1: String): List<HomeModel> {
        return withContext(Dispatchers.IO) {
            val response = api.getHomes(sessionToken)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        }
    }

    suspend fun getHomeById(homeId: String): HomeModel? {
        return withContext(Dispatchers.IO) {
            val response = api.getHome(homeId)
            if (response.isSuccessful) response.body() else null
        }
    }

    suspend fun getRoomsForHome(homeId: String): List<Rooms> {
        return withContext(Dispatchers.IO) {
            val response = api.getRooms(homeId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        }
    }

    suspend fun getDevicesForRoom(roomId: String): List<Devices> {
        return withContext(Dispatchers.IO) {
            val response = api.getDevices(roomId)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        }
    }

    suspend fun updateDeviceStatus(deviceId: String, status: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            val response = api.postDeviceStatus(deviceId, StatusUpdate(status))
            response.isSuccessful
        }
    }

    suspend fun moveDeviceToAnotherRoom(
        roomId: String,
        deviceId: String,
        newRoomId: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val response = api.moveDeviceToAnotherRoom(roomId, deviceId, newRoomId)
            response.isSuccessful
        }
    }

    suspend fun createRoom(homeId: String, roomName: String): Boolean {
        return withContext(Dispatchers.IO) {
            val response = api.createRoom(homeId, roomName)
            response.isSuccessful
        }
    }
}
