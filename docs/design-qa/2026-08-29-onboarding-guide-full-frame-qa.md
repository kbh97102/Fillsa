# Onboarding guide full-frame Figma UI QA

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X
- Target frames/nodes: `3088:30089` (`0.onboarding_new_01`), `3088:30215` (`0.onboarding_new_02`), `3088:30412` (`0.onboarding_new_03`)
- Full-frame reference images: `artifacts/2026-08-29-onboarding-guide-full-frame/figma-page-1.png`, `figma-page-2.png`, `figma-page-3.png`; each is a Figma 360 × 720 export including root background, status area, and home-indicator area.
- Runtime target: `emulator-5556`, `sdk_gphone64_arm64`, API 36, light mode (`ui_night_mode=1`), gesture navigation (`navigation_mode=2`), locale unset/default. `wm size` and `wm density` were temporarily overridden from physical 1280 × 2856 / 480 dpi to 360 × 720 / 160 dpi for logical Figma-frame alignment, then restored after capture.
- Capture platform/device/emulator: Android Emulator via ADB; each runtime PNG is the uncropped 360 × 720 device screenshot (coordinates `(0,0)` through `(360,720)`).
- Ephemeral verification method: the normal onboarding entry is behind online login. For each RED/GREEN capture build only, `MainNavHost`'s `startDestination` was temporarily changed to `Screens.OnBoardingGuide`; it was restored to `Screens.Splash` before the retained source diff. It is not a production navigation change.
- Automated focused-test assessment: Compose UI-test dependencies are present, but the existing suite contains only example instrumentation and has no host-level route/system-bar harness. A composable-only or source-string assertion could not observe the edge-to-edge status/gesture surfaces, so no non-genuine automated test was added.
- Dark-mode preservation: Figma supplied only light-mode references and the verified correction is light-mode only. `MainActivity` evaluates the existing `isDarkMode` branch before the onboarding route branch, preserving `R.color.gray_700` for dark-mode onboarding.

## Component inventory

| Component | Figma node | Target state | Final result |
|---|---|---|---|
| `MainActivity` scaffold full-frame surface | `3088:30089`, `3088:30215`, `3088:30412` roots | all three guide pager pages; status and gesture/nav areas | White surface correction verified on all pages |
| `IntroduceView` assembled page 1 | `3088:30089` | pager page 0 | Blocked for pre-existing full-frame copy/layout mismatch outside this correction |
| `IntroduceView` assembled page 2 | `3088:30215` | pager page 1 | Blocked for pre-existing full-frame copy/layout mismatch outside this correction |
| `IntroduceView` assembled page 3 | `3088:30412` | pager page 2 | Blocked for pre-existing full-frame copy/layout mismatch outside this correction |

## Validation rounds

### Round 1 — RED

| Scope | Difference | Fix | Result |
|---|---|---|---|
| Onboarding page 1 full frame | Runtime status strip and gesture/nav surface used `#FFEFCC` (`R.color.primary`) while the Figma root/full frame is white. | None; captured the failing baseline. | RED |
| Onboarding pages 2–3 full frame | Same route-level scaffold behavior applied to every pager page. | None; captured baseline pages. | RED |

- Runtime captures: `artifacts/2026-08-29-onboarding-guide-full-frame/runtime-red-page-1-primary-system-surface.png`, `runtime-red-page-2-primary-system-surface.png`, `runtime-red-page-3-primary-system-surface.png`.
- Root cause: `MainActivity` selected `R.color.primary` for every non-splash light route, so edge-to-edge system regions exposed the onboarding route's scaffold color.
- Comparison: side-by-side at native 360 × 720 with no crop. The page-1 blank gesture-area sample `(10,710)` was Figma `#FFFFFF` and runtime `#FFEFCC`.

### Round 2 — GREEN surface correction

| Scope | Difference | Fix | Result |
|---|---|---|---|
| `MainActivity` onboarding route | Scaffold did not distinguish `OnBoardingGuide` from other non-splash routes. | Added the route-specific `isOnboardingGuide` check; the existing dark-mode branch is evaluated first, then light-mode onboarding selects `R.color.white` before the remaining default light branch. | GREEN for the requested full-frame surface correction |
| Onboarding pages 1–3 | Status and gesture/nav surfaces needed to remain white while pages changed. | Captured all three pager pages after the correction. | GREEN for system/root surface on all pages |

- Runtime captures: `artifacts/2026-08-29-onboarding-guide-full-frame/runtime-green-page-1-white-system-surface.png`, `runtime-green-page-2-white-system-surface.png`, `runtime-green-page-3-white-system-surface.png`.
- Comparison: side-by-side, native 360 × 720, no crop; full device frame includes status `(0..51)`, app content, and gesture/nav area `(696..719)` on this emulator. Page-1 blank gesture-area sample `(10,710)` changed from `#FFEFCC` to `#FFFFFF`; the same sample is `#FFFFFF` on Figma and on GREEN pages 1–3.
- The logical viewport matches Figma's 360 × 720 canvas, but Android system chrome glyphs are platform-native rather than Figma's iOS glyphs; this is not claimed pixel-perfect.

## Full-frame comparison

The following references are intentionally full-size and uncropped. [Open the persistent side-by-side comparison](artifacts/2026-08-29-onboarding-guide-full-frame/full-frame-side-by-side.html) at 1:1 scale.

| Page | Figma full frame | Corrected runtime full frame |
|---|---|---|
| 1 | [figma-page-1.png](artifacts/2026-08-29-onboarding-guide-full-frame/figma-page-1.png) | [runtime-green-page-1-white-system-surface.png](artifacts/2026-08-29-onboarding-guide-full-frame/runtime-green-page-1-white-system-surface.png) |
| 2 | [figma-page-2.png](artifacts/2026-08-29-onboarding-guide-full-frame/figma-page-2.png) | [runtime-green-page-2-white-system-surface.png](artifacts/2026-08-29-onboarding-guide-full-frame/runtime-green-page-2-white-system-surface.png) |
| 3 | [figma-page-3.png](artifacts/2026-08-29-onboarding-guide-full-frame/figma-page-3.png) | [runtime-green-page-3-white-system-surface.png](artifacts/2026-08-29-onboarding-guide-full-frame/runtime-green-page-3-white-system-surface.png) |

## Final assembled-screen result

- Final runtime captures: the three `runtime-green-page-*-white-system-surface.png` files above; all are complete 360 × 720 device frames.
- Scoped surface result: 부분 통과(최종 수용 아님). In the verified light-mode captures, the root/status/gesture surfaces are white in every onboarding pager state, correcting the prior `#FFEFCC` exposure. Dark-mode onboarding retains its existing `R.color.gray_700` surface and was not part of the light-mode Figma comparison.
- Final result: Blocked after round 2 for this scoped task. All three runtime frames retain pre-existing mismatches outside the requested surface treatment, including different guide titles/copy, top/vertical composition, and guide visual placement/size. Android status/navigation glyphs also differ from the iOS glyphs drawn in Figma. No pages, copy, pager navigation, or existing 4× guide assets were changed here.
- Remaining differences: the out-of-scope assembled guide differences listed above; therefore this record does not claim pixel-perfect full-frame parity.
