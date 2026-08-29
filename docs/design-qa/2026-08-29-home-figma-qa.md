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
| Prompt response | `2929:13630` | empty answer, `0 / 200` | UI partial pass; persistence/handoff Blocked |
| Navigation/ad | `3087:29254`; ad reference `2929:29249` unavailable | Home selected, Figma 3-tab metrics vs Android 4 shared routes | Blocked — product decision required |

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
| Quote card / action row | The card and action controls were not named as independently verifiable Home components, and the card's swipe direction did not express Figma `2929:13642`'s latest-date constraint. | Separated `HomeQuoteCard` (14dp card, local wave/search assets, author navigation, swipe mapping) and `HomeQuoteActionRow` (16dp local assets, labels, 42dp controls, 28dp dividers). The author/search row opens the existing `https://wikipedia.org/wiki/<author>` URI format. `HomeQuoteSwipeTest` verifies right swipe = previous, left swipe = next, and latest date blocks only left/next; copy/share/like/image callbacks retain their existing actions. | Focused RED→GREEN unit test and build verified; runtime visual comparison pending. |
| Prompt response | Figma `2929:13630` specifies question, answer area, counter, and record CTA. Home has no question feed, while existing Typing `korTyping`/`engTyping` are quote-transcription state and cannot represent a prompt answer. | `HomePromptAnswerSection` preserves the established static Figma question placeholder, 17dp/`#DED4BD`/50% white answer box, 200-grapheme input/count, accessible input/CTA labels, and the downloaded 18dp CTA SVG. The CTA intentionally retains original parameterless `HomeAction.ClickQuote` navigation. | UI behavior is partial pass: `HomeAnswerInputStateTest` verifies the cap/count. Answer persistence or handoff is Blocked—there is no question-answer model, repository storage/API, or Home→Typing route/state contract. A separate task must define a date/question-keyed record, local or real backend storage, and explicit Save/Back feedback without repurposing quote transcription or memo fields. Runtime visual comparison pending. |
| Global bottom navigation/ad | Figma `3087:29254` has three 120 × 60dp items (Home, Calendar, My page), 32dp icons, selected `#5C65FF`, and unselected `#212121`. Android `BottomNavigationBar` has the same light selected/unselected colors and 32dp local vectors, but intentionally preserves four shared routes (Home, QuoteList, Calendar, My page). Its Material3 metrics cannot be made into Figma's three equal columns without changing that shared route layout. The recorded Figma ad node `2929:29249` is no longer found. | No production change: downloading the three Figma icon assets would not solve the 3-versus-4 layout and would make the shared bar inconsistent; existing live `SingleLineAdSection` behavior/assets are preserved. | Blocked full-frame difference. Required product decision: either update Figma to the established four-tab/live-ad contract, or explicitly authorize an app-wide navigation/ad policy change to three tabs. |

- Comparison: no valid Home runtime capture yet. The emulator is in onboarding state and changing that state would not be isolated from the existing app data.

## Final assembled-screen result

- Final runtime capture: pending; no persistent Home app state was mutated merely to obtain a capture.
- Comparison: pending 360 × 821 full-frame overlay/side-by-side comparison.
- Result: Blocked.
- Remaining differences: (1) Figma `3087:29254` has three 120dp tabs, while Android deliberately preserves four shared routes; (2) the Figma ad reference `2929:29249` no longer exists, while Android preserves its live ad surface; (3) Figma `2929:17161` shows a completed date/badge, but the current Home contract has no completed-date source, so the Android strip truthfully renders no completion marker; (4) Figma `2929:13630`'s question is a static established placeholder because Home has no question source; (5) the Figma answer field has no current answer record/storage or Home→Typing handoff contract, so CTA preserves only the original quote navigation; (6) a matching-state 360 × 821 full Android frame has not been captured without changing existing emulator app state; (7) final component and assembled-frame pixel comparison is therefore incomplete.
