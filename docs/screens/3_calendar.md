# Calendar

## Figma UI 기준 (2026-09-08 재검토)

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X/%E2%9C%92%EF%B8%8F%ED%95%84%EC%82%AC?node-id=2929-13366
- 파일 키 / 루트 섹션: `VdFocqyqTgevMVCQxwAQ2X` / `2929:13366` (`3. calendar`). 섹션은 방향만 제공하며 아래 실제 프레임을 Figma MCP로 직접 읽는다.
- 대상 프레임:
  - `2985:21952` — 기본/미필사 선택, 360×816.
  - `2987:22796` — 필사 완료·답변 미기록·이미지 미등록, 360×1101.
  - `2985:22510` — 필사 완료·답변 기록·이미지 있음, 360×1101. 2026-09-08 export에는 `3207:2973` 이미지 보기 액션에 사진 썸네일이 포함되며 기존 2026-08-30 export와 바이트가 다르다.
- 색상 모드 / 로케일: light / `ko-KR`. 루트 배경, status bar, top header, bottom navigation, ad 영역을 전체 프레임 비교에 포함한다.
- 기준 이미지: `docs/design-qa/assets/calendar-figma-2929-13366/2026-09-08/figma-basic-2985-21952.png`, `figma-unanswered-2987-22796.png`, `figma-answered-image-2985-22510.png`.
- 런타임 기준: Android Emulator `Medium_Phone_API_35`, API 35, 160dpi, 360dp 폭, light, app locale `ko-KR`; 기본은 360×816, 두 완료 상태는 360×1101 전체 프레임.
- 검증 상태: Calendar 소유 컴포넌트와 조립 위치는 Round 6 shared-geometry 재검증 완료(부모 작업자의 최종 조립 승인 대기); 전체 시스템/공유 표면은 Android status/gesture bar와 보존된 4-route navigation, 비네트워크 fixture에서 생략한 live ad 때문에 제품 경계 차이 있음.
- QA 기록: `docs/design-qa/2026-09-08-android-calendar-screen-root-qa.md`.
- API 상태 연결 QA: `docs/design-qa/2026-09-08-android-calendar-api-binding-qa.md` (Task 5; 자동 상태 검증과 최종 조립 검증을 구분).
- 구현 플랜: `docs/superpowers/plans/2026-09-08-android-calendar-screen-root.md`.

### 상태별 Figma 노드 맵

| 상태/영역 | Figma 노드 | 핵심 기준 |
|---|---|---|
| 기본 전체 프레임 | `2985:21952` | 360×816, 미필사 안내와 80dp 명언 카드 |
| 기본 달력 카드 | `2985:21954` | x20/y90, 320×396, 12dp radius |
| 기본 선택 행 | `2985:22026`; cells `2985:22028`, `2985:22030`, `2985:22032`, `2985:22039`, `2985:22046`, `2985:22055`, `2985:22057` | Figma 원본의 중복 17/누락 21과 18 fire, 19/20 heart+fire를 fixture visual cell map으로만 재현 |
| 기본 월 통계 | `2987:23057` | x272/y496, 68×21, heart/fire 16dp |
| 미필사 안내 / 명언 카드 | `2985:22183`, `2985:22145` | 320×100 / 320×80 |
| 답변 미기록 전체 프레임 | `2987:22796` | 360×1101 |
| 답변 미기록 달력 / 통계 | `2987:22799`, `2987:23117` | 320×396 / 68×21 |
| 답변 미기록 선택 행 | `2987:22871`; labels `2987:22874`, `2987:22876`, `2987:22883`, `2987:22890`, `2987:22899` | 21 selected, 18–21 heart+fire |
| 답변 미기록 명언·액션 | `2987:22950` | 320×133, 이미지 등록 |
| 답변 미기록 질문 | `2987:22977` | 320×298, placeholder와 기록 CTA |
| 답변 미기록 입력 / count / CTA | `2987:22981`, `2987:22983`, `2987:23049` | 320×174, `0 / 200`, primary 기록 CTA |
| 답변·이미지 있음 전체 프레임 | `2985:22510` | 360×1101 |
| 답변·이미지 있음 달력 / 통계 | `3145:2024`, `3145:2158` | 320×396 / 68×21 |
| 답변·이미지 있음 명언·액션 | `3207:2950`, actions `3207:2957`, `3207:2962`, `3207:2967`, `3207:2973` | 320×133, 사진 썸네일과 이미지 보기 |
| 등록 이미지 썸네일/label | `3207:2974`, `3207:2977` | 28×28 radius 5, 4dp gap, bold 12 purple `이미지 보기` |
| 답변·이미지 있음 질문 | `2985:22670` | 320×298, 기존 답변과 수정 CTA |
| 답변 있음 입력 / count / CTA | `2985:22674`, `2985:22676`, `2985:22681` | secondary `#D3D5FF` 수정 CTA; 원본은 비어 있지 않은 답변에도 `0 / 200` 표시 |
| 헤더 | `2985:22104`, `2987:22995`, `2985:22688` | 360×50 |
| 하단 내비게이션 | `2987:23075`, `3087:29201`, `3087:29220` | 360×60, Calendar 선택 |
| 광고 영역 | `2985:22098`, `3087:29196`, `3087:29215` | 360×35 |

