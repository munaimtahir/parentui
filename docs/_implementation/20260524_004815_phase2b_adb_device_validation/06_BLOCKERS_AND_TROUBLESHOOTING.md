# Phase 7 — Blockers and Troubleshooting

## BLOCKER-001
Default HOME launcher could not be set via ADB (manual OEM flow required).

### Area
Default launcher / HOME behavior.

### Command
- Check: `adb -s 34081500040008N shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME`
- Assist: `adb -s 34081500040008N shell am start -a android.settings.HOME_SETTINGS`

### Error
- No technical error; OEM default-home selection requires manual UI confirmation.

### Attempt 1
- Diagnosis: Device default HOME remains OEM launcher (`com.android.launcher3`).
- Fix tried: Opened Home settings screen to allow manual selection.
- Result: Still requires on-device manual selection; not automated further.

### Attempt 2
- Diagnosis: Forcing default launcher via shell/package-manager commands is OEM/ROM-dependent.
- Fix tried: **Not attempted** (per instruction: do not hack default launcher selection).
- Result: N/A.

### Attempt 3
- Diagnosis: Same as Attempt 2.
- Fix tried: **Not attempted**.
- Result: N/A.

### Final status
DEFERRED (Manual validation required due to OEM/default launcher settings behavior).

### Recommended next action
- On device `V2109`, manually set `EasyUI Guardian Launcher` as the default Home app in system settings, then re-run Phase 3 HOME validation (press HOME + screenshot).

---

## BLOCKER-002
Parent dashboard content (Guardian Checks / Setup limitations) blocked by unknown PIN.

### Area
Parent dashboard / Guardian Checks / Setup limitations.

### Command
- Navigation attempt: `adb -s 34081500040008N shell input tap 970 220` (tap Parent Dashboard icon area)
- Evidence: `uiautomator dump` + screenshot.

### Error
- No crash; UI displayed PIN gate: `Enter PIN for Parent Dashboard`.

### Attempt 1
- Diagnosis: Parent dashboard is intentionally PIN-protected.
- Fix tried: None (no known test PIN provided).
- Result: Parent content not accessible via ADB-only flow.

### Attempt 2
- Diagnosis: Brute forcing PIN is unsafe and out of scope.
- Fix tried: **Not attempted**.
- Result: N/A.

### Attempt 3
- Diagnosis: Same as Attempt 2.
- Fix tried: **Not attempted**.
- Result: N/A.

### Final status
DEFERRED (Manual PIN validation required).

### Recommended next action
- Provide a known test PIN (or a test build flavor that bypasses PIN for QA only), then re-run Guardian Checks / Setup limitations smoke checks.

---

## BLOCKER-003
Bugreport artifact could not be captured as a zipped bugreport via ADB.

### Area
Logs / artifacts.

### Command
- Attempt 1: `timeout 120 adb -s 34081500040008N bugreport docs/_implementation/20260524_004815_phase2b_adb_device_validation/artifacts/bugreport.zip`
- Attempt 2: `timeout 60 adb -s 34081500040008N bugreport > docs/_implementation/20260524_004815_phase2b_adb_device_validation/artifacts/bugreport_stream.txt`
- Attempt 3: `timeout 60 adb -s 34081500040008N shell bugreport > docs/_implementation/20260524_004815_phase2b_adb_device_validation/artifacts/bugreport_shell.txt`

### Error
- Attempt 1: timed out / non-zero (captured as `BUGREPORT_NOT_CAPTURED`).
- Attempt 2: `Failed to connect to dumpstatez service: No such file or directory`.
- Attempt 3: produced 0-byte output file.

### Attempt 1
- Diagnosis: Zipped bugreport sometimes hangs on OEM builds.
- Fix tried: Added `timeout 120` wrapper.
- Result: Not captured.

### Attempt 2
- Diagnosis: Try streaming bugreport to file.
- Fix tried: Redirected `adb bugreport` stdout.
- Result: Device reported missing `dumpstatez` service.

### Attempt 3
- Diagnosis: Try legacy flat bugreport output.
- Fix tried: `adb shell bugreport` stdout redirect.
- Result: Empty output.

### Final status
BLOCKED (non-critical).

### Recommended next action
- Try on-device `bugreportz` (if supported) and `adb pull` the generated filename, or capture OEM diagnostic logs manually.

---

## Non-blocking warnings
- `uiautomator dump` printed a permission-denied stack trace for `/sys/board_info/user_cpu_freq` on this vivo device, but still generated usable XML.
