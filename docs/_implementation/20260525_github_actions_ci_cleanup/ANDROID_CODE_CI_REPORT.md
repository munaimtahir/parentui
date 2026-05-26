# Android Code CI Report (2026-05-25)

## Workflow
- Name: `Android Code CI`
- File: `.github/workflows/android-code-ci.yml`
- Latest passing run: https://github.com/munaimtahir/parentui/actions/runs/26425787104

## Gradle commands
- `./gradlew --no-daemon --stacktrace :app:clean`
- `./gradlew --no-daemon --stacktrace :app:assembleDebug`
- `./gradlew --no-daemon --stacktrace :app:testDebugUnitTest`
- `./gradlew --no-daemon --stacktrace :app:lintDebug`

## Artifacts uploaded (always)
- `debug-apk`
- `android-code-ci-reports`
