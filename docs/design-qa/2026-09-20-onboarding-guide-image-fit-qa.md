# Onboarding guide image fit Figma UI QA

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X
- Target frames/nodes: `3110:31993`, `3110:32702`, `3110:33500` (`app_visual` light); `3110:32587`, `3088:30233`, `3110:33268` (`app_visual` dark)
- Reference images: `presentation/src/main/res/drawable-nodpi/onboarding_guide_visual_{1,2,3}.png` and `presentation/src/main/res/drawable-night-nodpi/onboarding_guide_visual_{1,2,3}.png`
- Full-frame reference: `docs/design-qa/artifacts/2026-08-29-onboarding-guide-full-frame/figma-page-{1,2,3}.png`
- Runtime target: Android 360×720dp reference viewport; light/dark pages 1–3; smaller available image regions must scale down without cropping
- Capture platform/device/emulator: connected Android device for instrumentation; full-frame runtime capture pending

## Component inventory

| Component | Figma node | Target state | Final result |
|---|---|---|---|
| `IndicatorImageSection` page 1 | `3110:31993`, `3110:32587` | light/dark, 288×430dp maximum | implementation complete; runtime verification pending |
| `IndicatorImageSection` page 2 | `3110:32702`, `3088:30233` | light/dark, 288×430dp maximum | implementation complete; runtime verification pending |
| `IndicatorImageSection` page 3 | `3110:33500`, `3110:33268` | light/dark, 280×430dp maximum | implementation complete; runtime verification pending |

## Validation rounds

### Round 1

| Scope | Difference | Fix | Result |
|---|---|---|---|
| `IndicatorImageSection` | `Image.fillMaxSize()` makes the image layout consume the full pager page instead of the Figma visual bounds. | Center the image in a full-size pager page; constrain pages 1–2 to 288×430dp and page 3 to 280×430dp; use `ContentScale.Fit` so tighter runtime constraints scale without cropping. | implementation complete; runtime verification pending |

## Final assembled-screen result

- Final runtime capture: pending
- Comparison: pending
- Result: pending
- Remaining differences: pending
