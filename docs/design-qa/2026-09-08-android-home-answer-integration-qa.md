# Home question answer integration QA

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556
- Target frame: `2929:13556` (Home, light); component nodes `3087:29376`, `3139:1399`, `3087:29378`, toast `3110:34293`.
- Full-frame reference: `assets/home-figma-2929-13556/reference-full.png` (360 × 821 px), includes the original full Figma frame.
- Further state references: `assets/home-figma-2929-17193/2026-09-07/`.
- Runtime target: existing Home exact-size target, logical 360 × 821dp (945 × 2155px, 420dpi), Korean light. This worker did not capture a new runtime frame; emulator/API confirmation and assembled capture belong to the parent validation round.

## Component inventory

| Component | Figma node | Target state | Result |
|---|---|---|---|
| HomePromptAnswerSection | `3087:29376`, `3139:1399`, `3087:29378` | default, focus, saved; real localized question/answer | Runtime comparison pending; code changes limited to saving readOnly/click guard |
| Existing answer snackbar | `3110:34293` | after member POST success, guest session save | Production reducer verifies success-only emission; runtime comparison pending |

## Validation evidence

- Real production coordinator tests cover blank/200/201 graphemes, duplicate save, POST failure, success-normalized answer/time, best-effort daily failure, original date/cache targeting, account generation changes, refresh sequence/revision rejection, and guest-only session behavior.
- No Compose layout, color, copy, hierarchy, asset, or animation change. Existing LocaleType question binding is reused.
- POST patch changes only answer/answeredAt; it does not mark completion or increase streak. A valid daily response reconciles the server day and produces no streak effect.
- Reference screenshot and prior Home QA remain authoritative. Unit tests and code inspection are non-final evidence and do not count as a Figma comparison round.

## Final assembled-screen result

- Final runtime capture: pending parent worker.
- Comparison: not performed by this component worker.
- Result: Blocked pending assembled runtime/Figma validation; no final Pass claimed.
- Existing app-wide bottom-navigation/live-ad differences remain tracked in the screen's earlier QA.
