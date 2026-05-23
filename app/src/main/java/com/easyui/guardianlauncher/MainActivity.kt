package com.easyui.guardianlauncher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.easyui.guardianlauncher.data.SettingsRepository
import com.easyui.guardianlauncher.guardian.GuardianStatusServiceImpl
import com.easyui.guardianlauncher.guardian.clock.SystemClock
import com.easyui.guardianlauncher.guardian.datasource.SettingsGuardianDataSource
import com.easyui.guardianlauncher.guardian.providers.android.AndroidBatteryStatusProvider
import com.easyui.guardianlauncher.guardian.providers.android.AndroidLauncherStatusProvider
import com.easyui.guardianlauncher.guardian.providers.android.AndroidNetworkStatusProvider
import com.easyui.guardianlauncher.ui.navigation.NavigationGraph
import com.easyui.guardianlauncher.ui.navigation.Routes
import com.easyui.guardianlauncher.ui.theme.GuardianLauncherTheme
import com.easyui.guardianlauncher.ui.viewmodels.LauncherViewModel
import kotlinx.coroutines.flow.first

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val settingsRepository = SettingsRepository(applicationContext)
        val guardianStatusService = GuardianStatusServiceImpl(
            dataSource = SettingsGuardianDataSource(settingsRepository),
            batteryStatusProvider = AndroidBatteryStatusProvider(applicationContext),
            networkStatusProvider = AndroidNetworkStatusProvider(applicationContext),
            launcherStatusProvider = AndroidLauncherStatusProvider(applicationContext),
            clock = SystemClock(),
        )
        val viewModel = LauncherViewModel(
            repository = settingsRepository,
            guardianStatusService = guardianStatusService,
        )

        setContent {
            val activeMode by viewModel.activeMode.collectAsState()
            var startDestination by remember { mutableStateOf<String?>(null) }

            // Determine starting screen based on local onboarding status
            LaunchedEffect(Unit) {
                val completed = settingsRepository.onboardingCompleted.first()
                startDestination = if (completed) Routes.CHILD_HOME else Routes.ONBOARDING
            }

            GuardianLauncherTheme(mode = activeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (startDestination == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        val navController = rememberNavController()
                        NavigationGraph(
                            navController = navController,
                            startDestination = startDestination!!,
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}
