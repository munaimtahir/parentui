# Phase A — Guardian Status Engine

## Goal
Introduce a core “Guardian Checks” status engine that can compute setup/safety state without requiring ADB or device-only tests.

## What was added

### New model + service
- `app/src/main/java/com/easyui/guardianlauncher/guardian/CheckState.kt`
- `app/src/main/java/com/easyui/guardianlauncher/guardian/GuardianCheckStatus.kt`
- `app/src/main/java/com/easyui/guardianlauncher/guardian/GuardianStatusService.kt`
- `app/src/main/java/com/easyui/guardianlauncher/guardian/GuardianStatusServiceImpl.kt`

Status fields implemented:
- Default launcher active (`CheckState`)
- Battery level percent + low-battery boolean (default threshold 20%)
- Network connected / disconnected (nullable; unknown supported)
- Offline duration minutes (computed when offline and last-online timestamp is known)
- Parent contact configured (phone number present)
- Emergency contact configured (enabled + phone number present)
- PIN configured (PIN hash present)
- Layout lock enabled (new local boolean setting; default `true`)
- Current mode (`Mode`)
- Last checked timestamp (`lastCheckedAtMillis`)

Convenience summary helpers:
- `GuardianCheckStatus.hasWarnings`
- `GuardianCheckStatus.needsSetupCount`
- `GuardianCheckStatus.warningCount`

### Data source abstraction (test-friendly)
To avoid Android/DataStore in JVM unit tests, Guardian status reads settings via a small interface:
- `app/src/main/java/com/easyui/guardianlauncher/guardian/datasource/GuardianDataSource.kt`
- `app/src/main/java/com/easyui/guardianlauncher/guardian/datasource/SettingsGuardianDataSource.kt`

### System provider interfaces + Android implementations
Providers keep Android system APIs out of core logic:
- `app/src/main/java/com/easyui/guardianlauncher/guardian/providers/BatteryStatusProvider.kt`
- `app/src/main/java/com/easyui/guardianlauncher/guardian/providers/NetworkStatusProvider.kt`
- `app/src/main/java/com/easyui/guardianlauncher/guardian/providers/LauncherStatusProvider.kt`

Android implementations:
- `app/src/main/java/com/easyui/guardianlauncher/guardian/providers/android/AndroidBatteryStatusProvider.kt`
  - Uses sticky `Intent.ACTION_BATTERY_CHANGED` (no new permission).
- `app/src/main/java/com/easyui/guardianlauncher/guardian/providers/android/AndroidNetworkStatusProvider.kt`
  - Uses `ConnectivityManager` + `NetworkCapabilities.NET_CAPABILITY_INTERNET`.
  - Requires normal permission `android.permission.ACCESS_NETWORK_STATE` (added).
- `app/src/main/java/com/easyui/guardianlauncher/guardian/providers/android/AndroidLauncherStatusProvider.kt`
  - Uses `PackageManager.resolveActivity(ACTION_MAIN + CATEGORY_HOME)` and compares package to this app.

### Clock abstraction
To test offline duration deterministically:
- `app/src/main/java/com/easyui/guardianlauncher/guardian/clock/Clock.kt`
- `app/src/main/java/com/easyui/guardianlauncher/guardian/clock/SystemClock.kt`

## Existing code updated

### SettingsRepository additions
File changed:
- `app/src/main/java/com/easyui/guardianlauncher/data/SettingsRepository.kt`

New persisted keys:
- `layout_lock_enabled` (Boolean, default `true`)
- `last_online_at_millis` (Long?)

New flows / setters:
- `layoutLockEnabled`, `saveLayoutLockEnabled(...)`
- `lastOnlineAtMillis`, `saveLastOnlineAtMillis(...)`

### Manifest addition
File changed:
- `app/src/main/AndroidManifest.xml`

Added:
- `android.permission.ACCESS_NETWORK_STATE` (normal permission) for network status checks.

## Offline duration behavior
- When `GuardianStatusService.getGuardianStatus()` is called and network is connected, it persists `last_online_at_millis = now`.
- When network is disconnected and `last_online_at_millis` exists, `offlineDurationMinutes` is computed as `(now - last_online_at_millis) / 60_000`.
- No background work or services were added; this only updates when status is requested (e.g., from the parent dashboard).

## Tests (JVM, non-ADB)
New unit tests:
- `app/src/test/java/com/easyui/guardianlauncher/guardian/GuardianStatusServiceImplTest.kt`

Coverage includes:
- all-safe state
- missing parent contact
- missing emergency contact
- battery below/above 20% threshold
- network disconnected + offline duration computed
- PIN missing
- layout lock disabled
- active mode reflected

## Commands run (non-ADB)
- `./gradlew testDebugUnitTest` ✅

## Known limitations (Phase A)
- Network “connected” check uses `NET_CAPABILITY_INTERNET` and does not guarantee captive portal/actual reachability.
- Default launcher detection may vary by OEM launcher behavior; we return `UNKNOWN` if resolution fails.
- Offline duration is only as accurate as the last time Guardian status was computed while online (no background tracking by design).

