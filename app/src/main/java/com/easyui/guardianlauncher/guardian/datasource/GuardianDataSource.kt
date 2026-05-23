package com.easyui.guardianlauncher.guardian.datasource

import com.easyui.guardianlauncher.data.EmergencyContact
import com.easyui.guardianlauncher.data.GuardianContact
import com.easyui.guardianlauncher.data.Mode

interface GuardianDataSource {
    suspend fun getParentContact(): GuardianContact
    suspend fun getEmergencyContact(): EmergencyContact
    suspend fun getParentPinHash(): String
    suspend fun getActiveMode(): Mode
    suspend fun isLayoutLockEnabled(): Boolean
    suspend fun setLayoutLockEnabled(enabled: Boolean)
    suspend fun getLastOnlineAtMillis(): Long?
    suspend fun setLastOnlineAtMillis(timestampMillis: Long)
}

