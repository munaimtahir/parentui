# Technical Assumptions

## Platform

Android native app.

Recommended stack:

- Kotlin
- Jetpack Compose
- AndroidX
- Room or DataStore for local persistence
- Hilt/Koin optional
- MVVM or unidirectional state pattern
- Gradle
- GitHub Actions for CI

## Launcher behavior

The app should implement launcher/home intent filters so it can be selected as default launcher.

It should provide clear parent guidance for default launcher setup.

## Local persistence

Store MVP settings locally:

- parent PIN hash/secure representation
- selected apps
- mode assignments
- active mode
- layout preferences
- parent/emergency number

Recommended:

- DataStore for preferences
- Room if structured mode/app mapping grows

## Security

- Do not store PIN in plaintext.
- Use secure hashing.
- Rate-limit PIN attempts locally if feasible.
- Provide clear PIN reset limitation.

## App launching

Use package manager to list launchable apps.

Use explicit launch intents to open approved apps.

Do not transmit installed app lists.

## Build flavors

Suggested:

- debug
- release

Optional later:

- demo
- internal

## Testing

Recommended tests:

- unit tests for mode/app filtering
- unit tests for PIN validation
- UI tests for onboarding
- UI tests for parent dashboard
- UI tests for child home screen mode switching
- emulator-based GitHub Actions workflow

## No backend in MVP

Do not add backend until a future phase explicitly requires:

- cloud backup
- parent remote dashboard
- multi-device sync
