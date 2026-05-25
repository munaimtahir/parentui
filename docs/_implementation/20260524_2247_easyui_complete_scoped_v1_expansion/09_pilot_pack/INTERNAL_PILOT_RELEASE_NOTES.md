# Internal Family Pilot Release Notes

Build name: EasyUI Internal Family Pilot (Debug)  
Date/time: 2026-05-24 (local)  
Commit: `53d2135` (working tree dirty; see `copilot_session.md`)  
APK: `docs/_implementation/20260524_2247_easyui_complete_scoped_v1_expansion/08_apk/easyui-internal-family-pilot-debug-20260524-2308.apk`

## Included (core)
- Onboarding flow (PIN + contacts + app approval)
- Child home screen with Home/School/Sleep selector (MVP modes)
- Parent dashboard (PIN-gated)
- Default Home status guidance + Settings handoff
- Parent/Emergency tiles use ACTION_DIAL (no CALL_PHONE permission)

## Added/Polished in this mega-run snapshot
- Setup Health screen entry from parent dashboard
- Ready-to-Handover checklist entry from parent dashboard
- Limitations acknowledgement persistence (DataStore)
- Backup safety improvement: PIN/PIN-hash not exported/imported
- Lint fix for exact-alarm scheduling (safe fallback)

## Intentionally excluded / deferred (V1)
- Age presets
- Routine scheduling UX (only partial backend pieces exist)
- Exam/Travel modes
- Backup import preview UX
- Advanced app picker categories/bulk actions

## Required manual setup
- Parent must set EasyUI as the default Home app in Android Settings.

## Known limitations
See `docs/_implementation/20260524_2247_easyui_complete_scoped_v1_expansion/09_pilot_pack/PILOT_KNOWN_LIMITATIONS.md`.

## Pilot size / duration suggestion
- Start with 1–2 devices for 3–7 days.

## Feedback collection
- Use `PILOT_OBSERVATION_FORM.md` per device/session.

**This build is for internal family pilot only. It is not ready for public release or Play Store closed testing.**

