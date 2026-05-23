# Troubleshooting Notes (Phase 2 Guardian Layer)

## Phase 0 (Baseline)
- `./gradlew assembleDebug` printed: “SDK processing… SDK XML file of version 4 was encountered.”
  - Likely caused by Android SDK/command-line tools version mismatch in the environment.
  - Build still succeeded; no action taken in Phase 0.
- Kotlin compile warnings (deprecations):
  - `Icons.Default.ArrowBack` deprecated (prefer `Icons.AutoMirrored.Filled.ArrowBack`)
  - `LinearProgressIndicator(Float, ...)` overload deprecated (prefer lambda `progress = { ... }`)
  - Build still succeeded; defer changes unless touched by sprint work.

## Phase E (Verification)
- `./gradlew check` emits the same deprecation warnings during `compileReleaseKotlin`.
  - Non-blocking: build, unit tests, and lint all pass.
- No `.github/workflows/*` were present to review in this repo snapshot.
