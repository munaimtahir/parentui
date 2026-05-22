# Onboarding Fix Plan

This document proposes a unified fix strategy and repair plan to address all layout, scrolling, and clipping defects identified in the onboarding visual audit.

## Root Cause Analysis

The visual audit revealed that:
1. **Tiny dp Layout Space**: When device size is set to small bounds (e.g., 360x640) under high density (420 dpi), the screen height in density-independent pixels is extremely small (~243 dp).
2. **Fixed Height & Unscrollable Cards**: Current onboarding steps (specifically the PIN Setup screen) wrap content inside `Card` layouts that fill the screen but lack a scrollable modifier.
3. **Keypad Clipping**: Keypads and disclaimer text have fixed bounds and spacing that exceed the available vertical dp height, causing Jetpack Compose to clip the keypad and other interactive components completely out of the rendered tree.
4. **Scaffold BottomBar Overlaps**: Inner scaffolds place buttons in `bottomBar` without accounting for parent column boundaries and device navigation bars.

---

## Recommended Strategy: Unified Onboarding Layout Wrapper

We propose introducing a reusable wrapper composable, **`OnboardingStepScaffold`**, to replace the divergent layout implementations of each step.

### `OnboardingStepScaffold` Design

```kotlin
@Composable
fun OnboardingStepScaffold(
    modifier: Modifier = Modifier,
    title: String = "",
    showProgress: Boolean = true,
    progress: Float = 0f,
    onBackClick: (() -> Unit)? = null,
    bottomAction: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(title, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        if (onBackClick != null) {
                            IconButton(onClick = onBackClick) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                            }
                        }
                    }
                )
                if (showProgress) {
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(4.dp)
                    )
                }
            }
        },
        bottomBar = {
            Surface(
                tonalElevation = 4.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    bottomAction()
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            content()
        }
    }
}
```

### Key Refactoring Goals:
1. **WelcomeStep**: Replace the custom card/scaffold with `OnboardingStepScaffold`. This guarantees that the "Get Started" button resides in the bottom bar with `navigationBarsPadding` and the text content flows in a scrollable body.
2. **PinSetupStep**: Remove `fillMaxSize` card. Put title, instructions, and input indicators into the scrollable body of `OnboardingStepScaffold`. The keypad and "Continue" button will reside in a scrollable body or custom layout depending on screen size.
3. **ContactSetupStep**: Put name, phone, emergency label, and phone inputs inside the scrollable column. Ensure the "Save & Next" button is pinned to the bottom bar.
4. **AppSelectionStep**: Make sure the list is wrapped in a LazyColumn with bottom padding to avoid overlapping the bottom button.
5. **CompletionStep**: Standardize buttons using the bottom bar layout.

---

## GO/NO-GO Recommendation

**Status: GO**

We have successfully completed the visual audit, captured screenshots, dumped XML layout trees, and verified app behavior on standard configurations (Profile E). We have isolated the root causes for the layout failures on smaller screens. We are ready to begin the layout repair sprint.
