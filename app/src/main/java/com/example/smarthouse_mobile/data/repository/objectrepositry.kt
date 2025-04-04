package com.example.smarthouse_mobile.data.repository

import android.util.Log
import com.example.smarthouse_mobile.data.model.*
import com.example.smarthouse_mobile.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/*
object RemoteRepository {
    private val api = RetrofitClient.instance


    suspend fun authenticateUser(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(AuthRequest(email, password))
                if (response.isSuccessful) {
                    Log.d("AUTH", "Login successful: ${response.body()}")
                    true
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AUTH", "Login failed: $errorBody")
                    false
                }
            } catch (e: Exception) {
                Log.e("AUTH", "Exception during login: ${e.message}")
                false
            }
        }
    }

//    suspend fun authenticateUser(email: String, password: String): Boolean {
//        return withContext(Dispatchers.IO) {
//            try {
//                val response = api.login(AuthRequest(email, password))
//                response.isSuccessful
//            } catch (e: Exception) {
//                false
//            }
//        }
//    }

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

*/


object RemoteRepository {
    private val api = RetrofitClient.instance
    var sessionToken: String = ""


    suspend fun authenticateUser(email: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.login(AuthRequest(email, password))
                if (response.isSuccessful) {
                    sessionToken = "token=fake-token-for-testing"
                    Log.d("AUTH", "Login successful, fake token set: $sessionToken")
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e("AUTH", "Exception during login: ${e.message}")
                false
            }
        }
    }

    suspend fun getHomeById(homeId: String): HomeModel? {
        return withContext(Dispatchers.IO) {
            val response = api.getHome(homeId)
            if (response.isSuccessful) response.body() else null
        }
    }

    suspend fun getAllHomes(sessionToken1: String): List<HomeModel> {
        return withContext(Dispatchers.IO) {
            val response = api.getHomes(sessionToken)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else emptyList()
        }
    }

    suspend fun getRoomsForHome(homeId: String): List<Rooms> {
        return withContext(Dispatchers.IO) {
            val response = api.getRooms(homeId, "token=$sessionToken")
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else emptyList()
        }
    }

    suspend fun getDevicesForRoom(roomId: String): List<Devices> {
        return withContext(Dispatchers.IO) {
            val response = api.getDevices(roomId, "token=$sessionToken")
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else emptyList()
        }
    }

    suspend fun updateDeviceStatus(deviceId: String, status: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            val response = api.postDeviceStatus(
                deviceId,
                "token=$sessionToken",
                StatusUpdate(status)
            )
            response.isSuccessful
        }
    }

    suspend fun moveDeviceToAnotherRoom(
        roomId: String,
        deviceId: String,
        newRoomId: String
    ): Boolean {
        return withContext(Dispatchers.IO) {
            val response = api.moveDeviceToAnotherRoom(
                roomId,
                deviceId,
                "token=$sessionToken",
                NewRoomRequest(newRoomId)
            )
            response.isSuccessful
        }
    }

    suspend fun createRoom(homeId: String, roomName: String): Boolean {
        return withContext(Dispatchers.IO) {
            val response = api.createRoom(
                homeId,
                "token=$sessionToken",
                RoomNameRequest(roomName)
            )
            response.isSuccessful
        }
    }
}

