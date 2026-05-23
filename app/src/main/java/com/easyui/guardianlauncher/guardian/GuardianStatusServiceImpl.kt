package com.easyui.guardianlauncher.guardian

import com.easyui.guardianlauncher.guardian.clock.Clock
import com.easyui.guardianlauncher.guardian.datasource.GuardianDataSource
import com.easyui.guardianlauncher.guardian.providers.BatteryStatusProvider
import com.easyui.guardianlauncher.guardian.providers.LauncherStatusProvider
import com.easyui.guardianlauncher.guardian.providers.NetworkStatusProvider

class GuardianStatusServiceImpl(
    private val dataSource: GuardianDataSource,
    private val batteryStatusProvider: BatteryStatusProvider,
    private val networkStatusProvider: NetworkStatusProvider,
    private val launcherStatusProvider: LauncherStatusProvider,
    private val clock: Clock,
    private val batteryLowThresholdPercent: Int = 20,
) : GuardianStatusService {
    override suspend fun getGuardianStatus(): GuardianCheckStatus {
        val now = clock.nowMillis()

        val parentContact = dataSource.getParentContact()
        val emergencyContact = dataSource.getEmergencyContact()
        val pinConfigured = dataSource.getParentPinHash().isNotBlank()

        val networkConnected = networkStatusProvider.isConnected()
        if (networkConnected == true) {
            dataSource.setLastOnlineAtMillis(now)
        }
        val lastOnlineAt = dataSource.getLastOnlineAtMillis()
        val offlineDurationMinutes = when {
            networkConnected != false -> null
            lastOnlineAt == null -> null
            else -> ((now - lastOnlineAt) / 60_000L).coerceAtLeast(0L)
        }

        val batteryStatus = batteryStatusProvider.getBatteryStatus(batteryLowThresholdPercent)
        val defaultLauncher = launcherStatusProvider.isDefaultLauncher()

        val defaultLauncherState = when (defaultLauncher) {
            true -> CheckState.OK
            false -> CheckState.ACTION_REQUIRED
            null -> CheckState.UNKNOWN
        }

        val parentConfigured = parentContact.phoneNumber.isNotBlank()
        val emergencyConfigured = emergencyContact.enabled && emergencyContact.phoneNumber.isNotBlank()

        return GuardianCheckStatus(
            defaultLauncherActive = defaultLauncherState,
            batteryLevelPercent = batteryStatus.levelPercent,
            batteryLow = batteryStatus.isLow,
            networkConnected = networkConnected,
            offlineDurationMinutes = offlineDurationMinutes,
            parentContactConfigured = parentConfigured,
            emergencyContactConfigured = emergencyConfigured,
            pinConfigured = pinConfigured,
            layoutLockEnabled = dataSource.isLayoutLockEnabled(),
            activeMode = dataSource.getActiveMode(),
            lastCheckedAtMillis = now,
        )
    }
}

