# Phase 5 — Connected / Instrumentation Test Results

## Command
- `ANDROID_SERIAL=34081500040008N ./gradlew connectedDebugAndroidTest`

## Result
- **PASS**
- Console indicated: `Starting 4 tests on V2109 - 13`

## Attempt log
- Attempt 1: PASS (`docs/_implementation/20260524_004815_phase2b_adb_device_validation/phase5_connectedDebugAndroidTest_attempt1.txt`)

## Reports
- HTML report folder: `app/build/reports/androidTests/connected/debug/`
- Raw results folder: `app/build/outputs/androidTest-results/connected/debug/`
- File list capture: `docs/_implementation/20260524_004815_phase2b_adb_device_validation/phase5_connected_reports_list.txt`

## Notes
- Only one androidTest source file was present (`OnboardingScreenTest.kt`), but it executed 4 tests (multiple test methods).
