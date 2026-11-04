# Home 날짜 영역 연속필사 표시 QA · 계획 v3

## 선택·승인 기록

- Notion: [Home > 날짜 영역 > 연속필사 표시안됨](https://app.notion.com/p/Home-40de639fc7a4803ca620ed225eba2a24), page ID `40de639f-c7a4-803c-a620-ed225eba2a24`.
- 원문 속성: Android·iOS, Home, 기능, 우선순위 중간, `Not started`. 2026-11-03 10:14 UTC 생성, 10:26 UTC 최종 수정. 본문 블록 0건, 댓글 1건과 HEIC 첨부 1개를 재조회했다.
- 현재 단계: **v3 승인·Android 목데이터 기능 검증 통과, 전체 화면 Figma 최종 승인 보류**. 사용자는 “그럼 이거 진행해봐 진행하고 목데이터로 검증해서 결과좀 보여줄래”라고 v2 날짜별 서버 판정 매칭을 승인했고, 새 댓글·첨부 확인 후 “기존 불꽃 배지 표시 승인”으로 선택+연속필사 상태의 기존 배지 겹침을 Figma 예외로 명시 승인했다. v3는 이 두 범위와 독립 목데이터 검증을 포함한다. [결과 보고서](../design-qa/2026-11-03-home-streak-indicator-40de639f-report.html).
- 기준 리비전: `stage` `5489625`. 기존 미커밋 `app/.DS_Store`, `fastlane/report.xml`은 사용자 작업으로 보존한다. 이 계획의 사전 증거와 문서만 추가했다.

## 문제와 확인된 근거

- 제목은 “Home > 날짜 영역 > 연속필사 표시안됨”이다. 2026-11-03 10:26 UTC 추가된 SooJi 댓글: “대상 : iOS/Android (라이트/블랙)\n당일 연속필사를 한 경우, 불꽃 표시가 나타나지 않고 있습니다!” [첨부 PNG 사본](../design-qa/artifacts/2026-11-03-qa-home-streak/notion-comment-40de639f.png)에서 11월 3일 선택 원을 노란색으로 둘렀고 그 원에는 불꽃이 없다. 직전 2일에는 불꽃, 헤더에는 2일이 보인다. 첨부 화면은 iOS Dark로 보이나 실제 Android 서버 응답값은 없다.
- Figma 파일: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556. Light 전체 `2929:13556` 360×821, 날짜 줄 `3204:2435` 240×40, 완료일 `2929:13651`/`3204:2446`와 불꽃 배지, 기본·오늘 상태가 있다. [Figma 날짜 줄](../design-qa/artifacts/2026-11-03-qa-home-streak/figma-week-strip-3204-2435.png), [Figma 전체](../design-qa/assets/home-figma-2929-13556/reference-full.png). 10·11일은 완료, 16일은 오늘 선택 상태다. 선택일과 완료일이 **같은** Figma 변형은 이 노드에서 확인되지 않았다.
- AS-IS Compose Preview: [현재 소스 전체 이미지](../design-qa/artifacts/2026-11-03-qa-home-streak/before-home-light/com/arakene/presentation/ui/audit/HomeAuditPreviewsKt/AuditFigma292913556_0.png) 및 [component tree](../design-qa/artifacts/2026-11-03-qa-home-streak/before-home-light/com/arakene/presentation/ui/audit/HomeAuditPreviewsKt/AuditFigma292913556_0.json). `android preview render :presentation --fqn=com.arakene.presentation.ui.audit.HomeAuditPreviewsKt.AuditFigma292913556 --component-tree`로 렌더했다. fixture의 10·11일은 완료 데이터이지만 Preview PNG에서 불꽃 SVG가 빠진다.
- AS-IS 실제 런타임: [API31 에뮬레이터 360×821 전체 프레임](../design-qa/artifacts/2026-11-03-qa-home-streak/before-runtime-fixture-api31-360x821.png). 같은 Debug 회원 fixture의 10·11일에 보라색 원과 불꽃 배지가 **표시된다**. Preview의 SVG 누락을 운영 증상으로 간주하지 않는다. 이 fixture는 실제 계정/서버 응답을 재현하지 않는다. 사용자가 제보한 조건의 누락은 아직 재현하지 못했다.

## Swagger 서버 계약 · 확인된 사실

- 사용자 제공 [Swagger UI v2](https://api.fillsa.com/fillsa/docs/swagger-ui/index.html?urls.primaryName=v2)의 [OpenAPI 원문](https://api.fillsa.com/fillsa/docs/v3/api-docs/v2)을 2026-11-03 읽었다. 이 문서는 **계약**이며 실제 계정 응답은 아직 확인하지 않았다.
- `GET /api/v2/member-quotes/weekly`: `days[].state`는 `none/today/done`이고 `done`은 오늘 필사 완료 상태까지 포함한다. `days[].completed` 설명은 **필사 완료 여부**다. 주간 `DayQuoteData`에는 `todayCompleted`가 없다. 따라서 `state == "done"` 또는 `completed == true`만으로 **연속필사 여부**를 판정할 수 없다.
- `GET /api/v2/member-quotes/monthly?yearMonth=yyyy-MM`: `memberQuotes[].quoteDate`별 `todayCompleted` 설명은 **연속 필사 여부**다. 날짜별 불꽃 배지의 서버 원천으로 가장 직접적이다. `monthlySummary.streakCount`는 월별 **연속필사 갯수**로, 각 날짜에 배지를 놓을 수 있는 목록은 아니다.
- `POST /api/v2/member-quotes/{dailyQuoteSeq}/typing` 응답의 `todayCompletedChanged`는 **연속 필사 완료 변경여부**다. 현재 날짜 상태의 목록이나 확정값으로 쓰지 않고 저장 후 월간 데이터를 재조회하는 신호로만 다룬다.
- 월별 `todayCompleted`를 해당 `quoteDate`의 서버 판정으로 사용한다. Android에서 앞뒤 날짜의 인접 여부를 직접 계산하지 않는다. 서버의 월 경계 연속 판정 알고리즘은 공개 Swagger에 정의되어 있지 않다.
- [v1 OpenAPI](https://api.fillsa.com/fillsa/docs/v3/api-docs/v1)의 `GET /api/v1/member-streaks`는 `currentStreak`(연속 일수), `isTodayWritten`(오늘 필사 여부)을 제공한다. 헤더에는 적합하지만 과거 날짜별 연속필사 판정에는 부족하다.

## AS-IS 데이터 흐름과 원인 분석

1. 회원 Home 날짜 줄: `GET /api/v2/member-quotes/weekly` → `MemberQuoteDay.state == "done" || completed` → `HomeViewModel.memberHistoryCompletedDates` → `HomeView.completedDates` → `homeHistoryDayStates` → `HomeWeekStrip`의 보라색 원·불꽃. **서버 계약의 일반 필사 완료를 연속필사 표시로 사용 중인 불일치가 확인됐다.** 실제 제보의 누락이 이 불일치 때문인지는 실계정 응답이 없어 확정할 수 없다.
2. 비회원: 로컬 `GetAllStreakInfoUseCase` → `isDailyWritingCompleted`인 `StreakInfo.date` → `HomeViewModel.completedDates` → 동일 UI 경로. v2 회원 서버 계약은 비회원 로컬 기록에 적용되지 않는다.
3. 표시: 미선택 완료일은 보라색 원과 `home_complete_badge.svg`를 표시한다. 현재 `homeWeekDayPresentation`은 `isSelected`를 먼저 검사해 **선택한 완료일의 불꽃 배지를 숨긴다**. 새 댓글 첨부가 바로 선택한 오늘의 불꽃 누락을 가리킨다. 월간 `todayCompleted=true`여도 선택된 오늘은 기존 UI에서 불꽃이 숨겨진다.
4. 현재 fixture는 주간 `completed`만으로 10·11일을 표시해 통과한다. 따라서 Swagger가 구분하는 `completed=true`, `todayCompleted=false`와 반대 조합을 검증하지 못한다. 실계정 데이터·월간 응답·선택 상태는 별도 확인이 필요하다.

## 해결 설계 / TO-BE

- **권장안:** 회원 날짜별 연속필사는 월간 `memberQuotes[].todayCompleted == true`인 `quoteDate` 집합으로 판정한다. Figma의 `weekday/complete`(보라색 원+불꽃) 변형은 이 날짜에만 적용하고, 그 밖의 날짜는 기존 기본/선택 변형을 쓴다. 일반 `completed`, `state`, 답변·좋아요, 헤더 `currentStreak`로 불꽃을 추정하지 않는다. 월별 `streakCount`는 합계 확인에만 사용한다.
- Home 주간 viewport가 월을 넘으면 표시 중인 날짜의 두 `yearMonth`를 각각 조회한다. 기존 `GetQuotesMonthlyUseCase`·`MemberMonthlyQuoteResponse`를 재사용하고, `HomeViewModel`에서 인증 세대별 월 캐시/진행 요청을 관리해 같은 월의 중복 호출을 막는다. 스크롤 범위가 바뀔 때 필요한 월만 읽고, Home 복귀·필사 성공 후 현재 보이는 월을 새로고침한다. 실패 시 이미 확인된 배지는 유지하되 주간 일반 `completed`로 대체하지 않는다.
- `HomeView`는 회원의 월간 연속필사 날짜와 비회원의 로컬 완료 날짜를 구분해 `HomeWeekDayState`에 전달한다. `HomeWeekStrip`의 7칸 viewport·스크롤·선택 동작과 Figma 자산은 유지한다. 월간 응답과 주간 응답이 다르면 연속필사 배지는 월간 `todayCompleted`를 기준으로 하고 차이를 QA에 기록한다.
- **선택한 연속필사 완료일**의 불꽃이 새 댓글의 핵심이다. 기존 Figma 날짜 줄 `3204:2435`에는 미선택 완료와 선택 상태만 확인되고 선택+완료 변형은 없다. 사용자가 “기존 불꽃 배지 표시 승인”이라고 명시 승인했으므로, 기존 선택 원 모양·숫자는 유지하고 `todayCompleted=true`인 선택일에도 기존 `home_complete_badge.svg`를 같은 위치에 겹쳐 그린다. 새 자산·문구·애니메이션은 추가하지 않는다.
- 대안: 주간 API에 `todayCompleted`를 추가하면 월간 조회를 줄일 수 있으나, 현재 공개 v2 계약에는 없고 서버 변경이 필요하다. Android 단독 수정의 권장안은 기존 월간 API 재사용이다. 비회원 로컬 흐름은 유지하며 실제 누락이 재현되면 해당 경계만 별도 보완한다. iOS 수정은 범위 밖이다.

## 수정 순서와 검증 조건

1. 새 댓글·첨부의 선택된 오늘 누락을 기준으로 Android 목데이터를 구성한다. 실제 계정의 로그인 상태, 주간 `completed/state`·월간 `todayCompleted` 값은 아직 없어 원제보 실데이터 재현 한계와 목데이터 검증을 분리한다.
2. 회원 재현 테스트: 주간 `completed=true`/월간 `todayCompleted=false`는 불꽃 없음, 주간 완료값과 무관하게 월간 `todayCompleted=true`인 날짜는 불꽃 있음. 월 경계의 두 달, 지연/실패 응답, 인증 세대 전환, Home 복귀·필사 성공 뒤 갱신을 포함한다. 비회원 기존 로컬 완료 경계도 회귀 테스트한다.
3. 기존 `GetQuotesMonthlyUseCase`를 `HomeViewModel`에 연결해 월별 날짜 집합과 캐시·갱신을 만들고 `HomeView`의 회원 날짜 상태에만 반영한다. `HomeWeekStrip`의 선택 원은 유지하면서 연속필사 flag가 참인 선택일에도 기존 배지를 표시한다. 주간 API는 명언·날짜 선택에 계속 사용한다.
4. `:presentation:testDebugUnitTest`와 `:app:assembleDebug`를 실행한다. 실제 서버 응답을 조회할 권한/계정이 없으면 계약 기반 fake 테스트와 실계정 미검증을 분리해 보고한다.
5. `todayCompleted`가 구분되는 결정적 Light/Dark 하위 날짜 줄 Preview를 현재 소스로 렌더하고 Figma `3204:2435`와 비교한다. Preview SVG 누락은 PNG·component tree와 실제 런타임을 함께 기록한다. 하위 검증 뒤 전체 Home Preview, 동일 360×821 조건의 에뮬레이터 전체 프레임을 비교한다. 시스템 바와 기존 Home 차이도 결과에 별도 표시한다. 최대 5차 비교 후 불일치가 남으면 `Pass`로 표시하지 않는다.
6. 화면 문서·QA 기록·AS-IS/TO-BE HTML을 이 이슈 ID로 독립 갱신한다. 보고 직전 Notion 원문·댓글을 재조회한다. Notion 상태 수정은 별도 요청이 있을 때만 수행한다.

## 위험·복구·판단 필요 사항

- `todayCompleted`를 일반 `completed`와 혼동하면 연속필사하지 않은 날짜에 배지가 생긴다. 월간 날짜 플래그만 배지에 사용하고 응답 불일치·누락은 별도 기록한다.
- 월 경계에서 최대 두 월의 추가 조회가 필요하다. 인증 세대별 캐시와 요청 중복 방지, 실패 시 기존 표시 보존을 적용하고 날짜 스크롤 회귀 테스트를 재실행한다.
- 사용자 작업 파일 두 개를 건드리지 않는다. 구현 전후 변경은 이 이슈의 파일과 증거로 제한하고, 되돌릴 때 별도 커밋/브랜치 경계로 복구한다.
- 선택+연속필사 UI 예외는 사용자 승인 완료. 제보 캡처는 확보했으나 실제 Android 계정의 월간 `todayCompleted` 값은 아직 없다. 현재 Swagger만으로 서버의 월 경계 판정과 실계정 응답은 확정하지 않는다.
