# Home 날짜 영역 연속필사 표시 QA

- Notion: https://app.notion.com/p/Home-40de639fc7a4803ca620ed225eba2a24 · `40de639f-c7a4-803c-a620-ed225eba2a24` · 2026-11-03 13:57 UTC 최종 재조회: 본문 0건, 댓글 1건·HEIC 첨부 1개, Android/iOS, Home, 기능/중간, Not started. 사전 조회 당시에는 댓글이 없었고 10:26 UTC에 새로 작성됐다.
- 계획: [v3 Markdown](../screens/2026-11-03-home-streak-indicator-40de639f-plan.md) · [v3 HTML](../screens/2026-11-03-home-streak-indicator-40de639f-plan.html). v2의 Android 날짜별 서버 판정 매칭·목데이터 검증은 사용자가 승인했다. v3는 새 댓글의 선택일 상태와 Figma 판단 필요성을 기록한다.
- 기준 리비전: `stage` `5489625`; 기존 미커밋 `app/.DS_Store`, `fastlane/report.xml` 보존.

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556
- 대상 노드: Light Home 전체 `2929:13556`(360×821), 날짜 줄 `3204:2435`(240×40), 완료일 `2929:13651`/`3204:2446`와 18px 불꽃 배지. 완료된 10·11일과 선택한 16일을 확인. Figma 노드의 주석은 7일 노출·과거 히스토리·좌우 스크롤을 명시한다.
- Figma 기준: [날짜 줄](artifacts/2026-11-03-qa-home-streak/figma-week-strip-3204-2435.png), [전체](assets/home-figma-2929-13556/reference-full.png). 전체 이미지는 루트·시스템 바·하단 영역을 포함한다.
- Preview 명령: `~/.local/bin/android preview render :presentation --fqn=com.arakene.presentation.ui.audit.HomeAuditPreviewsKt.AuditFigma292913556 --component-tree --output=docs/design-qa/artifacts/2026-11-03-qa-home-streak/before-home-light`
- Preview 조건: 360×773dp 콘텐츠, Light, ko, fontScale 1.0, Debug fixture: 2026-08-16 선택, 10·11일 완료. [PNG](artifacts/2026-11-03-qa-home-streak/before-home-light/com/arakene/presentation/ui/audit/HomeAuditPreviewsKt/AuditFigma292913556_0.png) 945×2029px, [component tree](artifacts/2026-11-03-qa-home-streak/before-home-light/com/arakene/presentation/ui/audit/HomeAuditPreviewsKt/AuditFigma292913556_0.json).
- 런타임 조건: Android Emulator `emulator-5556` API31, 360×821px, density 160, Light, Debug `HOME_QA_FIXTURE=true`, `HOME_QA_STREAK=100`, `HOME_QA_HIDE_AD=true`. [전체 프레임](artifacts/2026-11-03-qa-home-streak/before-runtime-fixture-api31-360x821.png).

## 사전 재현 결과

| 범위 | Figma 목표 | 실제 결과 | 판정 |
|---|---|---|---|
| 완료 10·11일 날짜 원 | 보라색 완료 원 | Preview·런타임에 보라색 원 | fixture에서 표시 확인 |
| 완료 불꽃 배지 | 완료 원 위 불꽃 | CLI Preview PNG에는 누락, 실제 에뮬레이터에는 10·11일 모두 표시 | Preview 단독으로 장애 확정 불가 |
| 실제 제보 조건 | 완료한 날짜에 배지 표시 | 계정·완료일·선택일·재현 캡처가 없어 동일 조건을 구성하지 못함 | **재현 미확인** |
| 선택한 완료일 | 기준 변형 미확보 | 코드상 선택 상태가 완료 상태보다 우선해 배지 숨김 | 제품 결정 대기 |

