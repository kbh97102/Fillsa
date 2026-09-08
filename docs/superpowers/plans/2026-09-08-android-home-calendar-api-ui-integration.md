# Android Home·Calendar API UI Integration Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 완성된 Android Compose Home·Calendar UI를 리뉴얼 weekly/daily/answer/monthly API에 연결하고 회원 화면 상태를 서버 응답과 일치시킨다.

**Architecture:** `codex/calendar-screen-root`에서 독립 worktree를 만들고 `codex/member-api-usecases`를 병합한다. Retrofit/Repository/UseCase는 Domain 경계에 유지하고, 순수 presentation state reducer를 거쳐 `HomeViewModel`·`CalendarViewModel`이 Compose에 상태와 action을 제공한다. Home은 주간 창 캐시, Calendar는 월간 캐시를 사용하며 답변 POST 성공 뒤 선택 레코드만 갱신한다.

**Tech Stack:** Kotlin 2.2, Jetpack Compose, Hilt, Coroutines, Retrofit/Gson, JUnit4, Gradle

**Spec:** `docs/superpowers/specs/2026-09-08-home-calendar-ui-integration-contract.md`

## Global Constraints

- Figma URL과 node ID는 `docs/screens/2_home.md`, `docs/screens/3_calendar.md`의 현재 기준을 사용한다.
- Compose 레이아웃, 색상, 문구, 컴포넌트 계층은 변경하지 않고 데이터/action binding만 교체한다.
- 회원 Home 최초 weekly 요청은 `endDate = null`이며 실제 URL에서 query가 생략돼야 한다.
- 같은 weekly 창 안의 날짜 선택과 카드 스와이프는 네트워크를 호출하지 않는다.
- Calendar 날짜 선택은 monthly 응답 캐시만 사용한다.
- 답변 저장은 completion/state/streak/monthly summary를 변경하지 않는다.
- 비회원은 기존 v1/Room·DataStore 기반 로컬 흐름과 session answer를 유지한다.
- Runtime QA fixture는 실제 네트워크나 사용자 저장소를 변경하지 않는다.

---

### Task 1: UI 기준 브랜치에 API 브랜치 병합

**Files:**
- Modify: `docs/screens/2_home.md`
- Modify: `docs/screens/3_calendar.md`
- Verify: `app/src/main/java/com/arakene/fillsa/modules/RepositoryModule.kt`

**Interfaces:**
- Consumes: UI `codex/calendar-screen-root` at `d06110d`; API `codex/member-api-usecases` containing `a300cc0`.
- Produces: `codex/home-calendar-api-ui-integration` with UI, Retrofit, Repository, and UseCase layers together.

- [ ] **Step 1: Create a separate integration worktree**

```bash
git worktree add .worktrees/home-calendar-api-ui-integration \
  -b codex/home-calendar-api-ui-integration codex/calendar-screen-root
cd .worktrees/home-calendar-api-ui-integration
git merge --no-ff codex/member-api-usecases -m "merge: combine renewed quote APIs with Figma UI"
```

- [ ] **Step 2: Resolve additive conflicts without touching Compose visuals**

Keep the UI branch presentation sources and API branch `data`/`domain` sources. Preserve all eight nullable/defaulted `MemberQuotesData` fields so existing QA fixture constructors remain source-compatible. Confirm `RepositoryModule` still binds the single `HomeRepositoryImpl` and `CalendarRepositoryImpl` implementations.

- [ ] **Step 3: Update Home and Calendar screen contracts**

Replace the old session-only member answer and “monthly has no answer/image” statements. Document weekly first-load omission, in-window zero-call behavior, monthly detail mapping, daily post-save refresh, guest fallback, and unchanged like/image/typing behavior.

- [ ] **Step 4: Verify the merged baseline**

Copy the ignored local `app/google-services.json` into the worktree only for local verification, then run:

```bash
./gradlew test --console=plain
```

Expected: Debug/Release compilation and unit tests succeed before ViewModel wiring.

- [ ] **Step 5: Commit the integration baseline**

```bash
git add docs/screens/2_home.md docs/screens/3_calendar.md
git commit -m "docs(android): define Home Calendar API UI contract"
```

### Task 2: Add pure member-window presentation state

