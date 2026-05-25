# FINAL — EasyUI Guardian Launcher ADB Discovery Snapshot

**Snapshot time window:** 2026-05-24 ~21:42–21:57 (Asia/Karachi)  
**Device serial:** `08357252AE006901`  

## Device details
- Manufacturer/Model: `TECNO` / `TECNO CH6i`
- Android: `13` (SDK `33`)
- Display: `1080x2460` @ `480 dpi`

## Repo details
- Path: `/home/munaim/Documents/github/parentui`
- Branch: `main`
- Commit: `53d2135`
- App ID / package: `com.easyui.guardianlauncher`
- Launcher activity: `.MainActivity` (has `HOME` + `LAUNCHER` intent-filters)

## Commands run (bounded)
- Repo discovery:
  - `git status --short`
  - `git branch --show-current`
  - `git rev-parse --short HEAD`
  - Manifest/Gradle inspection (`app/src/main/AndroidManifest.xml`, `app/build.gradle.kts`)
- Build health:
  - `timeout 12m ./gradlew assembleDebug` (SUCCESS)
  - `timeout 10m ./gradlew testDebugUnitTest` (SUCCESS)
- Device baseline:
  - `adb -s 08357252AE006901 get-state`
  - `adb -s 08357252AE006901 shell getprop ...`
  - `adb -s 08357252AE006901 shell wm size|density`
  - Wake/unlock/stayon commands
- Install/launch:
  - `adb -s 08357252AE006901 install -r app/build/outputs/apk/debug/app-debug.apk` (SUCCESS)
  - `adb -s 08357252AE006901 shell monkey -p com.easyui.guardianlauncher 1`
- Default HOME resolution:
  - `adb -s 08357252AE006901 shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME`
- Evidence capture:
  - `adb ... exec-out screencap -p > screenshots/...png`
  - `adb ... shell uiautomator dump /sdcard/...xml && adb ... pull ... ui_dumps/`
  - `adb ... logcat -d` slices to `logs/`

## What was tested (on physical device)
- First launch opens and shows onboarding.
- Onboarding flow exercised end-to-end:
  - Welcome + limitations notice visible
  - Parent PIN creation + confirmation
  - Parent + emergency contacts entry
  - App approval list renders and allows selection
  - Completion step shown with “Set Default Home Screen” guidance
- Child home screen:
  - Renders time/date, mode label, contact tiles, and approved app tiles
  - Launching an approved app works (opened `net.bat.store`)
  - Returning back to EasyUI works via BACK (HOME returns to system launcher when not default)
- Parent dashboard:
  - PIN-gated entry works
  - Dashboard UI renders with multiple sections/tabs (not fully explored)
  - Setup help/limitations content includes Family Link guidance and non-lockdown disclaimers
- Mode switching:
  - Mode switch is PIN-gated
  - Mode selector UI exists (Home/School/Sleep + additional modes)
  - Switching to School shows an empty-state (“No apps…”) indicating mode-specific app assignment is not configured yet

## What was NOT tested (skipped/bounded)
- `connectedDebugAndroidTest` / instrumented E2E runs (not run).
- Lint (`./gradlew lintDebug`) (skipped to preserve time).
- Dialer launch and call flow for Parent/Emergency tiles (tiles visible; not verified on-device).
- Default launcher setting flow inside Android Settings (the app opens settings from onboarding completion, but we did not complete “set EasyUI as default HOME” on this device during the snapshot).
- Offline scenarios (airplane mode), permission denial flows, and reboot persistence.

## Evidence inventory
All evidence lives under this snapshot folder:
- `screenshots/` (18 files)
- `ui_dumps/` (22 files)
- `logs/` (2 files)

## Key findings (practical)
1. **Build/install is healthy**: debug build assembles and unit tests pass.
2. **Core UX exists on-device**: onboarding → child home → parent dashboard works with real device UI.
3. **Default launcher is NOT set** on this device:
   - HOME resolves to `com.transsion.hilauncher/...QuickstepLauncher`
   - Pressing HOME returns to system launcher (confirmed by UI dump package `com.transsion.hilauncher`)
4. **Mode system exists but is incomplete for MVP behavior**:
   - Mode selector exists and is PIN-gated
   - Non-Home modes show empty-state (no per-mode app assignment confirmed)
