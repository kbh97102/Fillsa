# Home

## 2026-11-03 날짜 영역 연속필사 표시 QA

- Notion [원본](https://app.notion.com/p/Home-40de639fc7a4803ca620ed225eba2a24) `40de639f…2a24` · 독립 [계획 v3 HTML](2026-11-03-home-streak-indicator-40de639f-plan.html) / [Markdown](2026-11-03-home-streak-indicator-40de639f-plan.md) · [QA 기록](../design-qa/2026-11-03-home-streak-indicator-40de639f-qa.md) · [AS-IS/TO-BE 보고서](../design-qa/2026-11-03-home-streak-indicator-40de639f-report.html). Swagger v2의 날짜별 `todayCompleted` 매칭과 사용자 승인 선택일 불꽃 표시를 구현·목데이터 검증했다.
- Figma 파일 URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556. Light 전체 프레임 `2929:13556` 360×821, 날짜 줄 `3204:2435` 240×40, 완료일 `2929:13651`/`3204:2446`. [날짜 줄 기준 이미지](../design-qa/artifacts/2026-11-03-qa-home-streak/figma-week-strip-3204-2435.png), [전체 기준 이미지](../design-qa/assets/home-figma-2929-13556/reference-full.png).
- 대상 상태: ko, Light/Dark 기본 Home, 2026-08-16 선택·10/11/16일 연속필사. 최종 Debug fixture에서는 선택된 16일에도 기존 불꽃이 표시되고 주간 완료만 true인 12일은 일반 날짜다. Light/Dark API31 전체 런타임을 캡처했지만 실계정 Android 응답은 미확보다. 전체 화면 최종 Figma Pass는 미승인.
- 컴포넌트: `HomeWeekStrip` ↔ Figma `3204:2435`(미선택 완료 원·불꽃 배지 확인, 선택+완료는 사용자 승인 예외), `HomeDateWeekSection` ↔ Home 날짜 영역(월 카드와 7칸 viewport), `HomeViewModel`/`HomeView`(회원 월간 `todayCompleted`를 `quoteDate`별 배지 원천으로 연결; 비회원 로컬 완료 유지). 회원 weekly `completed/state`는 일반 필사 완료에만 사용한다. 새 댓글 [첨부](../design-qa/artifacts/2026-11-03-qa-home-streak/notion-comment-40de639f.png)의 선택된 오늘 불꽃 누락을 목데이터로 재현해 수정했다.

## 2026-11-01 날짜 영역 좌우 스크롤 QA

- [Notion Android 날짜 스크롤 계획 v1 HTML](2026-11-01-home-week-scroll-407e639f-plan.html) · [Markdown](2026-11-01-home-week-scroll-407e639f-plan.md) · [QA 기록](../design-qa/2026-11-01-home-week-scroll-407e639f-qa.md).
- Figma Light 전체 `2929:13556`, 날짜 줄 `3204:2435`; 360×821, ko-KR, 오늘 7일 범위 오른쪽 배치. 기준 이미지는 QA 기록에 보관했다.
- 컴포넌트: `HomeDateWeekSection`(월 카드와 날짜 줄 조립), `HomeWeekStrip`(7칸 viewport와 과거 히스토리), `HomeViewModel`(선택과 분리된 회원 주간 캐시). 독립 워크트리에서 초기 10–16 표시, 좌우 드래그, 선택 분리, 회원 캐시 성공·실패를 검증했다. [이번 QA 기록](../design-qa/2026-11-01-home-week-scroll-407e639f-qa.md). 전체 Home 프레임의 기존 Figma 차이와 실계정 주간 호출은 최종 Pass 범위에서 보류한다.

## Android CLI Preview / Figma 재대조 · 2026-10-28

- [비교 HTML](../design-qa/2026-10-28-calendar-preview-figma.html) · [QA 기록](../design-qa/2026-10-28-calendar-preview-figma-qa.md).
- 달력 헤더 치수·정렬은 일치하나 그림자 차이가 남는다. 본체는 표면색·테두리·요일 간격·날짜 서체·선택 셀 형태 수정 필요. 새 최종 승인 없음.
- 검증만 수행했으며 앱 소스를 수정하지 않았다. 시스템 영역·IME를 포함한 전체 런타임 검증은 별도다.

## Android 구성

- Compose 위치: `presentation/ui/home/`
- 상태 관리: `HomeViewModel`, `TypingViewModel`, `ShareViewModel`
- 범위: 오늘의 명언, 필사, 이미지·공유, 홈 내 달력 요약

## Figma UI 기준

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556
- 대상 프레임/노드: light `2929:13556` (`2.home`), dark `3039:26518` (`2.home`). Parent section `2929:9603` is not a render target.
- 대상 기기/프레임 크기: Figma export 360 × 821 px; light root `#FFEFCC`, dark root `#212121`. (The supplied 360 × 720 description conflicts with the authoritative node export; QA uses the export.)
- 검증 상태: **Blocked** — `wm size 945x2155` (420 dpi)로 logical 360 × 821dp exact-size component capture를 확보했다. clean light/dark positive default, genuine-zero warning/tooltip, inline calendar open/select-close, question done/edit와 completion snackbar는 확인했고 app-wide navigation은 2026-09-16에 Figma 3탭으로 정렬했다. full soft keyboard, authenticated image, 실제 completed-writing marker, live-ad/static-ad 차이 때문에 assembled-screen acceptance는 계속 Blocked다. 컴포넌트별 검증은 부분 통과일 수 있으나 화면 최종 결과는 Partial/Pass가 아니다.
- 기준 이미지: `docs/design-qa/assets/home-figma-2929-13556/reference-full.png`
- QA 기록: `docs/design-qa/2026-09-07-android-home-interactions-qa.md`
- Dark root 상세 명세: `docs/screens/2_home_dark.md` (root `2929:9603`, 상태별 render node와 기준 이미지)

### 2026-09-07 Home 상호작용 구현 계약

- 상위 범위: `2929:17193` (`2. home`, SECTION)
- 기본 Home: `2929:13556`; Dark Home: `3039:26518`
- 월 선택 기본/열림: `2929:15667` / `2929:16221`; 달력 열림 화면 `3139:1501`
- 달력 popup: 본체 `2929:16227`; 이전 월 `2929:16229`; 연도 `2929:16232`; 월 `2929:16236`; 다음 월 `2929:16240`; 요일 `2929:16243`; 날짜 grid `2929:16258`
- 주간 날짜: `3204:2435`. 현재/선택 날짜가 오른쪽 끝에 오도록 7일을 노출하고 과거 방향 탐색을 지원한다.
- 질문 set: `3087:29377`; 기본 `3087:29376`; focus `3139:1399`; done `3087:29378`
- 질문 flow: `3110:33782`; 기록 전 `3110:33783`; 기록 중 `3110:34152`; 저장 toast `3110:34293`; 기록 후 `3110:34438`
- 연속 필사 안내: 화면 `2929:18871`; group `2929:19015`; tooltip `2929:19016`; Calendar link `2929:19017`
- 이미지 flow: `3223:5107`; 등록 전 `3223:4982`; 등록 후 `3223:4647`; 보기 `3223:4773`; 대체 보기 `2929:19326`; 삭제 확인 `2929:19488`
- 복사 toast: 화면 `2929:19645`, toast `2929:19773`; 좋아요 선택 `2929:19784`
- 구현 범위 밖 연결 화면: 필사 `2929:17920`, 공유 `2929:18239`/`2929:18543`, 로그인 modal `2929:19027`. 기존 route만 유지한다.
- 기준 캡처: `docs/design-qa/assets/home-figma-2929-17193/2026-09-07/`

#### 상태와 동작

1. 월 영역 탭은 전체 Calendar route로 즉시 이동하지 않고 Home 위의 `248×335dp` 달력 popup을 토글한다. 외부 탭은 닫기, 날짜 선택은 Home 날짜 갱신·명언 재조회·popup 닫기를 수행한다.
2. 연속 필사 수가 0일 때 상태 아이콘 탭은 안내 tooltip을 표시한다. 외부 탭은 닫고, `나의 필사현황 보기`는 기존 Calendar route로 이동한다.
3. 회원 최초 진입은 `GET /api/v2/member-quotes/weekly`를 `endDate` 없이 호출한다. 서버 응답의 `days`는 주간 날짜, 선택 명언 카드, 질문·답변, 좋아요·이미지, 완료 표시의 단일 원천이다. 같은 주간 창 안의 날짜 탭과 카드 스와이프는 캐시된 `days`만 선택하고 네트워크를 호출하지 않으며, 창 경계에서만 현재 `endDate`에 ±7일을 적용해 weekly를 다시 호출한다.
4. 회원 질문 CTA는 명언 필사 route로 이동하지 않는다. 200 grapheme 이내이고 공백만이 아닌 답변을 `POST /api/v2/member-quotes/{dailyQuoteSeq}/answer`로 저장한다. 성공 전에는 done/edit 상태나 `답변을 기록했어요.` snackbar를 표시하지 않는다. POST 성공 시 응답의 `answer`·`answeredAt`을 선택 주간 메모리 레코드에 반영하고 기존 snackbar를 표시한 뒤, 같은 날짜의 `GET /api/v2/member-quotes/daily?quoteDate=`를 best-effort로 호출해 서버 상태를 재조정한다. 이 보조 refresh가 실패해도 POST 성공 상태를 되돌리지 않는다.
5. 비회원은 weekly/daily/answer 회원 API를 호출하지 않는다. 기존 v1 일간 조회, 로컬 좋아요·필사, 질문 답변의 session-only 동작을 유지한다.
6. 회원 completed weekday는 `state == "done"` 또는 `completed == true`인 서버 날짜만 사용하며, 답변 유무는 완료 표시 조건이 아니다. 비회원은 기존 `GetAllStreakInfoUseCase`의 실제 완료 필사 날짜를 사용한다.
7. 이미지 등록/보기/변경/삭제, 복사 snackbar, 좋아요 선택의 기존 domain/API 연결을 보존한다. 타이핑 GET은 v1을 유지하고 타이핑 POST만 v2를 유지한다.
8. 앱 전역 하단 bar는 Figma대로 Home/Calendar/My page 3탭을 사용한다. QuoteList route는 Calendar에서 진입하는 하위 화면으로 유지하고 하단 bar에는 노출하지 않는다.
9. access token 문자열 갱신은 회원 세션 교체로 취급하지 않는다. Home resume은 선택 상태를 기준으로 서버/로컬 projection을 다시 읽고, 명시적인 로그아웃 후 로그인 상태 전환만 기존 회원 cache와 진행 중 요청을 무효화한다.
10. 같은 날짜를 다시 선택하면 저장 전 질문 draft를 보존한다. 같은 명언에 대한 연속 좋아요 요청은 순서대로 전송하고 최신 사용자 선택만 화면과 주간 cache에 확정한다.
11. 회원 inline calendar의 오늘·선택 가능일·다음 달 이동 상한은 최초 weekly 응답의 서버 anchor를 따른다. 비회원은 기존 기기 날짜 기준을 유지한다.
12. `BuildConfig.DEBUG`의 Home QA fixture는 날짜·월·답변·좋아요·이미지 상호작용을 process-local 상태로만 처리하고 production API mutation을 실행하지 않는다.

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| Header | `2929:15476` | Figma logo, current streak, profile navigation | `FigmaHomeContent` | 부분 통과(exact-size positive default, genuine 0 warning/tooltip); unknown/null은 tooltip 미노출 |
| Month calendar | `2929:15667`, `2929:16221`, `3139:1501` | `YYYY.MM` selector와 inline month popup, 월/연도 탐색 및 날짜 선택 | `HomeDateWeekSection` / `HomeInlineCalendar` | 통과(exact-size runtime open/select-close) |
| Week strip | `3204:2435` | 월 카드와 9dp 간격, 선택일을 오른쪽 끝으로 둔 7일과 실제 완료 필사 marker | `HomeDateWeekSection` | 간격 Preview 통과; completion data Blocked |
| Locale toggle | `2929:19887` (light Home `2929:13611`, dark Home `3039:26574`) | 텍스트와 패딩으로 측정되는 한/A 선택 토글; 선택 배경과 텍스트 정렬 | shared `LocaleSwitch` in `FigmaHomeContent` | 컴포넌트 통과(physical dark runtime, 한/영 상태); 전체 Home 수용 상태는 기존 Blocked 유지 |
| Quote card | `2929:13642` | selected quote, author/search opens existing Wikipedia URI, right/left date-bounded swipe actions | `HomeQuoteCard` | 부분 통과(빌드) |
| Quote loading | 사용자 승인 예외(Figma/MCP 없음) | 명언 요청 중 카드 배경 중앙에 기본 `CircularProgressIndicator` 표시 | `HomeQuoteCard` / `HomeViewModel.quoteLoadState` | light/dark Preview 통과; 사용자 런타임 확인 예정 |
| Quote action row | `2929:15503` | copy/share/like/image actions with current like visual state | `HomeQuoteActionRow` | 부분 통과(빌드) |
| Prompt response | `3087:29376`, `3139:1399`, `3087:29378`, `3110:34293` | 200-grapheme 입력, 회원 answer POST 성공 후 snackbar·done/edit 상태, 비회원 session 기록; 명언 필사 route와 분리 | `HomePromptAnswerSection` / `HomeViewModel` | 부분 통과(exact done/snackbar 통과; focus full keyboard 미확보); 회원 API binding은 별도 연동 범위 |
| Global bottom navigation/ad | `3087:29254`; Calendar instance `3087:29220`; ad reference `2929:29249` is unavailable | Figma 3 × 120dp tabs; Android shared navigation도 Home/Calendar/My page 3탭 사용. 광고는 live surface 유지 | scaffold / `BottomNavigationBar` | navigation 통과; 광고 정책은 별도 범위 |
| Dark palette/assets | `3039:26518` | dark root/card/input/text/divider plus night SVG variants | `FigmaHomeContent` / `assets/figma/home-night/` | 부분 통과(exact-size positive default clean capture) |

회원 Home 질문 답변은 선택 날짜의 `questionKo`/`questionEn`, `answer`, `answeredAt`을 weekly/daily 응답에서 읽고 answer API로 저장한다. 선택 날짜가 바뀌면 날짜별 draft/recorded-answer를 원자적으로 교체하며, `dailyQuoteSeq == null`인 날짜에서는 답변을 포함한 mutation을 실행하지 않는다. 비회원만 기존 `HomeViewModel` session state로 기록/수정 상태와 snackbar를 유지하며 Typing으로 전달하지 않는다. 기존 quote 필사와 memo v1 API 필드는 신규 회원 Home·Calendar에서 재사용하지 않는다.

### 2026-09-08 회원 질문 저장 연결

- 범위: 위 light `2929:13556`의 질문 기본 `3087:29376`, focus `3139:1399`, done `3087:29378`, 저장 toast `3110:34293`.
- `HomeMemberAnswerCoordinator`가 원래 날짜·dailyQuoteSeq·계정 세대·요청 revision을 보관한다. 저장 중 중복 입력/제출을 막고, POST 성공 시 서버 답변과 기록 시간을 현재/캐시 주간 데이터에 반영한다. 후속 daily refresh는 동일 origin/auth/revision에만 적용하며 실패 시 POST 결과를 유지한다.
- 이미 연결된 `questionKo`/`questionEn`의 LocaleType 표시를 유지한다. Compose 변경은 저장 중 입력 readOnly 및 버튼 클릭 차단뿐이며 레이아웃·색·문구·계층은 변경하지 않는다.
- QA 기록: [2026-09-08 Home answer integration QA](../design-qa/2026-09-08-android-home-answer-integration-qa.md). 단위 테스트와 코드 범위 검증은 비최종 증거이며, 전체 조립 화면의 runtime/Figma 최종 수용은 부모 작업자 검증 대기다.
- 2026-09-13 안정화 검증은 token renewal 중 POST 수용, resume refresh, 같은 날짜 draft 보존, 좋아요 최신 의도 직렬화, 서버 anchor 달력 경계, QA fixture 무네트워크 정책을 포함한다. 정확한 Gradle 명령과 결과는 위 QA 기록과 `docs/planning.md`에 남긴다.

### 2026-09-19 Home locale toggle 패딩 수정

- Figma 컴포넌트: `2929:19887` (`Toggle_off_S`), light Home 인스턴스 `2929:13611`, dark Home 인스턴스 `3039:26574`.
- 기준은 고정 폭/높이나 균등 `weight`가 아니라 텍스트 측정값과 패딩의 합이다. 항목 패딩은 좌우 `8dp`, 상하 `2dp`; 외곽 패딩은 시작 `4dp`, 상하 `3dp`다.
- Home 전용 `FigmaLocaleToggle`을 제거하고 shared `LocaleSwitch`를 색상·패딩 파라미터로 재사용한다. 기존 Typing/Quote detail 호출의 기본 보라색 스타일과 패딩은 유지한다.
- Home 색상은 outer `#FFCB5C`, selected `#FFFFFF`, text `#212121`을 사용한다.
- QA 기록: [2026-09-19 Home locale toggle QA](../design-qa/2026-09-19-android-home-locale-toggle-qa.md).

`3087:29254`는 Home/Calendar/My page 3개 탭(각 120 × 60dp, 32dp icon), 선택 `#5C65FF`, 비선택 `#212121`을 정의합니다. Android `BottomNavigationBar`도 2026-09-16부터 같은 3등분 구조와 기존 32dp local vector를 사용합니다. QuoteList destination은 Calendar 집계에서 진입하는 하위 route로 남지만 전역 하단 bar와 표시 대상 route에서는 제외합니다. 광고는 기존 live `SingleLineAdSection` 정책을 유지하며 이번 navigation 변경 범위에 포함하지 않습니다.

Dark Home은 `3039:26518`의 `#212121` root, `#424242` quote/input card, `#616161` border/divider(주 divider 55%), white primary text, `#E0E0E0` action label, `#9E9E9E` muted text를 `HomeColorPalette`로 해석합니다. calendar와 selected/completed weekday, `#5C65FF` CTA는 Figma 상태대로 유지합니다. 정확한 dark SVG는 `presentation/src/main/assets/figma/home-night/`의 logo/profile/wave/search/copy/share/like/camera 8종이며 SVG가 density-independent이므로 Figma의 60/24/16/320dp Compose 크기로 렌더합니다. calendar/write/badge/streak SVG는 Figma dark bytes가 light와 동일해 기존 `assets/figma/home/`을 재사용합니다.

### 2026-09-20 명언 로딩 표시

- 사용자가 이 상태에 한해 Figma/MCP 없이 기본 원형 로딩 표시를 사용하도록 승인했다.
- `HomeViewModel.quoteLoadState == Loading`인 동안 명언 카드 배경은 유지하고 중앙에 Compose 기본 `CircularProgressIndicator`를 표시한다.
- QA 기록: [2026-09-20 Home quote loading QA](../design-qa/2026-09-20-android-home-quote-loading-qa.md).

### 2026-09-20 상단 날짜 간격 수정

- Figma Home `2929:13556`에서 월 카드 `2929:15667`은 x=20..93, 주간 날짜 `3204:2435`는 x=102부터 시작한다.
- 월 카드와 주간 날짜 사이의 수평 간격을 기존 5dp에서 Figma 기준 9dp로 수정한다. 주간 날짜는 남은 폭에 Figma 좌우 끝 좌표대로 분배한다.
- QA 기록: [2026-09-20 Home date spacing QA](../design-qa/2026-09-20-android-home-date-spacing-qa.md).


## 2026-10-25 신규 QA: 연도·월 선택

- QA: https://www.notion.so/3ffe639fc7a48005b57ff2fe3eeeb952
- Figma: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-16221 (선택기 연도 2929:16232, 월 2929:16236); 전체 Light 3139:1501 / Dark 3139:1753.
- 분석 보고서: [QA 원문·원인·해결 계획](../design-qa/2026-10-25-android-new-qa-report.html#calendar).
- 당시 분석 상태(아래 후속 구현 전): 원인 분석 완료, 앱 수정 미실시. 기존 문서의 달력 통과는 open/select-close 경로에 한정됐고 연도/월 직접 선택은 당시 에뮬레이터에서 실패했다. 현재는 아래 시스템 선택기·헤더 수정이 적용돼 있다.
- 원인: HomeCalendarSelector에 클릭/선택 목록 상태가 없고, 달력 내부 비클릭 영역의 탭이 배경 dismiss 레이어로 전달되어 닫힌다. 연도·월·내부 여백 탭 각각 재현, 이전 월 화살표는 정상.
- 계획: 선택기와 목록 상태를 기존 onMonthChanged에 연결하고 내부/외부 터치 경계를 분리한다. 회원 서버 기준일과 2025-06 하한을 유지한다.
- 추가 디자인 필요: 연도/월 목록이 펼쳐진 상태와 경계 연도에서의 월 처리·닫기 순서. 확인한 컴포넌트 세트 2929:15668에는 Default/Open만 있으며 열린 하위 목록은 확보하지 못했다.
- 검증 대상: Light/Dark, 연도/월 목록, 경계 월, 내부/외부 탭, 날짜 확정. 결정적 컴포넌트 Preview → 전체 프레임 대조 후 완료 판단.


### 제공 페이지 추가 조사 · 2026-10-25

- 사용자 제공 페이지: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2438-3437 (`v. 3.0`). 기존 QA의 달력 선택기는 같은 파일의 `리뉴얼 ver.1.0`(2929:3720)에 있다.
- 조사: v3 노드 14,194개 및 Home/필사 안내·프로토타입 확인, 리뉴얼 노드 11,722개에서 관련 이름 검색 후 달력 세트/선택기 자식·reactions 확인. 열린 연도/월 목록을 찾지 못했다. 전체 파일의 타 버전까지 없다고 단정하지 않는다.
- 닫힌 선택기 규격: 연도 2929:16232 = 65×32, 월 2929:16236 = 63×32, 버튼 간격6, radius8, border1 #E5E5E5, white. Chevron12×12 원본 확보. 텍스트 반환값 Inter Medium14/20 #0A0A0A(토큰 바인딩은 구현 시 재확인).
- 선택기는 FRAME이고 reactions=[], 숫자 wrapper와 chevron만 포함한다. `calendar` 세트 Default/Open은 달력 열림 여부이며 연도/월 목록 확장 variant가 아니다.
- [추출 보고서](../design-qa/2026-10-25-android-new-qa-report.html#figma-extraction), [조사 JSON](../design-qa/artifacts/2026-10-25-android-new-qa/figma-page-extraction.json), [화살표 SVG](../design-qa/artifacts/2026-10-25-android-new-qa/calendar-chevron-down.svg).
- 앱 구현 미실시. 열린 목록 디자인·경계 월 처리·닫기 규칙의 추가 결정 필요 상태 유지.

### 연도·월 시스템 선택기 적용 · 2026-10-25

- 최신 재검토: [2026-10-28 Android QA 3건 AS-IS / TO-BE](../design-qa/2026-10-28-android-three-issues-report.html#calendar). 현재 HEAD에서 연월 선택 계측 4건(API31) 재통과, 헤더 Light/Dark CLI Preview 확인. 전체 달력의 시각 승인 대기는 유지한다.
- Notion은 10/28 20:11 KST 갱신 후 Completed임을 재조회로 확인했다. 이번 작업에서 Notion 상태를 변경하지 않았으며, 기능 티켓 완료와 위 전체 시각 미승인 기록을 구분한다.

- 사용자 승인: 연도·월 선택은 우선 시스템 기본 다이얼로그 사용. 이 열린 상태는 Figma 예외이며 별도 디자인 요청 없이 진행한다.
- Figma 기준: 위 2929:16232 / 2929:16236 선택 버튼과 전체 3139:1501 / 3139:1753. 기준 이미지 `../design-qa/artifacts/2026-10-25-android-new-qa/figma-calendar.png`.
- 컴포넌트: `HomeCalendarSelector`(연도/월 진입), `HomeYearMonthDialog`(시스템 AlertDialog + NumberPicker 2개), `HomeInlineCalendar`(내부 터치 경계). Home에 조립한다.
- 상태: Light/Dark, 선택/확인/취소/뒤로가기, 시작 월 2025-06 및 회원 서버 기준일/비회원 오늘 상한, 내부 빈 공간/외부 탭.
- 확인 전에는 달력을 변경하지 않는다. 확인하면 표시 월만 바꾸고 달력은 유지한다. 경계 연도에서는 유효한 월만 제공하며 이전 월이 범위를 벗어나면 가까운 경계로 맞춘다. 날짜 선택 동작은 기존 경로를 사용한다.
- 검증: API 35 / 360×720dp / density 160 / ko / fontScale 1. 컴포넌트 결정적 Preview → 전체 화면 런타임. QA 기록: [시스템 연월 선택기](../design-qa/2026-10-25-home-year-month-qa.md).
- 후속 결과: 기능 구현 및 검증 완료. 단위 테스트160개/계측4개 통과, Preview Light·Dark 및 Release APK 에뮬레이터 전체 프레임 확인. 기존 달력의 Figma 외형 차이는 남아 있어 전체 UI 최종 승인은 미완료. [화면 포함 HTML 결과](../design-qa/2026-10-25-home-year-month-report.html).


## 2026-10-25 홈 달력 연도·월 버튼 디자인 수정

- Figma: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-16228
- 대상: Light 헤더 `2929:16228`, Dark 헤더 `3039:28108`; 조립 프레임 Light `3139:1501` / Dark `3139:1753` (360×821).
- 기준 이미지/명세: `docs/design-qa/artifacts/2026-10-25-home-calendar-header/figma-header-{light,dark}.{png,txt}` 및 `figma-full-{light,dark}.png`.
- 컴포넌트: `HomeCalendarHeader`(224×32, 양쪽 이전/다음 버튼), `HomeCalendarSelector`(연도 65×32, 월 63×32, 간격 6), 원본 chevron 16×16 / 12×12.
- 대상 상태: Light/Dark, 이전·다음 월 활성/비활성, 연도·월 버튼으로 시스템 선택기 진입·확인·취소. 고정 Preview 2026-08 / 선택일 2026-08-16 / 상한 2026-10-25, ko, fontScale 1.
- 원인: 아이콘을 Unicode 글리프로 대체해 기준선에 따라 정렬이 어긋나며, 버튼 치수·간격도 Figma와 다름.
- 검증 상태: 요청한 아이콘·중앙 정렬 수정 적용. 헤더/달력 Light·Dark CLI Preview 4개, API35 에뮬레이터 전체 프레임 및 시스템 선택기, 계측 테스트4개 확인. 기존 달력 본문·팝업 위치·플랫폼 영역 차이로 전체 화면 최종 Figma 승인은 미완료. 기존 전체 비교 HTML/증거는 보존했다.
- QA 기록: [홈 달력 헤더 디자인](../design-qa/2026-10-25-home-calendar-header-qa.md).

## 전체 화면 대조 · 2026-10-25

- 기준 페이지: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-3720
- 범위: 현재 production UI 재사용 Preview. 시각 수정은 사용자 검토 후 별도 작업.
- 조건: ko, fontScale 1.0, Light/Dark, 고정 fixture. Preview는 시스템 바·IME를 포함한 최종 승인이 아님.
- QA 기록: [전체 화면 대조 보고서](../design-qa/2026-10-25-full-screen-audit.html)
- 상태·크기·원본 이미지: [screen-map.json](../design-qa/artifacts/2026-10-25-full-screen-audit/screen-map.json)

| 대상 노드 | production 컴포넌트 | 상태 / 책임 | 검증 상태 |
|---|---|---|---|
| 3039:26518 | FigmaHomeContent | 2.home · Dark | 조사 중 |
| 3139:1753 | FigmaHomeContent | 2.home · Dark | 조사 중 |
| 3039:26778 | FigmaHomeContent | 2.home · Dark | 조사 중 |
| 3039:26996 | FigmaHomeContent | 2.home · Dark | 조사 중 |
| 3039:27295 | FigmaHomeContent | 2.home · Dark | 조사 중 |
| 3039:28098 | FigmaHomeContent | calendar · Dark | 조사 중 |
| 3136:1198 | FigmaHomeContent | Dark · 02 · 답변 기록 중 · Dark | 조사 중 |
| 3139:910 | FigmaHomeContent | Dark · 01 · 답변 기록 전 · Dark | 조사 중 |
| 3139:1061 | FigmaHomeContent | Dark · 03 · 답변 기록 후-toast · Dark | 조사 중 |
| 3139:1238 | FigmaHomeContent | Dark · 04 · 답변 기록 후 · Dark | 조사 중 |
| 2929:13556 | FigmaHomeContent | 2.home · Light | 조사 중 |
| 3139:1501 | FigmaHomeContent | 2.home · Light | 조사 중 |
| 2929:18871 | FigmaHomeContent | 2.home · Light | 조사 중 |
| 2929:15668 | FigmaHomeContent | calendar · Light | 조사 중 |
| 2929:19645 | FigmaHomeContent | toast_copy · Light | 조사 중 |
| 2929:19784 | FigmaHomeContent | like_press · Light | 조사 중 |
| 3110:33783 | FigmaHomeContent | 01 · 답변 기록 전 · Light | 조사 중 |
| 3110:34293 | FigmaHomeContent | 03 · 답변 기록 후-toast · Light | 조사 중 |
| 3110:34438 | FigmaHomeContent | 04 · 답변 기록 후 · Light | 조사 중 |
| 3110:34152 | FigmaHomeContent | 02 · 답변 기록 중 · Light | 조사 중 |
| 3039:26980 | FigmaHomeContent | Description · Dark | 조사 중 |
| 3039:28626 | FigmaHomeContent | weekday · Dark | 조사 중 |
| 3139:1453 | FigmaHomeContent | question-dark · Dark | 조사 중 |
| 2929:18625 | FigmaHomeContent | Description · Light | 조사 중 |
| 2929:17172 | FigmaHomeContent | weekday · Light | 조사 중 |
| 3087:29377 | FigmaHomeContent | question · Light | 조사 중 |
