package com.easyui.guardianlauncher.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.easyui.guardianlauncher.ui.screens.child.ChildHomeScreen
import com.easyui.guardianlauncher.ui.screens.onboarding.OnboardingScreen
import com.easyui.guardianlauncher.ui.screens.parent.ParentDashboardScreen
import com.easyui.guardianlauncher.ui.screens.parent.ReadyToHandoverScreen
import com.easyui.guardianlauncher.ui.screens.parent.SetupHealthScreen
import com.easyui.guardianlauncher.ui.screens.parent.SetupLimitationsScreen
import com.easyui.guardianlauncher.ui.viewmodels.LauncherViewModel

object Routes {
    const val ONBOARDING = "onboarding"
    const val CHILD_HOME = "child_home"
    const val PARENT_DASHBOARD = "parent_dashboard"
    const val SETUP_HEALTH = "setup_health"
    const val READY_HANDOVER = "ready_handover"
    const val SETUP_LIMITATIONS = "setup_limitations"
}

@Composable
fun NavigationGraph(
    navController: NavHostController,
    startDestination: String,
    viewModel: LauncherViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                viewModel = viewModel,
                onComplete = {
                    viewModel.setOnboardingCompleted(true)
                    navController.navigate(Routes.CHILD_HOME) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }
        composable(Routes.CHILD_HOME) {
            ChildHomeScreen(
                viewModel = viewModel,
                onNavigateToDashboard = {
                    navController.navigate(Routes.PARENT_DASHBOARD)
                }
            )
        }
        composable(Routes.PARENT_DASHBOARD) {
            ParentDashboardScreen(
                viewModel = viewModel,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onOpenSetupHealth = { navController.navigate(Routes.SETUP_HEALTH) },
                onOpenReadyChecklist = { navController.navigate(Routes.READY_HANDOVER) },
                onOpenSetupLimitations = {
                    navController.navigate(Routes.SETUP_LIMITATIONS)
                },
            )
        }

        composable(Routes.SETUP_HEALTH) {
            SetupHealthScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.READY_HANDOVER) {
            ReadyToHandoverScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SETUP_LIMITATIONS) {
            SetupLimitationsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
