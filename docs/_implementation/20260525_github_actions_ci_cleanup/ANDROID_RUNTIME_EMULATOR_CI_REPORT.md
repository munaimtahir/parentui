# Android Runtime Emulator CI Report (2026-05-25)

## Workflow
- Name: `Android Runtime Emulator CI`
- File: `.github/workflows/android-runtime-emulator-ci.yml`
- Latest passing run: https://github.com/munaimtahir/parentui/actions/runs/26425998285

## What it validates (emulator-appropriate)
- App installs and launches on emulator
- Instrumentation/Compose UI tests (currently includes a smoke test that waits for onboarding/home UI)
- Captures evidence useful for follow-up debugging (logcat, screenshot, UIAutomator dump)

## Inputs (workflow_dispatch)
- `test_level`: `smoke | full | screenshots_only`
- `runtime_target`: informational selector (reserved for future routing)
- `api_level`: default `34`, optional `35`
- `retry_failed`: re-run connected tests once

## Gradle commands
- Build APKs:
  - `./gradlew --no-daemon --stacktrace :app:assembleDebug :app:assembleDebugAndroidTest`
- Connected tests (smoke/full):
  - `./gradlew --no-daemon --stacktrace :app:connectedDebugAndroidTest`

## Artifacts uploaded (always)
- `runtime-emulator-artifacts` (includes `ci-artifacts/`, APKs, reports)
