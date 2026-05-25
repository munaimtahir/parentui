# Gemini CLI Context — EasyUI Guardian Launcher

## Project Overview

**EasyUI Guardian Launcher** is a privacy-first, offline-first Android launcher designed to provide a safer, simpler, and parent-approved home screen for children. It focuses on "guided independence" rather than surveillance, complementing system tools like Google Family Link.

### Key Technologies
- **Platform:** Android Native
- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (Material 3)
- **Architecture:** MVVM or Unidirectional State Pattern
- **Persistence:** Jetpack DataStore Preferences (for simple settings) and potentially Room (for structured data)
- **Dependency Injection:** Hilt/Koin (Optional, to be verified)
- **Build System:** Gradle (Kotlin DSL)

### MVP Core Features
- Default launcher intent filtering.
- Fixed tile-based child home screen (apps, parent contact, emergency).
- Parent PIN protection for settings and layout changes.
- Manual mode switching (Home, School, Sleep).
- Offline-first: No accounts, no backend, no ads, no analytics.

---

## Building and Running

### Development Commands
- **Build Project:** `./gradlew assembleDebug`
- **Run Unit Tests:** `./gradlew test`
- **Run Instrumentation Tests:** `./gradlew connectedAndroidTest`
- **Linting/Static Analysis:** `./gradlew lint` (TODO: Verify specific lint configurations)
- **Install on Device:** `./gradlew installDebug`

### Environment Requirements
- Android SDK (Target 34, Min 26)
- JDK 17
- Gradle 8.x (Inferred from standard Android templates)

---

## Development Conventions

### AI-Agent Workflow
- **Session Tracking:** Every session MUST maintain `copilot_session.md` at the root.
- **Evidence-Based Success:** Claim success only after providing test results and evidence reports.
- **Surgical Edits:** Make small, coherent changes and update documentation alongside code.
- **No-Go Patterns:** Avoid hacks, suppressed warnings, or non-idiomatic workarounds.

### Privacy & Security Mandates
- **Privacy-First:** DO NOT add ad SDKs, analytics, location tracking, or invasive permissions (SMS, Call Logs, Microphone).
- **Offline-First:** No cloud backends or account systems.
- **Security:** PINs must be stored securely (hashed) using DataStore. No plaintext storage.
- **Honesty:** Do not claim full "lockdown" or "un-bypassable" security.

### Testing Standards
- **Reproduction First:** Fixes must be preceded by a reproduction script or test.
- **Coverage:** Aim for unit tests on logic (modes, PIN) and UI tests for critical flows (onboarding, home screen).
- **Verification:** Run project-specific build and lint commands before concluding tasks.

---

## Key Project Documents
- `docs/product/Project_Brief.md`: Product concept and thesis.
- `docs/scope/MVP_Scope.md`: Detailed feature list for the current phase.
- `docs/technical/Technical_Assumptions.md`: Architectural and stack guidelines.
- `docs/privacy_compliance/`: Critical constraints on data and permissions.
- `docs/agent/Agent.md`: Detailed AI-agent instructions and expectations.
