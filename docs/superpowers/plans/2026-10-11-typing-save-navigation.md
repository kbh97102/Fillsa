# Typing Save Navigation Implementation Plan

> **For agentic workers:** Implement tasks in order and verify each result before marking it done.

**Goal:** Save success and completed typing lead from the result popup's `확인` button back to Home, with no success navigation on save failure.

**Architecture:** Keep save outcome in `TypingViewModel`; emit the existing dialog effect only after a successful member API or local save. The dialog action requests a single navigation pop to the existing Home destination; it does not create another Home entry.

**Tech Stack:** Kotlin, coroutines, Jetpack Compose Navigation, JUnit, Gradle.

**Spec:** `docs/screens/2_typing.md` and the linked Notion Android QA.

## Global Constraints

- Figma is the sole UI authority; this task changes behavior but must retain the target popup appearance.
- Completed save navigates on popup `확인`, not before the popup appears.
- Save failure must not show a success dialog or navigate.
- Preserve Clean Architecture module boundaries and the existing Home refresh-on-resume behavior.

## Review Focus

- Member API failure or auth expiry must not produce a success dialog.
- Guest local save must finish before the dialog appears.
- Fast repeated taps must not issue duplicate saves or navigations.
- Back stack after confirmation must contain a single Home destination.
- Incomplete typing must keep the Figma two-button flow.

---

### Task 1: Save outcome and confirmation effect

**Files:**
- Modify: `presentation/src/main/java/com/arakene/presentation/viewmodel/TypingViewModel.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/home/TypingQuoteView.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/common/MainNavHost.kt` only if the existing pop callback cannot return to Home.
- Test: `presentation/src/test/java/com/arakene/presentation/viewmodel/TypingSaveOutcomeTest.kt`

**Interfaces:** Consume existing `TypingAction.Save`, `PostTypingUseCase: ApiResult<Int>`, and local save use case. Produce a completed-save dialog whose positive action calls the existing pop navigation once.

- [x] Write tests for member success, member failure, guest success, repeated Save, and completed popup confirmation. The initial outcome tests failed before the implementation and passed after it; the repeated Save test passed with the guard in place.
- [x] Gate dialog emission on successful persistence and prevent a second save while the first is active.
- [x] Connect completed popup `확인` to Home back-stack pop. Preserve incomplete popup choices.
- [x] Run the focused test class and the affected presentation tests.
- [x] Verify runtime guest Home return, inspect the existing `LifecycleResumeEffect` refresh hook, and verify member success/failure and Home move effect with fake repository data, as requested instead of a staging account. A server-backed refresh was outside this mock-data verification.

### Task 2: Runtime navigation QA

**Files:**
- Update: `docs/design-qa/2026-10-11-typing-result-qa.md`

**Interfaces:** Consume Task 1's save and pop flow. Produce emulator full-frame and route evidence for success and failure states.

- [x] Capture the typing result popup and Home after `확인` on an emulator.
- [x] Capture member failure behavior with a controlled failing API test or fixture; verify no success navigation.
- [x] Record commands, build, device, route result, and remaining limits in QA.
