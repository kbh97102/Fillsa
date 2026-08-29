# 온보딩 가이드

## Android 구성

- Compose 위치: `presentation/ui/common/IntroduceView.kt`, `IntroduceIndicatorSection.kt`, `IndicatorImageSection.kt`
- 범위: 앱 기능 소개와 가이드 페이지 전환

## Figma UI 기준

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X
- 대상 프레임/노드: `3088:30089` (`0.onboarding_new_01`) / `3110:31993` (`app_visual`), `3088:30215` (`0.onboarding_new_02`) / `3110:32702` (`app_visual`), `3088:30412` (`0.onboarding_new_03`) / `3110:33500` (`app_visual`)
- 대상 기기/프레임 크기: Figma 360 × 720. 런타임 검증은 `sdk_gphone64_arm64` (API 36), 360 × 720 override, 160 dpi override, light mode, gesture navigation으로 캡처했다. 원래 에뮬레이터 물리 크기/밀도는 1280 × 2856 / 480 dpi였다.
- 검증 상태: 온보딩 경로의 조립 full-frame 시스템 표면은 흰색으로 수정·캡처 완료. 다만 현재 Compose guide copy/layout은 Figma 전 프레임과 불일치하므로 전체 Figma 조립 화면 결과는 `Blocked`이며 이 범위에서는 수정하지 않는다.
- 기준 이미지: `docs/design-qa/artifacts/2026-08-29-onboarding-guide-full-frame/figma-page-{1,2,3}.png` (Figma 360 × 720 full-frame exports; root background/status bar/home-indicator surface 포함). `presentation/src/main/res/drawable-nodpi/onboarding_guide_visual_{1,2,3}.png`는 기존 4× `app_visual` exports다.
- QA 기록: [2026-08-29-onboarding-guide-graphics-qa.md](../design-qa/2026-08-29-onboarding-guide-graphics-qa.md), [2026-08-29-onboarding-guide-full-frame-qa.md](../design-qa/2026-08-29-onboarding-guide-full-frame-qa.md)

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| `MainActivity` onboarding scaffold surface | `3088:30089`, `3088:30215`, `3088:30412` root frame | root/status/gesture surface white | `MainActivity` → `MainNavHost` | surface 수정·full-frame 캡처 완료 |
| `IndicatorImageSection` guide visual 1 | `3110:31993` | page 1 `app_visual` Figma export 표시 | `IntroduceView` pager page 0 | 리소스 확인 완료; 전체 조립 비교는 Blocked |
| `IndicatorImageSection` guide visual 2 | `3110:32702` | page 2 `app_visual` Figma export 표시 | `IntroduceView` pager page 1 | 리소스 확인 완료; 전체 조립 비교는 Blocked |
| `IndicatorImageSection` guide visual 3 | `3110:33500` | page 3 `app_visual` Figma export 표시 | `IntroduceView` pager page 2 | 리소스 확인 완료; 전체 조립 비교는 Blocked |
