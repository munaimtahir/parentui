package com.easyui.guardianlauncher.guardian

import com.easyui.guardianlauncher.data.EmergencyContact
import com.easyui.guardianlauncher.data.GuardianContact
import com.easyui.guardianlauncher.data.Mode
import com.easyui.guardianlauncher.guardian.clock.Clock
import com.easyui.guardianlauncher.guardian.datasource.GuardianDataSource
import com.easyui.guardianlauncher.guardian.providers.BatteryStatus
import com.easyui.guardianlauncher.guardian.providers.BatteryStatusProvider
import com.easyui.guardianlauncher.guardian.providers.LauncherStatusProvider
import com.easyui.guardianlauncher.guardian.providers.NetworkStatusProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class GuardianStatusServiceImplTest {

    private class FakeClock(var now: Long) : Clock {
        override fun nowMillis(): Long = now
    }

    private class FakeGuardianDataSource(
        var parentContact: GuardianContact = GuardianContact("Parent", "111"),
        var emergencyContact: EmergencyContact = EmergencyContact("Emergency", "222", enabled = true),
        var parentPinHash: String = "hash",
        var activeMode: Mode = Mode.HOME,
        var layoutLockEnabled: Boolean = true,
        var lastOnlineAtMillis: Long? = null,
    ) : GuardianDataSource {
        override suspend fun getParentContact(): GuardianContact = parentContact
        override suspend fun getEmergencyContact(): EmergencyContact = emergencyContact
        override suspend fun getParentPinHash(): String = parentPinHash
        override suspend fun getActiveMode(): Mode = activeMode

        override suspend fun isLayoutLockEnabled(): Boolean = layoutLockEnabled
        override suspend fun setLayoutLockEnabled(enabled: Boolean) {
            layoutLockEnabled = enabled
        }

        override suspend fun getLastOnlineAtMillis(): Long? = lastOnlineAtMillis
        override suspend fun setLastOnlineAtMillis(timestampMillis: Long) {
            lastOnlineAtMillis = timestampMillis
        }
    }

    private class FakeBatteryStatusProvider(var level: Int?) : BatteryStatusProvider {
        override suspend fun getBatteryStatus(lowThresholdPercent: Int): BatteryStatus {
            val percent = level
            return BatteryStatus(
                levelPercent = percent,
                isLow = percent != null && percent < lowThresholdPercent,
            )
        }
    }

    private class FakeNetworkStatusProvider(var connected: Boolean?) : NetworkStatusProvider {
        override suspend fun isConnected(): Boolean? = connected
    }

    private class FakeLauncherStatusProvider(var isDefault: Boolean?) : LauncherStatusProvider {
        override suspend fun isDefaultLauncher(): Boolean? = isDefault
    }

    @Test
    fun allSafeState() = runBlocking {
        val clock = FakeClock(now = 1_000_000L)
        val dataSource = FakeGuardianDataSource(
            parentContact = GuardianContact("Mom", "111"),
            emergencyContact = EmergencyContact("911", "222", enabled = true),
            parentPinHash = "hash",
            activeMode = Mode.HOME,
            layoutLockEnabled = true,
            lastOnlineAtMillis = null,
        )
        val service = GuardianStatusServiceImpl(
            dataSource = dataSource,
            batteryStatusProvider = FakeBatteryStatusProvider(level = 55),
            networkStatusProvider = FakeNetworkStatusProvider(connected = true),
            launcherStatusProvider = FakeLauncherStatusProvider(isDefault = true),
            clock = clock,
        )

        val status = service.getGuardianStatus()
        assertEquals(CheckState.OK, status.defaultLauncherActive)
        assertEquals(55, status.batteryLevelPercent)
        assertFalse(status.batteryLow)
        assertEquals(true, status.networkConnected)
        assertNull(status.offlineDurationMinutes)
        assertTrue(status.parentContactConfigured)
        assertTrue(status.emergencyContactConfigured)
        assertTrue(status.pinConfigured)
        assertTrue(status.layoutLockEnabled)
        assertEquals(Mode.HOME, status.activeMode)
        assertEquals(1_000_000L, status.lastCheckedAtMillis)
        assertNotNull(dataSource.lastOnlineAtMillis)
        assertEquals(0, status.warningCount)
        assertEquals(0, status.needsSetupCount)
        assertFalse(status.hasWarnings)
    }

    @Test
    fun missingParentContact() = runBlocking {
        val dataSource = FakeGuardianDataSource(parentContact = GuardianContact("Mom", ""))
        val service = GuardianStatusServiceImpl(
            dataSource = dataSource,
            batteryStatusProvider = FakeBatteryStatusProvider(level = 90),
            networkStatusProvider = FakeNetworkStatusProvider(connected = true),
            launcherStatusProvider = FakeLauncherStatusProvider(isDefault = true),
            clock = FakeClock(now = 1000L),
        )

        val status = service.getGuardianStatus()
        assertFalse(status.parentContactConfigured)
        assertTrue(status.hasWarnings)
    }

    @Test
    fun missingEmergencyContact() = runBlocking {
        val dataSource = FakeGuardianDataSource(emergencyContact = EmergencyContact("Emergency", "", enabled = true))
        val service = GuardianStatusServiceImpl(
            dataSource = dataSource,
            batteryStatusProvider = FakeBatteryStatusProvider(level = 90),
            networkStatusProvider = FakeNetworkStatusProvider(connected = true),
            launcherStatusProvider = FakeLauncherStatusProvider(isDefault = true),
            clock = FakeClock(now = 1000L),
        )

        val status = service.getGuardianStatus()
        assertFalse(status.emergencyContactConfigured)
        assertTrue(status.hasWarnings)
    }

    @Test
    fun batteryBelow20IsLow() = runBlocking {
        val service = GuardianStatusServiceImpl(
            dataSource = FakeGuardianDataSource(),
            batteryStatusProvider = FakeBatteryStatusProvider(level = 19),
            networkStatusProvider = FakeNetworkStatusProvider(connected = true),
            launcherStatusProvider = FakeLauncherStatusProvider(isDefault = true),
            clock = FakeClock(now = 1000L),
        )

        val status = service.getGuardianStatus()
        assertTrue(status.batteryLow)
    }

    @Test
    fun batteryAbove20IsNotLow() = runBlocking {
        val service = GuardianStatusServiceImpl(
            dataSource = FakeGuardianDataSource(),
            batteryStatusProvider = FakeBatteryStatusProvider(level = 20),
            networkStatusProvider = FakeNetworkStatusProvider(connected = true),
            launcherStatusProvider = FakeLauncherStatusProvider(isDefault = true),
            clock = FakeClock(now = 1000L),
        )

        val status = service.getGuardianStatus()
        assertFalse(status.batteryLow)
    }

    @Test
    fun networkDisconnectedReportsOfflineDurationWhenLastOnlineKnown() = runBlocking {
        val now = 60L * 60L * 1000L // 60 minutes
        val dataSource = FakeGuardianDataSource(lastOnlineAtMillis = now - (40L * 60L * 1000L))
        val service = GuardianStatusServiceImpl(
            dataSource = dataSource,
            batteryStatusProvider = FakeBatteryStatusProvider(level = 90),
            networkStatusProvider = FakeNetworkStatusProvider(connected = false),
            launcherStatusProvider = FakeLauncherStatusProvider(isDefault = true),
            clock = FakeClock(now = now),
        )

        val status = service.getGuardianStatus()
        assertEquals(false, status.networkConnected)
        assertEquals(40L, status.offlineDurationMinutes)
    }

    @Test
    fun pinMissingIsReported() = runBlocking {
        val dataSource = FakeGuardianDataSource(parentPinHash = "")
        val service = GuardianStatusServiceImpl(
            dataSource = dataSource,
            batteryStatusProvider = FakeBatteryStatusProvider(level = 90),
            networkStatusProvider = FakeNetworkStatusProvider(connected = true),
            launcherStatusProvider = FakeLauncherStatusProvider(isDefault = true),
            clock = FakeClock(now = 1000L),
        )

        val status = service.getGuardianStatus()
        assertFalse(status.pinConfigured)
    }

    @Test
    fun layoutLockDisabledIsReported() = runBlocking {
        val dataSource = FakeGuardianDataSource(layoutLockEnabled = false)
        val service = GuardianStatusServiceImpl(
            dataSource = dataSource,
            batteryStatusProvider = FakeBatteryStatusProvider(level = 90),
            networkStatusProvider = FakeNetworkStatusProvider(connected = true),
            launcherStatusProvider = FakeLauncherStatusProvider(isDefault = true),
            clock = FakeClock(now = 1000L),
        )

        val status = service.getGuardianStatus()
        assertFalse(status.layoutLockEnabled)
    }

    @Test
    fun activeModeIsReflected() = runBlocking {
        val dataSource = FakeGuardianDataSource(activeMode = Mode.SCHOOL)
        val service = GuardianStatusServiceImpl(
            dataSource = dataSource,
            batteryStatusProvider = FakeBatteryStatusProvider(level = 90),
            networkStatusProvider = FakeNetworkStatusProvider(connected = true),
            launcherStatusProvider = FakeLauncherStatusProvider(isDefault = true),
            clock = FakeClock(now = 1000L),
        )

        val status = service.getGuardianStatus()
        assertEquals(Mode.SCHOOL, status.activeMode)
    }
}

