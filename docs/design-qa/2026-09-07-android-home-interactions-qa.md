# Android Home interactions Figma UI QA

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556
- Target frames/nodes: parent `2929:17193`; light `2929:13556`; dark `3039:26518`; calendar-open `3139:1501`; question `3110:33782`; streak tooltip `2929:18871`.
- Reference images: `docs/design-qa/assets/home-figma-2929-17193/2026-09-07/fillsa-home-default.png`, `fillsa-home-calendar-open.png`, `fillsa-home-question-flow.png`, `fillsa-home-streak-tooltip.png`, and `fillsa-home-image-flow.png`.
- Full-frame reference: the default, calendar-open, and streak-tooltip reference images are 360 × 821 px and include the root background, status bar, safe area, bottom navigation, and ad area.
- Runtime target: Android debug Home at 360 × 821, Korean locale, light/dark and interaction states listed below.
- Capture platform/device/emulator: Android Emulator `Medium_Phone_API_35` (`emulator-5554`), API 35, 1080 × 2400 px at 420 dpi. Wi-Fi was enabled for the attempt. This differs from the required 360 × 821 / matching-density comparison target.

## Component inventory

| Component | Figma node | Target state | Final result |
|---|---|---|---|
| Inline month calendar | `2929:15667`, `2929:16221`, `3139:1501`, `2929:16227`–`2929:16258` | closed/open, previous/next month, day selection, outside dismissal | Partial runtime evidence: open and valid-date selection/close captured; layout is not target-size matched. |
| Week strip | `3204:2435` | selected date at right edge, genuine completed-date marker, selected precedence | Default selection visible at runtime; completed marker had no genuine data. Regression unit coverage verifies selected precedence. |
| Question answer | `3087:29376`, `3139:1399`, `3087:29378`, `3110:34293` | default, focus, record snackbar, done/edit | Focus input and done/edit captured; debug ad validator prevented a clean snackbar frame. |
| Zero-streak tooltip | `2929:18871`, `2929:19015`–`2929:19017` | open, outside dismissal, Calendar link | Blocked: runtime streak is unknown/null, which correctly does not expose the zero-only tooltip. |
| Existing quote/image actions | `3223:5107`, `2929:19645`, `2929:19784` | image, copy snackbar, like, share/login/typing routes | Image action reached the existing login-required dialog; authenticated image states unavailable. |

## Validation rounds

### Round 1

| Scope | Difference | Fix | Result |
|---|---|---|---|
| Pure calendar/week/question states | No implementation initially provided the required popup, selected-right-edge week, session answer lifecycle, or genuine completed dates. | Added stateless month grid/Compose popup, `-6..0` week state, session-only `HomeAnswerUiState`, and `GetAllStreakInfoUseCase` completion-date loading. | `:presentation:testDebugUnitTest` passed; visual comparison Blocked. |
| Home interaction routing | Month CTA previously navigated to Calendar and question CTA opened Typing. | Month CTA now toggles inline state; valid day refreshes Home and closes it. Question record/edit stays in Home and emits `답변을 기록했어요.`; only zero-streak tooltip link emits `Screens.Calendar`. | `:presentation:assembleDebug` passed; runtime interaction exercise Blocked. |
| Full assembled frame | No runtime device was available. | Ran `adb devices -l`; it returned no attached devices, so a runtime frame, overlay, or side-by-side comparison could not be honestly produced. | Blocked. |

### Round 2 — emulator runtime attempt

| Scope | Difference | Fix | Result |
|---|---|---|---|
| App launch / default Home | An initial fresh launch showed `서버와의 통신이 원활하지 않습니다. 인터넷 연결 상태를 확인해 주세요.`. | Enabled Wi-Fi and retried; a later fresh launch loaded the default Home quote. The initial dialog is therefore a transient attempt result, not the final runtime state. | Superseded by Round 3. |
| Inline calendar / question / tooltip / image / dark states | The initial dialog consumed input. | No fake data or production-contract workaround was added. | Superseded by Round 3. |
| Review regressions | The review identified an unknown-streak-as-zero issue, incorrect done CTA colours, inconsistent current-date sourcing, and missing contract coverage. | Unknown/null streak no longer enables the zero-only tooltip; the done CTA now uses `#D3D5FF` with `#5C65FF` content; date bounds use `DateCondition.currentDay()`; Home interaction contract tests cover inline toggle/select, known-zero gating/link destination, Home snackbar, and selected-day precedence. | `:presentation:testDebugUnitTest :presentation:assembleDebug` passed. |

### Round 3 — reachable runtime states

| Scope | Evidence | Comparison/result |
|---|---|---|
| Default light Home | `runtime-android-default.png` | Quote, week strip, question default, and shared navigation/ad rendered. The hierarchy and Home palette are recognisable against Figma, but 1080 × 2400 / 420 dpi is not the required 360 × 821 comparison frame. |
| Inline calendar | `runtime-android-calendar-open.png`, `runtime-android-calendar-selected.png` | Month trigger opened the popup; selecting a valid date closed it and refreshed the Home date. The month trigger did not navigate directly to Calendar. |
| Question focus/done | `runtime-android-question-focus.png`, `runtime-android-question-done-snackbar.png` | Input focus/count and done state are reachable. The done CTA visibly uses the required lavender background and primary icon/text. The emulator input/accessibility overlay prevents a clean soft-keyboard comparison. |
| Question snackbar | `runtime-android-question-snackbar.png` | Record action changed to the done/edit state, but the debug AdMob native-ad validator obscured the snackbar placement. Unit coverage confirms the emitted message is `답변을 기록했어요.`; no clean snackbar visual pass is claimed. |
| Zero-streak tooltip | Header displayed `0일`, but its provider value was null/unknown. | Correctly unavailable after the unknown-versus-zero fix; genuine loaded zero, outside dismissal, and link interaction remain unverified. |
| Image / dark | `runtime-android-image-login.png`, `runtime-android-dark.png` | Image action reached the existing login-required dialog. Dark palette rendered, but the debug validator overlay obscures the lower portion. Authenticated image flow remains unavailable. |

- Comparison: Figma references were inspected alongside each listed capture. Runtime behavior for calendar selection and Home-local question recording is evidenced, but neither full-frame visual parity nor every required state can be passed at this emulator size and with the debug overlay.

## Final assembled-screen result

- Final runtime captures: `runtime-android-default.png`, `runtime-android-calendar-open.png`, `runtime-android-calendar-selected.png`, `runtime-android-question-focus.png`, `runtime-android-question-done-snackbar.png`, `runtime-android-question-snackbar.png`, `runtime-android-image-login.png`, and `runtime-android-dark.png` under `docs/design-qa/assets/home-figma-2929-17193/2026-09-07/` (1080 × 2400, 420 dpi).
- Comparison: Blocked. The captures demonstrate reachable Home behavior, but are not the required 360 × 821 frame; genuine zero-streak and authenticated image states are absent, and the debug AdMob validator prevents clean snackbar/dark lower-frame validation.
- Result: Blocked.
- Remaining differences: target-device spacing/typography/system surfaces, completed-marker data, focused soft keyboard, clean snackbar placement, genuine-zero tooltip/link, authenticated image flow, and unobstructed dark lower frame. The existing app-wide four-route navigation/live-ad discrepancy remains out of scope as documented in `docs/screens/2_home.md`.
