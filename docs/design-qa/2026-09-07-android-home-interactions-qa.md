# Android Home interactions Figma UI QA

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556
- Target frames/nodes: parent `2929:17193`; light `2929:13556`; dark `3039:26518`; calendar-open `3139:1501`; question `3110:33782`; streak tooltip `2929:18871`.
- Reference images: `docs/design-qa/assets/home-figma-2929-17193/2026-09-07/fillsa-home-default.png`, `fillsa-home-calendar-open.png`, `fillsa-home-question-flow.png`, `fillsa-home-streak-tooltip.png`, and `fillsa-home-image-flow.png`.
- Full-frame reference: the default, calendar-open, and streak-tooltip reference images are 360 × 821 px and include the root background, status bar, safe area, bottom navigation, and ad area.
- Runtime target: Android debug Home at 360 × 821, Korean locale, light/dark and interaction states listed below.
- Capture platform/device/emulator: Blocked — `adb devices -l` on 2026-09-07 reported no attached devices. No runtime captures were created or represented as equivalent evidence.

## Component inventory

| Component | Figma node | Target state | Final result |
|---|---|---|---|
| Inline month calendar | `2929:15667`, `2929:16221`, `3139:1501`, `2929:16227`–`2929:16258` | closed/open, previous/next month, day selection, outside dismissal | Unit/build verified; runtime Blocked |
| Week strip | `3204:2435` | selected date at right edge, genuine completed-date marker, selected precedence | Unit/build verified; runtime Blocked |
| Question answer | `3087:29376`, `3139:1399`, `3087:29378`, `3110:34293` | default, focus, record snackbar, done/edit | Unit/build verified; runtime Blocked |
| Zero-streak tooltip | `2929:18871`, `2929:19015`–`2929:19017` | open, outside dismissal, Calendar link | Build verified; runtime Blocked |
| Existing quote/image actions | `3223:5107`, `2929:19645`, `2929:19784` | image, copy snackbar, like, share/login/typing routes | Existing effects preserved; runtime Blocked |

## Validation rounds

### Round 1

| Scope | Difference | Fix | Result |
|---|---|---|---|
| Pure calendar/week/question states | No implementation initially provided the required popup, selected-right-edge week, session answer lifecycle, or genuine completed dates. | Added stateless month grid/Compose popup, `-6..0` week state, session-only `HomeAnswerUiState`, and `GetAllStreakInfoUseCase` completion-date loading. | `:presentation:testDebugUnitTest` passed; visual comparison Blocked. |
| Home interaction routing | Month CTA previously navigated to Calendar and question CTA opened Typing. | Month CTA now toggles inline state; valid day refreshes Home and closes it. Question record/edit stays in Home and emits `답변을 기록했어요.`; only zero-streak tooltip link emits `Screens.Calendar`. | `:presentation:assembleDebug` passed; runtime interaction exercise Blocked. |
| Full assembled frame | No runtime device was available. | Ran `adb devices -l`; it returned no attached devices, so a runtime frame, overlay, or side-by-side comparison could not be honestly produced. | Blocked. |

- Comparison: Figma reference images were inspected side-by-side with the Compose component specifications and source dimensions. A runtime pixel/overlay comparison was not performed because no Android runtime frame exists.

## Final assembled-screen result

- Final runtime capture: none — capture platform unavailable.
- Comparison: Blocked before comparison; required 360 × 821 light/dark and overlay-state runtime captures are absent.
- Result: Blocked.
- Remaining differences: unverified runtime spacing, typography, system surfaces, popup placement, focused keyboard state, snackbar placement, and interaction hit targets. The existing app-wide four-route navigation/live-ad discrepancy remains out of scope as documented in `docs/screens/2_home.md`.
