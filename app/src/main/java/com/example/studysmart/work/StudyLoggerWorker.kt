// app/src/main/java/com/example/studysmart/work/StudyLoggerWorker.kt
package com.example.studysmart.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class StudyLoggerWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        // JH：后台统计学习时长
        return Result.success()
    }
}