### 2026-09-08 컴포넌트 분해

| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| `CalendarSection` | `2985:21954`, `2987:22799`, `3145:2024` | 월 이동, 7열×6행 날짜, 선택/비활성/기록 아이콘 | `CalendarView` | Round 6 shared-geometry 재검증 완료 |
| `Day` / indicators | `2985:22026`, `2987:22871`, `3145:2096` descendants | selected/record week 50dp, ordinary week 40dp-centered; heart/fire 12dp | `CalendarSection` | Round 6 shared-geometry 재검증 완료 |
| `CalendarCountSection` | `2987:23057`, `2987:23117`, `3145:2158` | 월간 heart/fire 집계 (`likeCount`/`typingCount`) | `CalendarView` | 최종 통과 |
| `CalendarEmptyDay` / preview | `2985:22183`, `2985:22145` | 미필사 안내와 80dp 명언 진입 카드 | `CalendarQuoteSection` | Round 5 재검증 완료 |
| completed card/action row | `2987:22950`, `3207:2950` | 완료 명언과 70/70/70/107 action row, 미등록/등록 이미지 | `CalendarQuoteSection` | Round 4 재검증 완료 |
| prompt answer | `2987:22977`, `2985:22670` | 200자 입력, 기록/수정 시각 상태 | `CalendarQuoteSection` | Round 4 재검증 완료 |
| 전체 Calendar 조립 | three actual frames | header y30–80, card y90–486, selected-day sections | app scaffold | Round 5 조립 재검증 완료; parent 최종 승인 대기, shared/system surface 차이 기록 |

### 제품 계약 경계

- 회원 Calendar는 진입과 월 변경마다 `GET /api/v2/member-quotes/monthly?yearMonth=`를 한 번 호출한다. 날짜 탭은 캐시된 `memberQuotes`에서 상세를 선택하고 추가 GET을 호출하지 않는다.
- 월간 응답 `MemberQuotesData`의 `questionKo`, `questionEn`, `answer`, `answeredAt`, `imagePath`, `engQuote`, `engAuthor`, `authorUrl`은 선택된 날짜의 완료 상세에 직접 매핑한다. 답변·이미지 있음 상태는 더 이상 fixture 전용 계약이 아니며 production은 실제 월간 응답 상태를 렌더링한다.
- 회원 답변 저장은 Home과 같은 answer UseCase를 사용한다. POST 성공 시 선택 월간 레코드의 `answer`·`answeredAt`만 메모리에서 갱신하고, 같은 날짜 daily GET을 best-effort로 호출해 서버 상태를 재조정한다. 보조 refresh 실패는 POST 성공 상태를 되돌리지 않으며 완료 상태, streak, 월간 summary를 변경하지 않는다.
- 정상적인 access token 갱신은 Calendar 회원 세대를 바꾸거나 진행 중 answer POST를 폐기하지 않는다. 명시적인 로그인 상태 전환만 기존 월 projection과 요청 세대를 무효화한다.
- 비회원은 monthly/daily/answer 회원 API를 호출하지 않고 기존 v1 월간 조회, 로컬 좋아요·필사, 질문 답변 session-only 동작을 유지한다.
- 비회원 질문은 기존 디자인 문구를 유지한다. 회원 질문이 API에서 누락되어도 비회원 문구로 대체하지 않는다. 답변 입력·기록·수정 상태는 `CalendarViewModel`이 소유하며 날짜 선택은 추가 조회 없이 월 캐시에서 투영한다.
- Calendar의 복사/공유는 기존 로컬 동작을 유지한다. 좋아요/이미지 버튼의 기존 선택 날짜 Home 이동 계약도 이번 리뉴얼 API 연결에서 바꾸지 않는다.
- Figma의 합성 달력에는 `2025. 03`과 실제 달력 산술이 맞지 않고 기본 프레임에 17 중복/21 누락이 있다. Production은 실제 날짜 산술을 유지하고, 정확한 Figma 대조용 process-local fixture만 명시적 visual cell map을 사용한다.
- Figma는 합성 날짜의 요일도 잘못 표시한다(3월 22일 `(금)`, 3월 21일 `(목)`). Production과 fixture는 실제 `LocalDate` 요일인 `(토)`/`(금)`을 유지하며 거짓 요일 data를 주입하지 않는다.
- 답변 있음 원본의 `0 / 200`은 디자인 소스 불일치다. Production 입력은 실제 grapheme count를 유지하고 해당 명시적 fixture만 `0 / 200`을 표시한다.
- Calendar QA와 production은 같은 `CalendarView` geometry(+6dp root offset)와 Calendar-route status-bar inset을 사용한다. Fixture 전용 geometry 분기는 없다.
- Calendar 주차 셀 geometry도 production과 fixture가 동일하게 실행한다. 선택 날짜 또는 fire/heart 기록이 하나라도 있는 주는 50dp inner cell, 그 외 주는 50dp row 중앙의 40dp inner cell을 사용하며 fixture는 synthetic date/content만 주입한다.
- Figma의 3-tab/static-ad와 앱의 공유 4-route/live-ad는 Calendar 소유가 아니므로 유지하고 QA에 차이를 기록한다.
- Calendar QA fixture는 process-local/nonnetwork 상태를 유지하기 위해 Back, Calendar header exits, and shared bottom-tab navigation을 no-op으로 처리한다. Production navigation은 변경하지 않는다.
- 이번 작업은 TDD를 사용하지 않는다. 구현 후 기존 회귀 테스트와 추가 상태/런타임 테스트를 실행한다.

