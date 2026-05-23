package com.easyui.guardianlauncher.guardian.providers.android

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import com.easyui.guardianlauncher.guardian.providers.BatteryStatus
import com.easyui.guardianlauncher.guardian.providers.BatteryStatusProvider

class AndroidBatteryStatusProvider(
    private val context: Context,
) : BatteryStatusProvider {
    override suspend fun getBatteryStatus(lowThresholdPercent: Int): BatteryStatus {
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
            ?: return BatteryStatus(levelPercent = null, isLow = false)

        val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
        if (level <= 0 || scale <= 0) return BatteryStatus(levelPercent = null, isLow = false)

        val percent = ((level.toFloat() / scale.toFloat()) * 100f).toInt().coerceIn(0, 100)
        return BatteryStatus(levelPercent = percent, isLow = percent < lowThresholdPercent)
    }
}

