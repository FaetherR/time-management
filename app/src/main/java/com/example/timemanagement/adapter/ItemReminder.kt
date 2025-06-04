package com.example.timemanagement.adapter

import java.time.OffsetDateTime

data class ItemReminder(
    val time : OffsetDateTime,
    val message : String,
    val type : Int,
    val oneSignalMessageId : String
)