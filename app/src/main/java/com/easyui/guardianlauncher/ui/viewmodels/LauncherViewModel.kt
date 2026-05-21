package com.easyui.guardianlauncher.ui.viewmodels

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyui.guardianlauncher.data.AllowedApp
import com.easyui.guardianlauncher.data.Category
import com.easyui.guardianlauncher.data.EmergencyContact
import com.easyui.guardianlauncher.data.GuardianContact
import com.easyui.guardianlauncher.data.Mode
import com.easyui.guardianlauncher.data.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.security.MessageDigest

class LauncherViewModel(private val repository: SettingsRepository) : ViewModel() {

    // Exposure of repo flows
    val onboardingCompleted: StateFlow<Boolean> = repository.onboardingCompleted
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val activeMode: StateFlow<Mode> = repository.activeMode
        .stateIn(viewModelScope, SharingStarted.Lazily, Mode.HOME)

    val allowedApps: StateFlow<Set<String>> = repository.allowedApps
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    val modeAppsHome: StateFlow<Set<String>> = repository.modeAppsHome
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    val modeAppsSchool: StateFlow<Set<String>> = repository.modeAppsSchool
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    val modeAppsSleep: StateFlow<Set<String>> = repository.modeAppsSleep
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    val parentContact: StateFlow<GuardianContact> = repository.parentContact
        .stateIn(viewModelScope, SharingStarted.Lazily, GuardianContact("", ""))

    val emergencyContact: StateFlow<EmergencyContact> = repository.emergencyContact
        .stateIn(viewModelScope, SharingStarted.Lazily, EmergencyContact("", "", true))

    // List of all installed launchable apps (scanned dynamically at runtime)
    private val _installedApps = MutableStateFlow<List<AllowedApp>>(emptyList())
    val installedApps: StateFlow<List<AllowedApp>> = _installedApps.asStateFlow()

