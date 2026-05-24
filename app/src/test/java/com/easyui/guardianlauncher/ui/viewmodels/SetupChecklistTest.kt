package com.easyui.guardianlauncher.ui.viewmodels

import com.easyui.guardianlauncher.data.EmergencyContact
import com.easyui.guardianlauncher.data.GuardianContact
import com.easyui.guardianlauncher.data.Mode
import com.easyui.guardianlauncher.data.SettingsRepository
import com.easyui.guardianlauncher.guardian.CheckState
import com.easyui.guardianlauncher.guardian.GuardianCheckStatus
import com.easyui.guardianlauncher.guardian.GuardianStatusService
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SetupChecklistTest {

    private val repository = mockk<SettingsRepository>(relaxed = true)
    private val guardianService = mockk<GuardianStatusService>(relaxed = true)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        every { repository.onboardingCompleted } returns flowOf(false)
        every { repository.activeMode } returns flowOf(Mode.HOME)
        every { repository.allowedApps } returns flowOf(emptySet())
        every { repository.modeAppsHome } returns flowOf(emptySet())
        every { repository.modeAppsSchool } returns flowOf(emptySet())
        every { repository.modeAppsSleep } returns flowOf(emptySet())
        every { repository.parentContact } returns flowOf(GuardianContact("", ""))
        every { repository.emergencyContact } returns flowOf(EmergencyContact("", "", true))
        every { repository.layoutLockEnabled } returns flowOf(false)
        every { repository.parentPinHash } returns flowOf("")
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `checklist reflects incomplete setup items`() = runTest {
        val viewModel = LauncherViewModel(repository, guardianService)
        
        // Subscribe to trigger Lazy stateIn
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.setupChecklist.collect {}
        }

        advanceUntilIdle()
        
        val checklist = viewModel.setupChecklist.value
        assertFalse(checklist.isFullySetup)
        
        // Pin missing
        assertTrue(checklist.items.find { it.id == "pin" }?.isCompleted == false)
        // Parent contact missing
        assertTrue(checklist.items.find { it.id == "parent_contact" }?.isCompleted == false)
        // Apps missing
        assertTrue(checklist.items.find { it.id == "apps" }?.isCompleted == false)
        
        job.cancel()
    }

    @Test
    fun `checklist reflects complete setup items`() = runTest {
        every { repository.parentPinHash } returns flowOf("somehash")
        every { repository.parentContact } returns flowOf(GuardianContact("Dad", "123"))
        every { repository.emergencyContact } returns flowOf(EmergencyContact("911", "911", true))
        every { repository.allowedApps } returns flowOf(setOf("com.app"))
        every { repository.modeAppsSchool } returns flowOf(setOf("com.app"))
        every { repository.layoutLockEnabled } returns flowOf(true)
        
        // Mock guardian status as OK
        val status = GuardianCheckStatus(
            defaultLauncherActive = CheckState.OK,
            batteryLevelPercent = 100,
            batteryLow = false,
            networkConnected = true,
            offlineDurationMinutes = 0,
            parentContactConfigured = true,
            emergencyContactConfigured = true,
            pinConfigured = true,
            layoutLockEnabled = true,
            activeMode = Mode.HOME,
            lastCheckedAtMillis = 1000L
        )
        
        val viewModel = LauncherViewModel(repository, guardianService)
        
        // Subscribe to trigger Lazy stateIn
        val job = backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.setupChecklist.collect {}
        }

        coEvery { guardianService.getGuardianStatus() } returns status
        viewModel.refreshGuardianStatus()
        
        advanceUntilIdle()
        
        val checklist = viewModel.setupChecklist.value
        println("Checklist items: ${checklist.items.map { it.id + "=" + it.isCompleted }}")
        println("Is fully setup: ${checklist.isFullySetup}")
        
        assertTrue("Pin should be completed", checklist.items.find { it.id == "pin" }?.isCompleted == true)
        assertTrue("Parent contact should be completed", checklist.items.find { it.id == "parent_contact" }?.isCompleted == true)
        assertTrue("Launcher should be completed", checklist.items.find { it.id == "launcher" }?.isCompleted == true)
        assertTrue("Apps should be completed", checklist.items.find { it.id == "apps" }?.isCompleted == true)
        assertTrue("School apps should be completed", checklist.items.find { it.id == "school_apps" }?.isCompleted == true)
        assertTrue("Layout lock should be completed", checklist.items.find { it.id == "layout_lock" }?.isCompleted == true)
        
        assertTrue("Checklist should be fully setup", checklist.isFullySetup)
        
        job.cancel()
    }
}
