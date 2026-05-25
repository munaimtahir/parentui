package com.easyui.guardianlauncher.ui.screens.parent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.easyui.guardianlauncher.guardian.CheckState
import com.easyui.guardianlauncher.ui.components.OnResumeEffect
import com.easyui.guardianlauncher.ui.viewmodels.LauncherViewModel

enum class ReadinessStatus {
    READY,
    ALMOST_READY,
    NEEDS_SETUP,
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReadyToHandoverScreen(
    viewModel: LauncherViewModel,
    onNavigateBack: () -> Unit,
) {
    val guardianStatus by viewModel.guardianStatus.collectAsState()
    val setupChecklist by viewModel.setupChecklist.collectAsState()
    val limitationsAck by viewModel.limitationsAcknowledged.collectAsState()

    OnResumeEffect { viewModel.refreshGuardianStatus() }

    val missingRequired = setupChecklist.items.count { !it.isCompleted } + (if (limitationsAck) 0 else 1)
    val readiness = when {
        missingRequired == 0 -> ReadinessStatus.READY
        missingRequired <= 2 -> ReadinessStatus.ALMOST_READY
        else -> ReadinessStatus.NEEDS_SETUP
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ready to hand over?", fontWeight = FontWeight.Bold) },
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
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                ReadinessBanner(
                    readiness = readiness,
                    missingCount = missingRequired,
                )
            }

            item {
                Text(
                    "This checklist helps reduce accidental escapes and confusion. It does not guarantee full Android lockdown.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { viewModel.refreshGuardianStatus() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Refresh", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onNavigateBack,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back", fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                StatusRow(
                    title = "Default Home app",
                    state = guardianStatus?.defaultLauncherActive ?: CheckState.UNKNOWN,
                    description = "Until EasyUI is set as the default home app, pressing HOME may return to the normal launcher.",
                )
            }

            item {
                StatusRow(
                    title = "Limitations acknowledged",
                    state = if (limitationsAck) CheckState.OK else CheckState.ACTION_REQUIRED,
                    description = "Confirm you understand what EasyUI can and cannot control.",
                )
            }

            items(setupChecklist.items) { item ->
                StatusRow(
                    title = item.title,
                    state = if (item.isCompleted) CheckState.OK else CheckState.ACTION_REQUIRED,
                    description = item.description,
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun ReadinessBanner(
    readiness: ReadinessStatus,
    missingCount: Int,
) {
    val (icon, tint, title, msg) = when (readiness) {
        ReadinessStatus.READY -> Quad(
            Icons.Default.CheckCircle,
            MaterialTheme.colorScheme.primary,
            "Ready",
            "EasyUI looks ready for a supervised handover.",
        )
        ReadinessStatus.ALMOST_READY -> Quad(
            Icons.Default.Info,
            MaterialTheme.colorScheme.tertiary,
            "Almost ready",
            "A couple of setup items still need attention.",
        )
        ReadinessStatus.NEEDS_SETUP -> Quad(
            Icons.Default.Error,
            MaterialTheme.colorScheme.error,
            "Needs setup",
            "Finish the setup items before handing the phone over.",
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(imageVector = icon, contentDescription = null, tint = tint)
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Black)
                    Text("$missingCount item(s) remaining", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(msg, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun StatusRow(
    title: String,
    state: CheckState,
    description: String,
) {
    val (icon, tint, label) = when (state) {
        CheckState.OK -> Triple(Icons.Default.CheckCircle, MaterialTheme.colorScheme.primary, "OK")
        CheckState.WARNING -> Triple(Icons.Default.Info, MaterialTheme.colorScheme.tertiary, "Warning")
        CheckState.ACTION_REQUIRED -> Triple(Icons.Default.Error, MaterialTheme.colorScheme.error, "Needs setup")
        CheckState.UNKNOWN -> Triple(Icons.Default.Info, MaterialTheme.colorScheme.outline, "Unknown")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(imageVector = icon, contentDescription = null, tint = tint)
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Black)
                    Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

