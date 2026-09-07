# Android Calendar screen root implementation plan

> Date: 2026-09-08  
> Figma root: `2929:13366`  
> Worktree: `.worktrees/calendar-screen-root` / branch `codex/calendar-screen-root`  
> Base: `codex/home-dark-root` (`e50ffb7`), retaining the committed dark Home/shared-surface work.

## Goal

Bring the existing Compose Calendar screen up to the current Figma light-root contract for all three real frames, add deterministic Emulator-only visual states, preserve Clean Architecture and ViewModel boundaries, and record full runtime evidence.

## Scope and acceptance matrix

| State | Figma frame | Required runtime result |
|---|---|---|
| Basic / incomplete day | `2985:21952` | calendar, counts, handwriting companion, 80dp quote card |
| Completed / unanswered / no image | `2987:22796` | 133dp quote/action card, empty 200-char answer editor, record CTA |
| Completed / answered / image present | `2985:22510` | image thumbnail + `이미지 보기`, fixture answer, edit CTA |

Shared four-route navigation and live ad ownership remain unchanged; their difference from Figma's three-tab/static-ad frame is recorded as a product boundary.

## Task 1: Implement and verify the Calendar root

1. Reconfirm Figma metadata and durable exports for `2985:21952`, `2987:22796`, and `2985:22510`; maintain the complete node map in `docs/screens/3_calendar.md`.
2. Audit `CalendarView`, `CalendarSection`, `Day`, counts, selected quote/actions, and prompt-answer UI against the three references. Reuse existing theme tokens and Home registered-image rendering where appropriate.
3. Add a small presentation model for unanswered versus answered/image variants without adding fields to the monthly API response. Production mapping stays limited to genuine domain data.
4. Add deterministic debug/QA launch fixtures for all three visual states. Fixtures are process-local, make no API call, and do not write app data.
5. Implement component-first and validate every state on Android Emulator at 360dp width. Save full-frame captures and overlay/side-by-side evidence under `docs/design-qa/assets/calendar-figma-2929-13366/2026-09-08/`.
6. After implementation, run focused Calendar unit tests, the relevant presentation test task, and debug APK assembly. This plan deliberately excludes TDD; tests are post-implementation regression checks.
7. Request an independent scoped code/design review, apply required fixes, rerun verification, update QA, and commit. Do not merge or push without a new explicit request.

### Likely files

- `presentation/src/main/java/com/arakene/presentation/ui/calendar/CalendarView.kt`
- `presentation/src/main/java/com/arakene/presentation/ui/calendar/CalendarSection.kt`
- `presentation/src/main/java/com/arakene/presentation/ui/calendar/CalendarQuoteSection.kt`
- `presentation/src/main/java/com/arakene/presentation/ui/calendar/CalendarCountSection.kt`
- debug/runtime fixture plumbing in app/presentation
- `presentation/src/test/java/com/arakene/presentation/ui/calendar/CalendarRecordIndicatorTest.kt`
- `docs/screens/3_calendar.md`
- `docs/design-qa/2026-09-08-android-calendar-screen-root-qa.md`

### Verification commands

```bash
./gradlew :presentation:testDebugUnitTest --tests com.arakene.presentation.ui.calendar.CalendarRecordIndicatorTest --console=plain
./gradlew :presentation:testDebugUnitTest --console=plain
./gradlew :app:assembleDebug --console=plain
adb devices
```

The runtime pass additionally requires installing the built APK, opening the Calendar fixture states on an Emulator, capturing all reachable frames with `adb exec-out screencap -p`, and documenting any honest blocker instead of substituting build success for visual acceptance.
