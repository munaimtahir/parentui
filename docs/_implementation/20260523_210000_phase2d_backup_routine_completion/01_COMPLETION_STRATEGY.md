# Phase 2D & 3 - 01 Final Completion Strategy

## Objective
To complete the remaining roadmap items (Phase 2D and Phase 3) in a single cohesive push, resulting in a V1-ready application.

## Scope
### Phase 2D: Local Backup & PIN Recovery
- **Backup/Restore:** A simple way for parents to save their configuration.
- **PIN Recovery:** Educational content on how to reset the app if the PIN is forgotten.

### Phase 3: Routine Pack
- **Scheduling:** Automatic mode switching based on time of day.
- **Mode Expansion:** Bedtime (Sleep extension), Travel (Entertainment priority?), Exam (Extreme focus).
- **Quiet Mode Visuals:** Visual distinction for modes like Sleep or Bedtime.

## Implementation Map
1. **Data Layer:**
   - Update `SettingsRepository` to handle export/import.
   - Add `Schedule` persistence.
2. **Logic Layer:**
   - Create `RoutineManager` to handle time-based events.
   - Integrate routines into `LauncherViewModel`.
3. **UI Layer:**
   - Add "Advanced & Backup" tab to `ParentDashboardScreen`.
   - Add "Schedules" section to `AppsAndModesTab` or a new "Routines" tab.
4. **Testing:**
   - Unit tests for backup/restore logic.
   - Unit tests for schedule calculations.

## Exit Criteria
- Parent can export settings to a string (JSON) and import them back.
- Parent can set at least one time-based mode switch.
- All modes (Home, School, Sleep, Bedtime, Travel, Exam) are selectable.
- Build and tests pass.
