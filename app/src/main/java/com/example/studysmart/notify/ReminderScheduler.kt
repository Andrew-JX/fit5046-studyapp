// app/src/main/java/com/example/studysmart/notify/ReminderScheduler.kt
package com.example.studysmart.notify

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

/*class ReminderScheduler(private val ctx: Context) {
    fun scheduleTaskReminder(triggerAtMillis: Long) {
        val am = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(ctx, ReminderReceiver::class.java)
        val pi = PendingIntent.getBroadcast(ctx, 1001, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pi)
    }
}*/

