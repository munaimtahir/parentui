# Phase D — Child Safety Warnings

## Goal
Add gentle, child-facing warnings on the child home screen without blocking app launching or exposing technical details.

## Warnings implemented
At most 2 compact cards can appear below the date:
1. Low battery: “Battery is low. Please charge the phone.”
2. Emergency contact missing: “Emergency contact is not set yet. Ask your parent to add it.”
3. Setup incomplete (fallback): “Ask your parent to finish EasyUI setup.”

Priority order:
- Low battery first, then emergency contact missing, then setup incomplete (up to 2 total).

## Implementation details

### Warning selection logic (pure, unit-testable)
- `app/src/main/java/com/easyui/guardianlauncher/guardian/child/ChildSafetyWarning.kt`
- `app/src/main/java/com/easyui/guardianlauncher/guardian/child/ChildSafetyWarnings.kt`

Inputs:
- Uses `GuardianCheckStatus` from the Phase A engine.

### Child home UI integration
- `app/src/main/java/com/easyui/guardianlauncher/ui/screens/child/ChildHomeScreen.kt`

Behavior:
- Calls `viewModel.refreshGuardianStatus()` on entry and then every ~5 minutes while the screen is active (no background service).
- Displays warning cards only when Guardian status has been computed and warnings exist.
- Does not change onboarding, PIN gating, or app launching behavior.

## Tests (JVM, non-ADB)
- `app/src/test/java/com/easyui/guardianlauncher/guardian/child/ChildSafetyWarningsTest.kt`
  - low battery warning generated
  - normal battery generates no warning
  - emergency missing generates warning
  - maximum warnings capped at 2

## Commands run (non-ADB)
- `./gradlew assembleDebug` ✅
- `./gradlew testDebugUnitTest` ✅

## Known limitations
- Warning accuracy depends on when Guardian status is refreshed (no background monitoring by design).
- Emergency-contact warning uses the same “enabled + phone number present” definition as Guardian Checks.

