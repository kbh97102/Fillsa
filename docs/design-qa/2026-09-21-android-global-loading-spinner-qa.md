# Android Global Loading Spinner QA

## 기준

- Figma: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X/%E2%9C%92%EF%B8%8F%ED%95%84%EC%82%AC?node-id=2929-6086
- 대상 노드: 전체 예시 `2929:6086`, dim `2929:6175`, progress indicator `2929:6176`
- 기준 프레임: 360 × 720 px, light, ko-KR
- 기준 이미지: `docs/design-qa/assets/global-loading-spinner-2929-6086/reference-full.png`
- 로컬 SVG: `presentation/src/main/assets/figma/common/progress_indicator_01.svg`

## 디자인 계약

| 항목 | Figma 기준 | 구현 |
|---|---|---|
| 화면 dim | `#212121`, opacity 80% | 전체 root overlay |
| indicator frame | 120 × 120dp, 화면 중앙 | 중앙 정렬 Box |
| indicator visual | 112 × 112dp | 원본 SVG 112dp |
| track | `#D3D5FF`, 12dp | Figma SVG 원본 |
| active arc | `#5C65FF`, 12dp, round cap | Figma SVG 원본 |
| motion | indeterminate rotation | 1,000ms linear repeat |
| interaction | loading 중 화면 차단 | pointer event consume |
| accessibility | indeterminate progress | `ProgressBarRangeInfo.Indeterminate`, `로딩 중` |

## 적용 범위

- Blocking: Login submit, Home 진입/재개/주간 경계, Calendar 진입/월 변경, member Typing 진입.
- Paging: QuoteList와 Notice의 `refresh`만 포함하며 `append`/`prepend`는 제외.
- Non-blocking: 좋아요, 답변 저장, 이미지 업로드/삭제, 메모, 광고, 팝업, streak, widget/background refresh.
- 중복 요청은 owner/request count를 조합하여 모든 blocking 요청이 끝날 때까지 overlay를 유지한다.
- route dispose 시 해당 owner를 제거한다.

## 증거

| 검증 | 명령/파일 | 상태 |
|---|---|---|
| Figma reference 크기 | `sips -g pixelWidth -g pixelHeight docs/design-qa/assets/global-loading-spinner-2929-6086/reference-full.png` | Pass — 360 × 720px |
| Compose Preview | `android studio render-compose-preview ... GlobalLoadingSpinnerPreview --print-semantics` | Pass — indeterminate `ProgressBar`, 중앙 120dp frame |
| Preview 이미지 | `docs/design-qa/artifacts/2026-09-21-global-loading-spinner/preview.png` | Pass — 360 × 720dp preview (945 × 1890px, 2.625 density) |
| Kotlin compile + APK assembly | `./gradlew :presentation:compileDebugKotlin :app:assembleDebug --console=plain` | Pass — `BUILD SUCCESSFUL` |
| 전체 runtime frame | `docs/design-qa/artifacts/2026-09-21-global-loading-spinner/runtime-home.png` | Pass (시각) — Pixel 9 API 35, 1080 × 2160px / 360 × 720dp, GPRS throttle Home API |
| 기능 검증 | 입력 차단, 중복 요청, route dispose, Paging append, mutation 제외 | 사용자 직접 확인 대기 |

## 최종 상태

`조건부 통과` — Figma 시각 기준, Preview, compile/APK assembly, 지연된 Home API의 실제 전역 overlay 표시와 실패 후 해제까지 확인했다. 사용자 요청에 따라 추가 기능 검증은 중단했으며 입력 차단·중복 요청·route dispose·Paging append·mutation 제외 동작은 사용자 직접 확인 대기다. TDD/신규 단위 테스트는 사용자 요청에 따라 제외했다.
