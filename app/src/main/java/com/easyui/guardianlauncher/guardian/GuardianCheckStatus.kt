package com.easyui.guardianlauncher.guardian

import com.easyui.guardianlauncher.data.Mode

data class GuardianCheckStatus(
    val defaultLauncherActive: CheckState,
    val batteryLevelPercent: Int?,
    val batteryLow: Boolean,
    val networkConnected: Boolean?,
    val offlineDurationMinutes: Long?,
    val parentContactConfigured: Boolean,
    val emergencyContactConfigured: Boolean,
    val pinConfigured: Boolean,
    val layoutLockEnabled: Boolean,
    val activeMode: Mode,
    val lastCheckedAtMillis: Long,
)

val GuardianCheckStatus.warningCount: Int
    get() = listOf(
        defaultLauncherActive == CheckState.WARNING,
        batteryLow,
        networkConnected == false,
        !parentContactConfigured,
        !emergencyContactConfigured,
        !pinConfigured,
        !layoutLockEnabled,
    ).count { it }

val GuardianCheckStatus.needsSetupCount: Int
    get() = listOf(
        defaultLauncherActive == CheckState.ACTION_REQUIRED,
        !parentContactConfigured,
        !emergencyContactConfigured,
        !pinConfigured,
    ).count { it }

val GuardianCheckStatus.hasWarnings: Boolean
    get() = warningCount > 0 || needsSetupCount > 0