    // Derived list of apps that should appear on the child's home screen
    val childHomeApps: StateFlow<List<AllowedApp>> = combine(
        installedApps,
        activeMode,
        allowedApps,
        modeAppsHome,
        modeAppsSchool,
        modeAppsSleep
    ) { array ->
        @Suppress("UNCHECKED_CAST")
        val installed = array[0] as List<AllowedApp>
        val mode = array[1] as Mode
        @Suppress("UNCHECKED_CAST")
        val allowed = array[2] as Set<String>
        @Suppress("UNCHECKED_CAST")
        val homeApps = array[3] as Set<String>
        @Suppress("UNCHECKED_CAST")
        val schoolApps = array[4] as Set<String>
        @Suppress("UNCHECKED_CAST")
        val sleepApps = array[5] as Set<String>

        // First filter by total allowed apps set by parent
        val baseAllowed = installed.filter { it.packageName in allowed }
        
        // Then filter based on active mode
        when (mode) {
            Mode.HOME -> baseAllowed.filter { it.packageName in homeApps }
            Mode.SCHOOL -> baseAllowed.filter { it.packageName in schoolApps }
            Mode.SLEEP -> baseAllowed.filter { it.packageName in sleepApps }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Initialization: scan installed apps
    fun scanInstalledApps(context: Context) {
        viewModelScope.launch {
            _installedApps.value = getInstalledAppsFromSystem(context)
        }
    }

    private fun getInstalledAppsFromSystem(context: Context): List<AllowedApp> {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val list = pm.queryIntentActivities(intent, 0)
        return list.mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            // Hide own launcher application so child cannot launch a sub-instance of launcher
            if (packageName == context.packageName) return@mapNotNull null
            
            val displayLabel = resolveInfo.loadLabel(pm).toString()
            AllowedApp(packageName, displayLabel, getCategoryHeuristic(packageName))
        }.sortedBy { it.displayLabel.lowercase() }
    }

    private fun getCategoryHeuristic(packageName: String): Category {
        val pkg = packageName.lowercase()
        return when {
            pkg.contains("edu") || pkg.contains("learn") || pkg.contains("study") || pkg.contains("dict") || pkg.contains("math") -> Category.LEARNING
            pkg.contains("game") || pkg.contains("play") || pkg.contains("arcade") || pkg.contains("puzzle") || pkg.contains("toy") || pkg.contains("angrybirds") -> Category.GAMES
            pkg.contains("chat") || pkg.contains("messag") || pkg.contains("whatsapp") || pkg.contains("telegr") || pkg.contains("skype") || pkg.contains("discord") || pkg.contains("talk") || pkg.contains("mail") || pkg.contains("gmail") -> Category.COMMUNICATION
            pkg.contains("camera") || pkg.contains("photo") || pkg.contains("gallery") || pkg.contains("snap") || pkg.contains("instagram") -> Category.CAMERA_PHOTOS
            pkg.contains("calc") || pkg.contains("clock") || pkg.contains("calend") || pkg.contains("note") || pkg.contains("weather") || pkg.contains("setting") || pkg.contains("file") || pkg.contains("map") || pkg.contains("browser") || pkg.contains("chrome") -> Category.UTILITIES
            else -> Category.OTHER
        }
    }

    // Security PIN logic
    fun hashPin(pin: String): String {
        if (pin.isEmpty()) return ""
        val bytes = pin.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    suspend fun verifyPin(pin: String): Boolean {
        val storedHash = repository.parentPinHash.first()
        // If no PIN is configured, default to true or block?
        // Standard flow requires setting up PIN during onboarding.
        if (storedHash.isEmpty()) return false
        return hashPin(pin) == storedHash
    }

    fun isPinSet(onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val hash = repository.parentPinHash.first()
            onResult(hash.isNotEmpty())
        }
    }

    // Setters
    fun setParentPin(pin: String) {
        viewModelScope.launch {
            repository.saveParentPinHash(hashPin(pin))
        }
    }

    fun setOnboardingCompleted(completed: Boolean) {
        viewModelScope.launch {
            repository.saveOnboardingCompleted(completed)
        }
    }

    fun updateActiveMode(mode: Mode) {
        viewModelScope.launch {
            repository.saveActiveMode(mode)
        }
    }

    fun toggleAllowedApp(packageName: String) {
        viewModelScope.launch {
            val current = repository.allowedApps.first().toMutableSet()
            if (packageName in current) {
                current.remove(packageName)
                // Also remove from mode lists
                val homeSet = repository.modeAppsHome.first().toMutableSet().apply { remove(packageName) }
                val schoolSet = repository.modeAppsSchool.first().toMutableSet().apply { remove(packageName) }
                val sleepSet = repository.modeAppsSleep.first().toMutableSet().apply { remove(packageName) }
                repository.saveModeApps(Mode.HOME, homeSet)
                repository.saveModeApps(Mode.SCHOOL, schoolSet)
                repository.saveModeApps(Mode.SLEEP, sleepSet)
            } else {
                current.add(packageName)
                // Add to Home mode by default
                val homeSet = repository.modeAppsHome.first().toMutableSet().apply { add(packageName) }
                repository.saveModeApps(Mode.HOME, homeSet)
            }
            repository.saveAllowedApps(current)
        }
    }

    fun toggleAppForMode(mode: Mode, packageName: String) {
        viewModelScope.launch {
            val currentModeApps = when (mode) {
                Mode.HOME -> repository.modeAppsHome.first()
                Mode.SCHOOL -> repository.modeAppsSchool.first()
                Mode.SLEEP -> repository.modeAppsSleep.first()
            }.toMutableSet()

            if (packageName in currentModeApps) {
                currentModeApps.remove(packageName)
            } else {
                currentModeApps.add(packageName)
                // Ensure it is also in the master allowed list
                val master = repository.allowedApps.first().toMutableSet()
                if (packageName !in master) {
                    master.add(packageName)
                    repository.saveAllowedApps(master)
                }
            }
            repository.saveModeApps(mode, currentModeApps)
        }
    }

    fun updateParentContact(label: String, phone: String) {
        viewModelScope.launch {
            repository.saveParentContact(GuardianContact(label, phone))
        }
    }

    fun updateEmergencyContact(label: String, phone: String, enabled: Boolean) {
        viewModelScope.launch {
            repository.saveEmergencyContact(EmergencyContact(label, phone, enabled))
        }
    }

    // Launch approved app using Explicit Intent
    fun launchApp(context: Context, packageName: String) {
        try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
            }
        } catch (e: Exception) {
            // Log or ignore to prevent crashing the child home screen launcher
        }
    }
}
