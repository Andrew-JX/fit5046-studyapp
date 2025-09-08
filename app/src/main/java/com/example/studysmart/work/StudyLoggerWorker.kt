// app/src/main/java/com/example/studysmart/work/StudyLoggerWorker.kt
package com.example.studysmart.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class StudyLoggerWorker(ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        // TODO: 统计当天专注分钟数，写入Room或DataStore，供Dashboard图表用
        return Result.success()
    }
}
