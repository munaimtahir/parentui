# SCOPE SAFETY REVIEW — EasyUI Guardian Launcher

This mega run must remain inside the EasyUI “guided-control launcher” boundary.

## Confirmed exclusions (must not add)
- No ads
- No analytics SDKs
- No cloud dependency / backend requirement
- No child account system
- No location tracking
- No message monitoring
- No call recording
- No microphone monitoring
- No hidden camera access
- No browser history tracking
- No “impossible to bypass” / “full lockdown” claims
- No consumer device-owner / kiosk-mode enforcement
- No unsafe ADB/system hacks to force default launcher

## Confirmed inclusions (allowed / desired)
- Offline-first, local-only settings
- Default launcher guidance + verification (safe Settings intents)
- Child-safe home screen (no full app drawer exposure)
- Parent PIN gate for parent controls
- Approved apps + per-mode assignment (Home/School/Sleep)
- Parent contact tile + emergency tile (prefer `ACTION_DIAL`, no `CALL_PHONE` permission)
- Honest limitations copy + Family Link recommendation
- Optional V1 enhancements:
  - backup/export/import (local JSON)
  - routine scheduling (local-only, bounded)
  - age presets (starting points only)
  - optional Exam/Travel modes (no surveillance; no location)

