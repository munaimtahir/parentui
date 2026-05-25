package com.easyui.guardianlauncher.ui.screens.child

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.easyui.guardianlauncher.data.AllowedApp
import com.easyui.guardianlauncher.data.Mode
import com.easyui.guardianlauncher.guardian.CheckState
import com.easyui.guardianlauncher.guardian.child.ChildSafetyWarnings
import com.easyui.guardianlauncher.ui.components.PinEntryDialog
import com.easyui.guardianlauncher.ui.components.ModeSelectItem
import com.easyui.guardianlauncher.ui.components.OnResumeEffect
import com.easyui.guardianlauncher.ui.viewmodels.LauncherViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChildHomeScreen(
    viewModel: LauncherViewModel,
    onNavigateToDashboard: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // ViewModel states
    val activeMode by viewModel.activeMode.collectAsState()
    val childApps by viewModel.childHomeApps.collectAsState()
    val parentContact by viewModel.parentContact.collectAsState()
    val emergencyContact by viewModel.emergencyContact.collectAsState()
    val guardianStatus by viewModel.guardianStatus.collectAsState()

    // UI Local States
    var showPinDialogForDashboard by remember { mutableStateOf(false) }
    var showPinDialogForMode by remember { mutableStateOf(false) }
    var showModeSelector by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf("") }
    
    // Time/date updater
    var timeString by remember { mutableStateOf("") }
    var dateString by remember { mutableStateOf("") }

    // Visual tweaks based on mode
    val isQuietMode = activeMode in listOf(Mode.SLEEP, Mode.BEDTIME, Mode.EXAM)
    val backgroundColor = if (isQuietMode) Color(0xFF121212) else MaterialTheme.colorScheme.background
    val primaryTextColor = if (isQuietMode) Color.White else MaterialTheme.colorScheme.primary
    val secondaryTextColor = if (isQuietMode) Color.LightGray else MaterialTheme.colorScheme.secondary

    OnResumeEffect {
        viewModel.refreshGuardianStatus()
    }

    LaunchedEffect(Unit) {
        viewModel.scanInstalledApps(context)
        viewModel.refreshGuardianStatus()
        var lastGuardianRefreshAt = System.currentTimeMillis()
        while (true) {
            val now = Calendar.getInstance().time
            timeString = SimpleDateFormat("h:mm a", Locale.getDefault()).format(now)
            dateString = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(now)
            val nowMillis = System.currentTimeMillis()
            if (nowMillis - lastGuardianRefreshAt >= 5 * 60_000L) {
                viewModel.refreshGuardianStatus()
                lastGuardianRefreshAt = nowMillis
            }
            kotlinx.coroutines.delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Action Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mode selector button (PIN protected)
                TextButton(
                    onClick = {
                        pinError = ""
                        showPinDialogForMode = true
                    },
                    colors = ButtonDefaults.textButtonColors(
                        containerColor = if (isQuietMode) Color.DarkGray else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = when (activeMode) {
                            Mode.HOME -> "🏡 Home"
                            Mode.SCHOOL -> "🎒 School"
                            Mode.SLEEP -> "🌙 Sleep"
                            else -> "🏡 Home"
                        },
                        fontWeight = FontWeight.Bold,
                        color = if (isQuietMode) Color.White else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }

                // Settings Lock Button (PIN protected)
                IconButton(
                    onClick = {
                        pinError = ""
                        showPinDialogForDashboard = true
                    },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isQuietMode) Color.DarkGray else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Parent Dashboard",
                        tint = if (isQuietMode) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time & Date Display
            Text(
                text = timeString,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = primaryTextColor,
                textAlign = TextAlign.Center
            )
            Text(
                text = dateString,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = secondaryTextColor,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            val warnings = remember(guardianStatus) {
                guardianStatus?.let { ChildSafetyWarnings.fromGuardianStatus(it, maxWarnings = 2) }.orEmpty()
            }
            if (warnings.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    warnings.forEach { warning ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = if (isQuietMode) Color(0xFF332222) else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.55f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                text = warning.message,
                                modifier = Modifier.padding(14.dp),
                                color = if (isQuietMode) Color.LightGray else MaterialTheme.colorScheme.onErrorContainer,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Parent-facing setup banner if EasyUI isn't the default launcher yet.
            val launcherNotSet = guardianStatus?.defaultLauncherActive == CheckState.ACTION_REQUIRED
            if (launcherNotSet) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = if (isQuietMode) 0.22f else 0.35f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            "Setup needed: EasyUI is not the default home screen yet.",
                            fontWeight = FontWeight.Black,
                            color = if (isQuietMode) Color.White else MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Text(
                            "Until it’s set, pressing HOME may return to the phone’s normal launcher.",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isQuietMode) Color.LightGray else MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        OutlinedButton(
                            onClick = {
                                pinError = ""
                                showPinDialogForDashboard = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Parent setup", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // First Row: Contact shortcuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Parent contact shortcut tile
                ContactTile(
                    label = when {
                        parentContact.phoneNumber.isNotEmpty() -> parentContact.label.ifEmpty { "Call Parent" }
                        else -> "Set parent contact"
                    },
                    iconColor = Color(0xFF00897B),
                    icon = Icons.Default.Phone,
                    isQuiet = isQuietMode,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (parentContact.phoneNumber.isNotEmpty()) {
                            dialNumber(context, parentContact.phoneNumber)
                        } else {
                            pinError = ""
                            showPinDialogForDashboard = true
                        }
                    }
                )

                // Emergency contact shortcut tile
                if (emergencyContact.enabled) {
                    ContactTile(
                        label = when {
                            emergencyContact.phoneNumber.isNotEmpty() -> emergencyContact.label.ifEmpty { "Emergency" }
                            else -> "Set emergency contact"
                        },
                        iconColor = Color(0xFFD32F2F),
                        icon = Icons.Default.Emergency,
                        isQuiet = isQuietMode,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (emergencyContact.phoneNumber.isNotEmpty()) {
                                dialNumber(context, emergencyContact.phoneNumber)
                            } else {
                                pinError = ""
                                showPinDialogForDashboard = true
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Body: Allowed application launcher icons
            if (childApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (activeMode) {
                            Mode.SLEEP -> "Sleep mode is active.\nOnly calling tiles are available."
                            Mode.SCHOOL -> "No apps have been added to School mode yet.\nAsk your parent to add apps."
                            else -> "No apps have been added to this mode yet.\nAsk your parent to add apps."
                        },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = secondaryTextColor,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    items(childApps) { app ->
                        AppLauncherTile(app = app, isQuiet = isQuietMode) {
                            viewModel.launchApp(context, app.packageName)
                        }
                    }
                }
            }
        }

        // Parent PIN validation Dialogs
        if (showPinDialogForDashboard) {
            PinEntryDialog(
                onDismissRequest = { showPinDialogForDashboard = false },
                onPinSubmitted = { enteredPin ->
                    coroutineScope.launch {
                        if (viewModel.verifyPin(enteredPin)) {
                            showPinDialogForDashboard = false
                            onNavigateToDashboard()
                        } else {
                            pinError = "Incorrect PIN. Please try again."
                        }
                    }
                },
                errorMessage = pinError,
                titleText = "Enter PIN for Parent Dashboard"
            )
        }

        if (showPinDialogForMode) {
            PinEntryDialog(
                onDismissRequest = { showPinDialogForMode = false },
                onPinSubmitted = { enteredPin ->
                    coroutineScope.launch {
                        if (viewModel.verifyPin(enteredPin)) {
                            showPinDialogForMode = false
                            showModeSelector = true
                        } else {
                            pinError = "Incorrect PIN. Please try again."
                        }
                    }
                },
                errorMessage = pinError,
                titleText = "Enter PIN to Switch Modes"
            )
        }

        // Mode Choice dialog (shown after successful PIN validation)
        if (showModeSelector) {
            AlertDialog(
                onDismissRequest = { showModeSelector = false },
                title = { Text("Choose Active Mode", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        ModeSelectItem(
                            title = "🏡 Home Mode",
                            desc = "General learning, games, and camera access.",
                            isSelected = activeMode == Mode.HOME,
                            onClick = {
                                viewModel.updateActiveMode(Mode.HOME)
                                showModeSelector = false
                            }
                        )
                        ModeSelectItem(
                            title = "🎒 School Mode",
                            desc = "Focused learning tools. Games are hidden.",
                            isSelected = activeMode == Mode.SCHOOL,
                            onClick = {
                                viewModel.updateActiveMode(Mode.SCHOOL)
                                showModeSelector = false
                            }
                        )
                        ModeSelectItem(
                            title = "🌙 Sleep Mode",
                            desc = "Rest mode. Hides all app tiles. Only calling tiles remain.",
                            isSelected = activeMode == Mode.SLEEP,
                            onClick = {
                                viewModel.updateActiveMode(Mode.SLEEP)
                                showModeSelector = false
                            }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showModeSelector = false }) {
                        Text("Cancel")
                    }
                },
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
fun ContactTile(
    label: String,
    iconColor: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isQuiet: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = if (isQuiet) Color(0xFF1E1E1E) else MaterialTheme.colorScheme.surface),
        modifier = modifier
            .height(90.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(iconColor.copy(alpha = if (isQuiet) 0.3f else 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isQuiet) iconColor.copy(alpha = 0.8f) else iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = if (isQuiet) Color.White else Color.Unspecified
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppLauncherTile(
    app: AllowedApp,
    isQuiet: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = {} // Block editing gesture from child
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App icon container using standard platform views
        Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = if (isQuiet) Color(0xFF1E1E1E) else MaterialTheme.colorScheme.surface),
            modifier = Modifier.size(64.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isQuiet) 0.dp else 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(packageName = app.packageName, isQuiet = isQuiet)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = app.displayLabel,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (isQuiet) Color.White else MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun AppIcon(packageName: String, isQuiet: Boolean = false, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val iconDrawable = remember(packageName) {
        try {
            context.packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            null
        }
    }

    if (iconDrawable != null) {
        AndroidView(
            factory = { ctx ->
                android.widget.ImageView(ctx).apply {
                    setImageDrawable(iconDrawable)
                    scaleType = android.widget.ImageView.ScaleType.FIT_CENTER
                    if (isQuiet) {
                        // Apply a desaturation filter for quiet modes
                        val matrix = android.graphics.ColorMatrix().apply { setSaturation(0.2f) }
                        colorFilter = android.graphics.ColorMatrixColorFilter(matrix)
                    }
                }
            },
            modifier = modifier.fillMaxSize()
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(if (isQuiet) Color.DarkGray else MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = packageName.take(1).uppercase(Locale.getDefault()),
                fontWeight = FontWeight.Bold,
                color = if (isQuiet) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 18.sp
            )
        }
    }
}



private fun dialNumber(context: Context, phoneNumber: String) {
    try {
        // Dial intent is compliant: it opens the system dialer instead of invoking CALL_PHONE permission directly
        val intent = Intent(Intent.ACTION_DIAL).apply {
            data = Uri.parse("tel:$phoneNumber")
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Safe check
    }
}
