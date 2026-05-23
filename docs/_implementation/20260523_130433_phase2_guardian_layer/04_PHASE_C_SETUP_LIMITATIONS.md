# Phase C — Setup and Limitations

## Goal
Add a parent-facing screen: **“What EasyUI Can and Cannot Control”** to build trust and prevent false expectations.

## Implementation

### New screen
- `app/src/main/java/com/easyui/guardianlauncher/ui/screens/parent/SetupLimitationsScreen.kt`

Content includes:
- “EasyUI can help with” list (child home, approved tiles, modes, contact tiles, PIN, layout lock, setup checks)
- “EasyUI cannot fully control by itself” list (Settings, notification shade/quick settings, recents behavior on every device, Play Store controls, uninstall protection, web filtering, apps opened outside launcher)
- Explicit text avoiding “full lockdown” claims and recommending Android settings / Google Family Link for deeper restrictions.

### Navigation / access point
- Route added: `Routes.SETUP_LIMITATIONS` in `app/src/main/java/com/easyui/guardianlauncher/ui/navigation/NavGraph.kt`
- Entry point: Setup Help tab in Parent Dashboard (“Open details” button)
- Parent dashboard signature updated to accept `onOpenSetupLimitations` callback.

## Tests
- No new tests added in this phase.
  - Reason: Compose UI tests are currently ADB/device-based under `androidTest/` and are deferred by sprint rules; JVM-only Compose UI testing (Robolectric) is not configured in this repo.

## Commands run (non-ADB)
- `./gradlew assembleDebug` ✅
- `./gradlew testDebugUnitTest` ✅

## Known limitations
- The settings intents used to open default launcher settings are “best effort” and may vary by OEM.
- The limitations screen is informational (no device-owner / kiosk / lockdown features were added, by design).

