# Onboarding guide graphics Figma UI QA

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X
- Target frames/nodes: `3088:30089` / `3110:31993` (`app_visual`), `3088:30215` / `3110:32702` (`app_visual`), `3088:30412` / `3110:33500` (`app_visual`)
- Reference images: `presentation/src/main/res/drawable-nodpi/onboarding_guide_visual_1.png`, `onboarding_guide_visual_2.png`, and `onboarding_guide_visual_3.png`; 4× PNG exports of the listed Figma `app_visual` nodes (1152 × 1720, 1152 × 1720, and 1120 × 1720 px).
- Runtime target: Android module resource/package verification via Gradle. No emulator capture was produced for this asset-only delegated scope.

## Component inventory

| Component | Figma node | Target state | Final result |
|---|---|---|---|
| Guide visual 1 | `3110:31993` | pager page 0 | Figma PNG export mapped |
| Guide visual 2 | `3110:32702` | pager page 1 | Figma PNG export mapped |
| Guide visual 3 | `3110:33500` | pager page 2 | Figma PNG export mapped |

## Validation rounds

### Round 1

| Scope | Difference | Fix | Result |
|---|---|---|---|
| Guide pager image resources | Existing hand-authored vector drawables were not the supplied Figma `app_visual` node renders. | Added the three 4× Figma PNG exports under `drawable-nodpi` and remapped only the existing pager resource selection. The retained resource configuration keeps the existing Compose layout while providing source pixels for high-density rendering. | Resource mapping ready for Gradle verification. |

## Final assembled-screen result

- Full-frame follow-up: [2026-08-29-onboarding-guide-full-frame-qa.md](2026-08-29-onboarding-guide-full-frame-qa.md) records the persistent Figma references, RED/GREEN runtime captures, emulator mapping, and comparison.
- Final runtime capture: `artifacts/2026-08-29-onboarding-guide-full-frame/runtime-green-page-{1,2,3}-white-system-surface.png` (full 360 × 720 device frames).
- Result: the asset mapping remains verified; full assembled Figma parity is Blocked in the follow-up QA because current guide copy/layout differs outside the graphics and system-surface scopes.
- Remaining differences: current guide titles/copy, composition, and visual placement/size still require a separately scoped Figma implementation; pager ordering, navigation, and the 4× guide assets were intentionally unchanged.
