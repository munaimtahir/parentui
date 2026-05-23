# Phase E — Non-ADB Testing and Hardening

## Gradle task inventory
Listed via:
- `./gradlew tasks` ✅

Notable available tasks:
- `clean`, `assembleDebug`, `testDebugUnitTest`, `lint`, `check`

Not configured in this repo (therefore not run):
- `detekt`, `ktlintCheck`, `koverVerify`, `jacocoTestReport` (no tasks present)

## Verification table

| Check | Command | Result | Notes |
|---|---|---|---|
| tasks | `./gradlew tasks` | ✅ | Verified available non-ADB tasks |
| clean | `./gradlew clean` | ✅ | Run as part of combined command below |
| assembleDebug | `./gradlew assembleDebug` | ✅ | Compiles + packages debug APK |
| testDebugUnitTest | `./gradlew testDebugUnitTest` | ✅ | JVM unit tests only |
| lint | `./gradlew lint` | ✅ | HTML report at `app/build/reports/lint-results-debug.html` |
| check | `./gradlew check` | ✅ | Runs lint + unit tests (debug + release) |
| connectedDebugAndroidTest | NOT RUN | ⛔ | Deferred by sprint rule (ADB/device) |
| ADB physical-device smoke | NOT RUN | ⛔ | Deferred by user |

## Command log (executed)
- `./gradlew clean assembleDebug testDebugUnitTest` ✅
- `./gradlew lint` ✅
- `./gradlew check` ✅

## Notes
- Kotlin deprecation warnings remain (ArrowBack + LinearProgressIndicator overload). Build/test/lint still succeed; no functional impact for this sprint.
- No GitHub workflows were found in `.github/workflows/` at time of Phase E review.

