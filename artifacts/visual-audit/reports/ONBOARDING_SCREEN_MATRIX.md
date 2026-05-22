# Onboarding Screen Matrix

This matrix maps the test status of all onboarding screens across the five device/display profiles.

| Screen Name | Profile A (Normal) | Profile B (Small) | Profile C (Small + Large Font) | Profile D (Medium + Large Font) | Profile E (3-Button / Default) |
|---|---|---|---|---|---|
| **1. Welcome Screen** | PASS (Scrolled) | PASS (Scrolled) | PASS (Scrolled) | PASS (Scrolled) | PASS |
| **2. Parent PIN Creation** | **FAIL** (Clipped) | **FAIL** (Clipped) | **FAIL** (Clipped/Large Font) | **FAIL** (Clipped) | PASS |
| **3. Contact Shortcuts** | **BLOCKED** | **BLOCKED** | **BLOCKED** | **BLOCKED** | PASS |
| **4. App Selection** | **BLOCKED** | **BLOCKED** | **BLOCKED** | **BLOCKED** | PASS |
| **5. Completion Screen** | **BLOCKED** | **BLOCKED** | **BLOCKED** | **BLOCKED** | PASS |

### Legend:
- **PASS**: The screen loaded correctly, all text was readable, and all critical buttons/inputs were accessible and functional.
- **PASS (Scrolled)**: The button/content was initially hidden but became accessible after scrolling.
- **FAIL**: Essential elements (e.g., keypad, continue buttons) were clipped or missing from the layout, blocking completion of the screen.
- **BLOCKED**: Testing this screen was blocked by a failure on a preceding onboarding screen.
