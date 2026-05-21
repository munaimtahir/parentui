# Architecture

## Recommended architecture

Use a simple offline-first Android architecture.

```text
UI Layer
- Compose screens
- navigation
- state display

ViewModel Layer
- screen state
- user actions
- validation
- mode switching

Domain Layer
- app selection rules
- mode visibility rules
- PIN rules
- launcher tile rules

Data Layer
- local settings storage
- installed app reader
- contact number storage
```

## Major modules

### App module

Main Android app and launcher entry point.

### Core data

- local preferences
- app configuration
- mode assignment persistence

### Core domain

- allowed app filtering
- active mode resolution
- tile layout generation
- PIN validation rules

### Feature onboarding

- welcome
- default launcher guidance
- PIN setup
- parent contact setup
- app selection
- initial mode setup

### Feature child home

- child launcher screen
- mode-specific tiles
- parent/emergency tiles
- app launch handling

### Feature parent dashboard

- PIN gate
- layout editor
- allowed apps
- modes
- contacts
- security/settings
- setup limitations

## State model

The app should have one source of truth for:

- current active mode
- allowed apps
- tile layout
- contact settings
- lock state

## Offline-first rule

All MVP features must work without internet permission.

## Future backend boundary

If backend is added later, keep it outside MVP and behind explicit feature flags.