**Files:**
- Create: `presentation/src/main/java/com/arakene/presentation/model/HomeMemberQuoteWindow.kt`
- Create: `presentation/src/test/java/com/arakene/presentation/model/HomeMemberQuoteWindowTest.kt`

**Interfaces:**
- Consumes: `MemberWeeklyQuoteResponse`, `MemberQuoteDay`, `DailyQuoteDto`, `LocalDate`.
- Produces: `HomeMemberQuoteWindow`, `MemberQuoteDay.toDailyQuoteDto()`, `MemberQuoteDay.withAnswer()`.

- [ ] **Step 1: Write failing pure-state tests**

At the top of `HomeMemberQuoteWindowTest.kt`, add a private `memberDay(date, state, dailyQuoteSeq, answer, answeredAt, completed)` factory that supplies every `MemberQuoteDay` constructor field with deterministic Korean/English quote, author, question, `likeYn = "N"`, and `imagePath = null` values. Build `weeklyFixture` from the seven dates `2026-08-28..2026-09-03`, mark only `2026-09-03` as `state = "today"`, and define `unfinishedDay` as that last element with `completed = false`. Put the reusable factory in `presentation/src/test/java/com/arakene/presentation/model/MemberQuoteFixtures.kt` when Task 3 needs it from a second test class.

```kotlin
@Test
fun `initial window selects the server today and preserves server order`() {
    val window = HomeMemberQuoteWindow.from(weeklyFixture)
    assertEquals(LocalDate.of(2026, 9, 3), window.selectedDate)
    assertEquals(weeklyFixture.days.map { LocalDate.parse(it.date) }, window.visibleDates)
}

@Test
fun `window boundaries derive from response endDate`() {
    val window = HomeMemberQuoteWindow.from(weeklyFixture)
    assertEquals("2026-08-27", window.requestEndDate(WindowDirection.Previous))
    assertEquals("2026-09-10", window.requestEndDate(WindowDirection.Next))
}

@Test
fun `answer patch never completes a quote`() {
    val updated = unfinishedDay.withAnswer("기록", "2026-09-03 14:01:13")
    assertFalse(updated.completed)
    assertEquals("today", updated.state)
}
```

- [ ] **Step 2: Run and observe the missing model failure**

```bash
./gradlew :presentation:testDebugUnitTest \
  --tests 'com.arakene.presentation.model.HomeMemberQuoteWindowTest' --console=plain
```

Expected: compilation fails because the presentation window types do not exist.

- [ ] **Step 3: Implement immutable window state**

```kotlin
data class HomeMemberQuoteWindow(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val days: List<MemberQuoteDay>,
    val selectedDate: LocalDate,
) {
    val selectedDay: MemberQuoteDay?
        get() = days.firstOrNull { LocalDate.parse(it.date) == selectedDate }

    val visibleDates: List<LocalDate>
        get() = days.map { LocalDate.parse(it.date) }

    fun select(date: LocalDate): HomeMemberQuoteWindow? =
        takeIf { date in visibleDates }?.copy(selectedDate = date)
}
```

`from()` selects `state == "today"`, falling back to response `endDate`. Boundary calculation adds/subtracts exactly seven days from response `endDate`. `toDailyQuoteDto()` maps nullable sequence to zero only for compatibility with the existing view model mutation guards.

- [ ] **Step 4: Add cache and stale-response rules as pure functions**

Add tests and implementation for a session-only `Map<LocalDate, HomeMemberQuoteWindow>` keyed by window `endDate`, plus a monotonically increasing request ID comparison that rejects older responses.

- [ ] **Step 5: Run and commit**

```bash
./gradlew :presentation:testDebugUnitTest \
  --tests 'com.arakene.presentation.model.HomeMemberQuoteWindowTest' --console=plain
git add presentation/src/main/java/com/arakene/presentation/model/HomeMemberQuoteWindow.kt \
  presentation/src/test/java/com/arakene/presentation/model/HomeMemberQuoteWindowTest.kt
git commit -m "feat(android): model member quote weekly window"
```

### Task 3: Connect authenticated Home loading and window navigation

**Files:**
- Modify: `presentation/src/main/java/com/arakene/presentation/model/HomeMemberQuoteWindow.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/viewmodel/HomeViewModel.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/util/action/Action.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/home/HomeView.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/home/FigmaHomeContent.kt`
- Modify: `presentation/src/test/java/com/arakene/presentation/ui/home/HomeWeekStripStateTest.kt`
- Create: `presentation/src/test/java/com/arakene/presentation/model/HomeMemberFlowReducerTest.kt`

