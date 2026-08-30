# Calendar

## Android 구성

- Compose 위치: `presentation/ui/calendar/`
- 상태 관리: `CalendarViewModel`
- 범위: 월간 필사 캘린더, 날짜 선택, 선택한 날짜의 명언 요약

## Figma UI 기준

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2985-21952
- 재구현 기준: light section `2929:13366`; basic `2985:21952` (360 × 816), completed `2985:22510` (360 × 1101), expanded/scroll `2987:22796` (360 × 1101). 각 실제 프레임을 직접 조회했다.
- 대상 기기/프레임: root `#FFEFCC`; Figma export에는 status/safe/bottom surfaces가 포함된다.
- 검증 상태: focused/full unit test와 debug build 통과. live empty state의 controlled 360 × 816 capture는 존재하며, real completed record/overlay 비교는 Blocked다.
- 기준 이미지: `/private/tmp/fillsa-calendar-figma-basic-2985-21952.png`, `/private/tmp/fillsa-calendar-figma-selected-2985-22510.png`, `/private/tmp/fillsa-calendar-figma-expanded-2987-22796.png`
- QA 기록: `docs/design-qa/2026-08-30-calendar-figma-qa.md`

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| Calendar header | frame top_mobile descendants | Figma logo, genuine streak when supplied, profile-only action | `CalendarHeader` | build 통과; empty-state capture 확인 |
| Calendar shell | `2985:21954` | 320 × 396 shell, fixed 36dp seven columns/11dp gaps, 40dp weekday and 50dp date rows | `CalendarSection` | build 통과; empty-state capture 확인 |
| Daily indicators | `2985:22041`, `2985:22044` | existing completion/like data rendered as Figma fire/heart icons | `Day` / `calendarRecordIndicators` | 부분 통과(RED→GREEN mapping test) |
| Legend | `2985:22283` and related | Figma heart/Fire aggregate using existing like/streak data | `CalendarCountSection` | 부분 통과(runtime capture) |
| Selected-day companion | `2985:21952`, `2985:22510`, `2987:22796` | empty character + 80dp quote card, or 133dp completed detail/action row and question UI | `CalendarQuoteSection` | unit/build 통과; runtime completed data capture 대기 |
| Shared navigation/ad | frame descendants | existing 4-route nav and live ad surface | scaffold | Blocked — Figma has three tabs; shared routes/ad policy preserved |
