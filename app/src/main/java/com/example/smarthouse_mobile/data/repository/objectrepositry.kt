package com.example.smarthouse_mobile.data.repository

import com.example.smarthouse_mobile.data.model.*
import com.example.smarthouse_mobile.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RemoteRepository {
    private val api = RetrofitClient.instance


    suspend fun authenticateUser(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(AuthRequest(email, password))
                response.isSuccessful
            } catch (e: Exception) {
                false
            }
        }
    }

    suspend fun getAllHomes(): List<HomeModel> {
        return withContext(Dispatchers.IO) {
            val response = api.getHomes()
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

        // Get rooms for a specific home
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

        // Get devices for a specific room
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

        // Update device status
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

