package com.easyui.guardianlauncher

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class LauncherRuntimeSmokeTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchesAndShowsOnboardingOrHome() {
        val onboarding = hasTestTag("onboarding_welcome_screen")
        val home = hasTestTag("child_home_screen")

        composeRule.waitUntil(timeoutMillis = 25_000) {
            composeRule.onAllNodes(onboarding).fetchSemanticsNodes().isNotEmpty() ||
                composeRule.onAllNodes(home).fetchSemanticsNodes().isNotEmpty()
        }
    }
}

