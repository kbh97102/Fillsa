# Android Home Dark Root Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use `superpowers:subagent-driven-development` to execute this plan task-by-task. Read `figma:figma-design-to-code` before the first Figma MCP call.

**Goal:** Figma dark Home root `2929:9603`의 Home 조립 상태와 연결된 질문·이미지·필사·인증·공유 화면을 기존 Compose/ViewModel 동작을 보존하면서 시각적으로 정렬한다.

**Architecture:** 기존 `HomeViewModel` 및 light/dark 공용 state/action/effect를 유지하고, dark 차이는 `HomeColorPalette`, theme, Figma assets, composable presentation에 한정한다. Typing/Login/Share는 기존 navigation과 domain/API를 재사용하며 Home 전용 복제 상태를 추가하지 않는다.

**Tech Stack:** Kotlin, Jetpack Compose, Hilt ViewModel, coroutines/Flow, JUnit4, Gradle, Figma MCP, Android Emulator/ADB.

**Spec:** `docs/screens/2_home_dark.md`; Figma file `VdFocqyqTgevMVCQxwAQ2X`; root `2929:9603`.

## Global Constraints

- 구현 전에 맡은 render node에 `get_design_context`를 개별 호출한다. section root의 sparse metadata만으로 구현하지 않는다.
- `docs/ui-redesign-workflow.md`를 따르며 Figma가 유일한 시각 기준이다.
- shared 4-route navigation, live ad, 명언/좋아요/이미지/필사 API와 인증 정책을 보존한다.
- 질문 답변은 정의된 backend 계약이 없으므로 기존 UI session state 범위를 넘기지 않는다.
- 구현 후 회귀 테스트를 수행한다. 별도 TDD/red-green 단계는 두지 않는다.
- 최종 결과는 360×821dp dark emulator 전체 프레임을 상태별로 캡처해 최대 5회 비교한 뒤 `Pass` 또는 `Blocked`로 기록한다.

---

### Task 1: Dark Home shell과 공용 component delta audit

**Figma:** 기본 `3039:26518`; calendar open `3139:1753`; Calendar variants `3039:28099`/`3039:28101`; Weekday variants `3039:28627`/`3039:28629`/`3039:28631`.

**Files:**
- Modify if a measured delta exists: `presentation/src/main/java/com/arakene/presentation/ui/home/FigmaHomeContent.kt`
- Modify if a measured delta exists: `presentation/src/main/java/com/arakene/presentation/ui/home/HomeInlineCalendar.kt`
- Modify if a measured delta exists: `presentation/src/main/java/com/arakene/presentation/ui/home/HomeView.kt`
- Post-implementation tests: `presentation/src/test/java/com/arakene/presentation/ui/home/HomeColorPaletteTest.kt`, `HomeWeekStripStateTest.kt`, `HomeInlineCalendarStateTest.kt`

**Interfaces:**
- Preserve: `homeColorPalette(darkMode: Boolean): HomeColorPalette`
- Preserve existing `HomeAction.ClickCalendar`, date selection/month change/dismiss actions, and `HomeViewModel` effects.
- Preserve `FigmaHomeContent` callback direction: composables render state and emit actions only.

- [ ] Export/read both render nodes and record exact frame, spacing, typography, opacity, and asset differences against the current dark runtime.
- [ ] Correct only verified dark deltas in root background, system surfaces, header, date strip, locale toggle, quote card, action row, question shell, ad boundary, and popup stacking.
- [ ] Confirm calendar tap toggles the inline popup, outside tap dismisses it, and valid date selection refreshes the Home quote.
- [ ] Run the palette, week-strip, calendar, and ViewModel regression tests after implementation changes.

### Task 2: Streak tooltip, copy snackbar, and selected Home state

**Figma:** streak tooltip `3039:26778`; copy toast `3039:26996`; liked/image state `3039:27295`.

**Files:**
- Modify if required: `presentation/src/main/java/com/arakene/presentation/ui/home/FigmaHomeContent.kt`
- Modify if required: `presentation/src/main/java/com/arakene/presentation/ui/home/HomeView.kt`
- Modify behavior only for a verified mismatch: `presentation/src/main/java/com/arakene/presentation/viewmodel/HomeViewModel.kt`
- Modify shared snackbar presentation only if verified: `presentation/src/main/java/com/arakene/presentation/ui/common/`
- Post-implementation tests: `presentation/src/test/java/com/arakene/presentation/ui/home/HomeInteractionStateTest.kt`, `HomeLikeIconTest.kt`

**Interfaces:**
- Preserve known-zero-only tooltip behavior and existing Calendar navigation effect.
- Preserve copy/like/image action effects and the shared `SnackbarHostState`.

- [ ] Match tooltip anchor, caret, dim behavior, text, underline link, and Calendar navigation to `3039:26778`.
- [ ] Match `복사되었습니다.` snackbar geometry/color to `3039:26996` without changing the question success snackbar contract.
- [ ] Match selected heart color and registered-image thumbnail/`이미지 보기` label to `3039:27295`.
- [ ] Run post-implementation state regressions for zero/unknown/positive streak, snackbar dismissal, like toggle, and image label.

### Task 3: Dark question state family

**Figma:** component board `3139:1453`; default `3139:1454`; focus `3139:1478`; done `3139:1466`; flow `3136:863`; full frames `3139:910`, `3136:1198`, `3139:1061`, `3139:1238`.

