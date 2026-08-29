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

- Final runtime capture: not produced in this asset-only delegated scope.
- Result: Blocked pending parent assembled-screen visual capture.
- Remaining differences: runtime size/placement comparison against the 360 × 720 Figma frames has not been captured; pager ordering, copy, indicator, and navigation were intentionally unchanged.
