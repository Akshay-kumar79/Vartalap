package com.example.vartalap.utils

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

private const val BASE_URL = "https://fcm.googleapis.com/fcm/"


private val retrofit = Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(ScalarsConverterFactory.create()).build()

interface ApiService{

    @POST("send")
    fun sendMessage(
        @HeaderMap headers: HashMap<String, String>,
        @Body messageBody: String
    ):Call<String>

}

object ApiClient{
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}