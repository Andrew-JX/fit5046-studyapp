package com.example.studysmart.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class AlarmReciever: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val message = intent?.getStringExtra("EXTRA_MESSAGE") ?: return
        println("Alarm triggered: $message")
    }
}