**Interfaces:**
- Consumes: injected `GetMemberWeeklyQuotesUseCase`, `GetMemberQuoteDayUseCase`, existing `GetLoginStatusUseCase`, `HomeMemberQuoteWindow`.
- Produces: `HomeAction.SelectWeekDay`, `HomeAction.LoadPreviousWindow`, `HomeAction.LoadNextWindow`; observable member window/question state.

- [ ] **Step 1: Write failing member-flow tests**

```kotlin
@Test
fun `first authenticated load requests weekly without endDate`() {
    val command = homeInitialLoadCommand(isLoggedIn = true, requestedDate = null)
    assertEquals(HomeLoadCommand.MemberWeekly(endDate = null), command)
}

@Test
fun `selecting a cached date produces no network command`() {
    val result = selectMemberDate(window, LocalDate.of(2026, 9, 1))
    assertEquals(null, result.loadCommand)
    assertEquals(LocalDate.of(2026, 9, 1), result.window.selectedDate)
}
```

Add cases for previous/next boundary, future clamp response, `dailyQuoteSeq == null`, and stale request IDs.

- [ ] **Step 2: Run and observe missing flow reducer functions**

Run the two focused presentation test classes. Expected: compile failure for the flow types/functions.

- [ ] **Step 3: Inject and invoke member UseCases in HomeViewModel**

```kotlin
class HomeViewModel @Inject constructor(
    private val getMemberWeeklyQuotesUseCase: GetMemberWeeklyQuotesUseCase,
    private val getMemberQuoteDayUseCase: GetMemberQuoteDayUseCase,
    private val saveQuoteAnswerUseCase: SaveQuoteAnswerUseCase,
    // Keep all existing constructor dependencies.
) : BaseViewModel()
```

On authenticated first refresh, call `getMemberWeeklyQuotesUseCase()` with no argument. Store the response window and derive `date`, `currentQuota`, `isLike`, `backgroundImageUri`, completed dates, question, and answer state from `selectedDay`. Keep the existing v1 guest branch unchanged. Track the active request job/request ID so older responses cannot overwrite the selected window.

If `Screens.Home` contains a Calendar-selected `requestDate`, retain it until the first no-query weekly response supplies the server anchor. For a target outside that response, calculate `daysBack = DAYS.between(targetDate, anchorEndDate)`, `windowOffset = daysBack / 7`, and `targetEndDate = anchorEndDate.minusDays(windowOffset * 7)`; load that aligned window once and select the target. Clamp a future target to the server anchor end date.

- [ ] **Step 4: Bind the seven server days into Compose**

Change `FigmaHomeContent` and `HomeDateWeekSection` to accept `List<HomeWeekDayState>` and `onWeekDaySelected`. Build those states from `MemberQuoteDay.state/completed`, not local streak rows, for authenticated users. Add `noEffectClickable` to the existing 30dp day circles; do not alter their layout or styling. Card previous/next and day tap dispatch the same selection function, so an in-window selection performs no GET.

- [ ] **Step 5: Verify and commit**

```bash
./gradlew :presentation:testDebugUnitTest --console=plain
git add presentation/src/main/java/com/arakene/presentation/viewmodel/HomeViewModel.kt \
  presentation/src/main/java/com/arakene/presentation/util/action/Action.kt \
  presentation/src/main/java/com/arakene/presentation/ui/home/HomeView.kt \
  presentation/src/main/java/com/arakene/presentation/ui/home/FigmaHomeContent.kt \
  presentation/src/test/java/com/arakene/presentation
git commit -m "feat(android): bind Home to member weekly quotes"
```

### Task 4: Persist Home answers and perform best-effort daily refresh

**Files:**
- Modify: `presentation/src/main/java/com/arakene/presentation/viewmodel/HomeViewModel.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/util/HomeAnswerDraft.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/home/HomeView.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/home/FigmaHomeContent.kt`
- Modify: `presentation/src/test/java/com/arakene/presentation/ui/home/HomeAnswerInputStateTest.kt`
- Modify: `presentation/src/test/java/com/arakene/presentation/ui/home/HomeInteractionStateTest.kt`

