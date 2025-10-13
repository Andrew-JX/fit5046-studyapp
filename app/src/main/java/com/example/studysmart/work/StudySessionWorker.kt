// app/src/main/java/com/example/studysmart/work/StudySessionWorker.kt
package com.example.studysmart.work

import android.content.Context
import androidx.work.*
import com.example.studysmart.data.repo.SessionRepo
import com.example.studysmart.domain.model.Session
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * 后台记录学习时长的 Worker：
 * - ≥15 分钟：使用 PeriodicWorkRequest。
 * - < 15 分钟：使用 OneTimeWorkRequest + doWork 末尾自我重排程。
 */
class StudySessionWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        val subjectId   = inputData.getLong(KEY_SUBJECT_ID, -1L)
        val chunkMin    = inputData.getInt(KEY_CHUNK_MINUTES, 15)
        val startMillis = inputData.getLong(KEY_START_MILLIS, System.currentTimeMillis())
        if (subjectId <= 0L || chunkMin <= 0) return@withContext Result.failure()

        val repo = EntryPointAccessors.fromApplication(
            applicationContext, RepoEntryPoint::class.java
        ).sessionRepo()

        // 写入一条片段
        repo.upsertSession(
            Session(
                id = null,
                subjectId = subjectId,
                durationMinutes = chunkMin,
                dateMillis = startMillis
            )
        )

        // 若为短间隔任务，结束时排下一次
        if (chunkMin < 15) {
            startShortInterval(applicationContext, subjectId, chunkMin)
        }

        Result.success()
    }

    companion object {
        // inputs
        const val KEY_SUBJECT_ID    = "subjectId"
        const val KEY_CHUNK_MINUTES = "chunkMinutes"
        const val KEY_START_MILLIS  = "startMillis"

        // unique names
        private const val UNIQUE_PERIODIC_PREFIX = "study-tracker-"
        private const val UNIQUE_SHORT_PREFIX    = "study-tracker-short-"

        /**
         * 统一入口：根据分钟数选择策略。
         * - chunkMinutes >= 15 → PeriodicWork
         * - chunkMinutes <  15 → OneTimeWork + 自我重排程
         */
        fun startTracking(
            context: Context,
            subjectId: Long,
            chunkMinutes: Int = 15
        ) {
            if (chunkMinutes >= 15) {
                startPeriodic(context, subjectId, chunkMinutes)
            } else {
                startShortInterval(context, subjectId, chunkMinutes)
            }
        }

        /** 停止同一学科的所有后台追踪（无论长/短间隔）。 */
        fun stopTracking(context: Context, subjectId: Long) {
            val wm = WorkManager.getInstance(context)
            wm.cancelUniqueWork(UNIQUE_PERIODIC_PREFIX + subjectId)
            wm.cancelUniqueWork(UNIQUE_SHORT_PREFIX + subjectId)
        }

        /** 仅执行一次（可用于 Finish 时的补记）。 */
        fun enqueueOnce(
            context: Context,
            subjectId: Long,
            chunkMinutes: Int,
            startMillis: Long = System.currentTimeMillis()
        ) {
            val data = workDataOf(
                KEY_SUBJECT_ID to subjectId,
                KEY_CHUNK_MINUTES to chunkMinutes,
                KEY_START_MILLIS to startMillis
            )
            val req = OneTimeWorkRequestBuilder<StudySessionWorker>()
                .setInputData(data)
                .build()
            WorkManager.getInstance(context).enqueue(req)
        }

        // ---------- 内部：≥15min 周期 ----------
        private fun startPeriodic(context: Context, subjectId: Long, chunkMinutes: Int) {
            val data = baseInput(subjectId, chunkMinutes)
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
            val req = PeriodicWorkRequestBuilder<StudySessionWorker>(
                chunkMinutes.toLong(), TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .setInputData(data)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                UNIQUE_PERIODIC_PREFIX + subjectId,
                ExistingPeriodicWorkPolicy.UPDATE,
                req
            )
        }

        // ---------- 内部：<15min 单次 + 自排程 ----------
        private fun startShortInterval(context: Context, subjectId: Long, chunkMinutes: Int) {
            val data = baseInput(subjectId, chunkMinutes)
            val req = OneTimeWorkRequestBuilder<StudySessionWorker>()
                .setInitialDelay(chunkMinutes.toLong(), TimeUnit.MINUTES)
                .setInputData(data)
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_SHORT_PREFIX + subjectId,
                ExistingWorkPolicy.REPLACE,
                req
            )
        }

        private fun baseInput(subjectId: Long, chunkMinutes: Int) = workDataOf(
            KEY_SUBJECT_ID to subjectId,
            KEY_CHUNK_MINUTES to chunkMinutes,
            KEY_START_MILLIS to System.currentTimeMillis()
        )
    }
}

/** 通过 Hilt EntryPoint 暴露仓库给 Worker 使用。 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface RepoEntryPoint {
    fun sessionRepo(): SessionRepo
}
