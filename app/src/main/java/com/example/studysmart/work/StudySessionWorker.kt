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

/*
The Worker that records the learning duration in the background:
* - ≥15 minutes: Use PeriodicWorkRequest.
* - < 15 minutes: Use OneTimeWorkRequest + doWork to self-reschedule at the end.
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

        // Write a fragment
        repo.upsertSession(
            Session(
                id = null,
                subjectId = subjectId,
                durationMinutes = chunkMin,
                dateMillis = startMillis
            )
        )

        // If it is a short-interval task, the next one will be scheduled when it ends
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
         * Unified entry: Select strategies based on minutes.
         * - chunkMinutes >= 15 → PeriodicWork
         * - chunkMinutes <  15 → OneTimeWork + auto record
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

        /** Stop all background tracking of the same subject (regardless of long or short intervals). */
        fun stopTracking(context: Context, subjectId: Long) {
            val wm = WorkManager.getInstance(context)
            wm.cancelUniqueWork(UNIQUE_PERIODIC_PREFIX + subjectId)
            wm.cancelUniqueWork(UNIQUE_SHORT_PREFIX + subjectId)
        }

        /** Execute only once (can be used for completion at Finish). */
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

        // ---------- ≥15min period ----------
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

        // ---------- <15min  ----------
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


@EntryPoint
@InstallIn(SingletonComponent::class)
interface RepoEntryPoint {
    fun sessionRepo(): SessionRepo
}
