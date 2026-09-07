# Home

## Android 구성

- Compose 위치: `presentation/ui/home/`
- 상태 관리: `HomeViewModel`, `TypingViewModel`, `ShareViewModel`
- 범위: 오늘의 명언, 필사, 이미지·공유, 홈 내 달력 요약

## Figma UI 기준

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556
- 대상 프레임/노드: light `2929:13556` (`2.home`), dark `3039:26518` (`2.home`). Parent section `2929:9603` is not a render target.
- 대상 기기/프레임 크기: Figma export 360 × 821 px; light root `#FFEFCC`, dark root `#212121`. (The supplied 360 × 720 description conflicts with the authoritative node export; QA uses the export.)
- 검증 상태: Blocked — 2026-09-07 Android Emulator(1080 × 2400, 420 dpi)에서 기본·inline calendar·질문 focus/done·이미지 로그인·dark runtime capture를 확보했다. 그러나 matching 360 × 821 full-frame, genuine zero-streak tooltip, authenticated image flow는 확보하지 못했고 debug AdMob validator가 snackbar/dark 하단 비교를 가린다.
- 기준 이미지: `docs/design-qa/assets/home-figma-2929-13556/reference-full.png`
- QA 기록: `docs/design-qa/2026-09-07-android-home-interactions-qa.md`

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
3. 질문 CTA는 명언 필사 route로 이동하지 않는다. 200 grapheme 이내의 답변을 Home ViewModel 세션 상태에 기록하고 `답변을 기록했어요.` snackbar와 done/edit 상태를 표시한다. 서버·DB 영구 저장은 별도 data contract가 없어 이번 UI 범위에서 제외한다.
4. completed weekday는 `GetAllStreakInfoUseCase`의 실제 완료 필사 날짜를 사용한다.
5. 이미지 등록/보기/변경/삭제, 복사 snackbar, 좋아요 선택은 기존 domain/API 연결을 보존한다.
6. Figma 3-tab과 앱 4-route 하단 bar 차이는 앱 전역 제품 결정이므로 이번 Home 범위에서 변경하지 않는다.

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| Header | `2929:15476` | Figma logo, current streak, profile navigation | `FigmaHomeContent` | 부분 통과(runtime default); genuine 0 tooltip Blocked |
| Month calendar | `2929:15667`, `2929:16221`, `3139:1501` | `YYYY.MM` selector와 inline month popup, 월/연도 탐색 및 날짜 선택 | `HomeDateWeekSection` / `HomeInlineCalendar` | 부분 통과(runtime open/select-close); target frame Blocked |
| Week strip | `3204:2435` | 선택일을 오른쪽 끝으로 둔 7일과 실제 완료 필사 marker | `HomeDateWeekSection` | 부분 통과(runtime selected); completion data Blocked |
| Quote card | `2929:13642` | selected quote, author/search opens existing Wikipedia URI, right/left date-bounded swipe actions | `HomeQuoteCard` | 부분 통과(빌드) |
| Quote action row | `2929:15503` | copy/share/like/image actions with current like visual state | `HomeQuoteActionRow` | 부분 통과(빌드) |
| Prompt response | `3087:29376`, `3139:1399`, `3087:29378`, `3110:34293` | 200-grapheme 입력, session 기록, snackbar, done/edit 상태; 명언 필사 route와 분리 | `HomePromptAnswerSection` / `HomeViewModel` | 부분 통과(runtime focus/done); clean snackbar frame Blocked; 영구 저장은 별도 범위 |
| Global bottom navigation/ad | `3087:29254`; ad reference `2929:29249` is unavailable | Figma has 3 × 120dp tabs; Android keeps its shared 4-route bar and live ad surface | scaffold / `BottomNavigationBar` | Blocked — product decision required |
| Dark palette/assets | `3039:26518` | dark root/card/input/text/divider plus night SVG variants | `FigmaHomeContent` / `assets/figma/home-night/` | 부분 통과(runtime capture); unobstructed full frame Blocked |

현재 Home에는 질문 답변을 나타내는 domain/data 계약이 없습니다. 이번 UI 범위는 `HomeViewModel`의 session state로 기록/수정 상태와 snackbar를 구현하며 Typing으로 전달하지 않습니다. 영구 저장은 별도 작업에서 (1) 날짜·질문 기반 `PromptAnswerRecord` 모델, (2) `LocalRepository`의 저장/조회 계약과 device-local 또는 실제 backend 구현을 함께 정의해야 합니다. 기존 quote 필사와 memo/API 필드는 재사용하지 않습니다.

`3087:29254`는 Home/Calendar/My page 3개 탭(각 120 × 60dp, 32dp icon), 선택 `#5C65FF`, 비선택 `#212121`만 정의합니다. Android `BottomNavigationBar`는 동일한 light selected/unselected color와 32dp local vector를 사용하지만 Home/QuoteList/Calendar/My page의 4개 shared route를 보존합니다. 3등분 폭·3개 icon asset을 그대로 적용하려면 QuoteList의 위치·폭 또는 route를 변경해야 하므로 Home 범위에서 변경하지 않습니다. 광고는 live `SingleLineAdSection`의 native content이며, 이전 참조 `2929:29249`는 2026-08-30 Figma MCP에서 찾을 수 없었습니다. 향후 제품 결정은 (a) Android의 4탭/live ad contract를 Figma에 반영하거나, (b) shared navigation과 광고 노출 정책을 앱 전체 범위에서 3탭 디자인으로 변경하도록 명시 승인하는 것입니다.

Dark Home은 `3039:26518`의 `#212121` root, `#424242` quote/input card, `#616161` border/divider(주 divider 55%), white primary text, `#E0E0E0` action label, `#9E9E9E` muted text를 `HomeColorPalette`로 해석합니다. calendar와 selected/completed weekday, `#5C65FF` CTA는 Figma 상태대로 유지합니다. 정확한 dark SVG는 `presentation/src/main/assets/figma/home-night/`의 logo/profile/wave/search/copy/share/like/camera 8종이며 SVG가 density-independent이므로 Figma의 60/24/16/320dp Compose 크기로 렌더합니다. calendar/write/badge/streak SVG는 Figma dark bytes가 light와 동일해 기존 `assets/figma/home/`을 재사용합니다.
