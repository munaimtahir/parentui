# Agent Instructions

## Operating model

Development should be AI-agent assisted by default.

The agent must work autonomously where possible and ask the user only for unavoidable manual actions, such as:

- account login
- Play Console access
- signing key decisions
- sudo/system-level access
- secrets
- physical device testing

## Mandatory first action

At the start of every coding session, the agent must create or overwrite a root-level file:

```text
copilot_session.md
```

This file must include:

- project summary
- current sprint objective
- files reviewed
- execution plan
- task checklist
- commands planned
- commands run
- test results
- blockers
- next steps

The agent must update this file as work progresses.

## Before coding

The agent must read:

- `README.md`
- `docs/product/Project_Brief.md`
- `docs/scope/MVP_Scope.md`
- `docs/privacy_compliance/Guardrails_and_Feature_Rejection.md`
- `docs/privacy_compliance/Privacy_and_Child_Safety.md`
- `docs/privacy_compliance/Permissions_and_Data_Map.md`
- `docs/technical/Technical_Assumptions.md`
- `docs/testing/Tests.md`

## Verification rule

The agent must not claim success without evidence.

Every completed sprint must include:

- summary of changes
- changed files
- commands run
- test results
- failures
- limitations
- GO / CONDITIONAL GO / NO-GO verdict

## Privacy guardrail

The agent must not add:

- ad SDKs
- analytics SDKs
- location tracking
- SMS permissions
- call log permissions
- contact-reading permission
- microphone permission
- cloud backend
- child account system

unless explicitly requested in a later documented phase.

## Android limitations rule

The agent must not implement wording that claims full Android lockdown.

## Preferred behavior

- inspect repository before editing
- make small coherent commits
- keep docs updated
- generate evidence reports
- use subagents/delegates for independent audits where available
- continue without unnecessary confirmation
