# Home Quote Loading UI QA

## Reference

- Figma URL: 사용자 승인 예외 — 이 로딩 상태는 Figma/MCP 없이 진행
- Target: Home 명언 카드 로딩 상태
- Preview images: `artifacts/2026-09-20-home-quote-loading/light.png`, `artifacts/2026-09-20-home-quote-loading/dark.png`
- Preview conditions: 320 × 150dp, ko, fontScale 1.0, light/dark
- Runtime acceptance: 사용자 직접 확인 예정

## Component inventory

| Component | Target state | Result |
|---|---|---|
| `HomeQuoteCard` | `HomeQuoteLoadState.Loading` | light/dark Preview 통과 |

## Validation

- 로딩 상태의 단일 원천은 기존 `HomeViewModel.quoteLoadState`를 사용한다.
- 로딩 중 명언·작가 문구 대신 카드 배경 중앙에 Compose 기본 `CircularProgressIndicator`를 표시하며, 색상은 `내 답변 기록하기` CTA와 같은 `HomePrimary`(`#5C65FF`)를 사용한다.
- Android CLI semantics에서 두 Preview 모두 840 × 394px 카드 안의 indicator가 `(368,145)–(473,250)`에 렌더되어 카드 중심과 일치했다.
- 최종 조립 화면 결과: 사용자 런타임 확인 전까지 비최종 검증.
