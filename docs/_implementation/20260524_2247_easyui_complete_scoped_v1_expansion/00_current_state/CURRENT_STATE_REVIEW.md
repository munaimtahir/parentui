# CURRENT STATE REVIEW — EasyUI Guardian Launcher

**Run:** EasyUI Complete Scoped Feature Expansion + V1 Pilot Readiness Mega Run  
**Date:** 2026-05-24 (Asia/Karachi)  

## Repo details
- Path: `/home/munaim/Documents/github/parentui`
- Branch: `main`
- Commit: `53d2135`
- Working tree: **not clean** (Phase 2C changes + evidence folders exist locally)

## App identity
- Package / applicationId: `com.easyui.guardianlauncher`
- Launcher activity: `com.easyui.guardianlauncher/.MainActivity`
  - Has `MAIN` + `HOME` + `DEFAULT` intent filter
  - Also has `MAIN` + `LAUNCHER` intent filter

## Core feature inventory (as of now)
### Built / present in code
- Onboarding flow: welcome → PIN setup → contacts → app approval → completion.
- Child home screen:
  - Mode label, time/date, tiles, app grid
  - Parent/emergency tiles use `ACTION_DIAL`
  - Mode switching is PIN-gated
- Parent dashboard:
  - PIN-gated entry from child home
  - Status/preview/apps/routines/contacts/lock/backup/help tabs exist
- Data persistence:
  - DataStore in `SettingsRepository` for:
    - onboarding complete, active mode, allowed apps
    - per-mode app sets: home/school/sleep (+ bedtime/travel/exam currently)
    - contacts + emergency toggle
    - PIN hash
    - routine schedules
    - layout lock flag
- Guardian checks:
  - `GuardianStatusServiceImpl` computes default launcher state, battery, network/offline duration, etc.
- Backup/restore (partial but real):
  - `SettingsRepository.exportBackup()` / `importBackup()` (local JSON string)
  - Parent dashboard includes “Backup” tab UI (existing)
- Routine scheduling scaffolding:
  - `RoutineManager`, `RoutineReceiver`, `RoutineSchedule` model, exact-alarm permission present

### Partial / inconsistent
- Default launcher verification UX exists (Phase 2C additions), but default launcher still requires user action in Settings (expected).
- Modes:
  - MVP modes exist (Home/School/Sleep), but codebase still contains extra enum modes (`BEDTIME`, `TRAVEL`, `EXAM`) and some related storage.

### Missing vs mega-run scope
- Age presets (not found yet as a first-class feature).
- A dedicated “Setup Health Center” screen (partially present via dashboard status, but not unified as its own UX).
- “Ready-to-handover checklist” screen (not found yet).
- A focused, documented internal pilot pack under the new mega-run evidence root.

## Risky areas / cautions
- Scope creep: avoid drifting into full-device lockdown, surveillance, or risky OEM hacks.
- Mode explosion: extra modes exist in enum/storage; keep MVP UI simple and only expose Exam/Travel if implemented + explicitly enabled.
- Offline testing: OEM may block ADB airplane-mode toggles; offline verification may require manual steps.

## Tests available
- Unit tests:
  - `GuardianStatusServiceImplTest`
  - `ChildSafetyWarningsTest`
  - `LauncherViewModelTest`
  - `SetupChecklistTest`
- Instrumented test:
  - `OnboardingScreenTest`

## Device testing capability
- Physical ADB device serial: `08357252AE006901`
- Prior evidence: default HOME resolves to OEM launcher (`com.transsion.hilauncher/...QuickstepLauncher`) unless user changes it in Settings.

## Blockers (current)
- None identified at compile-time from this review step; build/ADB checks pending in this mega run.

