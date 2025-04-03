package com.example.smarthouse_mobile.data.repository

import com.example.smarthouse_mobile.data.model.HomeModel
import com.example.smarthouse_mobile.data.model.Rooms
import com.example.smarthouse_mobile.data.model.Devices
import com.example.smarthouse_mobile.data.model.StatusUpdate
import com.example.smarthouse_mobile.data.model.UserModel
import com.example.smarthouse_mobile.data.remote.RetrofitClient
import com.squareup.moshi.Json
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path


data class AuthRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)
data class LoginResponse(
    @Json(name = "token") val token: String? = null
)

interface ApiService {


    @POST("auth/register")
    suspend fun register(@Body authRequest: AuthRequest): Response<Unit>


    @POST("auth/login")
    suspend fun login(@Body authRequest: AuthRequest): Response<LoginResponse>


    @GET("users/{userId}/homes")
    suspend fun getHomes(): Response<List<HomeModel>>

    @GET("homes/{homeId}")
    suspend fun getHome(@Path("homeId") homeId: String, ): Response<HomeModel>


    @GET("homes/{homeId}/rooms")
    suspend fun getRooms(@Path("homeId") homeId: String,): Response<List<Rooms>>


    @GET("rooms/{roomId}/devices")
    suspend fun getDevices(
        @Path("roomId") roomId: String,
    ): Response<List<Devices>>


    @POST("devices/{deviceId}/status")
    suspend fun postDeviceStatus(
        @Path("deviceId") deviceId: String,
        @Body statusUpdate: StatusUpdate,
    ): Response<Unit>


    @POST("rooms/{roomId}/devices/{deviceId}/move")
    suspend fun moveDeviceToAnotherRoom(
        @Path("roomId") roomId: String,
        @Path("deviceId") deviceId: String,
        @Body newRoomId: String,
    ): Response<Unit>


    @POST("homes/{homeId}/rooms/add")
    suspend fun createRoom(
        @Path("homeId") homeId: String,
        @Body roomName: String,
    ): Response<Unit>
}
