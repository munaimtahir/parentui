# Phase 2C - 06 Testing and Validation

## CI / Workflow Review
- GitHub workflow directory `.github/workflows` was missing.
- Added `android-build-test.yml` with support for:
  - Build on push/PR to `main`.
  - Manual trigger via `workflow_dispatch`.
  - JDK 17 setup with Gradle caching.
  - APK and test/lint reports uploading.

## Local Verification

| Check | Command | Result | Notes |
|---|---|---|---|
| Build | `./gradlew assembleDebug` | PASS | Verified after each phase. |
| Unit Tests | `./gradlew testDebugUnitTest` | PASS | New tests added for Setup Checklist. |
| Lint | `./gradlew lint` | PASS | Ran baseline and final check. |
| Check | `./gradlew check` | PASS | Comprehensive project health check. |

## New Tests Added
- `SetupChecklistTest.kt`: Verifies derived checklist state correctly reflects app configuration and guardian status.

## ADB Validation Status
- **Status:** Pending optional quick physical-device validation.
- **Recommended Checks:**
  - Verify "Finish Setup" section appears when PIN or contacts are missing.
  - Verify app picker search and category filters work.
  - Verify child home preview accurately reflects selected apps.
  - Verify "Reset Layout" restores HOME mode.
