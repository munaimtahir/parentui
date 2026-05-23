# Phase B — Parent Dashboard Restructure

## Goal
Restructure the Parent Dashboard around parent confidence, with **Guardian Checks** surfaced first and the dashboard organized into the Phase 2 sections.

## What changed (UI/UX)

### New section layout (implemented as top-level tabs)
The Parent Dashboard now uses these Phase 2 sections:
1. Guardian Checks
2. Child Home
3. Apps & Modes
4. Contacts & Emergency
5. Parent Lock
6. Setup Help

Implementation detail:
- This is still tab-based (to minimize risk), but the first/default tab is **Guardian Checks**, so the confidence/health view is shown immediately.

### Guardian Checks content
The Guardian Checks tab displays parent-facing cards for:
- EasyUI Launcher (default HOME app check)
- Battery (level + low-battery warning at < 20%)
- Internet (connected/offline, with offline duration when available)
- Contacts (parent + emergency configured)
- Parent Lock (PIN set)
- Layout Lock (new local toggle)
- Current Mode
- Last Checked timestamp

Actions:
- “Set default home screen” opens Android default-home settings intent (best-effort fallbacks).
- “Edit contacts” jumps to Contacts & Emergency tab.
- “Manage PIN / Change layout lock” jumps to Parent Lock tab.
- “Setup Help” jumps to Setup Help tab.

### Child Home tab
Added a simple, parent-friendly summary:
- current mode
- count/list of apps currently visible to the child for that mode
- rescan installed apps action (reuses existing app scan behavior)

### Apps & Modes tab
Preserves existing behavior by reusing the existing composables, grouped together:
- Apps configuration (approved list + per-mode assignment)
- Mode selection

### Parent Lock tab
Preserves existing PIN update behavior and adds:
- Layout Lock toggle backed by DataStore (`layout_lock_enabled`)

### Setup Help tab
Preserves the “default home screen” guidance button and keeps a limitations/compliance notice.
- Phase C will upgrade this tab with the full “What EasyUI Can and Cannot Control” screen/content.

## Files changed
- `app/src/main/java/com/easyui/guardianlauncher/MainActivity.kt`
  - Constructs `GuardianStatusServiceImpl` and injects it into `LauncherViewModel`.
- `app/src/main/java/com/easyui/guardianlauncher/ui/viewmodels/LauncherViewModel.kt`
  - Adds `guardianStatus` state + `refreshGuardianStatus()`
  - Exposes `layoutLockEnabled`
  - Adds setter `setLayoutLockEnabled(...)`
- `app/src/main/java/com/easyui/guardianlauncher/ui/screens/parent/ParentDashboardScreen.kt`
  - Reworked dashboard sections
  - Added Guardian Checks UI
  - Split previous “Security & System” into `ParentLockTab` and `SetupHelpTab`

## Tests
- No new ParentDashboard UI tests were added in this phase.
  - Reason: current test setup is primarily JVM unit tests (no Compose UI JVM harness configured here), and the existing Compose UI tests live in `androidTest/` which is ADB/device-dependent and deferred for this sprint.
- Guardian engine unit tests remain in place from Phase A.

## Commands run (non-ADB)
- `./gradlew assembleDebug` ✅
- `./gradlew testDebugUnitTest` ✅

## Known limitations (Phase B)
- Guardian Checks are refreshed when the Guardian Checks tab is selected (no background polling by design).
- Navigation between “sections” is tab-based, not a fully redesigned dashboard page; chosen to preserve MVP behavior and reduce regression risk.

