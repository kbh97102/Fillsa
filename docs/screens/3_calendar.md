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
- 런타임 기준: Android Emulator, 360dp 폭, API/density는 캡처 시 기록.
- 검증 상태: 구현 및 Emulator 전체 조립 화면 검증 전 `미검증`.
- QA 기록: `docs/design-qa/2026-09-08-android-calendar-screen-root-qa.md`.
- 구현 플랜: `docs/superpowers/plans/2026-09-08-android-calendar-screen-root.md`.

### 상태별 Figma 노드 맵

| 상태/영역 | Figma 노드 | 핵심 기준 |
|---|---|---|
| 기본 전체 프레임 | `2985:21952` | 360×816, 미필사 안내와 80dp 명언 카드 |
| 기본 달력 카드 | `2985:21954` | x20/y90, 320×396, 12dp radius |
| 기본 월 통계 | `2987:23057` | x272/y496, 68×21, heart/fire 16dp |
| 미필사 안내 / 명언 카드 | `2985:22183`, `2985:22145` | 320×100 / 320×80 |
| 답변 미기록 전체 프레임 | `2987:22796` | 360×1101 |
| 답변 미기록 달력 / 통계 | `2987:22799`, `2987:23117` | 320×396 / 68×21 |
| 답변 미기록 명언·액션 | `2987:22950` | 320×133, 이미지 등록 |
| 답변 미기록 질문 | `2987:22977` | 320×298, placeholder와 기록 CTA |
| 답변·이미지 있음 전체 프레임 | `2985:22510` | 360×1101 |
| 답변·이미지 있음 달력 / 통계 | `3145:2024`, `3145:2158` | 320×396 / 68×21 |
| 답변·이미지 있음 명언·액션 | `3207:2950`, actions `3207:2957`, `3207:2962`, `3207:2967`, `3207:2973` | 320×133, 사진 썸네일과 이미지 보기 |
| 답변·이미지 있음 질문 | `2985:22670` | 320×298, 기존 답변과 수정 CTA |
| 헤더 | `2985:22104`, `2987:22995`, `2985:22688` | 360×50 |
| 하단 내비게이션 | `2987:23075`, `3087:29201`, `3087:29220` | 360×60, Calendar 선택 |
| 광고 영역 | `2985:22098`, `3087:29196`, `3087:29215` | 360×35 |

### 2026-09-08 컴포넌트 분해

| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| `CalendarSection` | `2985:21954`, `2987:22799`, `3145:2024` | 월 이동, 7열×6행 날짜, 선택/비활성/기록 아이콘 | `CalendarView` | 미검증 |
| `Day` / indicators | calendar descendants, 예: `3145:2102` | 36×50 셀, heart/fire 12dp | `CalendarSection` | 미검증 |
| `CalendarCountSection` | `2987:23057`, `2987:23117`, `3145:2158` | 월간 heart/fire 집계 | `CalendarView` | 미검증 |
| `CalendarEmptyDay` / preview | `2985:22183`, `2985:22145` | 미필사 안내와 80dp 명언 진입 카드 | `CalendarQuoteSection` | 미검증 |
| completed card/action row | `2987:22950`, `3207:2950` | 완료 명언과 미등록/등록 이미지 액션 | `CalendarQuoteSection` | 미검증 |
| prompt answer | `2987:22977`, `2985:22670` | 200자 입력, 기록/수정 시각 상태 | `CalendarQuoteSection` | 미검증 |
| 전체 조립 | three actual frames | system/shared surfaces 포함 | app scaffold | 미검증 |

### 제품 계약 경계

- 월간 응답 `MemberQuotesData`에는 답변과 `imagePath`가 없다. production은 실제 응답이 제공하는 상태만 렌더링한다.
- 답변·이미지 있음 상태는 Emulator QA fixture로 비영속·비네트워크 재현하여 컴포넌트를 검증한다. API/DB 계약을 임의로 확장하지 않는다.
- Figma의 3-tab/static-ad와 앱의 공유 4-route/live-ad는 Calendar 소유가 아니므로 유지하고 QA에 차이를 기록한다.
- 이번 작업은 TDD를 사용하지 않는다. 구현 후 기존 회귀 테스트와 추가 상태/런타임 테스트를 실행한다.

## Android 구성

- Compose 위치: `presentation/ui/calendar/`
- 상태 관리: `CalendarViewModel`
- 범위: 월간 필사 캘린더, 날짜 선택, 선택한 날짜의 명언 요약

## Figma UI 기준

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