- 현 코드 흐름: 회원 weekly 응답 `state == "done" || completed`; 비회원 로컬 `isDailyWritingCompleted`; 둘 다 `HomeView`의 `completedDates`를 거쳐 `HomeWeekStrip`에 전달된다. 회원 Home resume은 주간 API를 재요청한다. `HomeRuntimeQaFixture`는 완료 10·11일을 제공하며 런타임 배지가 표시된다.
- 서버 계약 재확인: [v2 OpenAPI](https://api.fillsa.com/fillsa/docs/v3/api-docs/v2)에서 weekly `days[].completed`는 필사 완료, monthly `memberQuotes[].todayCompleted`는 날짜별 연속 필사 여부다. weekly `DayQuoteData`에는 `todayCompleted`가 없다. `monthlySummary.streakCount`는 월별 합계이며 날짜 목록이 아니다. [v1 OpenAPI](https://api.fillsa.com/fillsa/docs/v3/api-docs/v1)의 `currentStreak`도 현재 연속 일수만 제공한다. 실계정 응답값은 확인하지 않았다.
- 확인된 설계 불일치: 현재 Home 불꽃 배지는 weekly의 일반 `completed/state`로 판정한다. 실제 제보 증상의 직접 원인인지는 재현 조건이 없어 미확정. 계획 v2는 월간 `todayCompleted` 날짜를 회원 배지 원천으로 제안한다.
- CLI Preview에서 SVG가 빠진 현상은 실제 앱과 결과가 다르다. 이 스킬의 Preview 확인과 별도로 `AGENTS.md`/`docs/ui-redesign-workflow.md`가 요구하는 실제 전체 프레임을 확보했다. 전체 Home 화면에는 기존 Figma 차이가 있어 이 사전 조사로 `Pass`를 부여하지 않는다.
- `emulator-5554` API35는 현재 패키지 설치 후 Activity 시작이 `Error type 3`으로 실패했다. `emulator-5556` API31에서 동일 Debug APK로 사전 런타임을 확인했다. 최종 수정 검증 시 API35 재시도를 계획한다.
- 사전 조사 시점에는 앱 코드를 수정하지 않았다. 이후 승인된 데이터 매칭을 구현했으며 아래 후속 검증을 기록한다. Notion 상태는 변경하지 않았다.
- HTML 계획서 정적 검사: 로컬 링크 7개 중 누락 0, 미치환 토큰 0, `git diff --check` 통과. 연결 가능한 브라우저가 없어 넓은/좁은 화면 렌더 시각 점검은 미실시.

## 새 Notion 댓글과 범위 교정

- SooJi, 2026-11-03 10:26 UTC, comment `40de639f-c7a4-80d5-90e0-001d464b1cba`: “대상 : iOS/Android (라이트/블랙)\n당일 연속필사를 한 경우, 불꽃 표시가 나타나지 않고 있습니다!”
- [원본 HEIC 사본](artifacts/2026-11-03-qa-home-streak/notion-comment-40de639f.heic) · [열람용 PNG](artifacts/2026-11-03-qa-home-streak/notion-comment-40de639f.png). 첨부는 제보자의 iOS Dark 전체 화면으로 보인다. 11월 3일 선택 원은 노란색 표시로 강조됐고 불꽃이 없다. 2일에는 불꽃, 헤더에는 `2일`이 보인다. 원본은 댓글 첨부이며 앱 검증 캡처와 구분한다.
- 새 정보로 원제보의 초점은 **선택된 당일**임이 확인됐다. 현재 `homeWeekDayPresentation`은 선택을 먼저 처리하고 `HomeWeekStrip`은 `Completed` 변형에만 불꽃을 그린다. 월간 `todayCompleted=true`가 전달돼도 선택된 당일 배지가 숨겨지는 문제는 이 코드에서 재현 가능하다.
- Figma 날짜 줄 `3204:2435`에는 선택+연속필사 조합이 없다. `AGENTS.md`는 Figma에 없는 표시 상태에 사용자 명시 승인을 요구한다. 사용자가 “기존 불꽃 배지 표시 승인”이라고 답해 기존 배지를 선택 원 위 같은 위치에 겹쳐 표시하는 예외를 승인했다.

## 승인된 데이터 매칭 구현 · 목데이터 검증

- `HomeViewModel`: `GetQuotesMonthlyUseCase`로 주간에 걸친 각 월을 조회해 `memberQuotes[].quoteDate` 중 `todayCompleted=true`인 날짜만 월 캐시에 보관한다. Home 복귀와 필사 POST 성공 시 재조회하고 실패 시 기존 확인 날짜를 유지한다. 인증 세대가 바뀌면 캐시와 진행 요청을 비운다.
- `HomeView`: 회원 날짜 줄은 월간 서버 판정 날짜, 비회원은 기존 로컬 날짜를 사용한다. 주간 `completed/state`에서 불꽃을 직접 판정하던 사용 경로는 제거했다.
- Debug 목데이터: 8월 10일은 주간 `completed=false`·연속필사=true, 12일은 주간 `completed=true`·연속필사=false, 11일은 양쪽 true. 16일은 선택된 연속필사=true 상태를 별도 재현한다. `todayCompleted`가 앞뒤 날짜에 붙었는지 앱에서 계산하지 않는다.
- `./gradlew :presentation:testDebugUnitTest :app:assembleDebug --console=plain`: 선택일 fixture 추가 후 2026-11-03 13:31 UTC 재실행, **둘 다 성공**. 월 경계 8월 30일/9월 1일 서버 true만 표시, 주간 완료만 true인 날짜 제외, 월 캐시/재진입/POST 후 갱신/실패 시 보존을 확인했다. 선택된 16일도 연속필사 flag가 참이지만 현 `homeWeekDayPresentation`이 `Selected`임을 테스트로 고정했다.

## UI 검증 · 1차

| 컴포넌트 | Figma 노드 | 상태 | 결과 |
|---|---|---|---|
| `HomeWeekStrip` | `3204:2435` | 10·11일 미선택 완료, 16일 선택 | Light/Dark Preview에서 원·숫자는 확인. CLI는 SVG 불꽃을 누락하므로 런타임으로 검증. 선택+완료는 Figma 변형 없음 |
| `HomeDateWeekSection` | `2929:13556` 날짜 영역 | 월 카드 + 7칸 | Light Preview는 구조 확인. Dark Preview의 월 텍스트가 잘려 보여 런타임 증거와 차이가 있음 |
| 전체 Home | `2929:13556` | Light Debug fixture | 실제 API31 전체 프레임에 10·11일 불꽃 표시. 기존 전체 화면 Figma 차이와 선택일 누락으로 최종 Pass 아님 |

- CLI 하위 Preview: [Light PNG](artifacts/2026-11-03-qa-home-streak/after-week-light/com/arakene/presentation/ui/home/FigmaHomeContentKt/HomeDateWeekSpacingPreview_0.png) · [tree](artifacts/2026-11-03-qa-home-streak/after-week-light/com/arakene/presentation/ui/home/FigmaHomeContentKt/HomeDateWeekSpacingPreview_0.json), [Dark PNG](artifacts/2026-11-03-qa-home-streak/after-week-dark/com/arakene/presentation/ui/home/FigmaHomeContentKt/HomeDateWeekSpacingDarkPreview_0.png) · [tree](artifacts/2026-11-03-qa-home-streak/after-week-dark/com/arakene/presentation/ui/home/FigmaHomeContentKt/HomeDateWeekSpacingDarkPreview_0.json). 360×50dp, ko, fontScale 1.0, 10·11일 완료/16일 선택.
- CLI 전체 Preview: [Light PNG](artifacts/2026-11-03-qa-home-streak/after-home-light/com/arakene/presentation/ui/audit/HomeAuditPreviewsKt/AuditFigma292913556_0.png) · [tree](artifacts/2026-11-03-qa-home-streak/after-home-light/com/arakene/presentation/ui/audit/HomeAuditPreviewsKt/AuditFigma292913556_0.json). 360×773dp 콘텐츠. 런타임 자산과 시스템 바를 포함하지 않아 Figma 전체 화면 통과 근거가 아니다.
- 실제 전체 프레임: [Light](artifacts/2026-11-03-qa-home-streak/after-runtime-fixture-api31-360x821.png), [Dark](artifacts/2026-11-03-qa-home-streak/after-runtime-fixture-dark-api31-360x821.png), 최신 빌드로 2026-11-03 13:31 UTC 재캡처. Android Emulator API31, 360×821px, density 160, ko, Debug fixture, 광고 숨김. SVG 불꽃은 10·11일에 표시된다. 12일은 일반 날짜이며 **연속필사 flag=true인 선택 16일에는 불꽃이 없다**. 시스템 바/하단 내비게이션을 포함한다.
- Figma Light 날짜 줄과 런타임 날짜 줄을 나란히 확인: 보라색 원·흰 숫자·불꽃 위치는 대응된다. 전체 화면은 기준과 시스템 바, 하단 영역, 광고/답변 영역 구성 등 차이가 남아 이 이슈 QA를 `Pass`로 표시하지 않는다. 선택된 오늘의 불꽃은 현재 여전히 숨겨진다.

## UI 검증 · 2차 · 승인된 선택일 상태

- 변경: `homeWeekDayPresentation`의 선택 원·숫자 우선순위는 유지하고 `homeWeekDayShowsBadge(day)`가 `isCompleted`인 모든 날짜에 기존 `home_complete_badge.svg`를 그린다. 새 자산·문구·애니메이션은 없다. Light/Dark의 모양은 사용자 승인 예외다.
- TDD: 선택된 연속필사 날짜에 기존 불꽃을 요구한 새 테스트는 `homeWeekDayShowsBadge` 미정의로 먼저 컴파일 실패했다. 구현 후 `./gradlew :presentation:testDebugUnitTest :app:assembleDebug --console=plain`이 2026-11-03 13:52 UTC **성공**했다. 선택일 불꽃 참/거짓, 월 경계 매칭, 복귀·POST·로그아웃 회귀를 포함한다.
- 결정적 하위 Preview: [선택일 Light PNG](artifacts/2026-11-03-qa-home-streak/selected-week-light/com/arakene/presentation/ui/home/FigmaHomeContentKt/HomeDateWeekSelectedStreakLightPreview_0.png) · [tree](artifacts/2026-11-03-qa-home-streak/selected-week-light/com/arakene/presentation/ui/home/FigmaHomeContentKt/HomeDateWeekSelectedStreakLightPreview_0.json), [선택일 Dark PNG](artifacts/2026-11-03-qa-home-streak/selected-week-dark/com/arakene/presentation/ui/home/FigmaHomeContentKt/HomeDateWeekSelectedStreakDarkPreview_0.png) · [tree](artifacts/2026-11-03-qa-home-streak/selected-week-dark/com/arakene/presentation/ui/home/FigmaHomeContentKt/HomeDateWeekSelectedStreakDarkPreview_0.json). 각각 360×50dp, ko, fontScale 1.0, 10·11·16일 연속필사, 16일 선택. CLI Preview의 SVG 누락과 Dark 텍스트 잘림이 남아 런타임으로 표시 상태를 확인했다.
- [최종 전체 Preview PNG](artifacts/2026-11-03-qa-home-streak/final-home-light/com/arakene/presentation/ui/audit/HomeAuditPreviewsKt/AuditFigma292913556_0.png) · [tree](artifacts/2026-11-03-qa-home-streak/final-home-light/com/arakene/presentation/ui/audit/HomeAuditPreviewsKt/AuditFigma292913556_0.json).
- 실제 전체 프레임: [최종 Light](artifacts/2026-11-03-qa-home-streak/final-runtime-fixture-api31-360x821.png) · [최종 Dark](artifacts/2026-11-03-qa-home-streak/final-runtime-fixture-dark-api31-360x821.png). Android Emulator API31, 360×821px, density 160, ko, Debug fixture. **10·11일과 선택된 16일 모두 불꽃이 표시되고 12일은 일반 날짜**로 남는다. 선택 원·숫자 모양과 위치는 이전과 같다.
- 제보 첨부의 선택된 당일 누락과 같은 코드 조건을 목데이터로 재현하고 수정 전·후를 비교했다. 실계정 Android monthly 응답과 iOS 앱은 검증하지 않았다.

## 최종 판정

- 승인된 **서버 판정 매칭과 선택일 불꽃 기능: 목데이터 검증 통과**. Notion 제보의 Android 실계정 데이터로 한 재현은 미검증이다.
- Figma `3204:2435`의 미선택 완료·선택 원은 대응하며 선택+완료 조합은 사용자 승인 예외로 확인했다. CLI Preview와 전체 Home의 기존 Figma 차이가 남아 **전체 조립 화면 Figma 최종 결과는 Blocked**. 이 이슈의 기능 결과와 전체 화면 디자인 승인을 구분한다.

## 2026-11-04 홈 재진입 갱신 재확인

- 저장 버튼 완료 경로: `TypingViewModel.saveTyping`은 회원 `postTypingUseCase`의 성공 응답을 받은 뒤 완료 다이얼로그를 표시한다. 확인 버튼은 Home으로 이동한다. Home의 `LifecycleResumeEffect`가 `HomeViewModel.initialRefresh`를 호출한다.
- 회원 Home 첫 진입은 주간 응답 적용 시 필요한 월을 조회한다. 기존 Home으로 복귀하면 보이는 날짜 범위의 월을 `refresh=true`로 다시 조회하고, 성공한 `todayCompleted`를 날짜 집합에 반영한다. 선택된 오늘도 이 집합에 있으면 기존 불꽃이 표시된다.
- `HomeViewModelIntegrationTest`의 `returning to Home after today's typing fetches the new streak flag`에서 오늘 플래그를 false→true로 바꾸고, 주간 날짜가 보이는 상태로 `initialRefresh`를 재호출했다. 월간 API 재요청과 오늘 날짜 집합 반영을 확인했다. `./gradlew :presentation:testDebugUnitTest --tests com.arakene.presentation.viewmodel.HomeViewModelIntegrationTest --console=plain` 통과.
- **뒤로가기는 예외:** `TypingQuoteView`의 BackHandler는 `saveTyping`의 비동기 작업을 시작한 직후 화면을 닫는다. Home 재조회가 서버 저장보다 먼저 끝날 수 있으므로 이 경로에서 당일 불꽃의 즉시 갱신은 보장되지 않는다. 서버가 월간 응답에 새 `todayCompleted`를 아직 반영하지 않았거나 월간 조회가 실패한 경우에도 새 배지는 표시되지 않는다.
