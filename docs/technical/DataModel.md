# Data Model

## MVP entities

### AppSettings

| Field | Type | Notes |
|---|---|---|
| id | string | singleton/local |
| activeMode | enum | HOME, SCHOOL, SLEEP |
| parentPinHash | string | never plaintext |
| onboardingComplete | boolean | setup status |
| theme | string | optional |

### GuardianContact

| Field | Type | Notes |
|---|---|---|
| id | string | local |
| label | string | e.g., Mom, Father |
| phoneNumber | string | manually entered |
| isPrimary | boolean | parent tile |

### EmergencyContact

| Field | Type | Notes |
|---|---|---|
| id | string | local |
| label | string | e.g., Emergency |
| phoneNumber | string | manually entered |
| enabled | boolean | visible on child screen |

### InstalledAppInfo

Runtime-derived, not necessarily stored fully.

| Field | Type | Notes |
|---|---|---|
| packageName | string | Android package |
| appLabel | string | display name |
| iconRef | platform | icon |
| isLaunchable | boolean | runtime |

### AllowedApp

| Field | Type | Notes |
|---|---|---|
| packageName | string | stored |
| displayLabelOverride | string? | optional |
| visible | boolean | shown in launcher |
| category | enum/string | learning, games, communication, camera, utility |
| createdAt | timestamp | optional |

### ModeAssignment

| Field | Type | Notes |
|---|---|---|
| mode | enum | HOME, SCHOOL, SLEEP |
| packageName | string | approved app |
| enabled | boolean | visible in mode |

### TileLayout

| Field | Type | Notes |
|---|---|---|
| slotIndex | int | fixed grid position |
| tileType | enum | APP, PARENT_CALL, EMERGENCY, CLOCK |
| packageName | string? | for app tile |
| contactId | string? | for contact tile |
| mode | enum? | optional mode-specific layout |

## Mode enum

```text
HOME
SCHOOL
SLEEP
```

Future:

```text
TRAVEL
EXAM
```

## Category enum

Initial categories:

```text
LEARNING
GAMES
COMMUNICATION
CAMERA_PHOTOS
UTILITIES
OTHER
```

## Storage note

Prefer storing only what is needed:

- package names
- local labels
- mode flags
- contact numbers
- layout slots

Do not store full installed app inventory unless needed.
