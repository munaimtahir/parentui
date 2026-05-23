# Onboarding Layout Inspection

This document summarizes the code inspection of the EasyUI Guardian Launcher onboarding screens.

## Files Inspected
1. `app/src/main/java/com/easyui/guardianlauncher/ui/components/OnboardingStepScaffold.kt`
2. `app/src/main/java/com/easyui/guardianlauncher/ui/screens/onboarding/OnboardingScreen.kt`
3. `app/src/main/java/com/easyui/guardianlauncher/ui/components/CommonComponents.kt` (for `PinKeypad`)

## Screens & Layout Patterns

### 1. Onboarding Scaffold (`OnboardingStepScaffold`)
- **Scaffold Pattern**: Implements a standard `Scaffold` with:
  - **topBar**: Fixed `TopAppBar` with title and optional back navigation button + `LinearProgressIndicator` (progress bar).
  - **bottomBar**: Fixed action area containing primary and optional secondary buttons.
  - **content**: Either scrollable (uses `.verticalScroll(rememberScrollState())`) or non-scrollable.
- **Paddings**:
  - The bottom bar uses `.navigationBarsPadding().imePadding()`.
  - When `scrollable = true`, a `Spacer(Modifier.height(96.dp))` is added at the bottom of the scrollable content to ensure scrollable items can scroll past the bottom bar area.
- **Tags**:
  - Scrollable container: `onboarding_scroll_container`
  - Back button: `onboarding_back_button`
  - Primary button: `onboarding_primary_button`
  - Secondary button: `onboarding_secondary_button`
  - Progress bar: `onboarding_progress`

---

### 2. Welcome Screen (`WelcomeStep`)
- **Scaffold Usage**: Uses `OnboardingStepScaffold` with default `scrollable = true`.
- **Primary Action**: "Get Started" (navigates to PIN creation).
- **Test Tag**: `onboarding_welcome_screen` on the scaffold.
- **Risks**: Text length of the system disclaimer card may cause scrolling on smaller/large-font devices. However, because it's wrapped in `OnboardingStepScaffold(scrollable = true)`, it should scroll correctly.

---

### 3. PIN Create Screen (`PinSetupStep` - Stage 1)
- **Scaffold Usage**: Uses `OnboardingStepScaffold` with default `scrollable = true`.
- **Primary Action**: "Continue" (disabled until 4+ digits entered).
- **Keypad**: Uses `PinKeypad` component.
- **Test Tags**:
  - Screen tag: `onboarding_pin_screen`
  - Pin dots row: `pin_dots`
  - Keypad digits: `pin_digit_0` to `pin_digit_9`
  - Backspace: `pin_backspace`
  - Clear: `pin_clear`
- **Risks**: Having a custom keypad inside a vertically scrollable column can sometimes cause the keypad to scroll off-screen if other elements take up too much vertical space. Since the descriptions and cards are short, it should fit.

---

### 4. PIN Confirm Screen (`PinSetupStep` - Stage 2)
- **Scaffold Usage**: Uses `OnboardingStepScaffold` with default `scrollable = true`.
- **Primary Action**: "Continue" (disabled until 4+ digits entered).
- **Keypad**: Uses `PinKeypad` component.
- **Test Tags**:
  - Screen tag: `onboarding_pin_confirm_screen`
- **Risks**: Same as PIN Create. Back navigation should return to PIN Create.

---

### 5. Contact Setup Screen (`ContactSetupStep`)
- **Scaffold Usage**: Uses `OnboardingStepScaffold` with default `scrollable = true`.
- **Primary Action**: "Save & Next" (navigates to App Selection).
- **Input Fields**:
  - Parent name: `parent_name_input`
  - Parent phone: `parent_phone_input`
  - Emergency name (optional, default visible): `emergency_name_input`
  - Emergency phone (optional, default visible): `emergency_phone_input`
- **Test Tags**:
  - Screen tag: `onboarding_contacts_screen`
- **Risks**: Multiple input fields can trigger the soft keyboard (IME). Since the screen has `imePadding()` on the bottom action bar and is scrollable, the focused fields must remain visible and the bottom button must not block the inputs.

---

### 6. App Selection Screen (`AppSelectionStep`)
- **Scaffold Usage**: Uses `OnboardingStepScaffold` with `scrollable = false`.
- **Primary Action**: "Continue" (navigates to Completion).
- **List Pattern**: Uses a `LazyColumn` with `.weight(1f)` for list elements.
- **Test Tags**:
  - Screen tag: `onboarding_app_selection_screen`
  - List: `app_selection_list`
  - Row: `app_selection_row`
  - Checkbox: `app_selection_checkbox`
- **Risks**: Empty states on emulators/clean-installs might show a loading spinner or empty view. The list must scroll independently without pushing the bottom action bar off-screen.

---

### 7. Completion Screen (`CompletionStep`)
- **Scaffold Usage**: Uses `OnboardingStepScaffold` with default `scrollable = true`.
- **Actions**:
  - Inline Button: "Set Default Home Screen" (opens system settings).
  - Bottom Primary Button: "Finish & Launch" (completes onboarding).
- **Test Tags**:
  - Screen tag: `onboarding_completion_screen`
- **Risks**: The "Set Default Home Screen" button and texts might be pushed down on small screens. Ensure the page scrolls so that all buttons are clickable.
