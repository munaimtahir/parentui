# EasyUI Guardian Launcher — Testing Matrix

## Purpose
Lock the current application state using local checks, GitHub Code CI, and GitHub Runtime Emulator CI.

## Test layers
| Layer | Tool | Required | Current status | Evidence |
|---|---|---:|---|---|
| Build | Gradle assembleDebug | Yes | Pending | |
| Unit | testDebugUnitTest | Yes | Pending | |
| Static | lintDebug | Yes | Pending | |
| Instrumented | connectedDebugAndroidTest | Yes if available | Pending | |
| Runtime | Emulator install/launch | Yes | Pending | |
| ADB smoke | screenshot/logcat/UI dump | Yes | Pending | |
| Launcher behavior | HOME/default launcher guidance | Yes | Pending | |
| Product flow | onboarding/PIN/apps/modes/contacts | Yes | Pending | |
| Privacy | manifest/permissions | Yes | Pending | |
| UX | child home/readability/navigation | Yes | Pending | |

## Critical user-reported issues to protect
- Added apps on additional pages must remain visible/reachable.
- Layout lock must not place confusing lock icons on every app.
- Caregiver settings access must work even when PIN is not set, according to intended onboarding logic.
- Theme changes must persist.
- Font/layout settings must either work or be removed.
- Onboarding and caregiver settings must not conflict.
- Parent/emergency shortcut setup must be reflected on child home.

## Final gate
The app can only be marked GO if:
- Code CI passes.
- Runtime Emulator CI passes.
- Critical MVP flows are tested.
- Known persistent bugs are either fixed or clearly documented as non-blocking.
