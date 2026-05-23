# Phase 0 — Discovery and Baseline (EasyUI Guardian Launcher)

Date (UTC): 2026-05-23

## Repo / Project basics
- Project: `easyui-guardian-launcher`
- Main module: `:app`
- Package / namespace: `com.easyui.guardianlauncher`
- Current branch: `main` (commit `72d2789`)
- Working tree: clean at start of Phase 0

## High-level structure
- App module: `app/`
- Kotlin sources: `app/src/main/java/com/easyui/guardianlauncher/`
- Docs: `docs/`
- Testing helpers (includes ADB scripts; **not used in this sprint**): `testing/`

## Key files located (Phase 2 anchor points)

### Entry / navigation
- Activity: `app/src/main/java/com/easyui/guardianlauncher/MainActivity.kt`
- Navigation: `app/src/main/java/com/easyui/guardianlauncher/ui/navigation/NavGraph.kt`

### Parent dashboard (current)
- Screen: `app/src/main/java/com/easyui/guardianlauncher/ui/screens/parent/ParentDashboardScreen.kt`
- ViewModel (currently shared across child + parent): `app/src/main/java/com/easyui/guardianlauncher/ui/viewmodels/LauncherViewModel.kt`

### Child home screen
- Screen: `app/src/main/java/com/easyui/guardianlauncher/ui/screens/child/ChildHomeScreen.kt`

### Onboarding
- Screen: `app/src/main/java/com/easyui/guardianlauncher/ui/screens/onboarding/OnboardingScreen.kt`
- Onboarding scaffold: `app/src/main/java/com/easyui/guardianlauncher/ui/components/OnboardingStepScaffold.kt`

### Settings / repositories (current state)
This codebase currently uses a single DataStore-backed repository rather than separate repositories per concern.
- Settings repository (PIN, contacts, modes, allowed apps, onboarding): `app/src/main/java/com/easyui/guardianlauncher/data/SettingsRepository.kt`
- Data models: `app/src/main/java/com/easyui/guardianlauncher/data/AppModel.kt`

Mapped to Phase 2 terminology:
- **PIN repository**: `SettingsRepository` (`parentPinHash`)
- **Contacts repository**: `SettingsRepository` (`parentContact`, `emergencyContact`)
- **Mode repository/service**: `SettingsRepository` (`activeMode`, plus per-mode app sets)
- **Settings repository**: `SettingsRepository`

### Default launcher status logic (current)
- Manifest declares HOME/DEFAULT intent filter in `app/src/main/AndroidManifest.xml`.
- App provides a Settings deep-link attempt (home settings / manage default apps / settings) from onboarding and parent dashboard.
- There is **no explicit runtime “is default launcher” check** implemented yet (Phase A will add this via a provider interface).

### Existing test structure
- Unit tests: `app/src/test/java/com/easyui/guardianlauncher/ui/viewmodels/LauncherViewModelTest.kt`
- Instrumented tests (ADB/device): `app/src/androidTest/java/com/easyui/guardianlauncher/ui/screens/onboarding/OnboardingScreenTest.kt` (**not run in this sprint**)

### GitHub workflows
- No `.github/workflows/*` present in repo at time of discovery.

## Gradle tasks available (relevant subset)
From `./gradlew tasks --all`:
- Build: `assembleDebug`, `assemble`, `build`
- Unit tests: `testDebugUnitTest`, `test`
- Lint: `lint`, `lintDebug`
- Checks: `check` (Android/Gradle checks; may include lint + unit tests)
- Device/instrumentation (explicitly deferred): `connectedDebugAndroidTest`, `connectedAndroidTest`, `deviceCheck`

## Baseline non-ADB verification (executed)
Commands required by Phase 0:
- `./gradlew assembleDebug` ✅ (build succeeded)
- `./gradlew testDebugUnitTest` ✅ (unit tests succeeded)
- `./gradlew lint` ✅ (lint succeeded; report at `app/build/reports/lint-results-debug.html`)

Notes observed during baseline:
- Gradle printed an SDK XML version warning during `assembleDebug` (likely a local SDK/tools mismatch).
- Kotlin compile printed deprecation warnings about `Icons.Default.ArrowBack` and a deprecated `LinearProgressIndicator` overload.

## Pre-existing issues / risks (Phase 2 relevant)
- Default launcher verification is not currently computed; only Settings intents exist.
- There is no “layout lock” setting today (Phase A may introduce a DataStore boolean).
- There is no dedicated “parent dashboard ViewModel”; parent/child share `LauncherViewModel` (Phase B may extend it carefully without breaking child home behavior).
- Only a small unit test suite exists; we’ll expand JVM unit tests for Guardian checks via provider fakes.

## Implementation approach (for Phase 2)
- Add a Guardian Status model + service that composes:
  - DataStore-backed settings checks (PIN, contacts, mode, layout lock)
  - System checks via provider interfaces (battery, network, default launcher)
- Keep Android APIs out of core logic where possible by:
  - Defining provider interfaces in `data/` or `domain/`-like package
  - Supplying Android implementations from the app layer
  - Using fake providers in JVM unit tests
- Restructure the Parent Dashboard UI (Compose) to show “Guardian Checks” at the top, but preserve:
  - onboarding flow
  - child home launching
  - parent PIN protection
  - existing app picker / mode assignment behavior

