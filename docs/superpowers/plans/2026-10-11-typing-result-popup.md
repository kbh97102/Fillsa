# Typing Result Popup Implementation Plan

> **For agentic workers:** Implement tasks in order and verify each result before marking it done.

**Goal:** Render all three typing result popups in light and dark exactly as the six Figma frames.

**Architecture:** Keep the popup specific to the typing route so other app dialogs stay intact. Map the existing save outcomes to one reusable popup content component with three states and a light/dark palette; use the exact Figma SVG assets.

**Tech Stack:** Kotlin, Jetpack Compose, Figma SVG originals with `drawable-nodpi` PNG render assets, Android CLI Compose Preview, Android emulator.

**Spec:** `docs/screens/2_typing.md` and the linked Notion Android QA.

## Global Constraints

- Figma nodes and saved 360×720 references in `docs/screens/2_typing.md` govern layout, text, assets, colors and state.
- Preview every popup state at a fixed size, theme, locale and font scale; validate children before assembled screens.
- Compare final emulator full frames including system bars and dim against the corresponding Figma frames.
- Record mismatches and final result in the QA document; do not mark the Notion QA Completed on component-only evidence.

## Review Focus

- Dark mode currently uses a light image when `isDarkMode` is omitted.
- First completed save and streak completion must show different character art and copy.
- Incomplete save must retain two actions and must not navigate Home on `필사하기`.
- Dialog text must remain readable at larger font scale without changing its base Figma geometry.
- Only the typing result popup may change; common app dialogs must keep their appearance.

---

### Task 1: Popup asset and content component

**Files:**
- Create: `presentation/src/main/assets/figma/typing-result/` exact exported character SVG files.
- Create: `presentation/src/main/java/com/arakene/presentation/ui/home/TypingResultDialog.kt`
- Test: six CLI Compose Preview image and semantics captures in `docs/design-qa/artifacts/2026-10-11-typing-result/`.

**Interfaces:** Consume result state (continue, streak, incomplete), dark mode and button callbacks. Produce 320dp popup content using the exact Figma copy, 120dp character slot and 49dp buttons.

- [x] Verify all three titles, button labels and assets in six CLI Compose Preview image and semantics captures.
- [x] Download SVG assets supplied by Figma `get_design_context` and verify each non-empty file and root dimensions.
- [x] Implement the result popup using shared project typography, buttons and colors where they exactly fit the Figma nodes.
- [x] Add six deterministic `@Preview`s, render them with `android studio render-compose-preview`, and compare each against its Figma popup crop and semantics.

### Task 2: Typing route assembly

**Files:**
- Modify: `presentation/src/main/java/com/arakene/presentation/viewmodel/TypingViewModel.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/home/TypingQuoteView.kt`
- Update: `docs/screens/2_typing.md`
- Update: `docs/design-qa/2026-10-11-typing-result-qa.md`

**Interfaces:** Consume Task 1 popup. Present it for save outcomes while preserving the existing navigation actions.

- [x] Connect the three save outcomes to the new popup and the corresponding button actions.
- [x] Render affected typing component Previews, then capture six light/dark popup states in a 360×720 emulator frame.
- [x] Compare all six full frames against saved Figma images and record exact differences, fixes and results. User approved the Android system chrome and live quote differences.
- [x] Run affected tests and `:app:assembleDebug`; report any states still blocked by unavailable staging data.
