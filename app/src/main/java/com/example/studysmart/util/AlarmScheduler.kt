package com.example.studysmart.util

import com.example.studysmart.domain.model.AlarmItem
import dagger.Provides


interface AlarmScheduler {

    fun schedule(item: AlarmItem)

    fun cancel(item: AlarmItem)
}


