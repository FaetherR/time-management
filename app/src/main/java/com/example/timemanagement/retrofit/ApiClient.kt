package com.example.timemanagement.retrofit

import android.util.Log
import com.onesignal.OneSignal
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

object ApiClient {
    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }
    fun sendNotification(message: String, offsetDateTime: OffsetDateTime, onResponseListener: ApiClient.OnResponseListener) : String {
        val request = NotificationRequest(
            app_id = APP_ID,
            contents = NotificationContent(en = message),
            include_aliases = NotificationOneSignalIDs(listOf(OneSignal.User.onesignalId)),
            target_channel = "push",
            send_after = offsetDateTime.format(DateTimeFormatter.ISO_DATE_TIME)
        )

        Log.d("Retrofit", "Date: $offsetDateTime")

        val call = ApiClient.apiService.sendNotification(request = request)
        var id = ""
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // handle the response
                Log.d("ApiClient", "Request got a response: $response")
                if(response.isSuccessful){
                    id = JSONObject(response.body()?.string()).get("id").toString()
                    onResponseListener.onResponseSuccess(id)
                }
                Log.d("ApiClient", "Message id: $id")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // handle the failure
                Log.e("ApiClient", "Error: $t")
            }
        })
        return id
    }

    interface OnResponseListener{
        fun onResponseSuccess(id: String)
    }

    fun deleteNotification(messageId: String) {
        Log.d("ApiClient", "MessageID: $messageId")
        val call = apiService.deleteNotification(messageId = messageId)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                // handle the response
                Log.d("ApiClient", "Request got a response: $response.body()?.string()")
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                // handle the failure
                Log.e("ApiClient", "Error: $t")
            }
        })
    }
}