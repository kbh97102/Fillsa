# Android Home Interactions Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Figma `2. home`의 inline 달력, 질문 기록/수정, 0일 연속 필사 안내와 완료 날짜 표시를 기존 이미지·복사·좋아요 흐름과 함께 Compose Home에 완성한다.

**Architecture:** `HomeViewModel`을 Home 상호작용의 단일 상태 원천으로 유지하고, 순수 Kotlin calendar/question 모델을 stateless Compose 컴포넌트가 렌더링한다. 달력 툴팁 링크만 기존 `Screens.Calendar` effect를 사용한다. 질문 답변은 session state로만 유지하며 정의되지 않은 domain/data 계약은 추가하지 않는다.

**Tech Stack:** Kotlin, Jetpack Compose, Hilt ViewModel, coroutines/Flow, JUnit4, Gradle, Figma MCP.

**Spec:** `docs/screens/2_home.md`; Figma file `VdFocqyqTgevMVCQxwAQ2X`; parent `2929:17193`; default `2929:13556`; calendar open `3139:1501`; question flow `3110:33782`; streak tooltip `2929:18871`.

## Global Constraints

- Figma가 UI의 유일한 시각 기준이다. iOS/기존 Android 화면을 시각 기준으로 삼지 않는다.
- shared 4-route bottom navigation과 질문 영구 저장은 이번 Home 범위 밖이다.
- 컴포넌트 단위 구현 후 빌드·기존 회귀 테스트·런타임 시각 QA 순서로 검증한다.
- 기존 이미지 업로드/삭제, 명언 조회/필사/공유, 좋아요 API 동작을 보존한다.
- 최종 QA는 360×821 light/dark 및 주요 overlay 상태를 캡처해 최대 5회 보정한다.

### Task 1: Pure state contracts

**Files:**
- Modify: `presentation/src/test/java/com/arakene/presentation/ui/home/HomeWeekStripStateTest.kt`
- Modify: `presentation/src/test/java/com/arakene/presentation/ui/home/HomeAnswerInputStateTest.kt`
- Create: `presentation/src/test/java/com/arakene/presentation/ui/home/HomeInlineCalendarStateTest.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/util/HomeAnswerDraft.kt`
- Create: `presentation/src/main/java/com/arakene/presentation/ui/home/HomeInlineCalendar.kt`

1. week strip은 선택일 포함 `-6..0`으로 정리한다.
2. `HomeAnswerUiState`와 순수 record/edit/update 함수를 구현한다.
3. `HomeCalendarDay`와 `homeMonthGrid(YearMonth, LocalDate)`를 구현한다.

### Task 2: ViewModel interaction state

**Files:**
- Modify: `presentation/src/main/java/com/arakene/presentation/util/action/Action.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/viewmodel/HomeViewModel.kt`
- Create: `presentation/src/test/java/com/arakene/presentation/viewmodel/HomeInteractionStateTest.kt`

1. `HomeAction.ClickCalendar`를 inline toggle로 변경하고 `SelectHomeDate`, `DismissHomeCalendar`, `ChangeHomeMonth`, `ClickStreakStatus`, `DismissStreakTooltip`, `ClickStreakCalendar`, `ChangeAnswer`, `RecordAnswer`, `EditAnswer`를 추가한다.
2. ViewModel에 `isCalendarOpen`, `displayedMonth`, `isStreakTooltipOpen`, `answerUiState`, `completedDates` 상태를 추가한다.
3. `GetAllStreakInfoUseCase`를 주입해 실제 완료 필사 날짜를 읽는다.
4. 날짜 선택은 popup을 닫고 기존 `refresh(date)`를 호출한다. streak Calendar 링크만 `CommonEffect.Move(Screens.Calendar)`를 발생시킨다.

### Task 3: Compose calendar, question, streak UI

**Files:**
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/home/FigmaHomeContent.kt`
- Create/Modify: `presentation/src/main/java/com/arakene/presentation/ui/home/HomeInlineCalendar.kt`
- Reuse where useful: `presentation/src/main/java/com/arakene/presentation/ui/common/StreakInfo.kt`

1. `HomeDateWeekSection`에 stateless toggle/action을 주입하고 popup을 selector 아래에 overlay한다.
2. Figma `2929:16227`의 248×335dp, `2929:16229`/`16240` month controls, `2929:16232` year, `2929:16236` month, `2929:16243` weekdays, `2929:16258` grid를 구현한다.
3. week strip은 `completedDates`를 받아 선택 우선 상태를 렌더링한다.
4. `HomeHeaderSection`에 `2929:19015` tooltip을 추가하고 외부 탭 닫기와 Calendar 링크 action을 연결한다.
5. `HomePromptAnswerSection`을 default `3087:29376`, focus `3139:1399`, done `3087:29378`로 렌더링하며 질문 CTA의 `ClickQuote` 연결을 제거한다.
6. record 성공은 기존 scaffold snackbar를 통해 `답변을 기록했어요.`를 표시한다.

### Task 4: Home assembly and regression verification

**Files:**
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/home/HomeView.kt`
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/home/FigmaHomeContent.kt`
- Modify if required: `presentation/src/main/java/com/arakene/presentation/ui/common/MainNavHost.kt`

1. ViewModel state/action을 Home composable에 단방향으로 연결한다.
2. 명언 카드 탭만 기존 Typing route를 유지한다.
3. image/copy/share/like/login flows의 기존 effect와 dialog 동작을 회귀 확인한다.
4. 실행: `./gradlew :presentation:testDebugUnitTest --console=plain` 및 `./gradlew :presentation:assembleDebug --console=plain`.

### Task 5: Runtime QA and documentation

**Files:**
- Modify: `docs/screens/2_home.md`
- Create/Modify: `docs/design-qa/2026-09-07-android-home-interactions-qa.md`
- Add captures: `docs/design-qa/assets/home-figma-2929-17193/2026-09-07/runtime-android-*.png`

1. emulator/device에서 default, calendar-open, question-focus/done/snackbar, streak-tooltip, image modal, light/dark 화면을 360×821 기준으로 캡처한다.
2. Figma 기준 캡처와 구조/간격/색/타이포/아이콘/상태를 비교하고 최대 5회 수정한다.
3. `./gradlew :presentation:testDebugUnitTest :presentation:assembleDebug --console=plain`을 최종 실행한다.
4. `docs/screens/2_home.md`의 검증 상태와 QA 링크를 최종 결과로 갱신한다.
