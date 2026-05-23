package com.easyui.guardianlauncher.guardian.providers

data class BatteryStatus(
    val levelPercent: Int?,
    val isLow: Boolean,
)

interface BatteryStatusProvider {
    suspend fun getBatteryStatus(lowThresholdPercent: Int = 20): BatteryStatus
}

