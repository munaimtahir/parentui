# EasyUI Architecture Notes (Mega Run)

Repo: `/home/munaim/Documents/github/parentui`  
Evidence root: `docs/_implementation/20260524_2247_easyui_complete_scoped_v1_expansion/`

## Current observed structure (practical)

EasyUI is a single-app, offline-first launcher implemented with Kotlin + Jetpack Compose. The current codebase already has:
- Compose screens for onboarding, child home, and parent dashboard.
- A `LauncherViewModel` as the primary UI state/orchestration point.
- A `SettingsRepository` backed by DataStore for persisted local settings.
- Android-facing providers/helpers for launcher/default-home status detection.

## Target layering (keep simple, avoid rewrites)

### UI layer
- Compose screens, components, navigation routes.
- Render state only; avoid direct persistence calls from Composables.
- Example responsibilities:
  - show Setup Health cards
  - show Ready-to-Handover checklist
  - show child home tiles and mode selector

### ViewModel layer
- Single source of UI state per “area” (start with existing `LauncherViewModel`).
- Handles user actions and validation, then delegates to repositories/providers.
- Emits simple `StateFlow` / `Flow` state that screens can collect.

### Domain layer (lightweight)
- Pure Kotlin rules and mappers (no Android context).
- Example responsibilities:
  - readiness scoring/checklist computation
  - mode visibility rules (MVP vs optional V1)
  - safe defaults for empty states and copy
  - backup model validation rules

### Data layer
- DataStore-backed repositories for settings/configuration.
- Android providers for:
  - installed apps query (package manager)
  - default HOME resolver detection (package manager)
  - backup file IO (Storage Access Framework when implemented)

## Sources of truth (must stay single)

The app should have exactly one clear persisted source for:
- Active mode (Home/School/Sleep; optional Exam/Travel behind flags).
- Approved apps (store package names and local metadata only).
- Per-mode app visibility/assignment (packageName → set of modes).
- Contacts (parent/emergency phone numbers + labels).
- Onboarding completion and limitations acknowledgement.
- Default launcher last-known status (cached display only; real-time check via resolver).
- Optional feature flags (exam/travel/scheduler/age presets/backup/layout customization).
- Backup/export data model (local-only; do not export PIN or PIN hash).

## Safety/Scope guardrails (architectural implications)

- No backend/cloud: repositories must not depend on network.
- No surveillance features: do not add background trackers, content readers, or analytics SDKs.
- No “force default launcher”: default-home setup is via safe Settings intents only.
- No raw PIN storage/export: store hashed PIN only; do not export/import it in backup.

