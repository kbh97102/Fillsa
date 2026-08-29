# Calendar

## Android 구성

- Compose 위치: `presentation/ui/calendar/`
- 상태 관리: `CalendarViewModel`
- 범위: 월간 필사 캘린더, 날짜 선택, 선택한 날짜의 명언 요약

## Figma UI 기준

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2985-21952
- 대상 프레임/노드: light `2985:21952` (`3. calendar`), dark `3039:28370` (`3. calendar`). Orientation-only sections `2929:13366` and `2929:6874` are not render targets.
- 대상 기기/프레임 크기: 360 × 821 px full device frame; light root `#FFEFCC`, dark root `#212121`.
- 검증 상태: 진행 중 — runtime light/dark captures exist, but matching 360 × 821 controlled comparison and dark photo-record modal contract are blocked.
- 기준 이미지: `docs/design-qa/assets/calendar-figma-2985-21952/reference-light-full.png`, `docs/design-qa/assets/calendar-figma-2985-21952/reference-dark-full.png`
- QA 기록: `docs/design-qa/2026-08-30-calendar-figma-qa.md`

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| Calendar shell | `2985:21954`; dark equivalent | month navigation, 7-column grid, selected/out-of-month treatment | `CalendarSection` | 부분 통과(빌드·runtime capture) |
| Daily indicators | `2985:22041`, `2985:22044` | existing completion/like data rendered as Figma fire/heart icons | `CalendarDayIndicators` | 부분 통과(RED→GREEN mapping test) |
| Legend | `2985:22283` and related | Figma heart/Fire aggregate using existing like/streak data | `CalendarCountSection` | 부분 통과(runtime capture) |
| Selected-day companion | light `2985:21952`; dark `3039:28370` | selected date summary or established empty state | `CalendarQuoteSection` | 부분 통과(runtime capture); dark photo modal Blocked |
| Shared navigation/ad | frame descendants | existing 4-route nav and live ad surface | scaffold | Blocked — Figma has three tabs; shared routes/ad policy preserved |
