# Home Date Spacing Figma UI QA

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556
- Target nodes: Home `2929:13556`, month `2929:15667`, week strip `3204:2435`
- Figma geometry: month x=20..93, week strip x=102; component gap 9dp
- Full-frame reference: `assets/home-figma-2929-13556/reference-full.png`
- Preview image: `artifacts/2026-09-20-home-date-spacing/light.png`
- Preview conditions: 360 × 50dp, light, ko, fontScale 1.0, 2026-08-10..16 fixture
- Runtime acceptance: 전체 Home 조립 화면의 기존 Blocked 상태 유지

## Component inventory

| Component | Figma node | Target state | Result |
|---|---|---|---|
| `HomeMonthCalendar` | `2929:15667` | 2026.08 | Preview 통과 |
| `HomeWeekStrip` | `3204:2435` | 10·11 완료, 16 선택 | Preview 통과 |

## Validation

- Root cause: `HomeDateWeekSection`의 컴포넌트 간격이 5dp였고, 모든 항목을 한 Row의 고정 간격으로 측정해 너비 제약 시 주간 날짜가 압축됐다.
- Fix: 월 카드 뒤에 고정 9dp `Spacer`를 두고, 남은 폭의 주간 날짜는 `SpaceBetween`으로 배치한다.
- Preview result: 월 카드 x≈20..93dp, 첫 날짜 원 x≈102..132dp, 마지막 선택 날짜 원 x≈310..340dp로 Figma 좌표와 일치한다.
- Final assembled-screen result: 전체 런타임 캡처 전까지 비최종 검증.
