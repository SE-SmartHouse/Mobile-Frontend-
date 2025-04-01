

package com.example.smarthouse_mobile.data.repository
import com.example.smarthouse_mobile.data.model.HomeModel
import com.example.smarthouse_mobile.data.model.Rooms
import com.example.smarthouse_mobile.data.model.Devices
import com.example.smarthouse_mobile.data.model.StatusUpdate
import com.example.smarthouse_mobile.data.model.UserModel
import com.example.smarthouse_mobile.data.remote.RetrofitClient


import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

data class AuthRequest(val email: String, val password: String)
data class AuthResponse(val token: String)

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body authRequest: AuthRequest): Response<Unit>

    @POST("auth/login")
    suspend fun login(@Body authRequest: AuthRequest): Response<AuthResponse>

    @GET("users")
    suspend fun getUsers(): Response<List<UserModel>>

    @GET("homes")
    suspend fun getHomes(@Header("Authorization") token: String): Response<List<HomeModel>>

    @GET("homes/{homeId}")
    suspend fun getHome(@Path("homeId") homeId: String, @Header("Authorization") token: String): Response<HomeModel>

    @GET("homes/{homeId}/rooms")
    suspend fun getRooms(@Path("homeId") homeId: String, @Header("Authorization") token: String): Response<List<Rooms>>

    @GET("homes/{homeId}/rooms/{roomId}/devices")
    suspend fun getDevices(
        @Path("homeId") homeId: String,
        @Path("roomId") roomId: String,
        @Header("Authorization") token: String
    ): Response<List<Devices>>


    @POST("homes/{homeId}/rooms/{roomId}/devices/{deviceId}")
    suspend fun postDeviceStatus(
        @Path("homeId") homeId: String,
        @Path("roomId") roomId: String,
        @Path("deviceId") deviceId: String,
        @Body statusUpdate: StatusUpdate,
        @Header("Authorization") token: String
    ): Response<Unit>
}



