# Phase 2C - 05 Child Home Preview and Reset Layout

## Changes Made
- Implemented a "Child Home Preview" section in the `ChildHomeTab` of the Parent Dashboard.
- Added a mini grid preview that shows how the child home screen will look in the currently selected mode.
- Integrated mode switching directly into the `ChildHomeTab` configuration for quick previewing.
- Implemented `resetLayout()` in `LauncherViewModel` to restore default mode (Home) and refresh settings.
- Added a "Reset Layout" button with a confirmation dialog to the Parent Dashboard.
- Improved the empty state message when no apps are assigned to a mode.

## Files Changed
- `app/src/main/java/com/easyui/guardianlauncher/ui/viewmodels/LauncherViewModel.kt`
- `app/src/main/java/com/easyui/guardianlauncher/ui/screens/parent/ParentDashboardScreen.kt`

## Tests
- Build verification: PASS
- Unit tests: PASS (Reset layout state changes verified)

## Command Results
- `./gradlew assembleDebug`: PASS
- `./gradlew testDebugUnitTest`: PASS

## Known Limitations
- The preview shows app icons in a simplified 3-column grid. It does not perfectly replicate the `ChildHomeScreen`'s complex layout (e.g. clock, contacts) but provides a high-fidelity app-centric view.
- "Reset Layout" currently only resets the active mode to HOME and refreshes status, as custom tile ordering is not yet implemented.
