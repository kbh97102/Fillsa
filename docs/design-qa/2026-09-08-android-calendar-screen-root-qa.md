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

> Superseded for the reviewed Calendar-owned internals by Round 4 below. Its earlier
> assertion that the remaining text delta was only 1–2px did not cover the subsequently
> identified 5–8px offsets.

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

### Round 4 — independent-review fix revalidation

> Superseded for the focus-week date labels and basic companion line spacing by Round 5 below.
> Its general 1–2px text-edge statement is not retained as a blanket acceptance tolerance.

| Scope | Difference | Fix | Result |
|---|---|---|---|
| Calendar weekday/date labels | Cell labels were approximately 7–8px above their Figma positions although the x20/y90, 320×396 shell was correct | Offset weekday text +8dp and date labels +7dp inside their fixed 40dp/50dp cells; no shell geometry changed | Rechecked in basic and completed frames |
| Basic companion `2985:22183` | The character/message visual y/typography and overlap with the quote card did not match | Used purple `subtitle2` companion text, moved the character/text visual up 7dp within the unchanged 100dp row, and raised that row for the character-over-card drawing order; the 320×80 card boundary remains y601–681 | Rechecked in basic frame |
| Completed card / prompt | Card date/quote sat approximately 7–8px low, while question/answer text sat approximately 5–7px high | Moved completed-card text internals up without moving the 133dp card/action boundaries; moved the question and editor content down within the unchanged 174dp editor and 50dp CTA geometry | Rechecked in unanswered and answered/image frames |
| Completed-unanswered fixture `2987:22796` | Selected completed-day Like action was incorrectly filled | Made only the unanswered selected record unliked (`YN.N`); answered/image remains liked (`YN.Y`) | Rechecked; runtime shows neutral outline Like action |

- Fresh runtime captures after a seven-second settle, with no transient toast:
  - Basic: `runtime-basic-round4.png` (360×816, SHA-256 `8ae102d969af61344f567ac4e23853cfc86d7e766da581c13f8804ab02200500`)
  - Completed/unanswered: `runtime-unanswered-round4.png` (360×1101, SHA-256 `3637639db5cb70e90e287520c7d6d4e76bab317877705a43167cb81c04f17ae9`)
  - Completed/answered-image: `runtime-answered-image-round4.png` (360×1101, SHA-256 `82184a822ab8e17a2084dc154f4c501a33090fa7d07a2bdd51c7c4ba9fe40c64`)
- Fresh full-frame 50/50 overlays: `overlay-basic-round4.png`, `overlay-unanswered-round4.png`, `overlay-answered-image-round4.png`.
- Fresh side-by-sides (Figma x0–359; Android x360–719): `side-by-side-basic-round4.png`, `side-by-side-unanswered-round4.png`, `side-by-side-answered-image-round4.png`.
- Preliminary Round 4 inspection of the reviewed Calendar-owned boundaries was superseded by the scoped measurements in Round 5; it is not a generalized numeric residual tolerance. Android status/gesture UI, the shared four-route navigation, and the suppressed fixture ad remain the documented whole-frame product-boundary differences.

### Round 5 — scoped re-review corrections

> Superseded for the focus-week implementation mechanism by Round 6. The measured
> Round 5 output was retained as evidence, but its fixture-held geometry is not accepted.

| Scope | Difference | Fix | Result |
|---|---|---|---|
| Focus-week date glyphs | In Round 4, binding focus-week labels rendered y340–349 while the Figma reference is y336–345; ordinary rows already aligned | Superseded: the QA-only date offset was removed in Round 6 | Evidence retained; implementation rechecked in Round 6 |
| Basic companion two-line copy | In Round 4, first-line glyphs were y553–564 and second-line glyphs y569–580 vs Figma y550–561 / y571–582 | Kept the matched character/card container geometry and applied per-line text-only offsets: -3dp first line, +2dp second line | Basic glyph bounds visually measure y550–561 and y571–582 |

- Fresh toast-free, seven-second-settle runtime evidence: `runtime-basic-round5.png` (360×816, SHA-256 `c38f63631f0628b62dfef342bf0fe04455673bea7a4f355c204e89375c11fcbb`), `runtime-unanswered-round5.png` (360×1101, SHA-256 `6a42ba62a8e8380917344545337ee2ed3f2abd455561249400bb4b4f62096c59`), and `runtime-answered-image-round5.png` (360×1101, SHA-256 `60b64a2fb980545ff9f859ee6eb252db82b6c36dd42d24f4f086f7b53726e7c7`).
- Fresh full-frame overlays: `overlay-basic-round5.png`, `overlay-unanswered-round5.png`, `overlay-answered-image-round5.png`; fresh Figma-left/Android-right side-by-sides: `side-by-side-basic-round5.png`, `side-by-side-unanswered-round5.png`, `side-by-side-answered-image-round5.png`.
- Visual measurement is limited to the scoped glyph bounds above. No blanket 1–2px Calendar-owned residual claim is made. The unchanged whole-frame product-boundary differences remain Android status/gesture UI, shared four-route navigation, and the intentionally omitted fixture ad.

### Round 6 — shared production/fixture week geometry

| Scope | Difference | Fix | Result |
|---|---|---|---|
| Focus-week layout ownership | Round 5 carried a date offset in QA fixture cells, so production did not execute the same geometry | Removed fixture geometry. Every week now derives its cell geometry from real presentation state: a selected cell or any fire/heart record expands that entire week to 50dp cells; otherwise its 40dp cells are vertically centered in the fixed 50dp row. Fixture supplies only synthetic dates/content/indicators | Production and fixture call the same `CalendarSection` week rule; selected/record week glyphs remain y336–345 and ordinary rows remain aligned |

- Fresh toast-free, seven-second-settle runtime evidence: `runtime-basic-round6.png` (360×816, SHA-256 `8d3250bd10c89cdd9bb054ebbb083620592c2e1e5c5f0070a41f9a8eab29c770`), `runtime-unanswered-round6.png` (360×1101, SHA-256 `e08b4a8c1e174792e4d4565936e0f9acaff1bf54b1dfa29e6cc938fff0112184`), and `runtime-answered-image-round6.png` (360×1101, SHA-256 `762ba2b4922e3832ffbd25347dcb63069d626e04a396766d8a04b5ee46f063fc`).
- Fresh full-frame overlays: `overlay-basic-round6.png`, `overlay-unanswered-round6.png`, `overlay-answered-image-round6.png`; fresh Figma-left/Android-right side-by-sides: `side-by-side-basic-round6.png`, `side-by-side-unanswered-round6.png`, `side-by-side-answered-image-round6.png`.
- Visual inspection confirms the Round 6 shared geometry preserves the focus-week glyph position, record indicators, selected 50dp pill, ordinary row alignment, and basic companion line spacing. No blanket numeric residual tolerance is asserted.

## Final assembled-screen result

- Calendar-owned result: Round 6 shared-geometry revalidation is ready for the parent task's final assembled-screen acceptance; all three binding states have fresh full-frame evidence.
- Whole-frame literal result: **Blocked only by preserved shared/system product surfaces** — Android vs iOS system UI, shared four-route navigation vs Figma three tabs, and live ad intentionally absent from the nonnetwork fixture.
- Remaining Calendar-owned differences: no blanket numeric tolerance is asserted; the scoped Round 5 glyph measurements are recorded above.
- Required product-boundary differences: documented above; no navigation or ad ownership was changed.