5. **Safety messaging is present**: onboarding and setup help include clear “not full lockdown” + Family Link guidance.

## Feature status table (required)
| Area | Status | Evidence |
|---|---|---|
| 1. Build/install | PASS | `screenshots/20260524_214953_after_launch.png` (post-install launch), Gradle output in session notes |
| 2. First launch | PASS | `ui_dumps/easyui_20260524_214959.xml` (`onboarding_welcome_screen`) |
| 3. Onboarding | PASS | `screenshots/20260524_215255_completion.png`, `screenshots/20260524_215355_child_home.png` |
| 4. Default launcher setup | PARTIAL | Completion guidance exists (`screenshots/20260524_215255_completion.png`), but not set on device |
| 5. HOME button behavior | FAIL (on this device) | `ui_dumps/easyui_20260524_215320.xml` (package `com.transsion.hilauncher`) |
| 6. Child home screen | PASS | `screenshots/20260524_215355_child_home.png`, `ui_dumps/easyui_20260524_215355.xml` |
| 7. Parent PIN gate | PASS | `screenshots/20260524_215446_parent_dashboard_entry.png` (PIN prompt) |
| 8. Parent dashboard | PARTIAL | `screenshots/20260524_215504_parent_dashboard.png` (renders), limited depth explored |
| 9. Allowed apps / app picker | PARTIAL | Onboarding list works (`ui_dumps/easyui_20260524_215207.xml`), dashboard mgmt not validated |
| 10. Mode switching | PARTIAL | Selector works (`screenshots/20260524_215630_after_mode_switch.png`), per-mode apps incomplete |
| 11. Parent contact tile | PARTIAL | Visible (`ui_dumps/easyui_20260524_215355.xml`), dialing not verified |
| 12. Emergency tile | PARTIAL | Visible (`ui_dumps/easyui_20260524_215355.xml`), dialing not verified |
| 13. Setup help / limitations | PASS | `screenshots/20260524_215520_setup_help.png` (Family Link + limitations copy) |
| 14. Offline-first / permissions | NOT TESTED | No airplane-mode / denial-flow audit in this snapshot |
| 15. Screen-size usability | PASS | UI is usable at 1080x2460 (`screenshots/*.png`) |
| 16. Test suite health | PARTIAL | Unit tests pass; instrumented tests not run |
| 17. Launch readiness | CONDITIONAL GO | See “Launch recommendation” below |

## Gap analysis
### A) Must fix before any internal/family pilot
1. **Default launcher path**: ensure users can reliably set EasyUI as default HOME and verify HOME returns to EasyUI (otherwise the launcher value proposition collapses).
2. **Mode MVP clarity**: either (a) implement per-mode app assignment in parent dashboard, or (b) hide/disable non-MVP modes and clearly communicate what modes do today.
3. **School/Sleep empty-state ergonomics**: current behavior is safe, but it must be obvious to the parent how to add apps to those modes (right now it appears “not configured”).
4. **Back/home escape expectations**: document and/or guard that pressing HOME will escape to system launcher until default is set.

### B) Should fix before closed testing
1. Add a quick “Setup status” section that explicitly says “Default launcher: NOT SET” with a one-tap deep link to the correct Settings screen for the device.
2. Verify dial actions for Parent/Emergency tiles on-device (ACTION_DIAL vs CALL permissions; SIM-less behavior).
3. Do a minimal offline pass (airplane mode) to confirm no onboarding/dashboard hard-depends on network.

### C) Can postpone after MVP pilot
1. Lint cleanup and deprecated icon API updates (currently warnings only).
2. Expand instrumented tests beyond onboarding and add 1–2 targeted smoke tests.

### D) V1/future improvements
1. Broader “guardian” checks UX (battery/network/launcher status) polish.
2. Better app categorization and safer defaults for app approval.

## Launch recommendation
**Verdict:** **CONDITIONAL GO for internal hands-on pilot** (not closed testing yet).  
**Reason:** the app is installable and core flows render on a real device, but **default launcher is not set** (HOME escapes) and **modes beyond Home are not product-complete** for a pilot without guidance.

**Recommended launch path:** internal testing / family pilot only **after** fixing the “Must fix” items above.  
**Full E2E recommendation:** **not yet**. Keep using targeted ADB smoke + 1–2 focused instrumented tests after the launcher-default + mode configuration story is solid.

