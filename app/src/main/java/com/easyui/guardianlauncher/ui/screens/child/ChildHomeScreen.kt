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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.easyui.guardianlauncher.data.AllowedApp
import com.easyui.guardianlauncher.data.Mode
import com.easyui.guardianlauncher.ui.components.PinEntryDialog
import com.easyui.guardianlauncher.ui.components.ModeSelectItem
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

    // UI Local States
    var showPinDialogForDashboard by remember { mutableStateOf(false) }
    var showPinDialogForMode by remember { mutableStateOf(false) }
    var showModeSelector by remember { mutableStateOf(false) }
    var pinError by remember { mutableStateOf("") }
    
    // Time/date updater
    var timeString by remember { mutableStateOf("") }
    var dateString by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.scanInstalledApps(context)
        while (true) {
            val now = Calendar.getInstance().time
            timeString = SimpleDateFormat("h:mm a", Locale.getDefault()).format(now)
            dateString = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(now)
            kotlinx.coroutines.delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
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
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = when (activeMode) {
                            Mode.HOME -> "🏡 Home Mode"
                            Mode.SCHOOL -> "🎒 School Mode"
                            Mode.SLEEP -> "🌙 Sleep Mode"
                        },
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
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
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Parent Dashboard",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Time & Date Display
            Text(
                text = timeString,
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
            Text(
                text = dateString,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // First Row: Contact shortcuts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Parent contact shortcut tile
                if (parentContact.phoneNumber.isNotEmpty()) {
                    ContactTile(
                        label = parentContact.label.ifEmpty { "Call Parent" },
                        iconColor = Color(0xFF00897B),
                        icon = Icons.Default.Phone,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            dialNumber(context, parentContact.phoneNumber)
                        }
                    )
                }

                // Emergency contact shortcut tile
                if (emergencyContact.enabled && emergencyContact.phoneNumber.isNotEmpty()) {
                    ContactTile(
                        label = emergencyContact.label.ifEmpty { "Emergency" },
                        iconColor = Color(0xFFD32F2F),
                        icon = Icons.Default.Emergency,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            dialNumber(context, emergencyContact.phoneNumber)
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
                        text = "No apps have been added to this mode yet.\nAsk your parent to add apps.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.outline,
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
                        AppLauncherTile(app = app) {
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
                            desc = "Quiet time. Only contact dials are visible.",
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
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    .background(iconColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor,
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
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppLauncherTile(
    app: AllowedApp,
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
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.size(64.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                AppIcon(packageName = app.packageName)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = app.displayLabel,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}

@Composable
fun AppIcon(packageName: String, modifier: Modifier = Modifier) {
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
                }
            },
            modifier = modifier.fillMaxSize()
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = packageName.take(1).uppercase(Locale.getDefault()),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
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
