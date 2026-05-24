# Troubleshooting Notes - Phase 2C

## Compilation Errors
- **Issue:** `kotlinx.coroutines.flow.combine` only supports up to 5 parameters in some versions/overloads.
- **Fix:** Used the array-based overload `combine(flow1, flow2, ..., flown) { array -> ... }` and cast elements to their expected types.
- **Issue:** `Column` doesn't support `horizontalArrangement`.
- **Fix:** Used `verticalArrangement` for `Column` and `horizontalArrangement` for `Row`.

## Test Failures
- **Issue:** `StateFlow` with `SharingStarted.Lazily` doesn't emit updates in tests unless collected.
- **Fix:** Subscribed to the flow in `backgroundScope` using `collect {}` before making assertions.
- **Issue:** Mocking `GuardianStatusService` required `coEvery` for suspend functions.
- **Fix:** Updated test to use `coEvery`.

## Tool Usage
- **Issue:** Multiple `replace` calls in one turn for the same file can cause race conditions or duplicate code if not careful.
- **Fix:** Used `write_file` for large-scale fixes to ensure file integrity.
