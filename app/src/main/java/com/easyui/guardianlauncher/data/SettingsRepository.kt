package com.easyui.guardianlauncher.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "guardian_settings")

class SettingsRepository(private val context: Context) {

    companion object {
        private val KEY_PARENT_PIN_HASH = stringPreferencesKey("parent_pin_hash")
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val KEY_ACTIVE_MODE = stringPreferencesKey("active_mode")
        private val KEY_ALLOWED_APPS = stringSetPreferencesKey("allowed_apps")
        private val KEY_MODE_APPS_HOME = stringSetPreferencesKey("mode_apps_home")
        private val KEY_MODE_APPS_SCHOOL = stringSetPreferencesKey("mode_apps_school")
        private val KEY_MODE_APPS_SLEEP = stringSetPreferencesKey("mode_apps_sleep")
        private val KEY_PARENT_NAME = stringPreferencesKey("parent_name")
        private val KEY_PARENT_PHONE = stringPreferencesKey("parent_phone")
        private val KEY_EMERGENCY_NAME = stringPreferencesKey("emergency_name")
        private val KEY_EMERGENCY_PHONE = stringPreferencesKey("emergency_phone")
        private val KEY_EMERGENCY_ENABLED = booleanPreferencesKey("emergency_enabled")
        private val KEY_LAYOUT_LOCK_ENABLED = booleanPreferencesKey("layout_lock_enabled")
        private val KEY_LAST_ONLINE_AT_MILLIS = longPreferencesKey("last_online_at_millis")
    }

    val parentPinHash: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_PARENT_PIN_HASH] ?: ""
    }

    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_ONBOARDING_COMPLETED] ?: false
    }

    val activeMode: Flow<Mode> = context.dataStore.data.map { preferences ->
        val modeStr = preferences[KEY_ACTIVE_MODE] ?: Mode.HOME.name
        try {
            Mode.valueOf(modeStr)
        } catch (e: IllegalArgumentException) {
            Mode.HOME
        }
    }

    val allowedApps: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[KEY_ALLOWED_APPS] ?: emptySet()
    }

    val modeAppsHome: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[KEY_MODE_APPS_HOME] ?: emptySet()
    }

    val modeAppsSchool: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[KEY_MODE_APPS_SCHOOL] ?: emptySet()
    }

    val modeAppsSleep: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[KEY_MODE_APPS_SLEEP] ?: emptySet()
    }

    val parentContact: Flow<GuardianContact> = context.dataStore.data.map { preferences ->
        GuardianContact(
            label = preferences[KEY_PARENT_NAME] ?: "",
            phoneNumber = preferences[KEY_PARENT_PHONE] ?: ""
        )
    }

    val emergencyContact: Flow<EmergencyContact> = context.dataStore.data.map { preferences ->
        EmergencyContact(
            label = preferences[KEY_EMERGENCY_NAME] ?: "",
            phoneNumber = preferences[KEY_EMERGENCY_PHONE] ?: "",
            enabled = preferences[KEY_EMERGENCY_ENABLED] ?: true
        )
    }

    val layoutLockEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_LAYOUT_LOCK_ENABLED] ?: true
    }

    val lastOnlineAtMillis: Flow<Long?> = context.dataStore.data.map { preferences ->
        preferences[KEY_LAST_ONLINE_AT_MILLIS]
    }

    suspend fun saveParentPinHash(hash: String) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PARENT_PIN_HASH] = hash
        }
    }

    suspend fun saveOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun saveActiveMode(mode: Mode) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ACTIVE_MODE] = mode.name
        }
    }

    suspend fun saveAllowedApps(apps: Set<String>) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ALLOWED_APPS] = apps
        }
    }

    suspend fun saveModeApps(mode: Mode, apps: Set<String>) {
        context.dataStore.edit { preferences ->
            when (mode) {
                Mode.HOME -> preferences[KEY_MODE_APPS_HOME] = apps
                Mode.SCHOOL -> preferences[KEY_MODE_APPS_SCHOOL] = apps
                Mode.SLEEP -> preferences[KEY_MODE_APPS_SLEEP] = apps
            }
        }
    }

    suspend fun saveParentContact(contact: GuardianContact) {
        context.dataStore.edit { preferences ->
            preferences[KEY_PARENT_NAME] = contact.label
            preferences[KEY_PARENT_PHONE] = contact.phoneNumber
        }
    }

    suspend fun saveEmergencyContact(contact: EmergencyContact) {
        context.dataStore.edit { preferences ->
            preferences[KEY_EMERGENCY_NAME] = contact.label
            preferences[KEY_EMERGENCY_PHONE] = contact.phoneNumber
            preferences[KEY_EMERGENCY_ENABLED] = contact.enabled
        }
    }

    suspend fun saveLayoutLockEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LAYOUT_LOCK_ENABLED] = enabled
        }
    }

    suspend fun saveLastOnlineAtMillis(timestampMillis: Long) {
        context.dataStore.edit { preferences ->
            preferences[KEY_LAST_ONLINE_AT_MILLIS] = timestampMillis
        }
    }
}
