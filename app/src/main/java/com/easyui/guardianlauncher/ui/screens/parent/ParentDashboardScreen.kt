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
import com.easyui.guardianlauncher.ui.components.ModeSelectItem
import com.easyui.guardianlauncher.ui.viewmodels.LauncherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParentDashboardScreen(
    viewModel: LauncherViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Apps", "Modes", "Contacts", "Security & System")

    // Collect Viewmodel States
    val installedApps by viewModel.installedApps.collectAsState()
    val allowedApps by viewModel.allowedApps.collectAsState()
    val modeAppsHome by viewModel.modeAppsHome.collectAsState()
    val modeAppsSchool by viewModel.modeAppsSchool.collectAsState()
    val modeAppsSleep by viewModel.modeAppsSleep.collectAsState()
    val activeMode by viewModel.activeMode.collectAsState()
    val parentContact by viewModel.parentContact.collectAsState()
    val emergencyContact by viewModel.emergencyContact.collectAsState()

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
                    0 -> AppsConfigTab(
                        installedApps = installedApps,
                        allowedApps = allowedApps,
                        modeAppsHome = modeAppsHome,
                        modeAppsSchool = modeAppsSchool,
                        modeAppsSleep = modeAppsSleep,
                        viewModel = viewModel
                    )
                    1 -> ModesConfigTab(
                        activeMode = activeMode,
                        viewModel = viewModel
                    )
                    2 -> ContactsConfigTab(
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
                    3 -> SecurityConfigTab(
                        viewModel = viewModel,
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
                        }
                    )
                }
            }
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
fun SecurityConfigTab(
    viewModel: LauncherViewModel,
    onOpenSettings: () -> Unit
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

                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp)
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
    }
}
