package com.example.studysmart.notify

interface ReminderScheduler {
    fun scheduleTaskReminder(taskId: Long, triggerAtMillis: Long)
    fun cancelTaskReminder(taskId: Long)
}
