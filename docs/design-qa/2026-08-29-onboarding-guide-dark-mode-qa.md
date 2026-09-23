# Onboarding guide dark-mode Figma UI QA

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X
- Target frames/nodes: `3110:31987` (`0.onboarding_guide01`) / `3110:32587` (`app_visual`), `3110:32359` (`0.onboarding_guide02`) / `3088:30233` (`app_visual`), `3110:32112` (`0.onboarding_guide03`) / `3110:33268` (`app_visual`).
- Reference images: `artifacts/2026-08-29-onboarding-guide-dark-mode/figma-page-{1,2,3}.png`; each is a persistent 360 × 720 Figma export including the root, status/safe area, and home-indicator region.
- Asset-density verification: the night-qualified PNGs are direct Figma 4× exports matching the retained light resources: `onboarding_guide_visual_1.png` (`3110:32587`) and `_2.png` (`3088:30233`) are 1152 × 1720; `_3.png` (`3110:33268`) is 1120 × 1720. `sips` verified these dimensions after download.
- Runtime target: `emulator-5556`, `sdk_gphone64_arm64`, API 36, system dark mode (`ui_night_mode=2`), gesture navigation (`navigation_mode=2`), 360 × 720 / 160 dpi override, default locale. Physical settings were 1280 × 2856 / 480 dpi and were restored after capture.
- Capture platform/device/emulator: Android Emulator via ADB. Every retained RED/GREEN capture is an uncropped 360 × 720 device screenshot including the status/safe area and gesture region.
- Automated focused-test assessment: the project has JUnit and Compose instrumentation dependencies, but no Robolectric or host route/system-bar harness. Resource qualifiers are resolved by Android configuration and the relevant behavior includes edge-to-edge system surfaces, so a JVM test cannot genuinely exercise it and a source-string test is prohibited. The existing light-vs-dark visual mismatch is recorded as RED below; full-device captures are the acceptance evidence.

## Component inventory

| Component | Figma node | Target state | Final result |
|---|---|---|---|
| `MainActivity` / `IntroduceView` root surface | `3110:31987`, `3110:32359`, `3110:32112` | system dark; root/status/gesture surface `#212121` | pending runtime capture |
| `IndicatorImageSection` dark visual 1 | `3110:32587` | pager page 0 | pending runtime capture |
| `IndicatorImageSection` dark visual 2 | `3088:30233` | pager page 1 | pending runtime capture |
| `IndicatorImageSection` dark visual 3 | `3110:33268` | pager page 2 | pending runtime capture |

## Validation rounds

### Round 1 — RED — actual system dark mode

| Scope | Difference | Fix | Result |
|---|---|---|---|
| Dark guide pager visuals | With the `drawable-night-nodpi` exports temporarily absent, all three system-dark pages rendered the retained light guide graphics rather than the Figma dark `app_visual` nodes. | None; captured before restoring the night-qualified Figma export bytes. | RED |

- Runtime captures: `artifacts/2026-08-29-onboarding-guide-dark-mode/runtime-red-page-{1,2,3}-light-graphic.png`.
- Comparison: native 360 × 720 side-by-side, no crop. The Android root/status/gesture surface was already `#212121`, but the page-1 central visual retained the light cream `#FFEFCC` card instead of Figma's dark `#212121` visual / `#424242` containers. Pages 2–3 similarly retained their light assets. A host-level test cannot observe resource configuration or the system bars, so these full-device captures are the acceptance evidence.

### Round 2 — night-qualified Figma exports

| Scope | Difference | Fix | Result |
|---|---|---|---|
| `IndicatorImageSection` page 1 | RED used the light page-1 guide image in system dark mode. | Added `drawable-night-nodpi/onboarding_guide_visual_1.png`, a 1152 × 1720 4× Figma export of `3110:32587`. | Dark graphic selected; density verified; component-level pass |
| `IndicatorImageSection` page 2 | RED used the light page-2 guide image in system dark mode. | Added `drawable-night-nodpi/onboarding_guide_visual_2.png`, a 1152 × 1720 4× Figma export of `3088:30233`. | Dark graphic selected; density verified; component-level pass |
| `IndicatorImageSection` page 3 | RED used the light page-3 guide image in system dark mode. | Added `drawable-night-nodpi/onboarding_guide_visual_3.png`, a 1120 × 1720 4× Figma export of `3110:33268`. | Dark graphic selected; density verified; component-level pass |
| Root/status/gesture surfaces | Must retain Figma dark `#212121` outside the pager asset. | Existing `MainActivity` dark branch and `IntroduceView` dark background already resolve to `R.color.gray_700` (`#212121`); no source change needed. | Visual sample pass |

- Runtime captures: `artifacts/2026-08-29-onboarding-guide-dark-mode/runtime-green-page-{1,2,3}-dark-graphic.png`.
- Comparison: native 360 × 720 side-by-side, no crop; [open the persistent comparison](artifacts/2026-08-29-onboarding-guide-dark-mode/full-frame-side-by-side.html). Page 1 confirms the dark Figma image bytes: the central card changes from cream to dark `#212121` / `#424242` surfaces, and the dark outer surface continues through both Android system regions. Pages 2–3 show their distinct Figma dark writing/calendar graphics.

## Final assembled-screen result

- Final runtime captures: `artifacts/2026-08-29-onboarding-guide-dark-mode/runtime-green-page-{1,2,3}-dark-graphic.png`.
- Comparison: full 360 × 720 side-by-side, uncropped; Figma references are `figma-page-{1,2,3}.png` in the same persistent artifact directory.
- Result: Blocked after round 2.
- Remaining differences: all three assembled Android pages retain pre-existing Figma parity gaps outside this dark-resource scope: guide title/copy differs (`필사, 이렇게 사용하면 편리해요.` versus the Figma page-specific copy), the title and central visual begin higher/larger than the Figma composition, the central image is clipped/reflowed differently on pages 2–3, and the Android status/gesture glyphs are platform-native instead of the iOS glyphs rendered in Figma. The requested dark root `#212121` surface and three central dark `app_visual` exports are present; the full-frame result cannot be `Pass` while these exact remaining differences exist.
