package com.easyui.guardianlauncher.guardian.datasource

import com.easyui.guardianlauncher.data.EmergencyContact
import com.easyui.guardianlauncher.data.GuardianContact
import com.easyui.guardianlauncher.data.Mode
import com.easyui.guardianlauncher.data.SettingsRepository
import kotlinx.coroutines.flow.first

class SettingsGuardianDataSource(
    private val settingsRepository: SettingsRepository,
) : GuardianDataSource {
    override suspend fun getParentContact(): GuardianContact = settingsRepository.parentContact.first()

    override suspend fun getEmergencyContact(): EmergencyContact = settingsRepository.emergencyContact.first()

    override suspend fun getParentPinHash(): String = settingsRepository.parentPinHash.first()

    override suspend fun getActiveMode(): Mode = settingsRepository.activeMode.first()

    override suspend fun isLayoutLockEnabled(): Boolean = settingsRepository.layoutLockEnabled.first()

    override suspend fun setLayoutLockEnabled(enabled: Boolean) {
        settingsRepository.saveLayoutLockEnabled(enabled)
    }

    override suspend fun getLastOnlineAtMillis(): Long? = settingsRepository.lastOnlineAtMillis.first()

    override suspend fun setLastOnlineAtMillis(timestampMillis: Long) {
        settingsRepository.saveLastOnlineAtMillis(timestampMillis)
    }
}

