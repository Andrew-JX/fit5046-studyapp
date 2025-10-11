package com.example.studysmart.data.prefs

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("user_prefs")

data class StudyProfile(
    val major: String = "",
    val difficulty: String = ""
)

class UserPrefs @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        // 之前给过的番茄钟示例（可留可删，你们用不上可以不管）
        val FOCUS_MIN = intPreferencesKey("focus_min")
        val BREAK_MIN = intPreferencesKey("break_min")

        // ==== 新增：Onboarding 相关 ====
        val MAJOR = stringPreferencesKey("major")
        val DIFFICULTY = stringPreferencesKey("difficulty")
        val WEEKLY_TARGET_HOURS = intPreferencesKey("weekly_target_hours")
    }

    // ==== 读取 Flow ====
    val hasSeenOnboarding: Flow<Boolean> =
        context.dataStore.data.map { it[Keys.HAS_SEEN_ONBOARDING] ?: false }

    val studyProfile: Flow<StudyProfile> =
        context.dataStore.data.map {
            StudyProfile(
                major = it[Keys.MAJOR] ?: "",
                difficulty = it[Keys.DIFFICULTY] ?: ""
            )
        }

    val weeklyTargetHours: Flow<Int> =
        context.dataStore.data.map { it[Keys.WEEKLY_TARGET_HOURS] ?: 10 }

    // ==== 写入方法 ====
    suspend fun setHasSeenOnboarding(value: Boolean) {
        context.dataStore.edit { it[Keys.HAS_SEEN_ONBOARDING] = value }
    }

    suspend fun setPomodoro(focusMin: Int, breakMin: Int) {
        context.dataStore.edit {
            it[Keys.FOCUS_MIN] = focusMin
            it[Keys.BREAK_MIN] = breakMin
        }
    }

    // ==== 新增：Onboarding 写入 ====
    suspend fun setStudyProfile(major: String, difficulty: String) {
        context.dataStore.edit {
            it[Keys.MAJOR] = major
            it[Keys.DIFFICULTY] = difficulty
        }
    }

    suspend fun setWeeklyTargetHours(hours: Int) {
        context.dataStore.edit { it[Keys.WEEKLY_TARGET_HOURS] = hours.coerceAtLeast(0) }
    }
}
