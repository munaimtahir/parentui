package com.easyui.guardianlauncher.ui.screens.parent

import android.content.Intent
import android.provider.Settings
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.easyui.guardianlauncher.guardian.CheckState
import com.easyui.guardianlauncher.ui.components.DefaultLauncherStatusCard
import com.easyui.guardianlauncher.ui.components.OnResumeEffect
import com.easyui.guardianlauncher.ui.viewmodels.LauncherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupHealthScreen(
    viewModel: LauncherViewModel,
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val guardianStatus by viewModel.guardianStatus.collectAsState()
    val setupChecklist by viewModel.setupChecklist.collectAsState()
    val limitationsAck by viewModel.limitationsAcknowledged.collectAsState()

    OnResumeEffect { viewModel.refreshGuardianStatus() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup Health", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "Quick checks to confirm EasyUI is ready for handover. EasyUI is a safer launcher surface (not full device lockdown).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            item {
                DefaultLauncherStatusCard(
                    state = guardianStatus?.defaultLauncherActive ?: CheckState.UNKNOWN,
                    onOpenHomeSettings = {
                        try {
                            context.startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
                        } catch (e: Exception) {
                            try {
                                context.startActivity(Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS))
                            } catch (ex: Exception) {
                                context.startActivity(Intent(Settings.ACTION_SETTINGS))
                            }
                        }
                    },
                    onCheckAgain = { viewModel.refreshGuardianStatus() },
                )
            }

            item {
                val state = if (limitationsAck) CheckState.OK else CheckState.ACTION_REQUIRED
                StatusCard(
                    title = "Limitations acknowledged",
                    state = state,
                    okText = "You’ve acknowledged EasyUI limitations and understand HOME behavior depends on Android settings.",
                    actionText = "Review limitations",
                )
            }

            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { viewModel.refreshGuardianStatus() },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Refresh checks", fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = onNavigateBack,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back to dashboard", fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Text("Checklist", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Black)
            }

            items(setupChecklist.items) { item ->
                val state = if (item.isCompleted) CheckState.OK else CheckState.ACTION_REQUIRED
                StatusCard(
                    title = item.title,
                    state = state,
                    okText = item.description,
                )
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
        }
    }
}

@Composable
private fun StatusCard(
    title: String,
    state: CheckState,
    okText: String,
    actionText: String? = null,
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
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Icon(imageVector = icon, contentDescription = null, tint = tint)
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Black)
                    Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Text(okText, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (actionText != null) {
                Text(actionText, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

