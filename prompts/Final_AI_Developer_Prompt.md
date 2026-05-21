# Final AI Developer Prompt — EasyUI Guardian Launcher

You are an AI coding agent working on the **EasyUI Guardian Launcher** project.

## Project summary

Build an Android launcher-first child safety app.

The app helps parents create a safer, simpler, parent-approved phone surface for a child.

It is not a surveillance app, not a full parental-control replacement, and not a guaranteed Android lockdown system.

## Locked product promise

> Let your child use a phone without giving them the whole smartphone experience at once.

## Locked MVP

Build an offline-first Android launcher with:

- default launcher support
- child home screen
- parent PIN
- approved/allowed apps
- parent contact tile
- emergency tile
- Home Mode
- School Mode
- Sleep Mode
- parent dashboard
- local-only storage
- no backend
- no ads
- no analytics
- no location tracking
- no message/call monitoring

## Mandatory session-continuity instruction

Before modifying code, create or overwrite a root-level file:

```text
copilot_session.md
```

The file must include:

- current date/time
- sprint objective
- project summary
- docs reviewed
- repository state
- execution plan
- task checklist
- commands planned
- commands run
- test results
- blockers
- next steps

Update `copilot_session.md` as work progresses so another agent can continue without redoing discovery.

## Required documents to read first

Read these before implementation:

- `README.md`
- `docs/product/Project_Brief.md`
- `docs/product/Positioning.md`
- `docs/scope/MVP_Scope.md`
- `docs/scope/Roadmap.md`
- `docs/privacy_compliance/Guardrails_and_Feature_Rejection.md`
- `docs/privacy_compliance/Privacy_and_Child_Safety.md`
- `docs/privacy_compliance/Permissions_and_Data_Map.md`
- `docs/privacy_compliance/Android_Limitations_Notice.md`
- `docs/technical/Technical_Assumptions.md`
- `docs/technical/Architecture.md`
- `docs/testing/Tests.md`
- `docs/testing/QA_Checklist.md`

## Hard guardrails

Do not add:

- ad SDKs
- analytics SDKs
- backend/cloud dependency
- child account system
- location tracking
- SMS reading
- call log reading
- contact harvesting
- microphone monitoring
- call recording
- web history tracking
- device-owner mode
- accessibility-service workaround
- full lockdown claims

Do not use wording such as:

- impossible to bypass
- full lockdown
- total control
- blocks everything
- cannot be uninstalled
- child cannot access anything else

## Technical preference

Use native Android:

- Kotlin
- Jetpack Compose
- AndroidX
- local storage using DataStore or Room
- MVVM or unidirectional state pattern
- Gradle
- GitHub Actions for CI

If the repository already has a different valid Android stack, inspect first and adapt rather than blindly replacing.

## Implementation phases

### Phase 0 — Repository inspection

1. Inspect repository structure.
2. Identify current Android stack.
3. Read existing docs.
4. Create/update `copilot_session.md`.
5. Run baseline build/tests if possible.
6. Produce initial plan.

### Phase 1 — Launcher foundation

1. Implement launcher intent filters.
2. Create child home screen shell.
3. Create parent dashboard shell.
4. Add navigation.
5. Add basic theme.
6. Run build.

### Phase 2 — Local settings and PIN

1. Add local settings storage.
2. Add PIN setup.
3. Store PIN securely, not plaintext.
4. Add PIN verification.
5. Protect parent dashboard.
6. Add tests.

### Phase 3 — Allowed apps

1. List launchable installed apps.
2. Add app picker.
3. Store approved package names locally.
4. Show approved apps on child home.
5. Launch approved apps.
6. Add tests.

### Phase 4 — Modes

1. Add Home / School / Sleep mode.
2. Add mode-specific app assignment.
3. Add manual mode switching.
4. Filter child home tiles by active mode.
5. Add empty state.
6. Add tests.

### Phase 5 — Contacts and emergency

1. Add manual parent contact setup.
2. Add manual emergency contact setup.
3. Add parent call tile.
4. Add emergency tile.
5. Prefer dial intent over direct call permission.
6. Add validation and tests.

### Phase 6 — Onboarding

1. Add welcome screen.
2. Add default launcher setup guidance.
3. Add Android limitations explanation.
4. Add PIN setup.
5. Add contacts setup.
6. Add app selection.
7. Add completion screen.

### Phase 7 — Evidence and verification

1. Run build.
2. Run unit tests.
3. Run lint.
4. Run UI tests if emulator/device exists.
5. Generate evidence report.
6. Update `copilot_session.md`.
7. Provide GO / CONDITIONAL GO / NO-GO verdict.

## Evidence report required

Create an evidence report under:

```text
docs/_implementation/YYYYMMDD_guardian_mvp_sprint/
```

Include:

- summary
- changed files
- commands run
- test results
- screenshots if available
- known issues
- compliance notes
- next steps
- final verdict

## Completion rule

Do not say the task is complete unless:

- build result is known
- tests were run or limitations documented
- privacy guardrails were checked
- `copilot_session.md` was updated
- evidence report was created

If a step cannot be completed because of missing permissions, emulator, signing key, Play Console access, or physical device requirement, document the exact blocker and continue with all possible non-blocked work.
