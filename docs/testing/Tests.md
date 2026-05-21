# Tests

## Testing principle

Do not claim success without verification.

## Unit tests

Required MVP unit test areas:

- mode visibility rules
- allowed app selection logic
- tile layout generation
- PIN validation
- contact validation
- onboarding state transition
- empty-state handling

## UI tests

Required MVP UI flow tests:

1. First launch opens onboarding.
2. Parent sets PIN.
3. Parent adds parent contact.
4. Parent adds emergency contact.
5. Parent selects allowed apps.
6. Child home screen displays correct tiles.
7. Parent dashboard requires PIN.
8. School Mode hides games.
9. Sleep Mode shows parent/emergency only.
10. Home Mode shows approved general apps.

## Launcher behavior tests

Manual/emulator tests:

- app can be selected as default launcher
- pressing Home opens child home screen
- approved app tile launches selected app
- child cannot edit layout without PIN

## Privacy tests

Check:

- no internet permission unless explicitly justified
- no location permission
- no SMS permission
- no call log permission
- no contacts permission unless intentionally added
- no ad SDK
- no analytics SDK
- no crash/telemetry SDK unless documented

## CI tests

GitHub Actions should run:

- Gradle build
- unit tests
- lint
- Compose UI tests where possible
- artifact upload

## Evidence reports

Each sprint must generate an evidence report containing:

- commit hash
- commands run
- test results
- build result
- known failures
- screenshots if UI tested
- GO / CONDITIONAL GO / NO-GO verdict
