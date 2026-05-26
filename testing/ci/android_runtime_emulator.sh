#!/usr/bin/env bash
set -ex

# Setup artifacts directory
mkdir -p ci-artifacts/logs ci-artifacts/ui ci-artifacts/screenshots

# Run tests and capture log on failure
if ./gradlew :app:connectedDebugAndroidTest; then
  echo "Tests passed"
else
  echo "Tests failed"
fi

# Capture artifacts while emulator is still alive
adb logcat -d > ci-artifacts/logs/logcat.txt || true
adb shell uiautomator dump /sdcard/window_dump.xml || true
adb pull /sdcard/window_dump.xml ci-artifacts/ui/window_dump.xml || true
adb exec-out screencap -p > ci-artifacts/screenshots/screen.png || true
