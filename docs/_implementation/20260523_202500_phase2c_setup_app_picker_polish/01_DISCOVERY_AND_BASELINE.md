# Phase 2C - 01 Discovery and Baseline

## Project Structure Summary
The project is a native Android application using Jetpack Compose, Kotlin, and Gradle.
It follows a clean architecture pattern with:
- `data/`: Repositories and DataStore for persistence.
- `guardian/`: Logic for health checks and system status.
- `ui/`: Compose UI components and screens.
- `ui/viewmodels/`: Shared `LauncherViewModel` managing state.

## Key Files Found
- **Onboarding:** `OnboardingScreen.kt`
- **Dashboard:** `ParentDashboardScreen.kt`
- **Setup Help:** `SetupLimitationsScreen.kt`
- **Logic:** `LauncherViewModel.kt`
- **Data:** `SettingsRepository.kt`
- **Status:** `GuardianStatusServiceImpl.kt`

## Baseline Command Results
- `./gradlew assembleDebug`: PASS
- `./gradlew testDebugUnitTest`: PASS
- `./gradlew lint`: PASS

## Existing Issues
- No dedicated setup checklist screen.
- App picker lacks search and categorization UI (has heuristic in VM but not used in UI filters).
- Mode assignment is a bit basic.
- No child home preview.

## Planned Implementation Map
- **Phase A:** Create `SetupChecklist` component and integrate it into `ParentDashboardScreen`.
- **Phase B:** Update `SetupHelpTab` and `SetupLimitationsScreen` with launcher and Family Link guides.
- **Phase C:** Enhance `AppsConfigTab` with search, filtering, and better mode assignment UI.
- **Phase D:** Add a preview section to `ChildHomeTab` and implement `resetLayout` in `LauncherViewModel`.
- **Phase E:** Check `.github/workflows` and add a basic one if missing.
