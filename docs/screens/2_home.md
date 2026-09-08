# Home

## Android 구성

- Compose 위치: `presentation/ui/home/`
- 상태 관리: `HomeViewModel`, `TypingViewModel`, `ShareViewModel`
- 범위: 오늘의 명언, 필사, 이미지·공유, 홈 내 달력 요약

## Figma UI 기준

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556
- 대상 프레임/노드: light `2929:13556` (`2.home`), dark `3039:26518` (`2.home`). Parent section `2929:9603` is not a render target.
- 대상 기기/프레임 크기: Figma export 360 × 821 px; light root `#FFEFCC`, dark root `#212121`. (The supplied 360 × 720 description conflicts with the authoritative node export; QA uses the export.)
- 검증 상태: **Blocked** — `wm size 945x2155` (420 dpi)로 logical 360 × 821dp exact-size component capture를 확보했다. clean light/dark positive default, genuine-zero warning/tooltip, inline calendar open/select-close, question done/edit와 completion snackbar는 확인했지만, full soft keyboard, authenticated image, 실제 completed-writing marker, 그리고 app-wide 4-tab/live-ad와 Figma 3-tab/static-ad의 차이 때문에 assembled-screen acceptance는 Blocked다. 컴포넌트별 검증은 부분 통과일 수 있으나 화면 최종 결과는 Partial/Pass가 아니다.
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
8. Figma 3-tab과 앱 4-route 하단 bar 차이는 앱 전역 제품 결정이므로 이번 Home 범위에서 변경하지 않는다.

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| Header | `2929:15476` | Figma logo, current streak, profile navigation | `FigmaHomeContent` | 부분 통과(exact-size positive default, genuine 0 warning/tooltip); unknown/null은 tooltip 미노출 |
| Month calendar | `2929:15667`, `2929:16221`, `3139:1501` | `YYYY.MM` selector와 inline month popup, 월/연도 탐색 및 날짜 선택 | `HomeDateWeekSection` / `HomeInlineCalendar` | 통과(exact-size runtime open/select-close) |
| Week strip | `3204:2435` | 선택일을 오른쪽 끝으로 둔 7일과 실제 완료 필사 marker | `HomeDateWeekSection` | 부분 통과(runtime selected); completion data Blocked |
| Quote card | `2929:13642` | selected quote, author/search opens existing Wikipedia URI, right/left date-bounded swipe actions | `HomeQuoteCard` | 부분 통과(빌드) |
| Quote action row | `2929:15503` | copy/share/like/image actions with current like visual state | `HomeQuoteActionRow` | 부분 통과(빌드) |
| Prompt response | `3087:29376`, `3139:1399`, `3087:29378`, `3110:34293` | 200-grapheme 입력, 회원 answer POST 성공 후 snackbar·done/edit 상태, 비회원 session 기록; 명언 필사 route와 분리 | `HomePromptAnswerSection` / `HomeViewModel` | 부분 통과(exact done/snackbar 통과; focus full keyboard 미확보); 회원 API binding은 별도 연동 범위 |
| Global bottom navigation/ad | `3087:29254`; ad reference `2929:29249` is unavailable | Figma has 3 × 120dp tabs; Android keeps its shared 4-route bar and live ad surface | scaffold / `BottomNavigationBar` | Blocked — product decision required |
| Dark palette/assets | `3039:26518` | dark root/card/input/text/divider plus night SVG variants | `FigmaHomeContent` / `assets/figma/home-night/` | 부분 통과(exact-size positive default clean capture) |

회원 Home 질문 답변은 선택 날짜의 `questionKo`/`questionEn`, `answer`, `answeredAt`을 weekly/daily 응답에서 읽고 answer API로 저장한다. 선택 날짜가 바뀌면 날짜별 draft/recorded-answer를 원자적으로 교체하며, `dailyQuoteSeq == null`인 날짜에서는 답변을 포함한 mutation을 실행하지 않는다. 비회원만 기존 `HomeViewModel` session state로 기록/수정 상태와 snackbar를 유지하며 Typing으로 전달하지 않는다. 기존 quote 필사와 memo v1 API 필드는 신규 회원 Home·Calendar에서 재사용하지 않는다.

`3087:29254`는 Home/Calendar/My page 3개 탭(각 120 × 60dp, 32dp icon), 선택 `#5C65FF`, 비선택 `#212121`만 정의합니다. Android `BottomNavigationBar`는 동일한 light selected/unselected color와 32dp local vector를 사용하지만 Home/QuoteList/Calendar/My page의 4개 shared route를 보존합니다. 3등분 폭·3개 icon asset을 그대로 적용하려면 QuoteList의 위치·폭 또는 route를 변경해야 하므로 Home 범위에서 변경하지 않습니다. 광고는 live `SingleLineAdSection`의 native content이며, 이전 참조 `2929:29249`는 2026-08-30 Figma MCP에서 찾을 수 없었습니다. 향후 제품 결정은 (a) Android의 4탭/live ad contract를 Figma에 반영하거나, (b) shared navigation과 광고 노출 정책을 앱 전체 범위에서 3탭 디자인으로 변경하도록 명시 승인하는 것입니다.

Dark Home은 `3039:26518`의 `#212121` root, `#424242` quote/input card, `#616161` border/divider(주 divider 55%), white primary text, `#E0E0E0` action label, `#9E9E9E` muted text를 `HomeColorPalette`로 해석합니다. calendar와 selected/completed weekday, `#5C65FF` CTA는 Figma 상태대로 유지합니다. 정확한 dark SVG는 `presentation/src/main/assets/figma/home-night/`의 logo/profile/wave/search/copy/share/like/camera 8종이며 SVG가 density-independent이므로 Figma의 60/24/16/320dp Compose 크기로 렌더합니다. calendar/write/badge/streak SVG는 Figma dark bytes가 light와 동일해 기존 `assets/figma/home/`을 재사용합니다.
