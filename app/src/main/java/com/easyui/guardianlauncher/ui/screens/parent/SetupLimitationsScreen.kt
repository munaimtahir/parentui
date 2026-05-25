package com.easyui.guardianlauncher.ui.screens.parent

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.easyui.guardianlauncher.ui.components.DefaultLauncherStatusCard
import com.easyui.guardianlauncher.ui.components.OnResumeEffect
import com.easyui.guardianlauncher.ui.viewmodels.LauncherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupLimitationsScreen(
    viewModel: LauncherViewModel,
    onNavigateBack: () -> Unit,
) {
    val guardianStatus by viewModel.guardianStatus.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    OnResumeEffect { viewModel.refreshGuardianStatus() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Setup & Limitations", fontWeight = FontWeight.Bold) },
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
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                "What EasyUI Can and Cannot Control",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black
            )
            Text(
                "Use EasyUI for a simpler child home screen. For deeper device restrictions, use Android settings or Google Family Link.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            DefaultLauncherStatusCard(
                state = guardianStatus?.defaultLauncherActive,
                onOpenHomeSettings = {
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
                onCheckAgain = { viewModel.refreshGuardianStatus() }
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("EasyUI can help with:", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    Bullet("Child home screen")
                    Bullet("Parent-approved app tiles")
                    Bullet("Home / School / Sleep modes")
                    Bullet("Parent call tile")
                    Bullet("Emergency contact tile")
                    Bullet("Parent PIN for EasyUI settings")
                    Bullet("Layout lock inside EasyUI")
                    Bullet("Setup health checks")
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "EasyUI cannot fully control by itself:",
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.error
                    )
                    Bullet("Android Settings")
                    Bullet("Notification shade and quick settings")
                    Bullet("Recent apps behavior on every device")
                    Bullet("Play Store installation controls")
                    Bullet("Full uninstall protection")
                    Bullet("Full web filtering")
                    Bullet("Apps opened outside the launcher")

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "This is not full lockdown. EasyUI is designed as an offline-first, child-friendly launcher layer.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                "Guide: Setting the Default Launcher",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "To ensure your child stays inside EasyUI:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Bullet("Open Android Settings.")
                    Bullet("Search for 'Default apps' or 'Home app'.")
                    Bullet("Select 'EasyUI Guardian Launcher' as the Home app.")
                    Bullet("Test by pressing the Home button; it should now return to EasyUI.")
                }
            }

            Text(
                "Guide: Google Family Link",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "EasyUI and Family Link work great together:",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Bullet("Use Family Link to block app installs from Play Store.")
                    Bullet("Set overall screen time limits in Family Link.")
                    Bullet("Use Family Link web filtering for Chrome.")
                    Bullet("Use EasyUI for the simplified, mode-based home screen experience.")
                }
            }
        }
    }
}

@Composable
private fun Bullet(text: String) {
    Text("• $text", style = MaterialTheme.typography.bodyMedium)
}
