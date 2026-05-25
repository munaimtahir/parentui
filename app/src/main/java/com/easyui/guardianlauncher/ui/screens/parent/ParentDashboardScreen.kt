package com.easyui.guardianlauncher.ui.screens.parent

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.guardianlauncher.data.AllowedApp
import com.easyui.guardianlauncher.data.Category
import com.easyui.guardianlauncher.data.Mode
import com.easyui.guardianlauncher.data.RoutineSchedule
import com.easyui.guardianlauncher.data.SetupChecklist
import com.easyui.guardianlauncher.data.SetupChecklistItem
import com.easyui.guardianlauncher.guardian.CheckState
import com.easyui.guardianlauncher.guardian.GuardianCheckStatus
import com.easyui.guardianlauncher.ui.components.ModeSelectItem
import com.easyui.guardianlauncher.ui.components.OnResumeEffect
import com.easyui.guardianlauncher.ui.viewmodels.LauncherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    viewModel: LauncherViewModel,
    onNavigateBack: () -> Unit,
    onOpenSetupHealth: () -> Unit,
    onOpenReadyChecklist: () -> Unit,
    onOpenSetupLimitations: () -> Unit,
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "Status",
        "Preview",
        "Apps",
        "Routines",
        "Contacts",
        "Lock",
        "Backup",
        "Help"
    )

    // Collect Viewmodel States
    val installedApps by viewModel.installedApps.collectAsState()
    val allowedApps by viewModel.allowedApps.collectAsState()
    val modeAppsHome by viewModel.modeAppsHome.collectAsState()
    val modeAppsSchool by viewModel.modeAppsSchool.collectAsState()
    val modeAppsSleep by viewModel.modeAppsSleep.collectAsState()
    val modeAppsBedtime by viewModel.modeAppsBedtime.collectAsState()
    val modeAppsTravel by viewModel.modeAppsTravel.collectAsState()
    val modeAppsExam by viewModel.modeAppsExam.collectAsState()
    val routineSchedules by viewModel.routineSchedules.collectAsState()
    val activeMode by viewModel.activeMode.collectAsState()
    val parentContact by viewModel.parentContact.collectAsState()
    val emergencyContact by viewModel.emergencyContact.collectAsState()
    val layoutLockEnabled by viewModel.layoutLockEnabled.collectAsState()
    val guardianStatus by viewModel.guardianStatus.collectAsState()
    val childHomeApps by viewModel.childHomeApps.collectAsState()
    val setupChecklist by viewModel.setupChecklist.collectAsState()

    OnResumeEffect { viewModel.refreshGuardianStatus() }

    LaunchedEffect(selectedTab) {
        if (selectedTab == 0) {
            viewModel.refreshGuardianStatus()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Parent Dashboard", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.onSurfaceVariant
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Bold) }
                    )
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> GuardianChecksTab(
                        status = guardianStatus,
                        setupChecklist = setupChecklist,
                        onRefresh = { viewModel.refreshGuardianStatus() },
                        onNavigateToTab = { selectedTab = it },
                        onOpenSetupHealth = onOpenSetupHealth,
                        onOpenReadyChecklist = onOpenReadyChecklist,
                        onOpenDefaultLauncherSettings = {
                            try {
                                val intent = Intent(Settings.ACTION_HOME_SETTINGS)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                try {
                                    val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                                    context.startActivity(intent)
                                } catch (ex: Exception) {
                                    val intent = Intent(Settings.ACTION_SETTINGS)
                                    context.startActivity(intent)
                                }
                            }
                        },
                        onOpenSetupHelp = { selectedTab = 7 }
                    )
                    1 -> ChildHomeTab(
                        activeMode = activeMode,
                        childHomeApps = childHomeApps,
                        viewModel = viewModel,
                        onRescanApps = { viewModel.scanInstalledApps(context) },
                    )
                    2 -> AppsConfigTab(
                        installedApps = installedApps,
                        allowedApps = allowedApps,
                        modeAppsHome = modeAppsHome,
                        modeAppsSchool = modeAppsSchool,
                        modeAppsSleep = modeAppsSleep,
                        modeAppsBedtime = modeAppsBedtime,
                        modeAppsTravel = modeAppsTravel,
                        modeAppsExam = modeAppsExam,
                        viewModel = viewModel
                    )
                    3 -> RoutinesTab(
                        schedules = routineSchedules,
                        viewModel = viewModel,
                        context = context
                    )
                    4 -> ContactsConfigTab(
                        parentContactName = parentContact.label,
                        parentContactPhone = parentContact.phoneNumber,
                        emergencyContactName = emergencyContact.label,
                        emergencyContactPhone = emergencyContact.phoneNumber,
                        emergencyEnabled = emergencyContact.enabled,
                        onSave = { pName, pPhone, eName, ePhone, eEnabled ->
                            viewModel.updateParentContact(pName, pPhone)
                            viewModel.updateEmergencyContact(eName, ePhone, eEnabled)
                        }
                    )
                    5 -> ParentLockTab(
                        viewModel = viewModel,
                        layoutLockEnabled = layoutLockEnabled,
                        onLayoutLockChange = { viewModel.setLayoutLockEnabled(it) },
                    )
                    6 -> AdvancedSettingsTab(
                        viewModel = viewModel,
                        context = context
                    )
                    7 -> SetupHelpTab(
                        onOpenSettings = {
                            try {
                                val intent = Intent(Settings.ACTION_HOME_SETTINGS)
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                try {
                                    val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                                    context.startActivity(intent)
                                } catch (ex: Exception) {
                                    val intent = Intent(Settings.ACTION_SETTINGS)
                                    context.startActivity(intent)
                                }
                            }
                        },
                        onOpenSetupLimitations = onOpenSetupLimitations,
                    )
                }
            }
        }
    }
}

