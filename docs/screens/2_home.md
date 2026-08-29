# Home

## Android 구성

- Compose 위치: `presentation/ui/home/`
- 상태 관리: `HomeViewModel`, `TypingViewModel`, `ShareViewModel`
- 범위: 오늘의 명언, 필사, 이미지·공유, 홈 내 달력 요약

## Figma UI 기준

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556
- 대상 프레임/노드: `2929:13556` (`2.home`)
- 대상 기기/프레임 크기: Figma export 360 × 821 px, light (`#FFEFCC`) root. (The supplied 360 × 720 description conflicts with the authoritative node export; QA uses the export.)
- 검증 상태: 진행 중 — 하위 Home 본문 컴포넌트 구현 및 런타임 전체 프레임 검증 대기
- 기준 이미지: `docs/design-qa/assets/home-figma-2929-13556/reference-full.png`
- QA 기록: `docs/design-qa/2026-08-29-home-figma-qa.md`

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| Header | `2929:15476` | Figma logo, current streak, profile navigation | `FigmaHomeContent` | 부분 통과(빌드) |
| Month calendar | `2929:15661` | actual `YYYY.MM` display and calendar navigation action | `HomeDateWeekSection` | 부분 통과(빌드) |
| Week strip | `2929:17161` | actual selected date; Home contract has no completed-date collection, so completion marker is false | `HomeDateWeekSection` | 완료 배지 Figma 상태는 Blocked |
| Quote card | `2929:13642` | selected quote, author/search opens existing Wikipedia URI, right/left date-bounded swipe actions | `HomeQuoteCard` | 부분 통과(빌드) |
| Quote action row | `2929:15503` | copy/share/like/image actions with current like visual state | `HomeQuoteActionRow` | 부분 통과(빌드) |
| Prompt response | `2929:13630` | established question placeholder, 200-grapheme answer input/count; record CTA keeps the original parameterless quote navigation | `HomePromptAnswerSection` | UI 부분 통과; 답변 저장·전달 Blocked |

현재 Home에는 질문 답변을 나타내는 domain/data 계약이 없습니다. 답변을 저장하거나 Typing으로 전달하려면 별도 작업에서 (1) 날짜·질문 기반 `PromptAnswerRecord` 모델, (2) `LocalRepository`의 저장/조회 계약과 device-local 또는 실제 backend 구현, (3) Home/Typing route state 및 명시 Save/Back 사용자 피드백을 함께 정의해야 합니다. 기존 quote 필사와 memo/API 필드는 재사용하지 않습니다.
| Global bottom navigation/ad | `2929:29254`, `2929:29249` | full-frame navigation/ad surface; owned by `BottomNavigationBar` | scaffold | 공유 범위 확인 대기 |
