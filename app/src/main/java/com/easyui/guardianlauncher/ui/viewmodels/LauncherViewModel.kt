package com.easyui.guardianlauncher.ui.viewmodels

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyui.guardianlauncher.data.AllowedApp
import com.easyui.guardianlauncher.data.Category
import com.easyui.guardianlauncher.data.EmergencyContact
import com.easyui.guardianlauncher.data.GuardianContact
import com.easyui.guardianlauncher.data.Mode
import com.easyui.guardianlauncher.data.RoutineSchedule
import com.easyui.guardianlauncher.data.SettingsRepository
import com.easyui.guardianlauncher.data.SetupChecklist
import com.easyui.guardianlauncher.data.SetupChecklistItem
import com.easyui.guardianlauncher.guardian.CheckState
import com.easyui.guardianlauncher.guardian.GuardianCheckStatus
import com.easyui.guardianlauncher.guardian.GuardianStatusService
import com.easyui.guardianlauncher.guardian.routine.RoutineManager
import com.easyui.guardianlauncher.ui.navigation.Routes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.security.MessageDigest

class LauncherViewModel(
    private val repository: SettingsRepository,
    private val guardianStatusService: GuardianStatusService? = null,
) : ViewModel() {

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
    
    val modeAppsBedtime: StateFlow<Set<String>> = repository.modeAppsBedtime
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())
    
    val modeAppsTravel: StateFlow<Set<String>> = repository.modeAppsTravel
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())
    
    val modeAppsExam: StateFlow<Set<String>> = repository.modeAppsExam
        .stateIn(viewModelScope, SharingStarted.Lazily, emptySet())

    val parentContact: StateFlow<GuardianContact> = repository.parentContact
        .stateIn(viewModelScope, SharingStarted.Lazily, GuardianContact("", ""))

    val emergencyContact: StateFlow<EmergencyContact> = repository.emergencyContact
        .stateIn(viewModelScope, SharingStarted.Lazily, EmergencyContact("", "", true))

    val layoutLockEnabled: StateFlow<Boolean> = repository.layoutLockEnabled
        .stateIn(viewModelScope, SharingStarted.Lazily, true)

    val parentPinHash: StateFlow<String> = repository.parentPinHash
        .stateIn(viewModelScope, SharingStarted.Lazily, "")
    
    val routineSchedules: StateFlow<List<RoutineSchedule>> = repository.routineSchedules
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val limitationsAcknowledged: StateFlow<Boolean> = repository.limitationsAcknowledged
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    // List of all installed launchable apps (scanned dynamically at runtime)
    private val _installedApps = MutableStateFlow<List<AllowedApp>>(emptyList())
    val installedApps: StateFlow<List<AllowedApp>> = _installedApps.asStateFlow()

    private val _guardianStatus = MutableStateFlow<GuardianCheckStatus?>(null)
    val guardianStatus: StateFlow<GuardianCheckStatus?> = _guardianStatus.asStateFlow()

    private val _appSearchQuery = MutableStateFlow("")
    val appSearchQuery: StateFlow<String> = _appSearchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow<Category?>(null)
    val selectedCategory: StateFlow<Category?> = _selectedCategory.asStateFlow()

    val setupChecklist: StateFlow<SetupChecklist> = combine(
        guardianStatus,
        parentPinHash,
        parentContact,
        emergencyContact,
        allowedApps,
        modeAppsSchool,
        layoutLockEnabled
    ) { array ->
        val status = array[0] as? GuardianCheckStatus
        val pin = array[1] as String
        val parent = array[2] as GuardianContact
        val emergency = array[3] as EmergencyContact
        val allowed = array[4] as Set<String>
        val school = array[5] as Set<String>
        val layoutLock = array[6] as Boolean

        val items = mutableListOf<SetupChecklistItem>()

        // 1. Default Launcher
        val launcherOk = status?.defaultLauncherActive == CheckState.OK
        items.add(
            SetupChecklistItem(
                id = "launcher",
                title = "Set EasyUI as the Home app",
                description = "Ensure your child returns to EasyUI when pressing the Home button.",
                isCompleted = launcherOk,
                actionLabel = if (!launcherOk) "Configure" else null,
                route = Routes.PARENT_DASHBOARD,
                subTab = 5 // Setup Help
            )
        )

        // 2. Parent PIN
        val pinOk = pin.isNotEmpty()
        items.add(
            SetupChecklistItem(
                id = "pin",
                title = "Set parent PIN",
                description = "Protect EasyUI settings from accidental changes.",
                isCompleted = pinOk,
                actionLabel = if (!pinOk) "Set PIN" else null,
                route = Routes.PARENT_DASHBOARD,
                subTab = 4 // Parent Lock
            )
        )

        // 3. Parent Contact
        val parentOk = parent.phoneNumber.isNotBlank()
        items.add(
            SetupChecklistItem(
                id = "parent_contact",
                title = "Add parent contact",
                description = "Allow your child to call you with one tap.",
                isCompleted = parentOk,
                actionLabel = if (!parentOk) "Add contact" else null,
                route = Routes.PARENT_DASHBOARD,
                subTab = 3 // Contacts
            )
        )

        // 4. Emergency Contact
        val emergencyOk = !emergency.enabled || emergency.phoneNumber.isNotBlank()
        items.add(
            SetupChecklistItem(
                id = "emergency_contact",
                title = "Add emergency contact",
                description = "Quick access to emergency services or another guardian.",
                isCompleted = emergencyOk,
                actionLabel = if (!emergencyOk) "Add contact" else null,
                route = Routes.PARENT_DASHBOARD,
                subTab = 3 // Contacts
            )
        )

        // 5. Approved Apps
        val appsOk = allowed.isNotEmpty()
        items.add(
            SetupChecklistItem(
                id = "apps",
                title = "Choose approved apps",
                description = "Select which apps your child is allowed to use.",
                isCompleted = appsOk,
                actionLabel = if (!appsOk) "Choose apps" else null,
                route = Routes.PARENT_DASHBOARD,
                subTab = 2 // Apps & Modes
            )
        )

        // 6. School Mode Apps
        val schoolOk = school.isNotEmpty()
        items.add(
            SetupChecklistItem(
                id = "school_apps",
                title = "Assign apps to School mode",
                description = "Select learning and utility apps for focus time.",
                isCompleted = schoolOk,
                actionLabel = if (!schoolOk) "Assign apps" else null,
                route = Routes.PARENT_DASHBOARD,
                subTab = 2 // Apps & Modes
            )
        )

        // 7. Layout Lock
        items.add(
            SetupChecklistItem(
                id = "layout_lock",
                title = "Enable layout lock",
                description = "Prevent app tiles from being moved or removed.",
                isCompleted = layoutLock,
                actionLabel = if (!layoutLock) "Lock layout" else null,
                route = Routes.PARENT_DASHBOARD,
                subTab = 4 // Parent Lock
            )
        )

        SetupChecklist(
            items = items,
            isFullySetup = items.all { it.isCompleted }
        )
    }.stateIn(viewModelScope, SharingStarted.Lazily, SetupChecklist(emptyList(), false))

    // List of all apps shown on child home
    val childHomeApps: StateFlow<List<AllowedApp>> = combine(
        installedApps,
        activeMode,
        allowedApps,
        modeAppsHome,
        modeAppsSchool,
        modeAppsSleep,
        modeAppsBedtime,
        modeAppsTravel,
        modeAppsExam
    ) { array ->
        val installed = array[0] as List<AllowedApp>
        val mode = array[1] as Mode
        val allowed = array[2] as Set<String>
        val homeApps = array[3] as Set<String>
        val schoolApps = array[4] as Set<String>
        val _sleepApps = array[5] as Set<String>
        val bedtimeApps = array[6] as Set<String>
        val travelApps = array[7] as Set<String>
        val examApps = array[8] as Set<String>

        val baseAllowed = installed.filter { it.packageName in allowed }
        
        when (mode) {
            Mode.HOME -> baseAllowed.filter { it.packageName in homeApps }
            Mode.SCHOOL -> baseAllowed.filter { it.packageName in schoolApps }
            Mode.SLEEP -> emptyList()
            Mode.BEDTIME -> baseAllowed.filter { it.packageName in bedtimeApps }
            Mode.TRAVEL -> baseAllowed.filter { it.packageName in travelApps }
            Mode.EXAM -> baseAllowed.filter { it.packageName in examApps }
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
                Mode.entries.forEach { mode ->
                    val set = getModeAppsFlow(mode).first().toMutableSet().apply { remove(packageName) }
                    repository.saveModeApps(mode, set)
                }
            } else {
                current.add(packageName)
                val homeSet = repository.modeAppsHome.first().toMutableSet().apply { add(packageName) }
                repository.saveModeApps(Mode.HOME, homeSet)
            }
            repository.saveAllowedApps(current)
        }
    }

    private fun getModeAppsFlow(mode: Mode): StateFlow<Set<String>> = when (mode) {
        Mode.HOME -> modeAppsHome
        Mode.SCHOOL -> modeAppsSchool
        Mode.SLEEP -> modeAppsSleep
        Mode.BEDTIME -> modeAppsBedtime
        Mode.TRAVEL -> modeAppsTravel
        Mode.EXAM -> modeAppsExam
    }

    fun toggleAppForMode(mode: Mode, packageName: String) {
        viewModelScope.launch {
            val currentModeApps = getModeAppsFlow(mode).first().toMutableSet()

            if (packageName in currentModeApps) {
                currentModeApps.remove(packageName)
            } else {
                currentModeApps.add(packageName)
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

    fun setLayoutLockEnabled(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveLayoutLockEnabled(enabled)
        }
    }

    fun setAppSearchQuery(query: String) {
        _appSearchQuery.value = query
    }

    fun selectCategory(category: Category?) {
        _selectedCategory.value = category
    }

    fun resetLayout() {
        viewModelScope.launch {
            repository.saveActiveMode(Mode.HOME)
            refreshGuardianStatus()
        }
    }

    fun setLimitationsAcknowledged(acknowledged: Boolean) {
        viewModelScope.launch {
            repository.saveLimitationsAcknowledged(acknowledged)
        }
    }

    fun refreshGuardianStatus() {
        val service = guardianStatusService ?: return
        viewModelScope.launch {
            _guardianStatus.value = service.getGuardianStatus()
        }
    }

    // Routine & Scheduling
    fun saveRoutineSchedules(context: Context, schedules: List<RoutineSchedule>) {
        viewModelScope.launch {
            repository.saveRoutineSchedules(schedules)
            RoutineManager(context).scheduleNextEvent(schedules)
        }
    }

    // Backup & Restore
    fun exportSettings(onResult: (String) -> Unit) {
        viewModelScope.launch {
            onResult(repository.exportBackup())
        }
    }

    fun importSettings(context: Context, json: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.importBackup(json)
            if (success) {
                val schedules = repository.routineSchedules.first()
                RoutineManager(context).scheduleNextEvent(schedules)
            }
            onResult(success)
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
            // Log or ignore
        }
    }
}
