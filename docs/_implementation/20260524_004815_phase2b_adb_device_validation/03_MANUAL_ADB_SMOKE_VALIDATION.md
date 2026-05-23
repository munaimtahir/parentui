# Phases 3–4 — Default Launcher / Home + Manual ADB Smoke Validation

## Current default HOME (before manual selection)
- Resolve command (working form): `adb -s 34081500040008N shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME`
- Result: `com.android.launcher3/.Launcher` (`isDefault=true`)
- Raw output: `docs/_implementation/20260524_004815_phase2b_adb_device_validation/phase3_current_home_alt.txt`

## Home/default launcher behavior
- `adb -s 34081500040008N shell am start -a android.settings.HOME_SETTINGS` started successfully.
- Pressing HOME returned to OEM launcher focus (`com.android.launcher3/.Launcher`).
- Manual default launcher selection is required on-device (OEM flow not automated here).

## Evidence
- After HOME press screenshot: `docs/_implementation/20260524_004815_phase2b_adb_device_validation/home_after_press.png`
- Focus after HOME: `docs/_implementation/20260524_004815_phase2b_adb_device_validation/phase3_focus_after_home.txt`

## Smoke validation table

| Area | Result | Evidence | Notes |
|---|---|---|---|
| App launch | PASS | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/launch_screen.png`, `docs/_implementation/20260524_004815_phase2b_adb_device_validation/launch_logcat.txt` | No immediate crash found in captured logcat. |
| Home button (as default launcher) | BLOCKED | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/home_after_press.png` | Device default HOME is `com.android.launcher3`; requires manual selection of EasyUI as default. |
| Onboarding | PASS (Already onboarded) | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/app_foreground.png`, `docs/_implementation/20260524_004815_phase2b_adb_device_validation/window_dump.xml` | App opened directly to child home; onboarding not shown. |
| Parent PIN | BLOCKED (Manual PIN needed) | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/parent_after_tap.png`, `docs/_implementation/20260524_004815_phase2b_adb_device_validation/window_dump_parent.xml` | PIN not known; no brute-force attempted. |
| Guardian Checks | BLOCKED (Behind parent PIN) | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/window_dump_parent.xml` | Requires successful parent dashboard entry. |
| Setup limitations | BLOCKED (Behind parent PIN) | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/window_dump_parent.xml` | Requires successful parent dashboard entry. |
| Child home | PASS | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/app_foreground.png`, `docs/_implementation/20260524_004815_phase2b_adb_device_validation/window_dump.xml` | Child home rendered; app tiles visible in UI dump. |
| Modes | CONDITIONAL PASS | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/window_dump.xml` | `🏡 Home Mode` control visible; other modes not validated via ADB navigation. |

## UI dump notes
- `uiautomator dump` succeeded and produced:
  - `docs/_implementation/20260524_004815_phase2b_adb_device_validation/window_dump.xml`
  - `docs/_implementation/20260524_004815_phase2b_adb_device_validation/window_dump_parent.xml`
- Device printed a non-fatal permission-denied stack trace during dump (`/sys/board_info/user_cpu_freq`), but XML was still generated.
