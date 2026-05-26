# Android Runtime Emulator CI Review

- Triggers: `pull_request`, `workflow_dispatch`
- Setup: JDK 17, KVM enabled
- Builds: `assembleDebug`, `assembleDebugAndroidTest`
- Execution: Uses `reactivecircus/android-emulator-runner@v2` running `testing/ci/android_runtime_emulator.sh`
- Artifacts: uploads `emulator-verification-artifacts` (reports, apks, and ci-artifacts folder)

Status: Looks well configured. Need to ensure `testing/ci/android_runtime_emulator.sh` captures screenshots and logcat.
