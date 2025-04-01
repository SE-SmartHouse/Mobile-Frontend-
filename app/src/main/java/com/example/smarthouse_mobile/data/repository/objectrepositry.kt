package com.example.smarthouse_mobile.data.repository

import com.example.smarthouse_mobile.data.model.*
import com.example.smarthouse_mobile.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object RemoteRepository {
    private val api = RetrofitClient.instance

    suspend fun authenticateUser(email: String, password: String): UserModel? {
        return withContext(Dispatchers.IO) {
            val response = api.getUsers()
            if (response.isSuccessful) {
                response.body()?.find { it.email == email }
            } else {
                null
            }
        }
    }

    suspend fun getAllHomes(token: String): List<HomeModel> {
        return withContext(Dispatchers.IO) {
            val response = api.getHomes(token)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        }

        suspend fun getHomeById(homeId: String, token: String): HomeModel? {
            return withContext(Dispatchers.IO) {
                val response = api.getHome(homeId, token)
                if (response.isSuccessful) response.body() else null
            }
        }

    }

    suspend fun getRoomsForHome(homeId: String, token: String): List<Rooms> {
        return withContext(Dispatchers.IO) {
            val response = api.getRooms(homeId, token)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        }
    }


    suspend fun getDevicesForRoom(homeId: String, roomId: String, token: String): List<Devices> {
        return withContext(Dispatchers.IO) {
            val response = api.getDevices(homeId, roomId, token)
            if (response.isSuccessful) {
                response.body() ?: emptyList()
            } else {
                emptyList()
            }
        }
    }


    suspend fun updateDeviceStatus(homeId: String, roomId: String, deviceId: String, status: Boolean, token: String): Boolean {
        return withContext(Dispatchers.IO) {
            val response = api.postDeviceStatus(homeId, roomId, deviceId, StatusUpdate(status), token)
            response.isSuccessful
        }
    }
}
