# EasyUI Phase 2C — Default Launcher Verification + MVP Mode Completion

**Date/time:** 2026-05-24 (Asia/Karachi)  
**Device:** `08357252AE006901`  
**Repo:** `/home/munaim/Documents/github/parentui`  
**Branch/commit:** `main` / `53d2135`  

## Summary of changes
### Default launcher verification (UX + refresh)
- Added a reusable **Default launcher status card** with:
  - `Default launcher: Set/Not set/Unknown`
  - “Set default home” (safe Settings intent chain)
  - “Check again”
  - Clear copy about HOME behavior + Family Link (no lockdown claims)
- Added **on-resume refresh** hooks so launcher status updates after returning from Settings.
- Added **child-home banner** when default launcher is not set (PIN-gated “Parent setup”).

### MVP modes (Home/School/Sleep only)
- Hid extra modes from the **mode selector** (now only Home/School/Sleep).
- Limited parent dashboard mode UI to MVP modes (no Bedtime/Travel/Exam in normal UI).
- Made **Sleep mode intentionally contact-only** for MVP (no app tiles; explicit message).

### Dial behavior
- Parent/Emergency tiles remain **ACTION_DIAL-based** (no CALL_PHONE permission added).
- If a number is missing, tiles route to **PIN-gated parent setup** instead of disappearing.

## Files changed
- `app/src/main/java/com/easyui/guardianlauncher/ui/components/DefaultLauncherStatusCard.kt`
- `app/src/main/java/com/easyui/guardianlauncher/ui/components/LifecycleEffects.kt`
- `app/src/main/java/com/easyui/guardianlauncher/ui/screens/onboarding/OnboardingScreen.kt`
- `app/src/main/java/com/easyui/guardianlauncher/ui/screens/child/ChildHomeScreen.kt`
- `app/src/main/java/com/easyui/guardianlauncher/ui/screens/parent/ParentDashboardScreen.kt`
- `app/src/main/java/com/easyui/guardianlauncher/ui/screens/parent/SetupLimitationsScreen.kt`
- `app/src/main/java/com/easyui/guardianlauncher/ui/navigation/NavGraph.kt`
- `app/src/main/java/com/easyui/guardianlauncher/ui/viewmodels/LauncherViewModel.kt`
- `app/src/main/java/com/easyui/guardianlauncher/data/SettingsRepository.kt`

## Commands run (bounded)
- Build:
  - `timeout 12m ./gradlew assembleDebug` (SUCCESS after 1 quick fix)
  - `timeout 10m ./gradlew testDebugUnitTest` (SUCCESS)
- Install/run:
  - `adb -s 08357252AE006901 install -r app/build/outputs/apk/debug/app-debug.apk` (SUCCESS)
  - `adb -s 08357252AE006901 shell monkey -p com.easyui.guardianlauncher 1`
- Default HOME resolver / HOME press:
  - `adb -s 08357252AE006901 shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME`
  - `adb -s 08357252AE006901 shell input keyevent KEYCODE_HOME`
- Evidence capture:
  - `adb ... exec-out screencap -p > screenshots/...png`
  - `adb ... shell uiautomator dump ... && adb ... pull ... ui_dumps/`
  - `adb ... logcat -d ... > logs/...txt`
- Offline attempt (bounded):
  - `adb shell settings put global airplane_mode_on ...` (setting toggled)
  - `adb shell am broadcast ... AIRPLANE_MODE ...` (**BLOCKED** by SecurityException on this device)

## Device verification (ADB smoke)
### Default launcher state (still not set on this device)
- HOME resolves to OEM launcher: `com.transsion.hilauncher/...QuickstepLauncher`.
- Pressing HOME exits EasyUI (expected until user sets default).
- EasyUI now shows explicit, user-friendly **setup banner** on child home and a **status card** on Setup & Limitations.

### Modes
- Mode selector now shows **only**: Home / School / Sleep.
- School mode can show assigned apps:
  - Assigned an app in parent dashboard → confirmed School mode shows `1 2 3 4 Player Games` tile.
- Sleep mode is intentionally contact-only:
  - Child home shows: “Sleep mode is active. Only calling tiles are available.”

