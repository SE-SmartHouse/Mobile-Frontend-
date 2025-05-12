package com.example.smarthouse_mobile.data.repository

import com.example.smarthouse_mobile.data.model.AddHomeRequest
import com.example.smarthouse_mobile.data.model.AddRoomRequest
import com.example.smarthouse_mobile.data.model.DeviceModel
import com.example.smarthouse_mobile.data.model.HomeModel
import com.example.smarthouse_mobile.data.model.RoomModel
import com.example.smarthouse_mobile.data.model.DeviceToggleRequest
import com.example.smarthouse_mobile.data.model.StatusUpdate
import com.example.smarthouse_mobile.data.model.ToggleDeviceRequest
import com.example.smarthouse_mobile.data.model.UserModel
import com.example.smarthouse_mobile.data.remote.RetrofitClient
import com.squareup.moshi.Json
import okhttp3.RequestBody
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

data class registerauth(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String,
    @Json(name = "name") val name: String

)
data class LoginResponse(
    @Json(name = "token") val token: String? = null,
    @Json(name = "userId") val userId: String? = null,
    @Json(name = "message") val message: String? = null
)
interface ApiService {


    @POST("auth/register")
    suspend fun register(@Body authRequest: registerauth): Response<LoginResponse>


    @POST("auth/login")
    suspend fun login(@Body authRequest: AuthRequest): Response<LoginResponse>


    @GET("users/get/homes")
    suspend fun getHomes(
        @Header("Cookie") token: String
    ): Response<List<HomeModel>>

    @POST("users/add/{homeID}")
    suspend fun addHome(
        @Header("Cookie") token: String,
        @Path("homeID") homeId: String
    ): Response<Unit>


    @GET("homes/{homeId}")
    suspend fun getHome( @Header("Cookie") token: String ): Response<HomeModel>

    @GET("homes/{homeId}/rooms")
    suspend fun getRooms(
        @Path("homeId") homeId: String,
        @Header("Cookie") token: String
    ): Response<List<RoomModel>>


    @GET("rooms/{roomId}/devices")
    suspend fun getDevices(
        @Path("roomId") roomId: String,
        @Header("Cookie") token: String
    ): Response<List<DeviceModel>>

    @POST("homes/{homeId}/rooms/add")
    suspend fun addRoom(
        @Path("homeId") homeId: String,
        @Header("Cookie") token: String,
        @Body request: AddRoomRequest
    ): Response<Unit>

    @POST("devices/{deviceId}/status")
    suspend fun toggleDeviceStatus(
        @Path("deviceId") deviceId: String,
        @Header("Cookie") token: String,
        @Body body: DeviceToggleRequest
    ): Response<Unit>

    @GET("homes/{homeId}/sensors")
    suspend fun getHomeSensors(
        @Path("homeId") homeId: String,
        @Header("Cookie") token: String
    ): Response<List<DeviceModel>>


    @POST("rooms/{roomId}/devices/{deviceId}/move")
    suspend fun moveDeviceToAnotherRoom(
        @Path("roomId") roomId: String,
        @Path("deviceId") deviceId: String,
        @Body newRoomId: String,
    ): Response<Unit>


}
