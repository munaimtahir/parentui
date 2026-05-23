package com.easyui.guardianlauncher.ui.screens.parent

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.guardianlauncher.data.AllowedApp
import com.easyui.guardianlauncher.data.Category
import com.easyui.guardianlauncher.data.Mode
import com.easyui.guardianlauncher.guardian.CheckState
import com.easyui.guardianlauncher.guardian.GuardianCheckStatus
import com.easyui.guardianlauncher.ui.components.ModeSelectItem
import com.easyui.guardianlauncher.ui.viewmodels.LauncherViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    viewModel: LauncherViewModel,
    onNavigateBack: () -> Unit,
    onOpenSetupLimitations: () -> Unit,
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        "Guardian Checks",
        "Child Home",
        "Apps & Modes",
        "Contacts & Emergency",
        "Parent Lock",
        "Setup Help"
    )

    // Collect Viewmodel States
    val installedApps by viewModel.installedApps.collectAsState()
    val allowedApps by viewModel.allowedApps.collectAsState()
    val modeAppsHome by viewModel.modeAppsHome.collectAsState()
    val modeAppsSchool by viewModel.modeAppsSchool.collectAsState()
    val modeAppsSleep by viewModel.modeAppsSleep.collectAsState()
    val activeMode by viewModel.activeMode.collectAsState()
    val parentContact by viewModel.parentContact.collectAsState()
    val emergencyContact by viewModel.emergencyContact.collectAsState()
    val layoutLockEnabled by viewModel.layoutLockEnabled.collectAsState()
    val guardianStatus by viewModel.guardianStatus.collectAsState()
    val childHomeApps by viewModel.childHomeApps.collectAsState()

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
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
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
                        onRefresh = { viewModel.refreshGuardianStatus() },
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
                        onOpenContacts = { selectedTab = 3 },
                        onOpenParentLock = { selectedTab = 4 },
                        onOpenSetupHelp = { selectedTab = 5 }
                    )
                    1 -> ChildHomeTab(
                        activeMode = activeMode,
                        childHomeApps = childHomeApps,
                        onRescanApps = { viewModel.scanInstalledApps(context) },
                    )
                    2 -> AppsAndModesTab(
                        installedApps = installedApps,
                        allowedApps = allowedApps,
                        modeAppsHome = modeAppsHome,
                        modeAppsSchool = modeAppsSchool,
                        modeAppsSleep = modeAppsSleep,
                        activeMode = activeMode,
                        viewModel = viewModel
                    )
                    3 -> ContactsConfigTab(
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
                    4 -> ParentLockTab(
                        viewModel = viewModel,
                        layoutLockEnabled = layoutLockEnabled,
                        onLayoutLockChange = { viewModel.setLayoutLockEnabled(it) },
                    )
                    5 -> SetupHelpTab(
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
    onRefresh: () -> Unit,
    onOpenDefaultLauncherSettings: () -> Unit,
    onOpenContacts: () -> Unit,
    onOpenParentLock: () -> Unit,
    onOpenSetupHelp: () -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Guardian Checks", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
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
                    onAction = onOpenContacts,
                )
            }

            item {
                GuardianCheckCard(
                    title = "Parent Lock",
                    state = if (status.pinConfigured) CheckState.OK else CheckState.WARNING,
                    okText = "Parent settings are protected.",
                    warningText = "Set a parent PIN to protect EasyUI settings.",
                    actionText = "Manage PIN",
                    onAction = onOpenParentLock,
                )
            }

            item {
                GuardianCheckCard(
                    title = "Layout Lock",
                    state = if (status.layoutLockEnabled) CheckState.OK else CheckState.WARNING,
                    okText = "Layout editing is locked.",
                    warningText = "Layout editing is not locked.",
                    actionText = "Change layout lock",
                    onAction = onOpenParentLock,
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
    onRescanApps: () -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Text("Child Home", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Current mode", fontWeight = FontWeight.Bold)
                    Text(activeMode.name.lowercase().replaceFirstChar { it.uppercase() })
                    Text("${childHomeApps.size} app(s) shown on the child home screen in this mode.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedButton(onClick = onRescanApps, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                        Text("Rescan installed apps", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        if (childHomeApps.isNotEmpty()) {
            item {
                Text("Apps shown", fontWeight = FontWeight.Bold)
            }
            items(childHomeApps) { app ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(app.displayLabel, fontWeight = FontWeight.Bold)
                        Text(app.packageName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        }
    }
}

@Composable
private fun AppsAndModesTab(
    installedApps: List<AllowedApp>,
    allowedApps: Set<String>,
    modeAppsHome: Set<String>,
    modeAppsSchool: Set<String>,
    modeAppsSleep: Set<String>,
    activeMode: Mode,
    viewModel: LauncherViewModel,
) {
    var subTab by remember { mutableIntStateOf(0) } // 0: Apps, 1: Modes

    Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            val labels = listOf("Apps", "Modes")
            labels.forEachIndexed { idx, label ->
                Button(
                    onClick = { subTab = idx },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (subTab == idx) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (subTab == idx) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(label, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (subTab == 0) {
            AppsConfigTab(
                installedApps = installedApps,
                allowedApps = allowedApps,
                modeAppsHome = modeAppsHome,
                modeAppsSchool = modeAppsSchool,
                modeAppsSleep = modeAppsSleep,
                viewModel = viewModel
            )
        } else {
            ModesConfigTab(
                activeMode = activeMode,
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun AppsConfigTab(
    installedApps: List<AllowedApp>,
    allowedApps: Set<String>,
    modeAppsHome: Set<String>,
    modeAppsSchool: Set<String>,
    modeAppsSleep: Set<String>,
    viewModel: LauncherViewModel
) {
    var subTab by remember { mutableIntStateOf(0) } // 0: Master list, 1: Home, 2: School, 3: Sleep

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val list = listOf("All Approved", "Home Mode", "School Mode", "Sleep Mode")
            list.forEachIndexed { idx, label ->
                Button(
                    onClick = { subTab = idx },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (subTab == idx) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (subTab == idx) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(label, fontSize = 11.sp, maxLines = 1, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (installedApps.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (subTab == 0) {
                    // Master approval list
                    items(installedApps) { app ->
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
                                Text(app.packageName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
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
                    val masterAllowedApps = installedApps.filter { it.packageName in allowedApps }

                    if (masterAllowedApps.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("Approve apps in the 'All Approved' list first.", textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    } else {
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
                                    Text(app.packageName, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
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

@Composable
fun ModesConfigTab(
    activeMode: Mode,
    viewModel: LauncherViewModel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Switch Active Mode", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text("Select which mode is active. This filters the apps displayed on the child home screen.", style = MaterialTheme.typography.bodyMedium)

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                ModeSelectItem(
                    title = "🏡 Home Mode",
                    desc = "Standard home settings for relaxation or generic usage.",
                    isSelected = activeMode == Mode.HOME,
                    onClick = { viewModel.updateActiveMode(Mode.HOME) }
                )
                ModeSelectItem(
                    title = "🎒 School Mode",
                    desc = "Focus settings. Keeps only learning and utility apps.",
                    isSelected = activeMode == Mode.SCHOOL,
                    onClick = { viewModel.updateActiveMode(Mode.SCHOOL) }
                )
                ModeSelectItem(
                    title = "🌙 Sleep Mode",
                    desc = "Rest mode. Hides all app tiles. Only calling tiles remain.",
                    isSelected = activeMode == Mode.SLEEP,
                    onClick = { viewModel.updateActiveMode(Mode.SLEEP) }
                )
            }
        }
    }
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
                    Text("Launcher & System Settings", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text("Check system settings to confirm default launcher configurations and limitations.", style = MaterialTheme.typography.bodyMedium)

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
                        text = "Because this app is a custom home launcher, kids can technically bypass the launcher layout using device settings or system notification links. Use Android Parental Controls or Google Family Link for deep system level lockdown.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
