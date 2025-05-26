package com.example.timemanagement.retrofit

data class NotificationRequest(
    val app_id: String,
    val contents: NotificationContent,
    val include_aliases: NotificationOneSignalIDs,
    val target_channel: String,
    val send_after: String
)
