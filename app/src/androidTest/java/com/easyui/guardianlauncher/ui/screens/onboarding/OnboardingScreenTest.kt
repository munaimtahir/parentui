package com.easyui.guardianlauncher.ui.screens.onboarding

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import org.junit.Assert.assertEquals
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
            .onNodeWithTag("onboarding_primary_button")
            .assertExists()
            .performClick()

        // Verify action callback triggered
        assertTrue(clickedNext)
    }

    @Test
    fun pinSetupStep_showsAllDigitsAndConfirmFlow() {
        var createdPin = ""
        composeTestRule.setContent {
            PinSetupStep(onPinCreated = { createdPin = it })
        }

        // Verify stage 1 title
        composeTestRule.onNodeWithText("Create Parent PIN").assertExists()

        // Verify digits 0-9 exist by tag and click them
        for (i in 0..9) {
            composeTestRule.onNodeWithTag("pin_digit_$i").assertExists()
        }
        composeTestRule.onNodeWithTag("pin_backspace").assertExists()

        // Tap 1, 2, 3, 4
        composeTestRule.onNodeWithTag("pin_digit_1").performClick()
        composeTestRule.onNodeWithTag("pin_digit_2").performClick()
        composeTestRule.onNodeWithTag("pin_digit_3").performClick()
        composeTestRule.onNodeWithTag("pin_digit_4").performClick()

        // Continue should be enabled
        composeTestRule.onNodeWithTag("onboarding_primary_button").assertExists().assertIsEnabled().performClick()

        // Now in stage 2 (Confirm)
        composeTestRule.onNodeWithText("Confirm Parent PIN").assertExists()

        // Tap 1, 2, 3, 4
        composeTestRule.onNodeWithTag("pin_digit_1").performClick()
        composeTestRule.onNodeWithTag("pin_digit_2").performClick()
        composeTestRule.onNodeWithTag("pin_digit_3").performClick()
        composeTestRule.onNodeWithTag("pin_digit_4").performClick()

        // Continue should be enabled
        composeTestRule.onNodeWithTag("onboarding_primary_button").assertExists().assertIsEnabled().performClick()

        // PIN should be created
        assertEquals("1234", createdPin)
    }

    @Test
    fun contactSetupStep_showsFieldsAndTriggersSaved() {
        var savedName = ""
        var savedPhone = ""
        var savedEmerPhone = ""

        composeTestRule.setContent {
            ContactSetupStep(
                onContactsSaved = { pName, pPhone, _, ePhone, _ ->
                    savedName = pName
                    savedPhone = pPhone
                    savedEmerPhone = ePhone
                },
                onBack = {}
            )
        }

        // Verify fields exist
        composeTestRule.onNodeWithTag("parent_name_input").assertExists()
        composeTestRule.onNodeWithTag("parent_phone_input").assertExists()
        composeTestRule.onNodeWithTag("emergency_name_input").assertExists()
        composeTestRule.onNodeWithTag("emergency_phone_input").assertExists()

        // Input data
        composeTestRule.onNodeWithTag("parent_name_input").performTextInput("Dad")
        composeTestRule.onNodeWithTag("parent_phone_input").performTextInput("9876543210")
        
        // Tap Save & Next
        composeTestRule.onNodeWithTag("onboarding_primary_button").performClick()

        // Verify data saved
        assertEquals("Dad", savedName)
        assertEquals("9876543210", savedPhone)
        assertEquals("911", savedEmerPhone)
    }

    @Test
    fun completionStep_showsContentAndTriggersFinish() {
        var clickedFinish = false
        composeTestRule.setContent {
            CompletionStep(
                onOpenSettings = {},
                defaultLauncherState = null,
                onCheckAgain = {},
                onFinish = { clickedFinish = true },
                onBack = {}
            )
        }

        // Verify finish button exists and click
        composeTestRule.onNodeWithTag("onboarding_primary_button").assertExists().performClick()

        assertTrue(clickedFinish)
    }
}
