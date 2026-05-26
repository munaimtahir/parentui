package com.easyui.guardianlauncher.ui.screens.onboarding

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easyui.guardianlauncher.ui.components.OnboardingStepScaffold
import com.easyui.guardianlauncher.ui.components.DefaultLauncherStatusCard
import com.easyui.guardianlauncher.ui.components.PinKeypad
import com.easyui.guardianlauncher.ui.components.OnResumeEffect
import com.easyui.guardianlauncher.ui.viewmodels.LauncherViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OnboardingScreen(
    viewModel: LauncherViewModel,
    onComplete: () -> Unit
) {
    val context = LocalContext.current
    var currentStep by remember { mutableStateOf(1) }
    val guardianStatus by viewModel.guardianStatus.collectAsState()

    // Initialize/scan apps
    LaunchedEffect(Unit) {
        viewModel.scanInstalledApps(context)
        viewModel.refreshGuardianStatus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .semantics {
                testTagsAsResourceId = true
            }
    ) {
        when (currentStep) {
            1 -> WelcomeStep(onNext = { currentStep = 2 })
            2 -> PinSetupStep(
                onPinCreated = { pin ->
                    viewModel.setParentPin(pin)
                    currentStep = 3
                }
            )
            3 -> ContactSetupStep(
                onContactsSaved = { pName, pPhone, eName, ePhone, eEnabled ->
                    viewModel.updateParentContact(pName, pPhone)
                    viewModel.updateEmergencyContact(eName, ePhone, eEnabled)
                    currentStep = 4
                },
                onBack = { currentStep = 2 }
            )
            4 -> AppSelectionStep(
                viewModel = viewModel,
                onNext = { currentStep = 5 },
                onBack = { currentStep = 3 }
            )
            5 -> CompletionStep(
                onOpenSettings = {
                    try {
                        val intent = Intent(Settings.ACTION_HOME_SETTINGS)
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        try {
                            val intent = Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS)
                            context.startActivity(intent)
                        } catch (ex: Exception) {
                            // Fallback to normal settings
                            val intent = Intent(Settings.ACTION_SETTINGS)
                            context.startActivity(intent)
                        }
                    }
                },
                defaultLauncherState = guardianStatus?.defaultLauncherActive,
                onCheckAgain = { viewModel.refreshGuardianStatus() },
                onFinish = onComplete,
                onBack = { currentStep = 4 }
            )
        }
    }
}

