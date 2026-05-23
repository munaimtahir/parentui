# Phase 2B ADB Physical-Device Validation Report

## Verdict
CONDITIONAL GO

Rationale:
- App builds, installs, and launches without immediate crash.
- Child home renders and is interactable.
- Connected instrumentation tests passed.
- Default launcher selection + parent PIN-gated areas require manual completion/inputs.

## Device
- Device ID: `34081500040008N`
- Manufacturer/Model: `vivo V2109`
- Android version: `13` (SDK `33`)
- Screen size/density: `1080x2408` @ `440`

## Summary table

| Area | Result | Evidence |
|---|---|---|
| Device detected | PASS | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/phase0_adb_devices.txt` |
| APK build | PASS | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/phase0_assembleDebug.txt` |
| APK install | PASS | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/phase2_adb_install.txt` |
| App launch | PASS | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/launch_screen.png` |
| Home/default launcher | BLOCKED (Manual) | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/home_after_press.png` |
| Onboarding | PASS (Already onboarded) | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/app_foreground.png` |
| Child home | PASS | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/window_dump.xml` |
| Parent dashboard | BLOCKED (Manual PIN) | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/window_dump_parent.xml` |
| Guardian Checks | BLOCKED (Manual PIN) | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/window_dump_parent.xml` |
| Setup limitations | BLOCKED (Manual PIN) | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/window_dump_parent.xml` |
| Connected tests | PASS | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/phase5_connectedDebugAndroidTest_attempt1.txt` |
| Screenshots/logs | PASS (except bugreport) | `docs/_implementation/20260524_004815_phase2b_adb_device_validation/05_SCREENSHOT_AND_LOG_CAPTURE.md` |

## Commands run
- `git status`
- `./gradlew assembleDebug`
- `adb version`
- `adb devices -l`
- `adb -s 34081500040008N shell getprop ro.product.manufacturer`
- `adb -s 34081500040008N shell getprop ro.product.model`
- `adb -s 34081500040008N shell getprop ro.build.version.release`
- `adb -s 34081500040008N shell getprop ro.build.version.sdk`
- `adb -s 34081500040008N shell wm size`
- `adb -s 34081500040008N shell wm density`
- `adb -s 34081500040008N shell dumpsys power | grep -E "mWakefulness|Display Power|state="`
- `adb -s 34081500040008N shell dumpsys window | grep -E "mDreamingLockscreen|mShowingLockscreen|isStatusBarKeyguard|mCurrentFocus"`
- `adb -s 34081500040008N shell input keyevent KEYCODE_WAKEUP`
- `adb -s 34081500040008N shell wm dismiss-keyguard`
- `adb -s 34081500040008N shell settings put system screen_off_timeout 1800000`
- `adb -s 34081500040008N shell svc power stayon usb`
- `adb -s 34081500040008N install -r app/build/outputs/apk/debug/app-debug.apk`
- `adb -s 34081500040008N shell cmd package resolve-activity --brief com.easyui.guardianlauncher`
- `adb -s 34081500040008N logcat -c`
- `adb -s 34081500040008N shell monkey -p com.easyui.guardianlauncher -c android.intent.category.LAUNCHER 1`
- `adb -s 34081500040008N exec-out screencap -p > ...`
- `adb -s 34081500040008N shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME`
- `adb -s 34081500040008N shell am start -a android.settings.HOME_SETTINGS`
- `adb -s 34081500040008N shell input keyevent KEYCODE_HOME`
- `adb -s 34081500040008N shell uiautomator dump /sdcard/window_dump.xml`
- `ANDROID_SERIAL=34081500040008N ./gradlew connectedDebugAndroidTest`

## Fixes made
- None.

## Blockers
- See `docs/_implementation/20260524_004815_phase2b_adb_device_validation/06_BLOCKERS_AND_TROUBLESHOOTING.md`.

## Safety confirmation
- No SMS permission added.
- No location permission added.
- No backend added.
- No kiosk/device-owner/lock-task mode added.
- No surveillance features added.

## Recommendation
Needs manual phone validation:
- Manually set EasyUI as default Home launcher on-device and re-check HOME behavior.
- Validate parent dashboard with known PIN to confirm Guardian Checks + Setup limitations UI.
