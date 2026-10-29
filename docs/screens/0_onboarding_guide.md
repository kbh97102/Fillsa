# 온보딩 가이드

## 2026-10-29 In progress 후속 QA

- [Android 제목 위치·뒤로가기·문구 수정 계획 v1](2026-10-29-onboarding-3ffe639f-plan.md)을 승인받아 구현했다. [이번 QA 기록](../design-qa/2026-10-29-onboarding-3ffe639f-qa.md)의 headless Android CLI Preview에서 세 제목 상단 239px과 쉼표 없는 첫 문구를 확인했다. 최신 Figma 조회·실제 뒤로가기 전환은 대기 중이다.

## Android 구성

- Compose 위치: `presentation/ui/common/IntroduceView.kt`, `IntroduceIndicatorSection.kt`, `IndicatorImageSection.kt`
- 범위: 앱 기능 소개와 가이드 페이지 전환

## Figma UI 기준

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X
- 대상 프레임/노드: dark `3110:31987` (`0.onboarding_guide01`) / `3110:32587` (`app_visual`), `3110:32359` (`0.onboarding_guide02`) / `3088:30233` (`app_visual`), `3110:32112` (`0.onboarding_guide03`) / `3110:33268` (`app_visual`). 기존 light 참조와 4× asset은 유지한다.
- 대상 기기/프레임 크기: Figma 360 × 720. dark 검증 대상은 `sdk_gphone64_arm64` (API 36), 360 × 720 override, 160 dpi override, system dark mode, gesture navigation, 기본 locale이다. 원래 에뮬레이터 물리 크기/밀도는 1280 × 2856 / 480 dpi였다.
- 검증 상태: 2026-09-20 가이드 이미지 잘림 수정 진행 중. `app_visual`은 Figma 원본 크기(1·2페이지 288×430dp, 3페이지 280×430dp) 안에서 비율을 유지하고, 더 작은 런타임 영역에서는 축소되어야 한다. 기존 Compose guide copy/layout의 pre-existing full-frame 차이는 별도 범위로 유지한다.
- 기준 이미지: `docs/design-qa/artifacts/2026-08-29-onboarding-guide-dark-mode/figma-page-{1,2,3}.png` (Figma 360 × 720 full-frame exports; root background/status bar/home-indicator surface 포함). `presentation/src/main/res/drawable-nodpi/onboarding_guide_visual_{1,2,3}.png`는 light 4× exports이며, `presentation/src/main/res/drawable-night-nodpi/onboarding_guide_visual_{1,2,3}.png`는 대응 dark `app_visual` exports다.
- QA 기록: [2026-09-20-onboarding-guide-image-fit-qa.md](../design-qa/2026-09-20-onboarding-guide-image-fit-qa.md), [2026-08-29-onboarding-guide-dark-mode-qa.md](../design-qa/2026-08-29-onboarding-guide-dark-mode-qa.md), [2026-08-29-onboarding-guide-graphics-qa.md](../design-qa/2026-08-29-onboarding-guide-graphics-qa.md), [2026-08-29-onboarding-guide-full-frame-qa.md](../design-qa/2026-08-29-onboarding-guide-full-frame-qa.md)

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| `MainActivity` onboarding scaffold surface | `3088:30089`, `3088:30215`, `3088:30412` root frame | root/status/gesture surface white | `MainActivity` → `MainNavHost` | surface 수정·full-frame 캡처 완료 |
| `IndicatorImageSection` guide visual 1 | `3110:31993` | page 1 `app_visual`을 288×430dp 경계 안에 잘림 없이 표시 | `IntroduceView` pager page 0 | 수정 진행 중 |
| `IndicatorImageSection` guide visual 2 | `3110:32702` | page 2 `app_visual`을 288×430dp 경계 안에 잘림 없이 표시 | `IntroduceView` pager page 1 | 수정 진행 중 |
| `IndicatorImageSection` guide visual 3 | `3110:33500` | page 3 `app_visual`을 280×430dp 경계 안에 잘림 없이 표시 | `IntroduceView` pager page 2 | 수정 진행 중 |
| `IndicatorImageSection` dark guide visual 1 | `3110:32587` | system dark pager page 0 `app_visual` export 표시 | `IntroduceView` pager page 0 | component-level pass; full frame Blocked |
| `IndicatorImageSection` dark guide visual 2 | `3088:30233` | system dark pager page 1 `app_visual` export 표시 | `IntroduceView` pager page 1 | component-level pass; full frame Blocked |
| `IndicatorImageSection` dark guide visual 3 | `3110:33268` | system dark pager page 2 `app_visual` export 표시 | `IntroduceView` pager page 2 | component-level pass; full frame Blocked |


## 전체 화면 대조 · 2026-10-25

- 기준 페이지: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-3720
- 범위: 현재 production UI 재사용 Preview. 시각 수정은 사용자 검토 후 별도 작업.
- 조건: ko, fontScale 1.0, Light/Dark, 고정 fixture. Preview는 시스템 바·IME를 포함한 최종 승인이 아님.
- QA 기록: [전체 화면 대조 보고서](../design-qa/2026-10-25-full-screen-audit.html)
- 상태·크기·원본 이미지: [screen-map.json](../design-qa/artifacts/2026-10-25-full-screen-audit/screen-map.json)

| 대상 노드 | production 컴포넌트 | 상태 / 책임 | 검증 상태 |
|---|---|---|---|
| 2929:4324 | SplashContent | 0.onboarding · Light | 조사 중 |
| 3088:30089 | IntroduceView | 0.onboarding_new_01 · Light | 조사 중 |
| 3088:30215 | IntroduceView | 0.onboarding_new_02 · Light | 조사 중 |
| 3088:30412 | IntroduceView | 0.onboarding_new_03 · Light | 조사 중 |
| 2929:5948 | SplashContent | 0.onboarding_dark · Dark | 조사 중 |
| 3110:31987 | IntroduceView | 0.onboarding_guide01 · Dark | 조사 중 |
| 3110:32112 | IntroduceView | 0.onboarding_guide03 · Dark | 조사 중 |
| 3110:32359 | IntroduceView | 0.onboarding_guide02 · Dark | 조사 중 |
