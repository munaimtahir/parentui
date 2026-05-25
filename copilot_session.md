# EasyUI — Copilot Session

- Sprint name: EasyUI School Mode Assignment Repair + Offline Closure Sprint
- Start time: 2026-05-26T02:06:58+05:00
- Device serial: 08357252AE006901
- Evidence folder: docs/_implementation/20260526_0206_easyui_school_mode_assignment_repair

## Repo
- Branch: main
- Commit: 7c99e76
- Working tree (tracked): clean; evidence folders untracked

## Goals
- Fix/verify School mode app assignment end-to-end on device
- Close manual offline (airplane-mode) evidence

## Phase checklist
- [x] Confirm ADB device availability
- [x] Create evidence folder structure
- [x] Capture repo cleanliness snapshot
- [ ] Reproduce School assignment and verify School mode shows assigned app
- [ ] Build/test/lint logs captured (only if code changes or to reconfirm)
- [ ] ADB evidence: assign -> visible in School -> launch -> HOME return -> remove
- [ ] Manual offline check evidence recorded
- [ ] Final report written

## Commands run (so far)
- adb devices -l
- adb -s 08357252AE006901 get-state
- git branch --show-current
- git rev-parse --short HEAD
- git status --short
- git diff --stat

## Notes / blockers
- OEM blocks ADB airplane-mode toggle; offline check must be manual.

## 2026-05-26 PIN reset
- Action: adb pm clear com.easyui.guardianlauncher (resets PIN + all app settings)
- Evidence: docs/_implementation/20260526_0206_easyui_school_mode_assignment_repair/05_logs/pm_clear_com.easyui.guardianlauncher.txt

## 2026-05-26 School mode verification (re-run, PIN=0000)
- pm clear performed to reset unknown PIN.
- Onboarding completed via ADB; Parent Dashboard PIN set to 0000.
- Approved app: com.adobe.reader (Adobe Acrobat)
- Assigned to School mode from Parent Dashboard → Apps → School.
- Verified on child home in School mode: Adobe tile appears.
- Launched Adobe from School mode; verified foreground package com.adobe.reader.
- Pressed HOME; verified HOME resolver and focus return to com.easyui.guardianlauncher/.MainActivity.
- Removed Adobe from School mode; verified safe empty state copy shown.

Build evidence (no code changes):
- assembleDebug: PASS (log saved)
- testDebugUnitTest: PASS (log saved)
- lintDebug: PASS (log saved)