**Files:**
- Modify if required: `presentation/src/main/java/com/arakene/presentation/ui/home/FigmaHomeContent.kt`
- Modify if required: `presentation/src/main/java/com/arakene/presentation/ui/home/HomeView.kt`
- Modify behavior only for a verified mismatch: `presentation/src/main/java/com/arakene/presentation/viewmodel/HomeViewModel.kt`
- Modify if state helpers require a visual correction: `presentation/src/main/java/com/arakene/presentation/util/HomeAnswerDraft.kt`
- Post-implementation tests: `presentation/src/test/java/com/arakene/presentation/ui/home/HomeAnswerInputStateTest.kt`, `HomeInteractionStateTest.kt`

**Interfaces:**
- Preserve `HomeAnswerUiState`, the 200-grapheme limiter, record/edit actions, and `답변을 기록했어요.` success message.
- Preserve stateless `HomePromptAnswerSection` inputs and callbacks.

- [ ] Align default, focused, recorded-snackbar, and recorded states with the four dark full frames.
- [ ] Verify focus border, IME-driven viewport, counter, placeholder, CTA colors, and read-only recorded field independently.
- [ ] Confirm the question CTA never emits the Typing navigation effect and edit returns to inline input.
- [ ] Run post-implementation state regressions, including Korean composed-character length behavior.

### Task 4: Dark image registration and dialog family

**Figma:** section `3223:5589`; before `3223:5985`; after `3223:6126`; uploaded preview `3223:6435`; template preview `3223:6600`; delete confirmation `3223:6912`.

**Files:**
- Modify if required: `presentation/src/main/java/com/arakene/presentation/ui/home/ImageDialog.kt`
- Modify if required: `presentation/src/main/java/com/arakene/presentation/ui/home/HomeView.kt`
- Modify if required: `presentation/src/main/java/com/arakene/presentation/util/ImageDialogDataHolder.kt`
- Modify behavior only for a verified mismatch: `presentation/src/main/java/com/arakene/presentation/viewmodel/HomeViewModel.kt`
- Add/update only verified assets: `presentation/src/main/res/drawable/`, `presentation/src/main/assets/figma/home-night/`

**Interfaces:**
- Preserve `ImageDialog(quote, author, backgroundImageUrl, onDismiss, uploadImage, deleteOnClick)`.
- Preserve existing image picker, upload/delete use cases, and authenticated access rules.

- [ ] Separate uploaded-image and template-image preview presentation without duplicating upload/delete effects.
- [ ] Match dialog size, corner radius, dim layer, close/delete controls, quote layout, and `이미지 변경`/`확인` buttons.
- [ ] Match delete confirmation copy and button order to `3223:6912`.
- [ ] Run post-implementation image/auth regressions and use deterministic fixture images for emulator capture.

### Task 5: Linked Typing, Login, and Share dark surfaces

**Figma:** Typing `3087:28815`, `2929:9764`, `2929:9801`, `2929:9844`, `2929:9884`; completion modals `2929:10996`, `3025:24578`, `3025:24618`; login `2929:9931`, `2929:10788`; share `2929:10884`, `2929:10850`; templates listed in the spec.

**Files:**
- Modify if required: `presentation/src/main/java/com/arakene/presentation/ui/home/TypingQuoteView.kt`
- Modify if required: `presentation/src/main/java/com/arakene/presentation/ui/home/TypingQuoteBodySection.kt`
- Modify if required: `presentation/src/main/java/com/arakene/presentation/viewmodel/TypingViewModel.kt`
- Modify if required: `presentation/src/main/java/com/arakene/presentation/ui/LoginView.kt`
- Modify if required: `presentation/src/main/java/com/arakene/presentation/ui/home/ShareView.kt`
- Modify if required: `presentation/src/main/java/com/arakene/presentation/ui/home/ShareItem.kt`
- Modify if required: `presentation/src/main/java/com/arakene/presentation/viewmodel/ShareViewModel.kt`
- Add/update only verified assets: `presentation/src/main/res/drawable/`

- [ ] Treat these as route-level dark appearance regressions, not new Home-local state.
- [ ] Align typing input, save snackbar, three completion outcomes, login modal/full login, first-share guide, carousel, and action bar with their individual nodes.
- [ ] Preserve typing persistence, social login, gallery save, clipboard, Android share sheet, and Kakao integrations.
- [ ] Run existing route-level tests after all visual changes.

### Task 6: Build, emulator QA, and documentation

**Files:**
- Create/update: `docs/design-qa/2026-09-07-android-home-dark-root-qa.md`
- Modify: `docs/screens/2_home_dark.md`
- Add runtime captures: `docs/design-qa/assets/home-figma-2929-9603/2026-09-07/runtime-android-dark-*.png`

- [ ] Run `./gradlew :presentation:testDebugUnitTest :presentation:assembleDebug --console=plain` after implementation.
- [ ] Launch the app on an emulator configured to logical 360×821dp and force dark appearance.
- [ ] Capture every Task 1–5 target that is reachable, including the full IME, authenticated image fixture, and real completed-writing marker states.
- [ ] Compare component crops and complete assembled frames against the stored Figma references for up to five rounds.
- [ ] Keep the Figma 3-tab/static-ad versus app 4-route/live-ad discrepancy explicit; it remains `Blocked` unless a separate product decision changes the app-wide contract.
- [ ] Update the spec and QA document with emulator profile, commands, captures, comparison method, remaining differences, and final result.
