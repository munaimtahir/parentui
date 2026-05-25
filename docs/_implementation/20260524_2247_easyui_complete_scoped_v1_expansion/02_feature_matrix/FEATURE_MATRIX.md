# Feature Matrix — Mega Run Snapshot

Evidence root: `docs/_implementation/20260524_2247_easyui_complete_scoped_v1_expansion/`

Status values: PASS / PARTIAL / FAIL / BLOCKED / NOT TESTED / DESIGN ONLY

| Feature | Scope | Built | Main screens/files | Persistence | Unit tested | ADB observed | Screenshot evidence | Status | Notes |
|---|---|---:|---|---|---:|---:|---|---|---|
| Build (assembleDebug) | MVP | Yes | Gradle | N/A | N/A | N/A | N/A | PASS | `assembleDebug` succeeds. |
| Unit tests (testDebugUnitTest) | MVP | Yes | Gradle | N/A | Yes | N/A | N/A | PASS | `testDebugUnitTest` succeeds. |
| Lint (lintDebug) | MVP | Yes | Gradle | N/A | N/A | N/A | N/A | PASS | Fixed exact-alarm lint error by safe fallback. |
| Launcher intent filters | MVP | Yes | `app/src/main/AndroidManifest.xml` | N/A | N/A | N/A | N/A | PASS | MAIN + HOME launcher behavior exists in manifest. |
| Default Home detection | MVP | Yes | `AndroidLauncherStatusProvider.kt`, `DefaultLauncherStatusCard.kt` | Cached + real-time query | No | Yes | `04_screenshots/easyui_20260524_230055_completion.png` | PASS | Shows “Default launcher: Not set” on device and provides Settings handoff. |
| Default Home settings handoff | MVP | Yes | `DefaultLauncherStatusCard.kt`, onboarding completion | N/A | No | PARTIAL | `04_screenshots/easyui_20260524_230055_completion.png` | PARTIAL | Button present; OEM Settings UX varies (not fully verified end-to-end). |
| Child home screen | MVP | Yes | `ChildHomeScreen.kt`, `LauncherViewModel.kt` | DataStore | No | Yes | `04_screenshots/easyui_20260524_230641_child_home_before_parent_setup.png` | PASS | Renders and shows setup banner when not default home. |
| Child warning when not default home | MVP | Yes | `ChildHomeScreen.kt` | N/A | No | Yes | `04_screenshots/easyui_20260524_230641_child_home_before_parent_setup.png` | PASS | Calm banner + PIN-gated parent setup entry. |
| Parent dashboard PIN gate | MVP | Yes | `ParentDashboardScreen.kt`, PIN UI | DataStore (hashed PIN) | No | Yes | `04_screenshots/easyui_20260524_230646_parent_setup_pin_gate2.png` | PASS | PIN keypad gate appears when opening parent area. |
| Parent dashboard core | MVP | Yes | `ParentDashboardScreen.kt` | DataStore | No | Yes | `04_screenshots/easyui_20260524_230652_parent_dashboard_unlocked.png` | PASS | Tabs/sections render. |
| Setup Health screen | V1 | Partial | `SetupHealthScreen.kt` | DataStore | No | Yes | `04_screenshots/easyui_20260524_230701_setup_health_screen2.png` | PASS | Screen opens from parent dashboard and shows default launcher + limitations ack. |
| Ready-to-Handover checklist | V1 | Partial | `ReadyToHandoverScreen.kt` | Computed + DataStore | No | Yes | `04_screenshots/easyui_20260524_230706_ready_checklist_screen2.png` | PASS | Screen opens and shows remaining items + warnings (no “full lockdown” claims). |
| Limitations acknowledgement | MVP/V1 | Partial | `SetupLimitationsScreen.kt`, `SettingsRepository.kt` | DataStore | No | Yes | `04_screenshots/easyui_20260524_230701_setup_health_screen2.png` | PARTIAL | Flag exists + surfaced; full UX polish pending. |
| Allowed apps selection | MVP | Yes | Onboarding app selection | DataStore | No | PARTIAL | N/A | PARTIAL | Not fully re-verified in this run (onboarding selection step skipped). |
| Per-mode assignments (Home/School/Sleep) | MVP | Yes | Parent dashboard mode assignment | DataStore | No | NOT TESTED | N/A | NOT TESTED | Previously verified in Phase 2C; not re-tested in this mega snapshot. |
| Mode selector (MVP only) | MVP | Yes | child home mode selector | DataStore | No | Yes | `05_ui_dumps/easyui_20260524_230120_after_finish_tap.xml` | PASS | Only Home/School/Sleep shown (extra modes hidden). |
| Sleep mode contact-only | MVP | Yes | `LauncherViewModel.kt` | DataStore | No | Yes | `05_ui_dumps/easyui_20260524_225616_launch.xml` | PASS | App tiles hidden; intent is clear. |
| Parent tile ACTION_DIAL | MVP | Yes | child home tiles | DataStore contacts | No | Yes | `05_ui_dumps/easyui_20260524_230751_after_parent_tile_tap.xml` | PASS | Foreground package becomes dialer app (`com.sh.smart.caller`). |
| Emergency tile ACTION_DIAL | MVP | Yes | child home tiles | DataStore contacts | No | Yes | `05_ui_dumps/easyui_20260524_230759_after_emergency_tile_tap.xml` | PASS | Foreground package becomes dialer app (`com.sh.smart.caller`). |
| Offline-first (no backend required) | MVP | Yes | overall | DataStore | No | PARTIAL | N/A | PARTIAL | ADB airplane-mode broadcast blocked; manual offline check required. |
| Routine scheduling | V1 | Partial | `RoutineManager.kt` + receiver | DataStore (?) | No | NOT TESTED | N/A | PARTIAL | Code exists; no end-user UX verified in this snapshot. |
| Age presets | V1 | No | N/A | N/A | No | N/A | N/A | NOT BUILT | Deferred (not implemented in this run). |
| Backup/export/import UX | V1 | Partial | `SettingsRepository.kt` | JSON export/import | No | NOT TESTED | N/A | PARTIAL | PIN/hash not exported/imported (safer). UI flow not verified. |
| Exam/Travel modes | V1 | No | N/A | N/A | No | N/A | N/A | NOT BUILT | Kept hidden (avoid MVP confusion). |
| Layout lock/customization | V1 | Partial | Parent dashboard lock tab | DataStore | No | NOT TESTED | N/A | PARTIAL | Not re-verified; needs explicit pilot UX. |

