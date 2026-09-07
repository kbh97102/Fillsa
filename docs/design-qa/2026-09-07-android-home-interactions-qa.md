# Android Home interactions Figma UI QA

## Reference and target

- Figma: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556
- Target nodes: parent `2929:17193`; light `2929:13556`; dark `3039:26518`; calendar open `3139:1501`; question focus `3139:1399`; question done `3087:29378`; completion snackbar `3110:34293`; zero-streak tooltip `2929:18871`.
- Reference exports: `docs/design-qa/assets/home-figma-2929-17193/2026-09-07/fillsa-home-default.png`, `fillsa-home-calendar-open.png`, `fillsa-home-question-flow.png`, `fillsa-home-streak-tooltip.png`, and `fillsa-home-image-flow.png`.
- Device: Android Emulator `Medium_Phone_API_35` (`emulator-5554`), API 35, Korean locale. Physical size is 1080 × 2400 at 420 dpi; `adb shell wm size 945x2155` produced the required logical 360 × 821dp viewport (`945 / 2.625`, `2155 / 2.625`).

## Deterministic runtime fixture

The final clean evidence was captured only with scoped debug intent extras (`HOME_QA_FIXTURE`, `HOME_QA_STREAK`, `HOME_QA_DARK_MODE`, `HOME_QA_HIDE_AD`, and, for the recorded state, `HOME_QA_RECORDED_ANSWER`). `BuildConfig.DEBUG` gates every extra: release ignores them. The fixture supplies a stable quote/author and confirmed streak; it hides the debug AdMob validator and suppresses a live error dialog only while active. The recorded-answer fixture holds its snackbar only in debug so Android's splash transition cannot consume its short production duration before capture. It changes no release behavior or data contract.

## Final exact-size evidence

All files below are clean 945 × 2155 px / logical 360 × 821dp frames under `docs/design-qa/assets/home-figma-2929-17193/2026-09-07/`. Superseded validator/error/non-target captures were removed.

| Scope | Evidence | Result |
|---|---|---|
| Light default, positive streak | `runtime-android-360-default-clean.png` | Pass for baseline Home. Confirmed 100-day fixture retains flame/count. |
| Light default, confirmed zero | `runtime-android-360-default-zero-clean.png` | Pass. A genuine loaded zero replaces flame/count with the purple outlined warning control; unknown/null retains the ordinary header and cannot open the tooltip. |
| Zero tooltip | `runtime-android-360-zero-tooltip-clean.png` | Pass for known-zero tooltip visual. The Calendar link is the only navigation action; it was exercised separately and navigated to the existing Calendar route. |
| Inline calendar open | `runtime-android-360-calendar-open-clean.png` | Pass for Home-local inline popup opening; no direct Calendar navigation occurs. |
| Inline calendar selection | `runtime-android-360-calendar-selected-clean.png` | Pass. Selecting an eligible date closes the popup and refreshes the selected day/week strip. |
| Dark default | `runtime-android-360-dark-clean.png` | Pass for dark baseline using confirmed positive (100-day) streak, matching Figma default-state semantics. |
| Question done/edit | `runtime-android-360-question-done-clean.png` | Pass for `3087:29378`: lavender `#D3D5FF` CTA, primary `#5C65FF` icon/text, and Home-local edit state. |
| Question done + completion snackbar | `runtime-android-360-question-done-snackbar-clean.png` | Pass for `3110:34293`: recorded/edit state with the dark rounded snackbar, green check, and `답변을 기록했어요.`. The visual is held by the debug-only fixture; production continues to use the normal short duration. |
| Question focus | `runtime-android-360-question-focus-clean.png` | Partial. Focus and character count are visible, but the emulator displayed only the IME side toolbar—not Figma's full soft keyboard—even after `show_ime_with_hard_keyboard=1`. This is not claimed as an exact visual pass. |

## Interaction and regression verification

- Month trigger toggles the inline calendar. Valid selection closes it and refreshes Home; invalid/future dates remain unavailable.
- The tooltip is available only for a loaded real zero. Outside dismissal stays on Home; its link alone routes to Calendar.
- The question record/edit action never routes to Typing. Unit regression coverage verifies the emitted snackbar text is `답변을 기록했어요.` and the selected completed-day marker precedence.

## Deliberate limits and remaining differences

- The existing app-wide Android four-route bottom navigation and live-ad product contract differ from Figma's three-tab/static-ad composition; this Home scope does not change that shared product decision.
- The debug fixture intentionally suppresses live error/login dialogs. Therefore no clean exact-size authenticated image/login frame is included; existing image behavior was preserved but authenticated image data is unavailable.
- No genuine completed-writing dataset was available for a production runtime marker; selected-marker precedence is covered by regression tests.

## Final result

**Partial / evidence-backed.** The exact-size default, known-zero/tooltip, inline calendar, selection, dark positive default, question done, and completion snackbar are cleanly captured and compared. Full soft-keyboard, authenticated image flow, and the shared navigation/ad discrepancy remain open and are not labelled as visual passes.
