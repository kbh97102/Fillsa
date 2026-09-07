# Calendar Figma UI QA

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X/%E2%9C%92%EF%B8%8F%ED%95%84%EC%82%AC?node-id=2929-13366
- Target frames/nodes: `2985:21952` basic (360×816), `2987:22796` completed/unanswered/no image (360×1101), `2985:22510` completed/answered/image (360×1101).
- Full-frame references: `docs/design-qa/assets/calendar-figma-2929-13366/2026-09-08/figma-basic-2985-21952.png`, `figma-unanswered-2987-22796.png`, `figma-answered-image-2985-22510.png`. They include the root, status/header, safe area, bottom navigation, and Figma ad region.
- Reference SHA-256: basic `575e58fd73864689784491aac242113b42d57697acfb8381513259bc451ed1fa`; unanswered `556d5a7e5a59cdf222480bef6983827427cdea687ba4eff9898f0ab5c3e3ccdc`; answered/image `ce6621cc9f9a3530b29798417f974e4edf453fc513845ef7195ba7530fb7e7e0`.
- Runtime target: headless Android Emulator AVD `Medium_Phone_API_35`, API 35, 160dpi override, light mode, app locale `ko-KR`; 360×816 basic and 360×1101 completed states.
- Capture platform: Android Emulator `emulator-5554` on the local macOS host; gesture navigation. System locale remained `en-US`, while `cmd locale get-app-locales` confirmed `[ko-KR]` for the app.

## Reproducible fixture launch

The debug-only intent extra is `com.arakene.fillsa.extra.CALENDAR_QA_STATE` with one of `basic`, `completed_unanswered`, or `completed_answered_image`.

```bash
adb shell am force-stop com.arakene.fillsa
adb shell am start -W -n com.arakene.fillsa/com.arakene.presentation.MainActivity \
  --es com.arakene.fillsa.extra.CALENDAR_QA_STATE basic
```

The fixture is process-local presentation input. Calendar refresh/actions, widget initialization, streak refresh, and ad refresh are skipped in this mode. It performs no API request and writes no application/domain data. The same production Calendar composables and geometry are used; only unavailable synthetic date/data/answer/image state is injected.

## Component inventory

| Component | Figma node | Target state | Final result |
|---|---|---|---|
| `CalendarHeader` | `2985:22104`, `2987:22995`, `2985:22688` | logo, 100-day fixture streak, profile | Calendar-owned Pass |
| `CalendarSection` | `2985:21954`, `2987:22799`, `3145:2024` | 320×396 shell at x20/y90 | Pass |
| `Day` / indicators | selected-row descendants | 36×50, selected 50dp pill, 12dp heart/fire | Pass |
| `CalendarCountSection` | `2987:23057`, `2987:23117`, `3145:2158` | 16dp heart/fire and monthly like/typing totals | Pass |
| Empty companion / quote preview | `2985:22183`, `2985:22145` | 100dp message region and 320×80 quote card | Pass |
| Completed card/action row | `2987:22950`, `3207:2950` | 320×133; action widths 70/70/70/107 | Pass |
| Registered image action | `3207:2973`, `3207:2974`, `3207:2977` | 28×28 radius-5 crop, 4dp gap, bold purple label | Pass |
| Prompt answer | `2987:22977`, `2985:22670` | 320×298, 174dp editor, record/edit CTAs | Pass |
| Shared bottom navigation/ad | frame bottom descendants | preserved Android four-route navigation/live-ad ownership | Out-of-scope product difference; full-surface literal match Blocked |

## Source inconsistencies and rulings

- The binding frames label the month `2025. 03`, but their cells do not follow March 2025 arithmetic. The basic frame duplicates 17 and omits 21. Production continues to use real `YearMonth` arithmetic; only the explicit QA fixture carries the Figma visual cell sequence.
- `2985:22670` shows a non-empty answer and `0 / 200`. Production uses the existing grapheme-safe actual count. The answered/image fixture alone uses the reference's displayed zero for exact visual comparison.
- The monthly `MemberQuotesData` response has no answer or `imagePath`. Production completion maps to unanswered/no-image presentation. Answer/image fields exist only in the presentation model/QA fixture; domain/data contracts were not changed.
- Figma has three bottom tabs and a static 35dp ad. Android retains its shared four-route navigation and live ad. The nonnetwork fixture suppresses live-ad loading instead of fabricating an ad. Android and Figma also have different system status/gesture surfaces.

