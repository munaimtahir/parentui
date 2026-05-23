package com.easyui.guardianlauncher.guardian.child

import com.easyui.guardianlauncher.data.Mode
import com.easyui.guardianlauncher.guardian.CheckState
import com.easyui.guardianlauncher.guardian.GuardianCheckStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ChildSafetyWarningsTest {

    private fun baseStatus(
        batteryLow: Boolean = false,
        emergencyConfigured: Boolean = true,
        parentConfigured: Boolean = true,
        pinConfigured: Boolean = true,
        defaultLauncherActive: CheckState = CheckState.OK,
    ): GuardianCheckStatus {
        return GuardianCheckStatus(
            defaultLauncherActive = defaultLauncherActive,
            batteryLevelPercent = 50,
            batteryLow = batteryLow,
            networkConnected = true,
            offlineDurationMinutes = null,
            parentContactConfigured = parentConfigured,
            emergencyContactConfigured = emergencyConfigured,
            pinConfigured = pinConfigured,
            layoutLockEnabled = true,
            activeMode = Mode.HOME,
            lastCheckedAtMillis = 1234L,
        )
    }

    @Test
    fun lowBatteryGeneratesWarning() {
        val warnings = ChildSafetyWarnings.fromGuardianStatus(baseStatus(batteryLow = true))
        assertEquals(1, warnings.size)
        assertEquals(ChildSafetyWarningType.LOW_BATTERY, warnings.first().type)
    }

    @Test
    fun normalBatteryGeneratesNoWarning() {
        val warnings = ChildSafetyWarnings.fromGuardianStatus(baseStatus())
        assertTrue(warnings.isEmpty())
    }

    @Test
    fun emergencyMissingGeneratesWarning() {
        val warnings = ChildSafetyWarnings.fromGuardianStatus(baseStatus(emergencyConfigured = false))
        assertEquals(1, warnings.size)
        assertEquals(ChildSafetyWarningType.EMERGENCY_CONTACT_MISSING, warnings.first().type)
    }

    @Test
    fun onlyLimitedNumberOfWarningsShown() {
        val warnings = ChildSafetyWarnings.fromGuardianStatus(
            baseStatus(
                batteryLow = true,
                emergencyConfigured = false,
                parentConfigured = false,
                pinConfigured = false,
                defaultLauncherActive = CheckState.ACTION_REQUIRED
            ),
            maxWarnings = 2
        )
        assertEquals(2, warnings.size)
        assertEquals(ChildSafetyWarningType.LOW_BATTERY, warnings[0].type)
        assertEquals(ChildSafetyWarningType.EMERGENCY_CONTACT_MISSING, warnings[1].type)
    }
}