@Composable
fun WelcomeStep(onNext: () -> Unit) {
    OnboardingStepScaffold(
        modifier = Modifier.testTag("onboarding_welcome_screen"),
        title = "Welcome",
        progress = 0.2f,
        primaryButtonText = "Get Started",
        onPrimaryClick = onNext
    ) {
        Text(
            text = "Welcome to Guardian Launcher",
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Let your child use a phone without giving them the whole smartphone experience at once.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Limitations Callout Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Important System Notice",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "This app changes the home screen layout but does not fully lock down Android system files. Depending on the device model, kids may access settings or notifications. For comprehensive device restrictions, we highly recommend setting up Google Family Link in combination with this launcher.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
    }
}

@Composable
fun PinSetupStep(onPinCreated: (String) -> Unit) {
    var stage by remember { mutableStateOf(1) } // 1: Enter, 2: Confirm
    var firstPin by remember { mutableStateOf("") }
    var enteredPin by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    OnboardingStepScaffold(
        modifier = Modifier.testTag(if (stage == 1) "onboarding_pin_screen" else "onboarding_pin_confirm_screen"),
        title = if (stage == 1) "Create Parent PIN" else "Confirm Parent PIN",
        progress = if (stage == 1) 0.4f else 0.5f,
        onBackClick = if (stage == 2) { { stage = 1; enteredPin = ""; errorMsg = "" } } else null,
        primaryButtonText = "Continue",
        primaryEnabled = enteredPin.length >= 4,
        onPrimaryClick = {
            if (enteredPin.length < 4) {
                errorMsg = "PIN must be at least 4 digits."
            } else {
                if (stage == 1) {
                    firstPin = enteredPin
                    enteredPin = ""
                    errorMsg = ""
                    stage = 2
                } else {
                    if (enteredPin == firstPin) {
                        onPinCreated(enteredPin)
                    } else {
                        errorMsg = "PINs do not match. Restarting."
                        enteredPin = ""
                        stage = 1
                    }
                }
            }
        }
    ) {
        Text(
            text = if (stage == 1) {
                "This PIN is used to access settings and switch modes. Choose a 4-to-6 digit code."
            } else {
                "Re-enter the PIN code to confirm accuracy."
            },
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Dots Indicators
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.testTag("pin_dots")
        ) {
            repeat(6) { index ->
                val isFilled = index < enteredPin.length
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(androidx.compose.foundation.shape.CircleShape)
                        .background(
                            if (isFilled) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outlineVariant
                        )
                )
            }
        }

        if (errorMsg.isNotEmpty()) {
            Text(
                text = errorMsg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }

        // Security disclaimer
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Note: There is no cloud reset. If you lose this PIN, you must clear local application storage inside Android settings.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(10.dp),
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        PinKeypad(
            onDigitClick = { digit ->
                if (enteredPin.length < 6) {
                    enteredPin += digit
                }
            },
            onDeleteClick = {
                if (enteredPin.isNotEmpty()) {
                    enteredPin = enteredPin.dropLast(1)
                }
            },
            onClearClick = {
                enteredPin = ""
            },
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .testTag("pin_keypad")
        )
    }
}

@Composable
fun ContactSetupStep(
    onContactsSaved: (String, String, String, String, Boolean) -> Unit,
    onBack: () -> Unit
) {
    var parentName by remember { mutableStateOf("") }
    var parentPhone by remember { mutableStateOf("") }
    var emergencyName by remember { mutableStateOf("Emergency Services") }
    var emergencyPhone by remember { mutableStateOf("911") }
    var emergencyEnabled by remember { mutableStateOf(true) }

    var pNameError by remember { mutableStateOf(false) }
    var pPhoneError by remember { mutableStateOf(false) }
    var ePhoneError by remember { mutableStateOf(false) }

    OnboardingStepScaffold(
        modifier = Modifier.testTag("onboarding_contacts_screen"),
        title = "Contact Shortcuts Setup",
        progress = 0.6f,
        onBackClick = onBack,
        primaryButtonText = "Save & Next",
        onPrimaryClick = {
            val nameEmpty = parentName.trim().isEmpty()
            val phoneEmpty = parentPhone.trim().isEmpty()
            val emerEmpty = emergencyEnabled && emergencyPhone.trim().isEmpty()

            pNameError = nameEmpty
            pPhoneError = phoneEmpty
            ePhoneError = emerEmpty

            if (!nameEmpty && !phoneEmpty && !emerEmpty) {
                onContactsSaved(
                    parentName.trim(),
                    parentPhone.trim(),
                    emergencyName.trim(),
                    emergencyPhone.trim(),
                    emergencyEnabled
                )
            }
        }
    ) {
        Text(
            text = "Configure parent contact and emergency dial numbers. Numbers are stored locally for dialing shortcuts.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Parent/Guardian Contact",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = parentName,
            onValueChange = {
                parentName = it
                pNameError = it.isEmpty()
            },
            label = { Text("Parent Name (e.g. Mom, Dad)") },
            isError = pNameError,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("parent_name_input"),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = parentPhone,
            onValueChange = {
                parentPhone = it
                pPhoneError = it.isEmpty()
            },
            label = { Text("Parent Phone Number") },
            isError = pPhoneError,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("parent_phone_input"),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Emergency Contact Tile",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.secondary
            )
            Switch(
                checked = emergencyEnabled,
                onCheckedChange = { emergencyEnabled = it }
            )
        }

        AnimatedVisibility(visible = emergencyEnabled) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = emergencyName,
                    onValueChange = { emergencyName = it },
                    label = { Text("Emergency Label (e.g. Police, 911)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("emergency_name_input"),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = emergencyPhone,
                    onValueChange = {
                        emergencyPhone = it
                        ePhoneError = it.isEmpty()
                    },
                    label = { Text("Emergency Number") },
                    isError = ePhoneError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("emergency_phone_input"),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
fun AppSelectionStep(
    viewModel: LauncherViewModel,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val installedApps by viewModel.installedApps.collectAsState()
    val allowedApps by viewModel.allowedApps.collectAsState()

    OnboardingStepScaffold(
        modifier = Modifier.testTag("onboarding_app_selection_screen"),
        title = "Approve Installed Apps",
        progress = 0.8f,
        onBackClick = onBack,
        primaryButtonText = "Continue",
        onPrimaryClick = onNext,
        scrollable = false
    ) {
        Text(
            text = "Select apps that are safe for your child. These will be added to Home Mode. Unapproved apps remain hidden from child view.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (installedApps.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .testTag("app_selection_list"),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(installedApps) { app ->
                    val isAllowed = app.packageName in allowedApps
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isAllowed) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            )
                            .clickable { viewModel.toggleAllowedApp(app.packageName) }
                            .padding(12.dp)
                            .testTag("app_selection_row"),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = app.displayLabel,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge
                            )
                            Text(
                                text = app.packageName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Checkbox(
                            checked = isAllowed,
                            onCheckedChange = { viewModel.toggleAllowedApp(app.packageName) },
                            modifier = Modifier.testTag("app_selection_checkbox")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CompletionStep(
    onOpenSettings: () -> Unit,
    defaultLauncherState: com.easyui.guardianlauncher.guardian.CheckState?,
    onCheckAgain: () -> Unit,
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    OnResumeEffect { onCheckAgain() }

    OnboardingStepScaffold(
        modifier = Modifier.testTag("onboarding_completion_screen"),
        title = "Setup Completed!",
        progress = 1.0f,
        onBackClick = onBack,
        primaryButtonText = "Finish & Launch",
        onPrimaryClick = onFinish
    ) {
        Text(
            text = "You are now ready to activate the launcher.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))

        DefaultLauncherStatusCard(
            state = defaultLauncherState,
            onOpenHomeSettings = onOpenSettings,
            onCheckAgain = onCheckAgain,
        )

        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Next: Tap “Finish & Launch”. If HOME still returns to your old launcher, return here and use “Set default home”.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )
    }
}
