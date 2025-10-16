package com.example.studysmart.domain.model

import android.os.Message
import java.time.LocalDateTime

data class AlarmItem (
    val time: LocalDateTime,
    val message: String
)
