package com.easyui.guardianlauncher.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "guardian_settings")

class SettingsRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

    companion object {
        private val KEY_ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        private val KEY_ACTIVE_MODE = stringPreferencesKey("active_mode")
        private val KEY_ALLOWED_APPS = stringSetPreferencesKey("allowed_apps")
        
        private val KEY_MODE_APPS_HOME = stringSetPreferencesKey("mode_apps_home")
        private val KEY_MODE_APPS_SCHOOL = stringSetPreferencesKey("mode_apps_school")
        private val KEY_MODE_APPS_SLEEP = stringSetPreferencesKey("mode_apps_sleep")
        private val KEY_MODE_APPS_BEDTIME = stringSetPreferencesKey("mode_apps_bedtime")
        private val KEY_MODE_APPS_TRAVEL = stringSetPreferencesKey("mode_apps_travel")
        private val KEY_MODE_APPS_EXAM = stringSetPreferencesKey("mode_apps_exam")

        private val KEY_PARENT_CONTACT_LABEL = stringPreferencesKey("parent_contact_label")
        private val KEY_PARENT_CONTACT_PHONE = stringPreferencesKey("parent_contact_phone")
        
        private val KEY_EMERGENCY_CONTACT_LABEL = stringPreferencesKey("emergency_contact_label")
        private val KEY_EMERGENCY_CONTACT_PHONE = stringPreferencesKey("emergency_contact_phone")
        private val KEY_EMERGENCY_CONTACT_ENABLED = booleanPreferencesKey("emergency_contact_enabled")

        private val KEY_PARENT_PIN_HASH = stringPreferencesKey("parent_pin_hash")
        private val KEY_LAYOUT_LOCK_ENABLED = booleanPreferencesKey("layout_lock_enabled")
        
        private val KEY_LAST_ONLINE_AT_MILLIS = longPreferencesKey("last_online_at_millis")
        
        private val KEY_ROUTINE_SCHEDULES = stringPreferencesKey("routine_schedules")

        private val KEY_LIMITATIONS_ACKNOWLEDGED = booleanPreferencesKey("limitations_acknowledged")
    }

    // Flows
    val onboardingCompleted: Flow<Boolean> = context.dataStore.data.map { it[KEY_ONBOARDING_COMPLETED] ?: false }
    
    val activeMode: Flow<Mode> = context.dataStore.data.map { 
        val modeStr = it[KEY_ACTIVE_MODE] ?: Mode.HOME.name
        val parsed = try { Mode.valueOf(modeStr) } catch (e: Exception) { Mode.HOME }
        when (parsed) {
            Mode.HOME, Mode.SCHOOL, Mode.SLEEP -> parsed
            else -> Mode.HOME
        }
    }

    val allowedApps: Flow<Set<String>> = context.dataStore.data.map { it[KEY_ALLOWED_APPS] ?: emptySet() }

    val modeAppsHome: Flow<Set<String>> = context.dataStore.data.map { it[KEY_MODE_APPS_HOME] ?: emptySet() }
    val modeAppsSchool: Flow<Set<String>> = context.dataStore.data.map { it[KEY_MODE_APPS_SCHOOL] ?: emptySet() }
    val modeAppsSleep: Flow<Set<String>> = context.dataStore.data.map { it[KEY_MODE_APPS_SLEEP] ?: emptySet() }
    val modeAppsBedtime: Flow<Set<String>> = context.dataStore.data.map { it[KEY_MODE_APPS_BEDTIME] ?: emptySet() }
    val modeAppsTravel: Flow<Set<String>> = context.dataStore.data.map { it[KEY_MODE_APPS_TRAVEL] ?: emptySet() }
    val modeAppsExam: Flow<Set<String>> = context.dataStore.data.map { it[KEY_MODE_APPS_EXAM] ?: emptySet() }

    val parentContact: Flow<GuardianContact> = context.dataStore.data.map {
        GuardianContact(it[KEY_PARENT_CONTACT_LABEL] ?: "", it[KEY_PARENT_CONTACT_PHONE] ?: "")
    }

    val emergencyContact: Flow<EmergencyContact> = context.dataStore.data.map {
        EmergencyContact(
            it[KEY_EMERGENCY_CONTACT_LABEL] ?: "Emergency",
            it[KEY_EMERGENCY_CONTACT_PHONE] ?: "",
            it[KEY_EMERGENCY_CONTACT_ENABLED] ?: true
        )
    }

    val parentPinHash: Flow<String> = context.dataStore.data.map { it[KEY_PARENT_PIN_HASH] ?: "" }
    
    val layoutLockEnabled: Flow<Boolean> = context.dataStore.data.map { it[KEY_LAYOUT_LOCK_ENABLED] ?: true }

    val routineSchedules: Flow<List<RoutineSchedule>> = context.dataStore.data.map {
        val jsonStr = it[KEY_ROUTINE_SCHEDULES] ?: "[]"
        try { json.decodeFromString<List<RoutineSchedule>>(jsonStr) } catch (e: Exception) { emptyList() }
    }

    val lastOnlineAtMillis: Flow<Long> = context.dataStore.data.map { it[KEY_LAST_ONLINE_AT_MILLIS] ?: 0L }

    val limitationsAcknowledged: Flow<Boolean> = context.dataStore.data.map { it[KEY_LIMITATIONS_ACKNOWLEDGED] ?: false }

    // Setters
    suspend fun saveOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { it[KEY_ONBOARDING_COMPLETED] = completed }
    }

    suspend fun saveActiveMode(mode: Mode) {
        context.dataStore.edit { it[KEY_ACTIVE_MODE] = mode.name }
    }

    suspend fun saveAllowedApps(apps: Set<String>) {
        context.dataStore.edit { it[KEY_ALLOWED_APPS] = apps }
    }

    suspend fun saveModeApps(mode: Mode, apps: Set<String>) {
        context.dataStore.edit {
            when (mode) {
                Mode.HOME -> it[KEY_MODE_APPS_HOME] = apps
                Mode.SCHOOL -> it[KEY_MODE_APPS_SCHOOL] = apps
                Mode.SLEEP -> it[KEY_MODE_APPS_SLEEP] = apps
                Mode.BEDTIME -> it[KEY_MODE_APPS_BEDTIME] = apps
                Mode.TRAVEL -> it[KEY_MODE_APPS_TRAVEL] = apps
                Mode.EXAM -> it[KEY_MODE_APPS_EXAM] = apps
            }
        }
    }

    suspend fun saveParentContact(contact: GuardianContact) {
        context.dataStore.edit {
            it[KEY_PARENT_CONTACT_LABEL] = contact.label
            it[KEY_PARENT_CONTACT_PHONE] = contact.phoneNumber
        }
    }

    suspend fun saveEmergencyContact(contact: EmergencyContact) {
        context.dataStore.edit {
            it[KEY_EMERGENCY_CONTACT_LABEL] = contact.label
            it[KEY_EMERGENCY_CONTACT_PHONE] = contact.phoneNumber
            it[KEY_EMERGENCY_CONTACT_ENABLED] = contact.enabled
        }
    }

    suspend fun saveParentPinHash(hash: String) {
        context.dataStore.edit { it[KEY_PARENT_PIN_HASH] = hash }
    }

    suspend fun saveLayoutLockEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_LAYOUT_LOCK_ENABLED] = enabled }
    }

    suspend fun saveRoutineSchedules(schedules: List<RoutineSchedule>) {
        context.dataStore.edit { it[KEY_ROUTINE_SCHEDULES] = json.encodeToString(schedules) }
    }

    suspend fun saveLastOnlineAtMillis(timestampMillis: Long) {
        context.dataStore.edit { it[KEY_LAST_ONLINE_AT_MILLIS] = timestampMillis }
    }

    suspend fun saveLimitationsAcknowledged(acknowledged: Boolean) {
        context.dataStore.edit { it[KEY_LIMITATIONS_ACKNOWLEDGED] = acknowledged }
    }

    // Backup & Restore
    suspend fun exportBackup(): String {
        val backup = AppBackup(
            parentContact = parentContact.first(),
            emergencyContact = emergencyContact.first(),
            allowedApps = allowedApps.first(),
            modeAppsHome = modeAppsHome.first(),
            modeAppsSchool = modeAppsSchool.first(),
            modeAppsSleep = modeAppsSleep.first(),
            modeAppsBedtime = modeAppsBedtime.first(),
            modeAppsTravel = modeAppsTravel.first(),
            modeAppsExam = modeAppsExam.first(),
            schedules = routineSchedules.first(),
            layoutLockEnabled = layoutLockEnabled.first(),
            // Never export PIN/PIN hash in backups. If a parent forgets the PIN, the recovery path is still
            // "clear app data" as documented.
            parentPinHash = ""
        )
        return json.encodeToString(backup)
    }

    suspend fun importBackup(jsonStr: String): Boolean {
        return try {
            val backup = json.decodeFromString<AppBackup>(jsonStr)
            context.dataStore.edit {
                it[KEY_PARENT_CONTACT_LABEL] = backup.parentContact.label
                it[KEY_PARENT_CONTACT_PHONE] = backup.parentContact.phoneNumber
                it[KEY_EMERGENCY_CONTACT_LABEL] = backup.emergencyContact.label
                it[KEY_EMERGENCY_CONTACT_PHONE] = backup.emergencyContact.phoneNumber
                it[KEY_EMERGENCY_CONTACT_ENABLED] = backup.emergencyContact.enabled
                it[KEY_ALLOWED_APPS] = backup.allowedApps
                it[KEY_MODE_APPS_HOME] = backup.modeAppsHome
                it[KEY_MODE_APPS_SCHOOL] = backup.modeAppsSchool
                it[KEY_MODE_APPS_SLEEP] = backup.modeAppsSleep
                it[KEY_MODE_APPS_BEDTIME] = backup.modeAppsBedtime
                it[KEY_MODE_APPS_TRAVEL] = backup.modeAppsTravel
                it[KEY_MODE_APPS_EXAM] = backup.modeAppsExam
                it[KEY_ROUTINE_SCHEDULES] = json.encodeToString(backup.schedules)
                it[KEY_LAYOUT_LOCK_ENABLED] = backup.layoutLockEnabled
                // Intentionally do not import PIN/PIN hash.
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
