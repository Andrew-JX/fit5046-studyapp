package com.example.studysmart.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

// DataStore instance
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

data class UserPreferences(
    val focusLength: Int = 25,
    val breakLength: Int = 5,
    val username: String = ""
)

// ==== Onboarding Profile ====
data class StudyProfile(
    val major: String = "",
    val difficulty: String = ""
)

class UserPreferencesRepository(private val context: Context) {

    // Keys for DataStore
    companion object {
        val FOCUS_LENGTH_KEY = intPreferencesKey("focus_length")
        val BREAK_LENGTH_KEY = intPreferencesKey("break_length")
        val USERNAME_KEY = stringPreferencesKey("username")

        // ==== Onboarding  ====
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
        val MAJOR = stringPreferencesKey("major")
        val DIFFICULTY = stringPreferencesKey("difficulty")
        val WEEKLY_TARGET_HOURS = intPreferencesKey("weekly_target_hours")
    }

    // Flow for user preferences
    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                focusLength = preferences[FOCUS_LENGTH_KEY] ?: 25,
                breakLength = preferences[BREAK_LENGTH_KEY] ?: 5,
                username = preferences[USERNAME_KEY] ?: ""
            )
        }

    // Flow for username only
    val usernameFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[USERNAME_KEY] ?: ""
        }

    // ==== Onboarding  ====
    val hasSeenOnboardingFlow: Flow<Boolean> =
        context.dataStore.data.map { it[HAS_SEEN_ONBOARDING] ?: false }

    val studyProfileFlow: Flow<StudyProfile> =
        context.dataStore.data.map { p ->
            StudyProfile(
                major = p[MAJOR] ?: "",
                difficulty = p[DIFFICULTY] ?: ""
            )
        }

    val weeklyTargetHoursFlow: Flow<Int> =
        context.dataStore.data.map { p -> p[WEEKLY_TARGET_HOURS] ?: 10 }

    // Save focus length
    suspend fun saveFocusLength(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[FOCUS_LENGTH_KEY] = minutes
        }
    }

    // Save break length
    suspend fun saveBreakLength(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[BREAK_LENGTH_KEY] = minutes
        }
    }

    // Save username
    suspend fun saveUsername(username: String) {
        context.dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
        }
    }

    // ====  Onboarding ====
    suspend fun setHasSeenOnboarding(seen: Boolean) {
        context.dataStore.edit { it[HAS_SEEN_ONBOARDING] = seen }
    }

    suspend fun saveStudyProfile(major: String, difficulty: String) {
        context.dataStore.edit {
            it[MAJOR] = major
            it[DIFFICULTY] = difficulty
        }
    }

    suspend fun saveWeeklyTargetHours(hours: Int) {
        context.dataStore.edit { it[WEEKLY_TARGET_HOURS] = hours.coerceAtLeast(0) }
    }

    // Clear all preferences
    suspend fun clearPreferences() {
        context.dataStore.edit { it.clear() }
    }
}