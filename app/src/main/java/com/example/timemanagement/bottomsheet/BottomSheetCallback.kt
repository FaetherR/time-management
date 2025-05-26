package com.example.timemanagement.bottomsheet

import java.time.OffsetDateTime

interface BottomSheetCallback {
    fun onConfirm(message: String, date: OffsetDateTime, type: Int)
}