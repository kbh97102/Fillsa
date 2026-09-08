# Home question answer integration QA

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556
- Target frame: `2929:13556` (Home, light); component nodes `3087:29376`, `3139:1399`, `3087:29378`, toast `3110:34293`.
- Full-frame reference: `assets/home-figma-2929-13556/reference-full.png` (360 × 821 px), includes the original full Figma frame.
- Further state references: `assets/home-figma-2929-17193/2026-09-07/`.
- Runtime target: existing Home exact-size target, logical 360 × 821dp (945 × 2155px, 420dpi), Korean locale, light and dark.

## Component inventory

| Component | Figma node | Target state | Result |
|---|---|---|---|
| HomePromptAnswerSection | `3087:29376`, `3139:1399`, `3087:29378` | default and saved mapped question/answer | Light/dark default and recorded-answer frames captured through the production mapper |
| Existing answer snackbar | `3110:34293` | after member POST success, guest session save | Success-only reducer policy unit-tested; recorded fixture snackbar captured in light/dark |

## Validation evidence

- Real production coordinator tests cover blank/200/201 graphemes, duplicate save, POST failure, success-normalized answer/time, best-effort daily failure, original date/cache targeting, account generation changes, refresh sequence/revision rejection, and guest-only session behavior.
- No Compose layout, color, copy, hierarchy, asset, or animation change. Existing LocaleType question binding is reused.
- POST patch changes only answer/answeredAt; it does not mark completion or increase streak. A valid daily response reconciles the server day and produces no streak effect.
- Reference screenshot and prior Home QA remain authoritative. Unit tests and code inspection are non-final evidence and do not count as a Figma comparison round.
- Task 6 coroutine-level `HomeViewModel` tests additionally verify counted member weekly/daily/POST invocation order, Calendar target anchoring, account-generation reset, logout/guest isolation, and silent daily reconciliation failure.
- Runtime fixture data is constructed as `MemberWeeklyQuoteResponse`/`MemberQuoteDay` and passed through `HomeMemberQuoteWindow.from`, `toDailyQuoteDto`, and `homeMemberWeekDayStates`; it does not bypass the production API-to-presentation mapping.

## Final assembled-screen result

- Final runtime captures: `assets/home-figma-2929-13556/2026-09-08-integration/runtime-light-default.png`, `runtime-light-recorded-answer.png`, `runtime-dark-default.png`, and `runtime-dark-recorded-answer.png` (all full 945 × 2155px frames).
- Comparison: full-frame visual inspection against the existing Home light, dark, and question-flow Figma exports. Home-owned mapped content introduced no visual regression.
- Result: **Blocked** at assembled-screen level. Exact IME/authenticated image/real completion evidence and the existing shared bottom-navigation/live-ad differences remain unavailable or mismatched; no blanket visual Pass is claimed.
