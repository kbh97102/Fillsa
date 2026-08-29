# 온보딩 가이드

## Android 구성

- Compose 위치: `presentation/ui/common/IntroduceView.kt`, `IntroduceIndicatorSection.kt`, `IndicatorImageSection.kt`
- 범위: 앱 기능 소개와 가이드 페이지 전환

## Figma UI 기준

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X
- 대상 프레임/노드: dark `3110:31987` (`0.onboarding_guide01`) / `3110:32587` (`app_visual`), `3110:32359` (`0.onboarding_guide02`) / `3088:30233` (`app_visual`), `3110:32112` (`0.onboarding_guide03`) / `3110:33268` (`app_visual`). 기존 light 참조와 4× asset은 유지한다.
- 대상 기기/프레임 크기: Figma 360 × 720. dark 검증 대상은 `sdk_gphone64_arm64` (API 36), 360 × 720 override, 160 dpi override, system dark mode, gesture navigation, 기본 locale이다. 원래 에뮬레이터 물리 크기/밀도는 1280 × 2856 / 480 dpi였다.
- 검증 상태: dark root/status/gesture surface의 기준은 `#212121`, central `app_visual`은 위 Figma dark export이다. 현재 Compose guide copy/layout의 pre-existing full-frame 차이로 최종 결과는 `Blocked`이며, QA에 개별 차이를 기록한다.
- 기준 이미지: `docs/design-qa/artifacts/2026-08-29-onboarding-guide-dark-mode/figma-page-{1,2,3}.png` (Figma 360 × 720 full-frame exports; root background/status bar/home-indicator surface 포함). `presentation/src/main/res/drawable-nodpi/onboarding_guide_visual_{1,2,3}.png`는 light 4× exports이며, `presentation/src/main/res/drawable-night-nodpi/onboarding_guide_visual_{1,2,3}.png`는 대응 dark `app_visual` exports다.
- QA 기록: [2026-08-29-onboarding-guide-dark-mode-qa.md](../design-qa/2026-08-29-onboarding-guide-dark-mode-qa.md), [2026-08-29-onboarding-guide-graphics-qa.md](../design-qa/2026-08-29-onboarding-guide-graphics-qa.md), [2026-08-29-onboarding-guide-full-frame-qa.md](../design-qa/2026-08-29-onboarding-guide-full-frame-qa.md)

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| `MainActivity` onboarding scaffold surface | `3088:30089`, `3088:30215`, `3088:30412` root frame | root/status/gesture surface white | `MainActivity` → `MainNavHost` | surface 수정·full-frame 캡처 완료 |
| `IndicatorImageSection` guide visual 1 | `3110:31993` | page 1 `app_visual` Figma export 표시 | `IntroduceView` pager page 0 | 리소스 확인 완료; 전체 조립 비교는 Blocked |
| `IndicatorImageSection` guide visual 2 | `3110:32702` | page 2 `app_visual` Figma export 표시 | `IntroduceView` pager page 1 | 리소스 확인 완료; 전체 조립 비교는 Blocked |
| `IndicatorImageSection` guide visual 3 | `3110:33500` | page 3 `app_visual` Figma export 표시 | `IntroduceView` pager page 2 | 리소스 확인 완료; 전체 조립 비교는 Blocked |
| `IndicatorImageSection` dark guide visual 1 | `3110:32587` | system dark pager page 0 `app_visual` export 표시 | `IntroduceView` pager page 0 | component-level pass; full frame Blocked |
| `IndicatorImageSection` dark guide visual 2 | `3088:30233` | system dark pager page 1 `app_visual` export 표시 | `IntroduceView` pager page 1 | component-level pass; full frame Blocked |
| `IndicatorImageSection` dark guide visual 3 | `3110:33268` | system dark pager page 2 `app_visual` export 표시 | `IntroduceView` pager page 2 | component-level pass; full frame Blocked |
