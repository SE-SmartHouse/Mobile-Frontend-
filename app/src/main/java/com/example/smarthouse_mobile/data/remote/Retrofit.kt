package com.example.smarthouse_mobile.data.remote


import com.example.smarthouse_mobile.data.repository.ApiService
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://your-api-url.com" // Replace with your backend API URL

    // This will provide a single instance of ApiService
    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL) // API base URL
            .addConverterFactory(MoshiConverterFactory.create()) // Converts JSON to Kotlin objects
            .build()
            .create(ApiService::class.java) // Create ApiService instance
    }
}

