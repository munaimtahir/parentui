# Setup

## Repository expectation

The project should be a native Android repository.

Recommended initial structure:

```text
easyui-guardian-launcher/
  app/
  docs/
  prompts/
  templates/
  .github/workflows/
  README.md
  copilot_session.md
```

## First development steps

1. Create Android project.
2. Configure Kotlin and Jetpack Compose.
3. Add launcher intent filters.
4. Add basic navigation.
5. Implement local settings storage.
6. Implement onboarding.
7. Implement child home screen.
8. Implement parent PIN gate.
9. Implement app picker.
10. Implement mode filtering.
11. Add tests.
12. Build release candidate.

## Local build commands

The AI agent should detect the actual Gradle setup first.

Typical commands:

```bash
./gradlew clean
./gradlew assembleDebug
./gradlew test
./gradlew connectedDebugAndroidTest
```

If no emulator/device is available, the agent must say so honestly and still run non-device tests.

## GitHub Actions

Add manual workflow dispatch for:

- unit tests
- debug build
- lint
- emulator UI tests when practical
- artifact upload

## Environment

MVP should not require secrets or backend environment variables.

If secrets appear necessary, the agent must stop and justify them.
