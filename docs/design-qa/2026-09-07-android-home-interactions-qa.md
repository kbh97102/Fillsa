# Android Home interactions Figma UI QA

## Reference, target, and comparison method

- Figma: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556
- Target nodes: parent `2929:17193`; light `2929:13556`; dark `3039:26518`; calendar open `3139:1501`; question focus `3139:1399`; question done `3087:29378`; completion snackbar `3110:34293`; zero-streak tooltip `2929:18871`.
- Reference exports: `docs/design-qa/assets/home-figma-2929-17193/2026-09-07/fillsa-home-default.png`, `fillsa-home-calendar-open.png`, `fillsa-home-question-flow.png`, `fillsa-home-streak-tooltip.png`, and `fillsa-home-image-flow.png`.
- Device: Android Emulator `Medium_Phone_API_35` (`emulator-5554`), API 35, Korean locale. Physical size is 1080 × 2400 at 420 dpi; `adb shell wm size 945x2155` produces the required logical 360 × 821dp viewport (`945 / 2.625`, `2155 / 2.625`).
- Method: compare each stored Figma full-frame export to the matching clean emulator full frame at the same logical viewport. No image was cropped or rescaled for acceptance: runtime bounds are `[0,945) × [0,2155)` and the Figma exports are their full 360 × 821 root frames. Review covers the root/status area, Home header/week strip, quote/actions, question/CTA, bottom navigation, and any state overlay within those bounds. A system keyboard or login dialog is evidence only when it is actually visible in that full frame.

## Component inventory

| Component | Figma nodes | Implementation | Final evidence |
|---|---|---|---|
| Header/streak | `2929:15476`, `2929:18871` | `FigmaHomeContent` | Positive header and genuine-zero warning/tooltip captured. Unknown/null gating is unit-covered. |
| Month calendar/week strip | `2929:15667`, `2929:16221`, `3139:1501`, `3204:2435` | `HomeDateWeekSection`, `HomeInlineCalendar`, `HomeViewModel` | Open and selected-close full frames captured. Real completed-writing marker unavailable. |
| Quote/action row | `2929:13642`, `2929:15503` | `HomeQuoteCard`, `HomeQuoteActionRow` | Stable fixture quote/action row captured; authenticated image state unavailable. |
| Prompt response | `3087:29376`, `3139:1399`, `3087:29378`, `3110:34293` | `HomePromptAnswerSection`, `HomeViewModel` | Done and snackbar frames captured; full IME keyboard unavailable. |
| Dark appearance | `3039:26518` | `HomeColorPalette`, night assets | Settled positive-streak full frame captured. |
| Shared navigation/ad | `3087:29254`, unavailable ad ref `2929:29249` | app `BottomNavigationBar`, live ad | Android 4-route/live-ad contract remains different; no matching Figma evidence. |

## Deterministic runtime fixture

Final clean state evidence uses scoped debug intent extras (`HOME_QA_FIXTURE`, `HOME_QA_STREAK`, `HOME_QA_DARK_MODE`, `HOME_QA_HIDE_AD`, and, for the recorded state, `HOME_QA_RECORDED_ANSWER`). `BuildConfig.DEBUG` gates every extra, so release ignores them. The fixture supplies stable quote/author and confirmed streak, hides the debug AdMob validator, and suppresses a live error dialog only while active. The recorded-answer fixture holds the otherwise short snackbar only in debug so Android's splash transition cannot consume it before capture. It changes no release behavior or data contract.

## Validation rounds

| Round | Observed difference or limit | Change / evidence | Result |
|---|---|---|---|
| 1 — interaction implementation | The month CTA previously navigated to Calendar and the question CTA entered Typing rather than retaining Home state. | Implemented inline calendar toggle/select-close and session-local question record/edit with a Home snackbar. | Behavior covered by state tests and later runtime frames. |
| 2 — review binding fixes | Null/unknown streak could be treated as zero; done CTA did not match `3087:29378`; date and selected-marker contracts lacked coverage. | Gated zero UI to loaded `0`, applied `#D3D5FF`/`#5C65FF`, centralized today bounds, and added interaction-state regression tests. | Covered by unit tests; zero and done visual states later captured. |
| 3 — first runtime pass | Live server error and debug AdMob validator obstructed Home and snackbar/dark comparison. | No production workaround; documented blocked live attempt. | Historical captures discarded; no acceptance result claimed from them. |
| 4 — exact-size deterministic capture | The required viewport and known-zero state were otherwise not reproducible; Figma zero state used a warning control rather than flame plus `0일`. | Set 945 × 2155 override; added debug-only stable fixture and replaced confirmed-zero header with outlined warning control. | Clean light, zero, tooltip, calendar, done, snackbar, and dark candidates captured. |
| 5 — final reviewer correction | Earlier dark candidate was visually unsettled (missing month/selected numeral/CTA in reviewer inspection). Keyboard, authenticated image, real completion marker, live ad, and 4-tab parity remain unavailable. | Relaunched dark positive fixture, waited for UI tree to contain `100일`, `2026.09`, `오늘의 질문`, and `내 답변 기록하기`, then recaptured and visually inspected. | Dark frame replaced; final assembled-screen status remains **Blocked**. |

## Verified exact-size evidence

