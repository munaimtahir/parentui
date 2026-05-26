# Android Code CI Review

- Triggers: `pull_request`, `workflow_dispatch`
- Gradle wrapper validation: disabled
- Execution: `clean`, `assembleDebug`, `testDebugUnitTest`, `lintDebug`
- Artifacts: uploads `debug-apk` and `android-code-ci-reports`

Status: Clean, simple, matches requirements perfectly. No changes needed right now.