## Android 구성

- Compose 위치: `presentation/ui/calendar/`
- 상태 관리: `CalendarViewModel`
- 범위: 월간 필사 캘린더, 날짜 선택, 선택한 날짜의 명언 요약

## 이전 Figma UI 기준 (2026-08-30, 위 2026-09-08 기준으로 대체됨)

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2985-21952
- 재구현 기준: light section `2929:13366`; basic `2985:21952` (360 × 816), completed `2985:22510` (360 × 1101), expanded/scroll `2987:22796` (360 × 1101). 각 실제 프레임을 직접 조회했다.
- 대상 기기/프레임: root `#FFEFCC`; Figma export에는 status/safe/bottom surfaces가 포함된다.
- 검증 상태: focused/full unit test와 debug build 통과. live empty state의 controlled 360 × 816 capture는 존재하며, real completed record/overlay 비교는 Blocked다.
- 기준 이미지: `docs/design-qa/assets/calendar-figma-2985-21952/rework-8464dbf/reference-basic-empty-2985-21952.png`, `docs/design-qa/assets/calendar-figma-2985-21952/rework-8464dbf/reference-selected-completed-2985-22510.png`, `docs/design-qa/assets/calendar-figma-2985-21952/rework-8464dbf/reference-expanded-completed-2987-22796.png`
- QA 기록: `docs/design-qa/2026-08-30-calendar-figma-qa.md`

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| Calendar header | frame top_mobile descendants | Figma logo Home action, genuine streak when supplied, profile action | `CalendarHeader` | build 통과; empty-state capture 확인 |
| Calendar shell | `2985:21954` | 320 × 396 shell, Sunday-first 36dp seven columns/11dp gaps, 40dp weekday and 50dp date rows | `CalendarSection` | RED→GREEN ordering test, empty-state capture 확인 |
| Daily indicators | `2985:22041`, `2985:22044` | existing completion/like data rendered as Figma fire/heart icons | `Day` / `calendarRecordIndicators` | 부분 통과(RED→GREEN mapping test) |
| Legend | `2985:22283` and related | Figma heart/Fire aggregate using existing like/streak data | `CalendarCountSection` | 부분 통과(runtime capture) |
| Selected-day companion | `2985:21952`, `2985:22510`, `2987:22796` | empty character + 80dp quote card, or 133dp completed detail/action row and question UI | `CalendarQuoteSection` | unit/build 통과; runtime completed data capture 대기 |
| Shared navigation/ad | frame descendants | existing 4-route nav and live ad surface | scaffold | Blocked — Figma has three tabs; shared routes/ad policy preserved |