### Dial tiles
- Parent tile opens system dialer app package `com.sh.smart.caller` (ACTION_DIAL confirmed).
- Emergency tile opens the same dialer app package (ACTION_DIAL confirmed).

## Evidence inventory
All evidence is under this folder:
- `screenshots/` (Phase 2C)
- `ui_dumps/` (Phase 2C)
- `logs/` (Phase 2C)

Notable files:
- Default HOME escape evidence: `ui_dumps/phase2c_20260524_222341.xml`
- Child home “not default” banner: `ui_dumps/phase2c_20260524_222405.xml`
- MVP mode selector (no extra modes): `ui_dumps/phase2c_20260524_222427.xml`
- School mode with assigned app tile: `ui_dumps/phase2c_20260524_222857.xml`
- Sleep mode intentional empty-state: `ui_dumps/phase2c_20260524_222922.xml`
- Setup & Limitations status card: `ui_dumps/phase2c_20260524_223253.xml`
- Dialer opened from tiles: `ui_dumps/phase2c_20260524_222940.xml`, `ui_dumps/phase2c_20260524_223013.xml`
- Offline attempt logs: `logs/20260524_2231*_airplane_*_attempt.txt`

## Feature status table
| Area | Status | Notes / Evidence |
|---|---|---|
| 1. Build/install | PASS | `assembleDebug` + install succeeded |
| 2. First launch | PASS | App launches on device |
| 3. Onboarding completion | NOT TESTED | Not re-run (no data wipe in this sprint) |
| 4. Default launcher status detection | PASS | Card shows “Not set” and refreshes on resume (`ui_dumps/phase2c_20260524_223253.xml`) |
| 5. Default launcher settings handoff | PASS | Uses safe Settings intents (verified by UI presence; Settings screen itself varies by OEM) |
| 6. HOME button behavior | PASS (documented) | HOME escapes to OEM launcher until default set (`ui_dumps/phase2c_20260524_222341.xml`) |
| 7. Child home warning when not default | PASS | Banner shown (`ui_dumps/phase2c_20260524_222405.xml`) |
| 8. Parent dashboard setup status | PASS | Existing Guardian status area + refresh |
| 9. Allowed apps | PASS | App list + selection works (seen in dashboard dumps) |
| 10. Home mode assignment | PARTIAL | Not re-verified in this sprint |
| 11. School mode assignment | PASS | Assigned app shows on child home (`ui_dumps/phase2c_20260524_222857.xml`) |
| 12. Sleep mode behavior | PASS | Intentional contact-only + message (`ui_dumps/phase2c_20260524_222922.xml`) |
| 13. Extra/future modes hidden/disabled | PASS | Mode selector shows only MVP (`ui_dumps/phase2c_20260524_222427.xml`) |
| 14. Parent tile dial flow | PASS | Dialer opened (`ui_dumps/phase2c_20260524_222940.xml`) |
| 15. Emergency tile dial flow | PASS | Dialer opened (`ui_dumps/phase2c_20260524_223013.xml`) |
| 16. Offline smoke | BLOCKED | AIRPLANE broadcast denied on this device; manual toggle recommended |
| 17. Launch readiness | GO (internal family pilot) | With “default launcher must be set” guidance and MVP mode clarity in place |

## Remaining issues / notes
- **Default launcher still requires user action** in Android Settings. EasyUI now clearly communicates this and provides “Check again”.
- Offline test should be performed via **manual airplane-mode toggle** (OEM blocks ADB broadcast).
- Optional polish (post-pilot): remove unused mode parameters in dashboard, tighten warnings, add 1–2 small unit tests for launcher state mapping.

## Final verdict
**GO for internal family pilot.**

Conditions to communicate to pilot users:
- During setup, **set EasyUI as the default Home app**; otherwise HOME will return to the OEM launcher.
- EasyUI is a safer launcher surface, **not full Android lockdown**; Family Link recommended for deeper restrictions.

## Recommended next sprint
Pilot hardening + packaging:
- Add a tiny “Pilot checklist” screen/handout for setting default Home app (OEM-specific guidance).
- Manual offline smoke + reboot persistence check.
- Optional: 1 unit test for launcher status provider mapping + 1 Compose test ensuring only MVP modes render.