@Composable
private fun GuardianChecksTab(
    status: GuardianCheckStatus?,
    setupChecklist: SetupChecklist,
    onRefresh: () -> Unit,
    onNavigateToTab: (Int) -> Unit,
    onOpenSetupHealth: () -> Unit,
    onOpenReadyChecklist: () -> Unit,
    onOpenDefaultLauncherSettings: () -> Unit,
    onOpenSetupHelp: () -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        if (!setupChecklist.isFullySetup) {
            item {
                Text("Finish Setup", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            }
            items(setupChecklist.items.filter { !it.isCompleted }) { item ->
                SetupChecklistCard(item, onAction = {
                    if (item.subTab != null) {
                        onNavigateToTab(item.subTab)
                    }
                })
            }
            item {
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Guardian Status", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                Text(
                    "Check whether EasyUI is active and the child phone setup is safe.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = onRefresh,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Refresh", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onOpenSetupHelp,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Setup Help", fontWeight = FontWeight.Bold)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = onOpenSetupHealth,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Setup Health", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onOpenReadyChecklist,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Ready Checklist", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (status == null) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Text("Checking…", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            item {
                GuardianCheckCard(
                    title = "EasyUI Launcher",
                    state = status.defaultLauncherActive,
                    okText = "EasyUI is set as the default home app.",
                    warningText = "Set EasyUI as the default launcher so the child returns here when pressing Home.",
                    actionText = "Set default home screen",
                    onAction = onOpenDefaultLauncherSettings,
                )
            }

            item {
                val batteryState = when {
                    status.batteryLevelPercent == null -> CheckState.UNKNOWN
                    status.batteryLow -> CheckState.WARNING
                    else -> CheckState.OK
                }
                GuardianCheckCard(
                    title = "Battery",
                    state = batteryState,
                    okText = "Battery level is normal.",
                    warningText = "Battery is low. Please charge the phone.",
                    unknownText = "Battery level is unknown.",
                    trailing = {
                        if (status.batteryLevelPercent != null) {
                            Text("${status.batteryLevelPercent}%", fontWeight = FontWeight.Black)
                        }
                    }
                )
            }

            item {
                val state = when (status.networkConnected) {
                    true -> CheckState.OK
                    false -> CheckState.WARNING
                    null -> CheckState.UNKNOWN
                }
                val offlineSuffix = if (status.networkConnected == false && status.offlineDurationMinutes != null) {
                    " (offline ~${status.offlineDurationMinutes} min)"
                } else ""
                GuardianCheckCard(
                    title = "Internet",
                    state = state,
                    okText = "Phone is connected.",
                    warningText = "Phone is offline. Some online apps may not work.$offlineSuffix",
                    unknownText = "Internet status is unknown.",
                )
            }

            item {
                val contactsOk = status.parentContactConfigured && status.emergencyContactConfigured
                GuardianCheckCard(
                    title = "Contacts",
                    state = if (contactsOk) CheckState.OK else CheckState.WARNING,
                    okText = "Parent and emergency contacts are ready.",
                    warningText = "Add parent and emergency numbers for safety access.",
                    actionText = "Edit contacts",
                    onAction = { onNavigateToTab(4) },
                )
            }

            item {
                GuardianCheckCard(
                    title = "Parent Lock",
                    state = if (status.pinConfigured) CheckState.OK else CheckState.WARNING,
                    okText = "Parent settings are protected.",
                    warningText = "Set a parent PIN to protect EasyUI settings.",
                    actionText = "Manage PIN",
                    onAction = { onNavigateToTab(5) },
                )
            }

            item {
                GuardianCheckCard(
                    title = "Layout Lock",
                    state = if (status.layoutLockEnabled) CheckState.OK else CheckState.WARNING,
                    okText = "Layout editing is locked.",
                    warningText = "Layout editing is not locked.",
                    actionText = "Change layout lock",
                    onAction = { onNavigateToTab(5) },
                )
            }

            item {
                GuardianCheckCard(
                    title = "Current Mode",
                    state = CheckState.OK,
                    okText = "Current mode: ${status.activeMode.name.lowercase().replaceFirstChar { it.uppercase() }}.",
                )
            }

            item {
                val formatted = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(status.lastCheckedAtMillis))
                GuardianCheckCard(
                    title = "Last Checked",
                    state = CheckState.OK,
                    okText = formatted,
                )
            }
        }
    }
}

