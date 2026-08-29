# Home Figma UI QA

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556
- Target frame/node: `2929:13556` (`2.home`)
- Reference image: `docs/design-qa/assets/home-figma-2929-13556/reference-full.png`
- Full-frame reference: 360 × 821 px, including the Figma status, safe, navigation, and ad areas.
- Runtime target: Android emulator `sdk_gphone64_arm64`, API 36, 1280 × 2856 px at 480 dpi, light mode, Korean locale. It must be resized to the reference 360 × 821 dp frame for final comparison and restored afterwards.
- Capture platform/device/emulator: Android Emulator (`emulator-5556`).
- Figma source assets: `presentation/src/main/assets/figma/home/` (downloaded from Figma on 2026-08-29).

## Component inventory

| Component | Figma node | Target state | Final result |
|---|---|---|---|
| Top home controls | `2929:15476`, `2929:15661`, `2929:17161` | light, 2026.08, 10 complete/12 today | completion marker Blocked; remaining controls pending |
| Quote card/actions | `2929:13642`, `2929:15503` | Korean quote, action row | pending |
| Prompt response | `2929:13630` | empty answer, `0 / 200` | pending |
| Navigation/ad | `2929:29254`, `2929:29249` | Home selected | shared-surface decision pending |

## Validation rounds

### RED baseline

- `docs/design-qa/assets/home-figma-2929-13556/runtime-before.png` is a full emulator capture before changes. It is the app's onboarding-guide state, not a Home capture; using the live app to dismiss onboarding would persist a user-state change, so it was not used as a Home visual baseline.
- No genuine focused unit test can assert Figma layout/pixels or the full Home composition. The project has Compose instrumentation dependencies but no isolated Home renderer that can supply the required real data, navigation, and system frame; source-string tests were not added.

### Round 1

| Scope | Difference | Fix | Result |
|---|---|---|---|
| Header / month calendar / week strip | The initial Home composition grouped the Figma header and date row inline. | Extracted `HomeHeaderSection`, `HomeMonthCalendar`, and `HomeWeekStrip`; existing current streak, profile action, date/month data, and calendar action remain the inputs. | Build verified; runtime visual comparison pending. |
| Week completion marker | `HomeWeekStrip` marked its first rendered date complete solely because it was index `0`, so changing the selected date could show a stale/fake completion. `HomeViewModel` supplies `date`, `DailyQuoteDto`, like state, and a scalar streak count, but no completed-date collection. | Added pure `homeWeekDayStates(selectedDate, completedDates)` mapping. `HomeWeekStrip` now passes `emptySet()` explicitly until the Home contract exposes genuine completion dates, so it renders no fake completed date or badge. | Figma `2929:17161`'s completed-day/badge state is Blocked; focused RED→GREEN unit test required. |
| Home body components | Existing Home card/calendar composition did not match the Figma hierarchy. | Replaced the body composition with Figma top controls, date strip, quote card/actions, question, answer state, CTA, and downloaded Figma SVG assets. Existing ViewModel actions/data were retained. | Build verified; runtime visual comparison pending. |
| Quote action like state | The initial Figma action row always rendered the unselected Figma heart after `isLike` changed. | The unselected state uses the downloaded Figma SVG; the selected state uses the existing local filled-heart drawable while retaining `HomeAction.ClickLike`. `HomeLikeIconTest` verifies both mappings. | Focused unit test verified; runtime comparison pending. |
| Quote card / action row | The card and action controls were not named as independently verifiable Home components, and the card's swipe direction did not express Figma `2929:13642`'s latest-date constraint. | Separated `HomeQuoteCard` (14dp card, local wave/search assets, author navigation, swipe mapping) and `HomeQuoteActionRow` (16dp local assets, labels, 42dp controls, 28dp dividers). `HomeQuoteSwipeTest` verifies that the latest date allows only a left swipe to the previous date; copy/share/like/image callbacks retain their existing actions. | Focused RED→GREEN unit test and build verified; runtime visual comparison pending. |
| Global bottom navigation/ad | Figma `2929:29254` has three items (Home, Calendar, My page); the shared Android bar intentionally retains four items (Home, List, Calendar, My page) to preserve navigation. | No change by approved Home-only scope. | Blocked full-frame difference. |

- Comparison: no valid Home runtime capture yet. The emulator is in onboarding state and changing that state would not be isolated from the existing app data.

## Final assembled-screen result

- Final runtime capture: pending; no persistent Home app state was mutated merely to obtain a capture.
- Comparison: pending 360 × 821 full-frame overlay/side-by-side comparison.
- Result: Blocked.
- Remaining differences: (1) Figma's three-item bottom navigation differs from the intentionally preserved four-item shared navigation; (2) Figma `2929:17161` shows a completed date/badge, but the current Home contract has no completed-date source, so the Android strip truthfully renders no completion marker; (3) a matching-state 360 × 821 full Android frame has not been captured without changing existing emulator app state; (4) final component and assembled-frame pixel comparison is therefore incomplete.
