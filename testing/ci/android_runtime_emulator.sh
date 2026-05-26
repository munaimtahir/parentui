#!/usr/bin/env bash
set -euxo pipefail

mkdir -p ci-artifacts/meta
{
  echo "test_level=${TEST_LEVEL:-smoke}"
  echo "runtime_target=${RUNTIME_TARGET:-all}"
  echo "api_level=${API_LEVEL:-34}"
  echo "retry_failed=${RETRY_FAILED:-false}"
} > ci-artifacts/meta/inputs.txt

dump_on_exit() {
  set +e
  mkdir -p ci-artifacts/logs ci-artifacts/screenshots ci-artifacts/ui

  timeout 20s adb devices -l > ci-artifacts/logs/adb_devices_exit.txt || true
  timeout 20s adb logcat -d > ci-artifacts/logs/logcat_exit.txt || true
  timeout 20s adb exec-out screencap -p > ci-artifacts/screenshots/screen_exit.png || true

  timeout 20s adb shell uiautomator dump /sdcard/window_dump_exit.xml || true
  timeout 20s adb pull /sdcard/window_dump_exit.xml ci-artifacts/ui/window_dump_exit.xml || true
}
trap dump_on_exit EXIT

timeout 20s adb devices -l | tee ci-artifacts/meta/adb_devices_pre.txt || true

timeout 20s adb shell settings put global window_animation_scale 0 || true
timeout 20s adb shell settings put global transition_animation_scale 0 || true
timeout 20s adb shell settings put global animator_duration_scale 0 || true

if [[ "${TEST_LEVEL:-smoke}" == "screenshots_only" ]]; then
  timeout 120s adb install -r app/build/outputs/apk/debug/app-debug.apk
  timeout 30s adb shell am start -W -n "${MAIN_ACTIVITY:?Missing MAIN_ACTIVITY}" | tee ci-artifacts/meta/am_start.txt || true
  sleep 8
else
  timeout 30s adb shell am start -W -n "${MAIN_ACTIVITY:?Missing MAIN_ACTIVITY}" | tee ci-artifacts/meta/am_start.txt || true

  if [[ "${RETRY_FAILED:-false}" == "true" ]]; then
    ./gradlew --no-daemon --stacktrace :app:connectedDebugAndroidTest || ./gradlew --no-daemon --stacktrace :app:connectedDebugAndroidTest
  else
    ./gradlew --no-daemon --stacktrace :app:connectedDebugAndroidTest
  fi
fi