**Interfaces:**
- Consumes: `SaveQuoteAnswerUseCase`, `GetMemberQuoteDayUseCase`, selected `MemberQuoteDay`.
- Produces: answer state with `isSaving`, `recordedAnswer`, date key, and POST success/failure transitions.

- [ ] **Step 1: Write failing answer transition tests**

Build `memberState` with the Task 2 `weeklyFixture` through the production `HomeMemberFlowState.from(window)` mapper. Define `answerResponse` as `AnswerResponse(memberQuoteSeq = 91, answer = "기록할 답변", answeredAt = "2026-09-03 14:01:13")`. Reuse this exact setup for failure and best-effort refresh cases so no test bypasses the production mapper.

```kotlin
@Test
fun `member answer is not recorded until post succeeds`() {
    val submitting = beginMemberAnswerSave(memberState, "기록할 답변")
    assertTrue(submitting.answer.isSaving)
    assertEquals(null, submitting.answer.recordedAnswer)

    val saved = applyMemberAnswerSuccess(submitting, answerResponse)
    assertFalse(saved.answer.isSaving)
    assertEquals(answerResponse.answer, saved.answer.recordedAnswer)
    assertFalse(saved.window.selectedDay!!.completed)
}
```

Add blank input, 200/201 grapheme, duplicate tap, POST failure, daily refresh failure, and guest session-only cases.

- [ ] **Step 2: Run and observe current immediate-session behavior failing**

Expected: current `RecordAnswer` immediately emits the success snackbar and has no saving state.

- [ ] **Step 3: Implement POST-first Home answer handling**

For authenticated users, `RecordAnswer` validates the selected sequence and draft, sets `isSaving`, and calls `saveQuoteAnswerUseCase`. Only a non-null `getResponse(..., useLoading = false)` result patches the selected weekly day and emits `HomeAnswerRecordedSnackbar`. Start `getMemberQuoteDayUseCase(selectedDate.toString())` afterward and merge the refreshed day if it returns. A failed refresh keeps the successful answer response.

- [ ] **Step 4: Bind real question and submission state**

Add `question` and `isAnswerSaving` parameters to the existing Home question composable. Select `questionKo` or `questionEn` using `LocaleType`; keep the existing placeholder and button visuals. Ignore record clicks while saving. Changing the selected date replaces draft/recorded state with that day’s values.

- [ ] **Step 5: Verify and commit**

```bash
./gradlew :presentation:testDebugUnitTest --console=plain
git add presentation/src/main/java/com/arakene/presentation/viewmodel/HomeViewModel.kt \
  presentation/src/main/java/com/arakene/presentation/util/HomeAnswerDraft.kt \
  presentation/src/main/java/com/arakene/presentation/ui/home \
  presentation/src/test/java/com/arakene/presentation/ui/home
git commit -m "feat(android): persist Home question answers"
```

### Task 5: Project monthly fields into Calendar and save selected-day answers

**Files:**
- Modify: `presentation/src/main/java/com/arakene/presentation/viewmodel/CalendarViewModel.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/util/action/Action.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/calendar/CalendarPresentation.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/calendar/CalendarQuoteSection.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/calendar/CalendarView.kt`
- Modify: `presentation/src/test/java/com/arakene/presentation/ui/calendar/CalendarRecordIndicatorTest.kt`
- Create: `presentation/src/test/java/com/arakene/presentation/model/CalendarAnswerStateTest.kt`

**Interfaces:**
- Consumes: expanded `MemberQuotesData`, `SaveQuoteAnswerUseCase`, `GetMemberQuoteDayUseCase`.
- Produces: `CalendarSelectedDayPresentation` carrying question/answer/image path and Calendar answer actions/state.

- [ ] **Step 1: Write failing monthly mapping and call-policy tests**

Define `renewedMonthlyQuote` in the test with all existing required `MemberQuotesData` fields plus `questionKo = "질문"`, `questionEn = "Question"`, `answer = "답변"`, `answeredAt`, `imagePath`, `engQuote`, `engAuthor`, and `authorUrl`. Define `monthState` through the production monthly-response mapper with that quote selected; do not construct presentation output directly.

