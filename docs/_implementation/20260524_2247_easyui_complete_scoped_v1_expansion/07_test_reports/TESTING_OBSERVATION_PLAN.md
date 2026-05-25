# Testing & Observation Plan (Bounded)

Evidence root: `docs/_implementation/20260524_2247_easyui_complete_scoped_v1_expansion/`

## Goals
- Validate that the **pilot-critical flows** still work after UI additions.
- Capture **real device evidence** (screenshots + UI dumps + logs).
- Avoid long-running E2E loops; prefer unit tests + targeted ADB smoke.

## Commands (this run)
- Build: `timeout 12m ./gradlew assembleDebug`
- Unit tests: `timeout 10m ./gradlew testDebugUnitTest`
- Lint: `timeout 10m ./gradlew lintDebug`

## ADB observation checklist (bounded)
Device: `08357252AE006901`

1. Install debug APK
2. Launch EasyUI
3. Verify onboarding completion shows default home card
4. Verify child home renders + shows setup banner when default home is not set
5. Verify parent setup banner leads to PIN gate
6. Verify parent dashboard unlock works with PIN
7. Verify Setup Health screen opens and shows default home status
8. Verify Ready Checklist opens and shows remaining setup items
9. Verify HOME resolver and HOME key behavior
10. Verify Parent + Emergency tiles open dialer via ACTION_DIAL
11. Offline smoke: attempt airplane-mode toggle; if blocked, document manual instructions

## Evidence capture rules
- For each major screen:
  - `adb shell screencap -p` → `04_screenshots/`
  - `adb shell uiautomator dump` → `05_ui_dumps/`
  - a short `logcat` slice for context → `06_logs/`
- Do not change resolution/density.
- Do not wipe global device data.
- Clearing app data is allowed **only** to re-run onboarding for evidence; document when used.

## Known constraints (this device)
- OEM blocks `AIRPLANE_MODE` broadcast via ADB (`SecurityException`), so **offline testing must be manual**.

