# Home

## Android 구성

- Compose 위치: `presentation/ui/home/`
- 상태 관리: `HomeViewModel`, `TypingViewModel`, `ShareViewModel`
- 범위: 오늘의 명언, 필사, 이미지·공유, 홈 내 달력 요약

## Figma UI 기준

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556
- 대상 프레임/노드: light `2929:13556` (`2.home`), dark `3039:26518` (`2.home`). Parent section `2929:9603` is not a render target.
- 대상 기기/프레임 크기: Figma export 360 × 821 px; light root `#FFEFCC`, dark root `#212121`. (The supplied 360 × 720 description conflicts with the authoritative node export; QA uses the export.)
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
| Global bottom navigation/ad | `3087:29254`; ad reference `2929:29249` is unavailable | Figma has 3 × 120dp tabs; Android keeps its shared 4-route bar and live ad surface | scaffold / `BottomNavigationBar` | Blocked — product decision required |
| Dark palette/assets | `3039:26518` | dark root/card/input/text/divider plus night SVG variants | `FigmaHomeContent` / `assets/figma/home-night/` | 부분 통과(RED→GREEN token test·runtime capture); full frame Blocked |

현재 Home에는 질문 답변을 나타내는 domain/data 계약이 없습니다. 답변을 저장하거나 Typing으로 전달하려면 별도 작업에서 (1) 날짜·질문 기반 `PromptAnswerRecord` 모델, (2) `LocalRepository`의 저장/조회 계약과 device-local 또는 실제 backend 구현, (3) Home/Typing route state 및 명시 Save/Back 사용자 피드백을 함께 정의해야 합니다. 기존 quote 필사와 memo/API 필드는 재사용하지 않습니다.

`3087:29254`는 Home/Calendar/My page 3개 탭(각 120 × 60dp, 32dp icon), 선택 `#5C65FF`, 비선택 `#212121`만 정의합니다. Android `BottomNavigationBar`는 동일한 light selected/unselected color와 32dp local vector를 사용하지만 Home/QuoteList/Calendar/My page의 4개 shared route를 보존합니다. 3등분 폭·3개 icon asset을 그대로 적용하려면 QuoteList의 위치·폭 또는 route를 변경해야 하므로 Home 범위에서 변경하지 않습니다. 광고는 live `SingleLineAdSection`의 native content이며, 이전 참조 `2929:29249`는 2026-08-30 Figma MCP에서 찾을 수 없었습니다. 향후 제품 결정은 (a) Android의 4탭/live ad contract를 Figma에 반영하거나, (b) shared navigation과 광고 노출 정책을 앱 전체 범위에서 3탭 디자인으로 변경하도록 명시 승인하는 것입니다.

Dark Home은 `3039:26518`의 `#212121` root, `#424242` quote/input card, `#616161` border/divider(주 divider 55%), white primary text, `#E0E0E0` action label, `#9E9E9E` muted text를 `HomeColorPalette`로 해석합니다. calendar와 selected/completed weekday, `#5C65FF` CTA는 Figma 상태대로 유지합니다. 정확한 dark SVG는 `presentation/src/main/assets/figma/home-night/`의 logo/profile/wave/search/copy/share/like/camera 8종이며 SVG가 density-independent이므로 Figma의 60/24/16/320dp Compose 크기로 렌더합니다. calendar/write/badge/streak SVG는 Figma dark bytes가 light와 동일해 기존 `assets/figma/home/`을 재사용합니다.