```kotlin
@Test
fun `monthly response fields drive completed detail`() {
    val presentation = calendarSelectedDayPresentation(renewedMonthlyQuote)
    assertEquals("질문", presentation.question)
    assertEquals("답변", presentation.answer)
    assertEquals("https://example.com/image.jpg", presentation.registeredImageUri)
}

@Test
fun `selecting another day uses monthly cache without a daily command`() {
    val result = selectCalendarDay(monthState, LocalDate.of(2026, 9, 2))
    assertEquals(null, result.networkCommand)
}
```

- [ ] **Step 2: Run and observe the empty production presentation failure**

Expected: current mapping returns `Completed` without answer/image/question.

- [ ] **Step 3: Map monthly response into Calendar UI**

Extend `CalendarSelectedDayPresentation` with `question` and map `answer`, `imagePath`, and completion directly from the selected `MemberQuotesData`. `CalendarPromptAnswer` must accept ViewModel-owned draft/edit/saving state instead of `rememberSaveable`. Remove the hardcoded question and the accessibility statement that saving is unavailable. `AsyncImage` continues to render the remote `imagePath` in the existing 28dp frame.

- [ ] **Step 4: Wire Calendar answer actions**

Add `CalendarAction.ChangeAnswer`, `RecordAnswer`, and `EditAnswer`. Inject `SaveQuoteAnswerUseCase` and `GetMemberQuoteDayUseCase` into `CalendarViewModel`. On success, copy only answer/answeredAt into the matching `data.memberQuotes` entry; retain monthly summary and writing completion values. Trigger a best-effort daily refresh only after POST success. Ordinary `SelectDay` remains cache-only.

- [ ] **Step 5: Verify and commit**

```bash
./gradlew :presentation:testDebugUnitTest --console=plain
git add presentation/src/main/java/com/arakene/presentation/viewmodel/CalendarViewModel.kt \
  presentation/src/main/java/com/arakene/presentation/util/action/Action.kt \
  presentation/src/main/java/com/arakene/presentation/ui/calendar \
  presentation/src/test/java/com/arakene/presentation
git commit -m "feat(android): bind Calendar detail to monthly answers"
```

### Task 6: Full regression, build, and Figma runtime QA

**Files:**
- Modify: `presentation/src/test/java/com/arakene/presentation/ui/home/HomeWeekStripStateTest.kt`
- Modify: `presentation/src/test/java/com/arakene/presentation/ui/calendar/CalendarRecordIndicatorTest.kt`
- Modify: `data/src/test/java/com/arakene/data/network/FillsaApiContractTest.kt`
- Modify: `docs/design-qa/2026-09-07-android-home-interactions-qa.md`
- Modify: `docs/design-qa/2026-09-08-android-calendar-screen-root-qa.md`
- Modify: `docs/planning.md`

**Interfaces:**
- Consumes: final ViewModels/Compose bindings and existing Calendar → Home selected-date route.
- Produces: regression evidence for member/guest, navigation, typing v2, answer invariants, and visual parity.

- [ ] **Step 1: Add cross-feature regression cases**

Cover Calendar → Home target date, cache reset on logout/account change, guest member-API call count zero, answer save not changing streak/completion/monthly summary, weekly month-boundary badge changes without GET, typing GET v1, and typing POST v2.

- [ ] **Step 2: Run all unit tests**

```bash
./gradlew test --console=plain
```

Expected: all app/data/domain/presentation Debug and Release unit-test tasks succeed.

- [ ] **Step 3: Build the app variants**

```bash
./gradlew :app:assembleDebug :app:assembleRelease --console=plain
```

Expected: both variants compile; release signing output is not installed or uploaded.

- [ ] **Step 4: Capture Home and Calendar integration states**

Feed the existing runtime QA fixtures through the same `MemberQuoteDay`/`MemberQuotesData` presentation mappers used by production. Capture Home light/dark default and recorded-answer states plus Calendar basic/unanswered/answered-image states on the documented emulator size, density, API level, locale, and system bars. Update the QA records with full-frame paths, crop boundaries, comparison method, and exact remaining differences. Preserve existing blockers unless the same run resolves and verifies them.

- [ ] **Step 5: Final verification and commit**

```bash
git diff --check codex/calendar-screen-root...HEAD
git status --short
git add presentation/src/test data/src/test docs/design-qa docs/planning.md
git commit -m "test(android): verify Home Calendar API UI integration"
```

Expected final history: merge baseline plus separate weekly state, Home answer, Calendar detail, and verification commits suitable for independent review.
