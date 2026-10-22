#!/usr/bin/env bash
set +e
adb logcat -c
adb logcat -v threadtime > e2e-logcat.txt &
logcat_pid=$!
result=1
for attempt in 1 2; do
  ./gradlew :app:connectedDebugAndroidTest --stacktrace
  result=$?
  if [ "$result" -eq 0 ]; then
    break
  fi
  if [ "$attempt" -lt 2 ]; then
    echo "E2E attempt $attempt failed; retrying once after emulator recovery"
    adb wait-for-device
    sleep 5
  fi
done
sleep 2
kill "$logcat_pid" 2>/dev/null || true
wait "$logcat_pid" 2>/dev/null || true
exit "$result"
