# 온보딩 가이드

## Android 구성

- Compose 위치: `presentation/ui/common/IntroduceView.kt`, `IntroduceIndicatorSection.kt`, `IndicatorImageSection.kt`
- 범위: 앱 기능 소개와 가이드 페이지 전환

## Figma UI 기준

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X
- 대상 프레임/노드: `3088:30089` (`0.onboarding_new_01`) / `3110:31993` (`app_visual`), `3088:30215` (`0.onboarding_new_02`) / `3110:32702` (`app_visual`), `3088:30412` (`0.onboarding_new_03`) / `3110:33500` (`app_visual`)
- 대상 기기/프레임 크기: Figma 360 × 720; `app_visual` exports are 4× PNGs: 1152 × 1720, 1152 × 1720, and 1120 × 1720 px (design sizes: 288 × 430, 288 × 430, and 280 × 430).
- 검증 상태: 가이드 그래픽 리소스 교체 완료; 런타임 조립 화면 캡처는 상위 화면 QA에서 확인 필요.
- 기준 이미지: `presentation/src/main/res/drawable-nodpi/onboarding_guide_visual_1.png`, `onboarding_guide_visual_2.png`, `onboarding_guide_visual_3.png` (4× Figma `app_visual` node exports, retained in `drawable-nodpi` to preserve existing Compose layout sizing)
- QA 기록: [2026-08-29-onboarding-guide-graphics-qa.md](../design-qa/2026-08-29-onboarding-guide-graphics-qa.md)

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| `IndicatorImageSection` guide visual 1 | `3110:31993` | page 1 `app_visual` Figma export 표시 | `IntroduceView` pager page 0 | 리소스 확인 완료 |
| `IndicatorImageSection` guide visual 2 | `3110:32702` | page 2 `app_visual` Figma export 표시 | `IntroduceView` pager page 1 | 리소스 확인 완료 |
| `IndicatorImageSection` guide visual 3 | `3110:33500` | page 3 `app_visual` Figma export 표시 | `IntroduceView` pager page 2 | 리소스 확인 완료 |