## Validation rounds

### Round 1

| Scope | Difference | Fix | Result |
|---|---|---|---|
| Header/root | Calendar card began near y65 rather than Figma y90 | Reconciled edge-to-edge/status-bar inset path | Recheck required |
| Calendar grid | Real March arithmetic could not reproduce the binding synthetic cells | Added explicit process-local visual cell sequence; production calendar arithmetic unchanged | Recheck required |
| Completed detail | No answer/image visual presentation existed | Added presentation-only completed details, exact action widths, selected like, thumbnail/image-view, and answer/edit states | Recheck required |

- Runtime evidence: `runtime-basic-round1.png`.

### Round 2

| Scope | Difference | Fix | Result |
|---|---|---|---|
| Header/calendar shell | x20/y90 and 320×396 aligned, but the correction was initially fixture-specific | Applied +6dp Calendar root geometry to production and fixture; Calendar route always consumes status-bar inset | Recheck required by self-review |
| Completed card | Card began 6dp above y528 | Completed detail top spacing increased by 6dp | Recheck required |
| Prompt editor/CTA | Editor and CTA were about 13–16dp above binding coordinates | Added the binding 11dp editor top gap | Recheck required |

- Comparison evidence: `overlay-*-round2.png`, `side-by-side-*-round2.png` in the asset directory.

### Round 3

| Scope | Difference | Fix | Result |
|---|---|---|---|
| Header/calendar/count | Header y30–80, shell x20/y90–486, count row aligned | None | Pass |
| Basic companion | Quote card x20/y601–681; selected cells/indicators/text aligned | None | Pass |
| Completed detail | Card x20/y528–661 and 70/70/70/107 actions aligned | None | Pass |
| Prompt answer | Title/question/editor/CTA aligned; editor approximately y719 vs reference y721, CTA y919 | 2px text/rasterization tolerance accepted | Pass |
| Registered image | Real local ocean fixture rendered as 28dp crop with `이미지 보기`; answered text/edit CTA present | None | Pass |
| Shared/system surfaces | Android four-route navigation, gesture/status icons, and no network ad differ from Figma | Preserved by product contract | Blocked outside Calendar ownership |

- Final runtime captures: `runtime-basic-final.png`, `runtime-unanswered-final.png`, `runtime-answered-image-final.png`.
- Final runtime SHA-256: basic `0c65833967052c6e8974692745c75f9d41e9d7a35358a1751b008ce671865d57`; unanswered `c3d6370a12cd61405fe475c021945b4f382526d68cad9b74580b08e1ef44bf1d`; answered/image `86d0b1079f68e72ba4cb58174a8520b75400d035641141ed35a3b5f430255b2f`.
- Final comparisons: `overlay-basic-final.png`, `overlay-unanswered-final.png`, `overlay-answered-image-final.png`; `side-by-side-basic-final.png`, `side-by-side-unanswered-final.png`, `side-by-side-answered-image-final.png`.
- Crop boundaries: full 360×816 or 360×1101 images, no crop. Side-by-side files place Figma at x0–359 and Android at x360–719. Overlays are 50/50 full-frame blends.
- The final captures were taken after a seven-second settle per state; the transient Android app-language/keyboard toast seen during the first final attempt is absent from every retained final runtime/comparison asset.

## Final assembled-screen result

- Calendar-owned result: **Pass in round 3** for all three binding states.
- Whole-frame literal result: **Blocked only by preserved shared/system product surfaces** — Android vs iOS system UI, shared four-route navigation vs Figma three tabs, and live ad intentionally absent from the nonnetwork fixture.
- Remaining Calendar-owned differences: none beyond normal 1–2px font/rasterization variance.
- Required product-boundary differences: documented above; no navigation or ad ownership was changed.
