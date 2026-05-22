package com.easyui.guardianlauncher.ui.screens.onboarding

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class OnboardingScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun welcomeStep_showsContentAndTriggersOnNext() {
        var clickedNext = false

        composeTestRule.setContent {
            WelcomeStep(onNext = { clickedNext = true })
        }

        // Verify title is visible
        composeTestRule
            .onNodeWithText("Welcome to Guardian Launcher")
            .assertExists()

        // Verify important system notice is visible
        composeTestRule
            .onNodeWithText("Important System Notice")
            .assertExists()

        // Verify get started button is visible and click it
        composeTestRule
            .onNodeWithText("Get Started")
            .assertExists()
            .performClick()

        // Verify action callback triggered
        assertTrue(clickedNext)
    }
}
