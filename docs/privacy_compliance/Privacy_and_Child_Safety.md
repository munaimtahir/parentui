# Privacy and Child Safety

## Privacy promise

EasyUI Guardian Launcher helps parents simplify and structure the child’s phone experience without secretly collecting the child’s personal activity.

## Default privacy model

MVP must be:

- offline-first
- no account required
- no backend
- no cloud sync
- no ads
- no third-party analytics
- no location tracking
- no message monitoring
- no browser history tracking
- no call recording
- no microphone monitoring

## Data stored locally

Possible local data:

- parent PIN hash or secure representation
- selected app package names
- selected mode
- mode-to-app assignments
- parent contact label/number
- emergency contact label/number
- layout preferences
- theme preference

## Data that should not be collected

Do not collect:

- child location
- contacts list
- SMS content
- call logs
- microphone audio
- camera images
- browsing history
- advertising ID
- device identifiers for tracking
- behavioral analytics
- school identity
- child name unless absolutely needed later

## Permission principle

Request only permissions needed for visible user-facing features.

Prefer Android intents over sensitive permissions where possible.

Examples:

- For calling: prefer dial intent if direct call permission is not necessary.
- For contacts: prefer manual number entry instead of reading contacts.
- For apps list: use package visibility declarations carefully and only as needed.
- For notifications: avoid notification access in MVP.

## Parent transparency

The app should clearly explain:

- what data is stored
- that settings are local
- that the app does not secretly monitor messages or location
- that Android system-level restrictions require system tools such as Family Link

## Child safety rule

No feature should create a private communication channel between the app and a child without parent awareness.
