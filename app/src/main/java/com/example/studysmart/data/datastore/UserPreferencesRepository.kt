package com.example.studysmart.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")


class UserPreferencesRepository(private val context: Context) {


    private object PreferencesKeys {
        val FOCUS_LENGTH = intPreferencesKey("focus_length")
        val BREAK_LENGTH = intPreferencesKey("break_length")
    }


    data class UserPreferences(
        val focusLength: Int = 25,  // Default 25 minutes
        val breakLength: Int = 5    // Default 5 minutes
    )

    val userPreferencesFlow: Flow<UserPreferences> = context.dataStore.data
        .map { preferences ->
            UserPreferences(
                focusLength = preferences[PreferencesKeys.FOCUS_LENGTH] ?: 25,
                breakLength = preferences[PreferencesKeys.BREAK_LENGTH] ?: 5
            )
        }


    suspend fun saveFocusLength(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FOCUS_LENGTH] = minutes
        }
    }

    suspend fun saveBreakLength(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.BREAK_LENGTH] = minutes
        }
    }

    suspend fun clearPreferences() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}