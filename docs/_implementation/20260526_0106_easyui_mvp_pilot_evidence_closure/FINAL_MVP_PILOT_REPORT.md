# FINAL MVP PILOT REPORT — Evidence Closure Sprint

Sprint name: **EasyUI MVP Pilot Evidence Closure Sprint**  
Timestamp: 2026-05-26 (Asia/Karachi)

Evidence folder:
`docs/_implementation/20260526_0106_easyui_mvp_pilot_evidence_closure/`

## Repo state (Phase 1)
- Branch: `main`
- Commit: `7c99e76`
- Working tree status: **clean for tracked files**; evidence folder is untracked
  - `01_repo_state/repo_cleanliness.md`
  - `01_repo_state/repo_cleanliness_summary.md`

## Build evidence (Phase 2 — logs included)
Logs saved under `02_build_logs/`:
- `assembleDebug`: **PASS** (`02_build_logs/assembleDebug.log`)
- `testDebugUnitTest`: **PASS** (`02_build_logs/testDebugUnitTest.log`)
- `lintDebug`: **PASS** (`02_build_logs/lintDebug.log`)

## Device details (Phase 3)
Device: `08357252AE006901`
- Manufacturer/model: TECNO TECNO CH6i
- Android: 13 (SDK 33)
- Screen: 1080x2460 @ 480dpi

Evidence:
- `05_logs/adb_recheck_20260526_011343.txt`
- `05_logs/device_baseline_20260526_011405.txt`
- Launch evidence:
  - `03_screenshots/easyui_20260526_011418_launch.png`
  - `04_ui_dumps/easyui_20260526_011418_launch.xml`
  - `05_logs/logcat_tail_20260526_011418_launch.txt`

## Default HOME manual verification (Phase 4)

### Before
HOME resolver was OEM launcher:
- `05_logs/home_resolver_20260526_011433.txt`
- `05_logs/home_resolver_after_20260526_011438.txt`

### Manual set + outcome
Default Home app was set successfully to EasyUI:
- HOME resolver after manual set:
  - `05_logs/home_resolver_after_manual_set_20260526_011802.txt`
  - `05_logs/home_resolver_recheck_20260526_011806.txt`
- Pressing HOME returns to EasyUI (evidence):
  - `03_screenshots/easyui_20260526_011806_after_home_post_set.png`
  - `04_ui_dumps/easyui_20260526_011806_after_home_post_set.xml`

Setup Health shows “Default launcher: Set”:
- `03_screenshots/easyui_20260526_013142_opened_Setup_Health.png`
- `04_ui_dumps/easyui_20260526_013142_opened_Setup_Health.xml`

## MVP modes verification (Phase 5)

### Home mode
- Home mode shows at least one approved app tile:
  - `04_ui_dumps/easyui_20260526_012337_after_home_return_from_settings.xml` (shows “Adobe Acrobat”)
- App launch verified (tile tap leads out of EasyUI):
  - `04_ui_dumps/easyui_20260526_012407_after_launch_acrobat.xml` (foreground package `com.google.android.gms` sign-in sheet after launch)
- HOME returns safely to EasyUI after leaving the app:
  - `04_ui_dumps/easyui_20260526_012412_after_home_return_from_acrobat.xml`

### School mode
- School mode is reachable and shows safe empty state:
  - `03_screenshots/easyui_20260526_013656_child_home_school_after_assign_verified.png`
  - `04_ui_dumps/easyui_20260526_013656_child_home_school_after_assign_verified.xml`
- **BLOCKER:** Could not verify any app appearing in School mode despite multiple attempts to assign an app from Parent Dashboard. School mode remained empty.

### Sleep mode
- Sleep mode shows intended contact-only behavior:
  - `03_screenshots/easyui_20260526_012710_child_home_sleep_mode_final.png`
  - `04_ui_dumps/easyui_20260526_012710_child_home_sleep_mode_final.xml`

## Parent dashboard verification (Phase 6)
- Parent dashboard is PIN-gated (PIN 1234 used in this run):
  - `03_screenshots/easyui_20260526_013132_pin_gate_open_dashboard_retry.png`
  - `04_ui_dumps/easyui_20260526_013132_pin_gate_open_dashboard_retry.xml`
- Setup Health opens:
  - `03_screenshots/easyui_20260526_013142_opened_Setup_Health.png`
  - `04_ui_dumps/easyui_20260526_013142_opened_Setup_Health.xml`
- Ready checklist opens:
  - `03_screenshots/easyui_20260526_013147_opened_Ready_Checklist.png`
  - `04_ui_dumps/easyui_20260526_013147_opened_Ready_Checklist.xml`

## Parent / Emergency dial verification (Phase 7)
- Parent tile opens dialer package `com.sh.smart.caller`:
  - `04_ui_dumps/easyui_20260526_013247_parent_dial.xml`
  - `05_logs/foreground_parent_dial_20260526_013247.txt`
- Emergency tile opens dialer package `com.sh.smart.caller`:
  - `04_ui_dumps/easyui_20260526_013436_emergency_dial.xml`
  - `05_logs/foreground_emergency_dial_20260526_013436.txt`
- No `CALL_PHONE` permission required (ACTION_DIAL behavior observed via external dialer opening).

## Manual offline check (Phase 8)
- ADB airplane-mode broadcast is blocked by OEM (SecurityException):
  - `06_manual_checks/airplane_mode_attempt_20260526_013448.txt`
- Manual offline check result: **NOT DONE** (requires human action).
  - Steps: `06_manual_checks/MANUAL_OFFLINE_CHECK_STEPS.md`

## Final verdict (Phase 9)
Verdict: **CONDITIONAL GO for supervised MVP internal family pilot**.

Conditions / blockers:
1. **School mode app assignment could not be verified** (School remained empty). Treat as pilot risk if School mode is part of the pilot story.
2. Offline manual check still needs to be performed and recorded (PASS/FAIL).

What is solid enough for a supervised MVP pilot:
- Build/test/lint pass with logs included.
- Manual default-home setup works and HOME returns to EasyUI afterwards.
- Parent dashboard PIN gate works.
- Setup Health and Ready checklist are accessible.
- Home mode can show and launch an approved app.
- Sleep mode contact-only behavior is clear.
- Parent/Emergency ACTION_DIAL opens the dialer app.