@Composable
private fun SetupChecklistCard(
    item: SetupChecklistItem,
    onAction: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary
                )
                Text(item.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            }
            Text(item.description, style = MaterialTheme.typography.bodyMedium)
            if (item.actionLabel != null) {
                Button(
                    onClick = onAction,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(item.actionLabel, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun GuardianCheckCard(
    title: String,
    state: CheckState,
    okText: String,
    warningText: String = "",
    unknownText: String = "Unknown.",
    actionText: String? = null,
    onAction: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val (icon, tint, label) = when (state) {
        CheckState.OK -> Triple(Icons.Default.CheckCircle, MaterialTheme.colorScheme.primary, "OK")
        CheckState.WARNING -> Triple(Icons.Default.Warning, MaterialTheme.colorScheme.tertiary, "Warning")
        CheckState.ACTION_REQUIRED -> Triple(Icons.Default.Error, MaterialTheme.colorScheme.error, "Needs setup")
        CheckState.UNKNOWN -> Triple(Icons.Default.Info, MaterialTheme.colorScheme.outline, "Unknown")
    }
    val message = when (state) {
        CheckState.OK -> okText
        CheckState.WARNING, CheckState.ACTION_REQUIRED -> warningText.ifBlank { okText }
        CheckState.UNKNOWN -> unknownText
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(imageVector = icon, contentDescription = null, tint = tint)
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Black)
                    Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (trailing != null) {
                    trailing()
                }
            }

            Text(message, style = MaterialTheme.typography.bodyMedium)

            if (actionText != null && onAction != null) {
                OutlinedButton(
                    onClick = onAction,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(actionText, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ChildHomeTab(
    activeMode: Mode,
    childHomeApps: List<AllowedApp>,
    viewModel: LauncherViewModel,
    onRescanApps: () -> Unit,
) {
    var showResetConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Text("Child Home Preview", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Configuration", fontWeight = FontWeight.Bold)
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(Mode.HOME, Mode.SCHOOL, Mode.SLEEP).forEach { mode ->
                            val isSelected = activeMode == mode
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.updateActiveMode(mode) },
                                label = { Text(mode.name.lowercase().take(4)) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    Text("${childHomeApps.size} app(s) shown in ${activeMode.name.lowercase().replaceFirstChar { it.uppercase() }} mode.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onRescanApps, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                            Text("Rescan Apps", fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(onClick = { showResetConfirm = true }, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                            Text("Reset Layout", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (childHomeApps.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No apps selected for this mode.\nChoose apps in 'Apps' to see them here.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }
        } else {
            item {
                Text("Screen Preview", fontWeight = FontWeight.Bold)
            }
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Mini Grid Preview
                        val columns = 3
                        val rows = (childHomeApps.size + columns - 1) / columns
                        
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            repeat(rows) { rowIndex ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    repeat(columns) { colIndex ->
                                        val itemIndex = rowIndex * columns + colIndex
                                        if (itemIndex < childHomeApps.size) {
                                            val app = childHomeApps[itemIndex]
                                            Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Card(
                                                        shape = RoundedCornerShape(12.dp),
                                                        modifier = Modifier.size(48.dp),
                                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                                    ) {
                                                        Box(modifier = Modifier.fillMaxSize().padding(6.dp), contentAlignment = Alignment.Center) {
                                                            com.easyui.guardianlauncher.ui.screens.child.AppIcon(packageName = app.packageName)
                                                        }
                                                    }
                                                    Spacer(modifier = Modifier.height(4.dp))
                                                    Text(
                                                        text = app.displayLabel,
                                                        fontSize = 10.sp,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                }
                                            }
                                        } else {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showResetConfirm) {
        AlertDialog(
            onDismissRequest = { showResetConfirm = false },
            title = { Text("Reset Layout?") },
            text = { Text("This will restore the default tile order and switch to Home Mode. Your approved apps and contacts will not be removed.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.resetLayout()
                    showResetConfirm = false
                }) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun AppsConfigTab(
    installedApps: List<AllowedApp>,
    allowedApps: Set<String>,
    modeAppsHome: Set<String>,
    modeAppsSchool: Set<String>,
    modeAppsSleep: Set<String>,
    modeAppsBedtime: Set<String>,
    modeAppsTravel: Set<String>,
    modeAppsExam: Set<String>,
    viewModel: LauncherViewModel
) {
    var subTab by remember { mutableIntStateOf(0) } // 0: Master, 1-6: Modes
    val searchQuery by viewModel.appSearchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(
            selectedTabIndex = subTab,
            edgePadding = 0.dp,
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            divider = {},
            indicator = {},
            modifier = Modifier.padding(bottom = 12.dp)
        ) {
            val list = listOf("All Approved", "Home", "School", "Sleep")
            list.forEachIndexed { idx, label ->
                FilterChip(
                    selected = subTab == idx,
                    onClick = { subTab = idx },
                    label = { Text(label) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.setAppSearchQuery(it) },
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            placeholder = { Text("Search apps...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setAppSearchQuery("") }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        // Category Filter chips
        ScrollableTabRow(
            selectedTabIndex = if (selectedCategory == null) 0 else Category.entries.indexOf(selectedCategory) + 1,
            edgePadding = 0.dp,
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            divider = {},
            indicator = {},
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { viewModel.selectCategory(null) },
                label = { Text("All") },
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Category.entries.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { viewModel.selectCategory(category) },
                    label = { Text(category.name.lowercase().replaceFirstChar { it.uppercase() }) },
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        if (installedApps.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            val filteredApps = installedApps.filter { app ->
                val matchesSearch = app.displayLabel.contains(searchQuery, ignoreCase = true) || 
                                   app.packageName.contains(searchQuery, ignoreCase = true)
                val matchesCategory = selectedCategory == null || app.category == selectedCategory
                matchesSearch && matchesCategory
            }

            if (filteredApps.isEmpty() && (searchQuery.isNotEmpty() || selectedCategory != null)) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text("No apps match your search or filter.", color = MaterialTheme.colorScheme.outline)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (subTab == 0) {
                        // Master approval list
                        items(filteredApps) { app ->
                            val isAllowed = app.packageName in allowedApps
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .clickable { viewModel.toggleAllowedApp(app.packageName) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(app.displayLabel, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                    Text("${app.category.name.lowercase()} • ${app.packageName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                }
                                Checkbox(
                                    checked = isAllowed,
                                    onCheckedChange = { viewModel.toggleAllowedApp(app.packageName) }
                                )
                            }
                        }
                    } else {
                        // Filter based on selected mode
                        val targetMode = when (subTab) {
                            1 -> Mode.HOME
                            2 -> Mode.SCHOOL
                            else -> Mode.SLEEP
                        }
                        val targetSet = when (subTab) {
                            1 -> modeAppsHome
                            2 -> modeAppsSchool
                            else -> modeAppsSleep
                        }

                        // Only show apps that are in the master allowed list
                        val masterAllowedApps = filteredApps.filter { it.packageName in allowedApps }

                        val isSleepModeConfig = targetMode == Mode.SLEEP
                        if (isSleepModeConfig) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.35f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("Sleep mode is contact-only in MVP.", fontWeight = FontWeight.Black)
                                        Text(
                                            "Sleep mode hides app tiles and keeps Parent/Emergency calling tiles available. This is intentional for the pilot.",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        if (!isSleepModeConfig && masterAllowedApps.isEmpty() && searchQuery.isEmpty() && selectedCategory == null) {
                            item {
                                Card(
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                                ) {
                                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("No apps assigned to this mode yet.", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                                        Text("Approve apps in the 'All Approved' list first, then assign them here.", style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        } else if (!isSleepModeConfig && masterAllowedApps.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                    Text("No approved apps match your search or filter.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.outline)
                                }
                            }
                        } else if (!isSleepModeConfig) {
                            items(masterAllowedApps) { app ->
                                val isAssigned = app.packageName in targetSet
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(MaterialTheme.colorScheme.surface)
                                        .clickable { viewModel.toggleAppForMode(targetMode, app.packageName) }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(app.displayLabel, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        Text("${app.category.name.lowercase()} • ${app.packageName}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                                    }
                                    Checkbox(
                                        checked = isAssigned,
                                        onCheckedChange = { viewModel.toggleAppForMode(targetMode, app.packageName) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoutinesTab(
    schedules: List<RoutineSchedule>,
    viewModel: LauncherViewModel,
    context: Context
) {
    var showAddDialog by remember { mutableStateOf(false) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("Automated Routines", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Text("Automatically switch modes based on the time of day.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        items(schedules) { schedule ->
            ScheduleCard(
                schedule = schedule,
                onDelete = {
                    val newList = schedules.filter { it.id != schedule.id }
                    viewModel.saveRoutineSchedules(context, newList)
                },
                onToggle = { enabled ->
                    val newList = schedules.map { 
                        if (it.id == schedule.id) it.copy(enabled = enabled) else it
                    }
                    viewModel.saveRoutineSchedules(context, newList)
                }
            )
        }

        item {
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add New Routine", fontWeight = FontWeight.Bold)
            }
        }
        
        if (schedules.isEmpty()) {
            item {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("No routines set up yet.", color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }

    if (showAddDialog) {
        AddScheduleDialog(
            onDismiss = { showAddDialog = false },
            onSave = { schedule ->
                viewModel.saveRoutineSchedules(context, schedules + schedule)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun ScheduleCard(
    schedule: RoutineSchedule,
    onDelete: () -> Unit,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = "${schedule.mode.name.lowercase().replaceFirstChar { it.uppercase() }} Mode",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Switch(checked = schedule.enabled, onCheckedChange = onToggle)
            }
            
            Text(
                text = "${schedule.startTime} – ${schedule.endTime}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                val days = listOf("S", "M", "T", "W", "T", "F", "S")
                days.forEachIndexed { index, day ->
                    val isEnabled = (index + 1) in schedule.daysEnabled
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(day, fontSize = 12.sp, color = if (isEnabled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun AddScheduleDialog(
    onDismiss: () -> Unit,
    onSave: (RoutineSchedule) -> Unit
) {
    var selectedMode by remember { mutableStateOf(Mode.SCHOOL) }
    var startTime by remember { mutableStateOf("08:00") }
    var endTime by remember { mutableStateOf("15:00") }
    var selectedDays by remember { mutableStateOf(listOf(2, 3, 4, 5, 6)) } // Mon-Fri

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Routine", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Mode", fontWeight = FontWeight.Bold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf(Mode.SCHOOL, Mode.SLEEP, Mode.BEDTIME, Mode.EXAM).forEach { mode ->
                        FilterChip(
                            selected = selectedMode == mode,
                            onClick = { selectedMode = mode },
                            label = { Text(mode.name.lowercase().take(4)) }
                        )
                    }
                }

                OutlinedTextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    label = { Text("Start Time (HH:mm)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    label = { Text("End Time (HH:mm)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Days", fontWeight = FontWeight.Bold)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val days = listOf("S", "M", "T", "W", "T", "F", "S")
                    days.forEachIndexed { index, day ->
                        val dayNum = index + 1
                        val isEnabled = dayNum in selectedDays
                        FilterChip(
                            selected = isEnabled,
                            onClick = {
                                selectedDays = if (isEnabled) selectedDays - dayNum else selectedDays + dayNum
                            },
                            label = { Text(day) }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(RoutineSchedule(
                    id = java.util.UUID.randomUUID().toString(),
                    mode = selectedMode,
                    startTime = startTime,
                    endTime = endTime,
                    daysEnabled = selectedDays
                ))
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ContactsConfigTab(
    parentContactName: String,
    parentContactPhone: String,
    emergencyContactName: String,
    emergencyContactPhone: String,
    emergencyEnabled: Boolean,
    onSave: (String, String, String, String, Boolean) -> Unit
) {
    var pName by remember { mutableStateOf(parentContactName) }
    var pPhone by remember { mutableStateOf(parentContactPhone) }
    var eName by remember { mutableStateOf(emergencyContactName) }
    var ePhone by remember { mutableStateOf(emergencyContactPhone) }
    var eEnabled by remember { mutableStateOf(emergencyEnabled) }

    var savedMessage by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Manage Contacts Shortcuts", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = pName,
                onValueChange = { pName = it },
                label = { Text("Parent/Guardian Name") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = pPhone,
                onValueChange = { pPhone = it },
                label = { Text("Parent/Guardian Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show Emergency Shortcut", fontWeight = FontWeight.Bold)
                Switch(checked = eEnabled, onCheckedChange = { eEnabled = it })
            }

            if (eEnabled) {
                OutlinedTextField(
                    value = eName,
                    onValueChange = { eName = it },
                    label = { Text("Emergency Contact Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = ePhone,
                    onValueChange = { ePhone = it },
                    label = { Text("Emergency Phone Number") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Button(
                onClick = {
                    onSave(pName.trim(), pPhone.trim(), eName.trim(), ePhone.trim(), eEnabled)
                    savedMessage = true
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save Contacts", fontWeight = FontWeight.Bold)
            }

            if (savedMessage) {
                Text(
                    text = "Contacts updated successfully!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun ParentLockTab(
    viewModel: LauncherViewModel,
    layoutLockEnabled: Boolean,
    onLayoutLockChange: (Boolean) -> Unit,
) {
    var newPin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var messageMsg by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Change Parent PIN", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    
                    OutlinedTextField(
                        value = newPin,
                        onValueChange = { newPin = it.filter { char -> char.isDigit() }.take(6) },
                        label = { Text("Enter New PIN (4-6 digits)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = confirmPin,
                        onValueChange = { confirmPin = it.filter { char -> char.isDigit() }.take(6) },
                        label = { Text("Confirm New PIN") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Button(
                        onClick = {
                            if (newPin.length < 4) {
                                messageMsg = "PIN must be at least 4 digits."
                                isError = true
                            } else if (newPin != confirmPin) {
                                messageMsg = "PINs do not match."
                                isError = true
                            } else {
                                viewModel.setParentPin(newPin)
                                messageMsg = "Parent PIN updated successfully!"
                                isError = false
                                newPin = ""
                                confirmPin = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Update PIN", fontWeight = FontWeight.Bold)
                    }

                    if (messageMsg.isNotEmpty()) {
                        Text(
                            text = messageMsg,
                            color = if (isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Layout Lock", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(
                        "Locks layout editing inside EasyUI. This does not block Android system settings.",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (layoutLockEnabled) "Locked" else "Unlocked", fontWeight = FontWeight.Bold)
                        Switch(checked = layoutLockEnabled, onCheckedChange = onLayoutLockChange)
                    }
                }
            }
        }
    }
}

@Composable
fun AdvancedSettingsTab(
    viewModel: LauncherViewModel,
    context: Context
) {
    var backupJson by remember { mutableStateOf("") }
    var showImportDialog by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text("Backup & Recovery", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Settings Backup", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text("Export your approved apps, contacts, and routines to a string for easy recovery.", style = MaterialTheme.typography.bodyMedium)
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                viewModel.exportSettings { json ->
                                    backupJson = json
                                    message = "Settings exported!"
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Export")
                        }
                        
                        OutlinedButton(
                            onClick = { showImportDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Upload, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Import")
                        }
                    }
                    
                    if (message.isNotEmpty()) {
                        Text(message, color = MaterialTheme.colorScheme.primary, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                    
                    if (backupJson.isNotEmpty()) {
                        OutlinedTextField(
                            value = backupJson,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Backup Data (JSON)") },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                            textStyle = MaterialTheme.typography.labelSmall
                        )
                        IconButton(onClick = {
                             // Copy to clipboard
                             val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                             val clip = android.content.ClipData.newPlainText("EasyUI Backup", backupJson)
                             clipboard.setPrimaryClip(clip)
                             message = "Copied to clipboard!"
                        }, modifier = Modifier.align(Alignment.End)) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy")
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Forgotten PIN?", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Text(
                        "If you forget your parent PIN, you can reset it by clearing the application data in Android Settings.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "1. Open Android Settings\n2. Go to Apps -> EasyUI Guardian Launcher\n3. Storage -> Clear Storage / Clear Data\n\nWarning: This will remove all your approved apps and settings. Use Export often!",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }

    if (showImportDialog) {
        var importText by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text("Import Settings", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = importText,
                    onValueChange = { importText = it },
                    label = { Text("Paste JSON here") },
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.importSettings(context, importText) { success ->
                        if (success) {
                            message = "Settings imported successfully!"
                            showImportDialog = false
                        } else {
                            message = "Error: Invalid backup data."
                        }
                    }
                }) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SetupHelpTab(
    onOpenSettings: () -> Unit,
    onOpenSetupLimitations: () -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Set EasyUI as the Home app", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(
                        "EasyUI works best when selected as the default Home app. Pressing the Home button will then always return the child to EasyUI.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        "Some phone brands show different menus. If the setting does not open automatically, search Settings for 'Default apps' or 'Home app'.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Button(
                        onClick = onOpenSettings,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Set Default Home Screen", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Use Family Link for deeper restrictions", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(
                        "EasyUI controls the child home screen experience. For Play Store restrictions, app install blocks, screen time limits, and web filtering, we recommend using Google Family Link alongside EasyUI.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("What EasyUI Can and Cannot Control", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(
                        "EasyUI helps simplify the child home screen and supports setup health checks, but Android still controls system-level features like Settings and the notification shade.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    OutlinedButton(
                        onClick = onOpenSetupLimitations,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text("Open details", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Compliance & Limitations", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Because this app is a custom home launcher, kids can technically bypass the launcher layout using device settings or system notification links. Use Google Family Link for deep system level lockdown.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