All retained files are clean 945 × 2155px / logical 360 × 821dp full frames under `docs/design-qa/assets/home-figma-2929-17193/2026-09-07/`. Superseded validator, splash, launcher, server-error, and non-target captures were removed.

| Scope | Evidence | Verified result |
|---|---|---|
| Light default, positive streak | `runtime-android-360-default-clean.png` | Baseline Home with confirmed 100-day flame/count. |
| Light default, confirmed zero | `runtime-android-360-default-zero-clean.png` | Loaded zero uses the purple outlined warning control; unknown/null does not enable tooltip. |
| Zero tooltip | `runtime-android-360-zero-tooltip-clean.png` | Known-zero tooltip visual; its Calendar link is the only navigation action. |
| Inline calendar open / selected | `runtime-android-360-calendar-open-clean.png`, `runtime-android-360-calendar-selected-clean.png` | Popup opens on Home; eligible selection closes it and refreshes Home week/date. |
| Dark default | `runtime-android-360-dark-clean.png` | Settled positive 100-day state with month, selected numeral, question, and record CTA present. |
| Question done / completion snackbar | `runtime-android-360-question-done-clean.png`, `runtime-android-360-question-done-snackbar-clean.png` | Done CTA uses lavender/primary colours; snackbar shows the dark rounded surface, green check, and `답변을 기록했어요.`. |
| Question focus | `runtime-android-360-question-focus-clean.png` | Input focus/count is visible, but not full-keyboard parity. |

## Interaction and regression verification boundary

- State-level regression tests directly cover calendar toggle/select state, confirmed-zero tooltip gating and Calendar destination, recorded-answer snackbar message, and zero warning header selection.
- This review does **not** instantiate `HomeViewModel` actions/effects directly: its constructor depends on the production Hilt/use-case graph and the existing tests deliberately exercise the extracted pure interaction contracts. This is a reviewer-minor coverage boundary, not evidence that an action/effect path was runtime-proven beyond the interactions listed above.

## Unavailable or blocking evidence

- Full soft keyboard: after `adb shell settings put secure show_ime_with_hard_keyboard 1`, the emulator still showed only the IME side toolbar. `runtime-android-360-question-focus-clean.png` is therefore not an exact keyboard comparison.
- Authenticated image/login: the fixture suppresses live failure/login dialogs to keep Home frames clean; no authenticated image data was available for a clean exact-size state.
- Completion data: no real completed-writing dataset was available, so the completed weekday marker has state-test coverage but no runtime proof.
- Live ad and four-tab parity: Android retains the shared 4-route navigation/live-ad contract whereas Figma presents a 3-tab/static-ad composition. This Home scope cannot make a full-frame comparison pass without product-direction change.

## Final assembled-screen result

**Blocked.** Multiple component states have verified clean evidence, but the full assembled screen cannot be accepted while the exact keyboard, authenticated-image, real completion-data, and shared live-ad/4-tab evidence remains unavailable or mismatched. Components may be partial; this screen-level result is not Partial or Pass.

## Task 6 — API/UI integration revalidation

The final integration build was relaunched with the existing debug-only Home QA fixture after the fixture had been routed through `MemberWeeklyQuoteResponse` → `HomeMemberQuoteWindow.from` → `toDailyQuoteDto` and the same `homeMemberWeekDayStates` projection used by production. This verifies the API mapper reaches the already-accepted Home UI without introducing fixture-only quote, question, answer, or completion rendering.

Full-frame comparison used the same logical 360 × 821dp target and the existing full Figma references listed above. Every runtime file is an uncropped 945 × 2155px capture:

| State | Runtime evidence | SHA-256 | Result |
|---|---|---|---|
| Light default | `assets/home-figma-2929-13556/2026-09-08-integration/runtime-light-default.png` | `0e94797f3810e5998eac64de6a65dedcc782e9ccfa50fa5969d1287b3b5a02c5` | Home-owned mapped quote/week/question state revalidated |
| Light recorded answer | `assets/home-figma-2929-13556/2026-09-08-integration/runtime-light-recorded-answer.png` | `be7c8ed76440e2dd76c53fb08e4ef85c71b593908665a9ecfb28e647b34cf593` | Mapped recorded answer and success snackbar revalidated |
| Dark default | `assets/home-figma-2929-13556/2026-09-08-integration/runtime-dark-default.png` | `9d4a18e663d23db409d695cafe37b0952b4d97a2c62ec49971c5224f19c59d07` | Home-owned mapped state revalidated in dark appearance |
| Dark recorded answer | `assets/home-figma-2929-13556/2026-09-08-integration/runtime-dark-recorded-answer.png` | `fd0fc6ec6855ae2744280de12ad9e8028a5df3d7567c3d9f1ef6ea38eb682878` | Mapped recorded answer and snackbar revalidated in dark appearance |

All four retained frames were captured after the UI settled and visually compared at full bounds against the light/dark and question-flow references. No Home geometry, copy, colour, hierarchy, or asset was changed in Task 6. The Home-owned mapper result is revalidated, while the assembled-screen result remains **Blocked** for the same documented acceptance boundaries: exact IME evidence, authenticated live image/completion data, shared four-tab navigation versus Figma's three tabs, and the live/static ad difference.
