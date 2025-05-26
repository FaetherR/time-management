package com.example.timemanagement.retrofit

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Authorization: Key $API_KEY"
    )
    @POST("notifications")
    fun sendNotification(
        @Query("c") c: String = "push",
        @Body request: NotificationRequest
    ): Call<ResponseBody>
}