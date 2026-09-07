# Android Home Dark Root Figma UI QA

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X/%E2%9C%92%EF%B8%8F%ED%95%84%EC%82%AC?node-id=2929-9603
- Root inventory: `2929:9603` (`2. home[Dark]`)
- Full-frame render targets: `3039:26518`, `3139:1753`, `3039:26778`, `3039:26996`, `3039:27295`, `3139:910`, `3136:1198`, `3139:1061`, `3139:1238`, `3223:5985`, `3223:6126`, `3223:6435`, `3223:6600`, `3223:6912`
- Linked route targets: `3087:28815`, `2929:9764`, `2929:9801`, `2929:9844`, `2929:9884`, `2929:9931`, `2929:10788`, `2929:10884`, `2929:10850`
- Reference images: `docs/design-qa/assets/home-figma-2929-9603/2026-09-07/figma-dark-*.png`
- Runtime target: Medium Phone API 35 Android Emulator, logical 360×821dp, density 160, dark appearance, Korean input
- Comparison method: individual render-node design context, full-frame visual inspection, and measured component geometry

## Runtime evidence

All primary captures are 360×821. The 360×720 suffixed captures are supplemental direct comparisons for linked Figma frames authored at 360×720.

- Home: `runtime-android-dark-home-default.png`, `runtime-android-dark-home-calendar-open.png`, `runtime-android-dark-home-streak-tooltip.png`
- Action and question states: `runtime-android-dark-home-copy-toast.png`, `runtime-android-dark-home-selected.png`, `runtime-android-dark-home-recorded-toast.png`, `runtime-android-dark-question-focus-ime.png`
- Image states: `runtime-android-dark-image-preview.png`, `runtime-android-dark-image-template.png`, `runtime-android-dark-image-delete-confirmation.png`
- Linked routes: `runtime-android-dark-typing-ime.png`, `runtime-android-dark-login-modal.png`, `runtime-android-dark-login-full.png`, `runtime-android-dark-share-first.png`, `runtime-android-dark-share-carousel.png`
- Supplemental linked-route crops: `runtime-android-dark-typing-ime-360x720.png`, `runtime-android-dark-login-full-360x720.png`, `runtime-android-dark-share-first-360x720.png`, `runtime-android-dark-share-carousel-360x720.png`
- Independent-review before/after: `runtime-review-question-focus-after.png`, `runtime-review-question-cta-clicked-after.png`, `runtime-review-common-dialog-font130-before.png`, `runtime-review-common-dialog-font130-after.png`, `runtime-review-image-dialog-font130-before.png`, `runtime-review-image-dialog-short-after.png`, `runtime-review-image-dialog-long-font130-after.png`
- Viewport/theme regressions: `runtime-review-share-controls-after-360x720.png`, `runtime-review-share-controls-after-360x821.png`, `runtime-review-light-home-after.png`, `runtime-review-light-calendar-after.png`, `runtime-review-light-login-dialog-after.png`, `runtime-review-light-typing-before.png`, `runtime-review-light-typing-after.png`, `runtime-review-dark-typing-after.png`
- Template action historical pair: `runtime-review-image-template-delete-after.png`, `runtime-review-image-template-delete-dismissed-after.png`. This pair brackets preview/dismissed states but does not by itself prove a single-tap transition.
- Second-review ImageDialog: `runtime-review2-image-long-font150-before.png`, failed `runtime-review2-image-short-after.png`, and final `runtime-review3-image-short-font100-after.png`, `runtime-review3-image-short-confirmed.png`, `runtime-review3-image-extreme-font150-top.png`, `runtime-review3-image-extreme-font150-bottom.png`, `runtime-review3-image-extreme-confirmed.png`.
- Second-review Share: `runtime-review2-share-font150-360x720-before.png`, `runtime-review2-share-font150-360x720-after.png`, `runtime-review2-share-dark-360x821-after.png`, `runtime-review2-share-light-360x821-after.png`.
- Template confirmation sequence: `runtime-review2-template-delete-before.png`, `runtime-review2-template-confirmation-after-one-tap.png`, `runtime-review2-template-safe-dismissed-after-confirm.png`.

## Component inventory

| Component/state family | Figma node | Runtime result |
|---|---|---|
| Home shell | `3039:26518` | Pass for Home-owned geometry, palette, quote, question, and action content. Global bottom navigation/ad composition remains Blocked below. |
| Inline calendar | `3139:1753`, `3039:28099`, `3039:28101` | Pass. Popup is 248×335 at x20/y126; selectors, 14sp dates, and selected/today markers match. Runtime keeps selected day 16 consistent with the Home header; the Figma open-calendar sample internally highlights 25. |
| Week strip | `3039:28627`, `3039:28629`, `3039:28631` | Pass. Aug 10/11 completed markers and Aug 16 selected/today state are present in the fixture. |
| Streak tooltip | `3039:26778` | Pass. 231×76 cream surface at x92/y85, 4dp radius, shadow, caret, title, and Calendar link match. |
| Copy/liked/image actions | `3039:26996`, `3039:27295` | Pass for app-owned UI. Registered thumbnail is 28×28 with yellow label; selected heart and label use `#FFCB5C`; snackbar width/gap match. API 35 additionally shows the native clipboard preview. |
| Question flow | `3139:1454`, `3139:1478`, `3139:1466`, `3139:910`, `3136:1198`, `3139:1061`, `3139:1238` | Pass after independent review. The fixed opaque header and available-space scroll body replace device constants. `mInputShown=true` and the full Gboard are recorded; the 174dp field and complete CTA remain above navigation/IME, and tapping the CTA reaches the recorded state. |
| Image flow | `3223:5985`, `3223:6126`, `3223:6435`, `3223:6600`, `3223:6912` | Pass in the captured short/font-1.0 and extreme/font-1.5 fixtures. The short surface is 320×373; in the extreme fixture the header/footer remain exposed while the bounded middle scroll reaches the wrapped author, and both states record successful confirm taps. The template delete sequence visibly opens confirmation after one tap, then uses an empty fixture callback. Production picker/delete contracts are unchanged. |
| Typing | `3087:28815` | Pass for active typing geometry and exact handwriting asset. Android Gboard is retained rather than reproducing the Figma iOS keyboard bitmap. API-backed success/outcome dialogs were code-audited but not invoked against production services during QA. |
| Login | `2929:9931`, `2929:10788` | Pass for modal and Android login route. Existing Kakao/Google providers are preserved; the Figma Apple row is intentionally not added to Android. |
| Share | `2929:10884`, `2929:10850`, template nodes | Pass in the captured dark 360×821/font-1.0, dark 360×720/font-1.5, and light 360×821/font-1.0 states. Intrinsic title/control measurement leaves a 270×377 compact card with all `저장`/`복사`/`카카오톡` labels visible; dark 360×821 remains 270×400, and light uses its uncapped baseline weighted pager. Existing save/copy/Kakao behavior is preserved. |
| Shared dialog | Home login/delete plus `WithBaseErrorHandling` callers | Pass for scoped Home geometry and content growth. Dark/font-1.0 Home uses explicit 181/165dp variants; light, font-1.3, and generic multiline callers remain content-driven. |
| Light regression | baseline `77af9fe` | Pass for captured Home, inline calendar, login dialog, and Typing. The dark handwriting asset/heading, dark landmark deltas, dark calendar metrics, and Home measured dialog are gated by appearance. |

## Validation rounds

### Round 1 — Home assembly

- Configured the API 35 emulator with `wm size 360x821`, density 160, dark UI mode, font scale 1, and disabled animation scales.
- The deterministic fixture renders Aug 16, 2026, the exact Figma quote/author, 100-day streak, and completed-day markers for Aug 10/11. Fixture-only upload/delete/like and Typing save/like/back-save callbacks are intercepted; production callbacks remain unchanged when the fixture is absent.
- Measured runtime landmarks match the render-node context: quote card y168/h150, action row y328/h42, divider y370, question group y389, answer field y438/h174, and CTA y637/h50.

### Round 2 — overlays and state variants

- Exercised inline calendar, zero-streak tooltip, copied snackbar, selected like/image action, recorded-answer snackbar, uploaded image preview, template preview, and delete confirmation.
- Corrected the login dialog button padding after the first capture exposed clipped `로그인 하기`; the final 320×181 modal shows the complete label.
- API 35's native clipboard preview coexists with the required app snackbar and is treated as an OS-owned difference.

### Round 3 — IME and linked routes

- The first focused-question capture exposed `requiredHeight(638.dp)`/`offset(-78.dp)` drawing through the header and hiding the CTA. The final implementation uses parent constraints and measured scroll range; `runtime-review-question-focus-after.png` records the full Gboard and complete CTA, and the paired post-tap frame proves clickability.
- The production bottom navigation remains above Gboard because the shared Scaffold owns it and the Figma focus frame retains navigation above its keyboard. The route-count mismatch remains the documented product blocker.
- Captured login modal/full route and the share guide/carousel. A signed-in emulator datastore was left intact; deterministic debug flags only select visual states and do not replace auth/domain/API contracts.

### Round 4 — independent review corrections

- Verified five single root-cause hypotheses before moving between issues: viewport constants, global dialog height, fixed Share pager, fixed ImageDialog height, and unconditional dark deltas.
- Captured CommonDialog and ImageDialog at font scale 1.3, Share at 360×720 and 360×821, and paired light/dark Typing. Before captures remain beside the after evidence rather than being re-labelled as Pass.
- Added post-implementation unit coverage for dialog mode selection, adaptive Share height, and dark-only Typing decoration.

### Round 5 — bounded text and intrinsic Share re-review

- Reproduced ImageDialog with a substantially longer fixture at font scale 1.5. The second hypothesis reserved controls and enabled scrolling, but its default `weight(fill=true)` failed the short-state gate by expanding the dialog to almost the full available height; `runtime-review2-image-short-after.png` remains labeled as failure evidence.
- The authorized third hypothesis changed only the middle viewport to `weight(fill=false)`. The final short capture is x20/y225/320×373 and clickable; the extreme capture scrolls to the wrapped author with the action footer fixed and clickable.
- Removed Share's fixed 51dp/77dp title/control reservations and 320dp pager minimum. Title and controls now measure intrinsically, dark receives only a 481dp pager cap, and light retains baseline `weight(1f)` sizing. Runtime coverage includes compact dark font 1.5, tall dark, and tall light.
- Replaced the ambiguous template before/dismissed inference with a three-frame sequence that visibly records confirmation after one delete tap and the subsequent safe fixture dismissal.

## Verification

- `./gradlew :presentation:testDebugUnitTest :presentation:assembleDebug --console=plain`: BUILD SUCCESSFUL in 3s; 36 tests, 0 skipped/failures/errors.
- `./gradlew :app:assembleDebug --console=plain`: BUILD SUCCESSFUL.
- Final emulator matrix includes logical 360×821/density 160 at dark font 1.0 and ImageDialog dark font 1.5, Share dark 360×720/font 1.5, and Share light 360×821/font 1.0. The emulator was returned to 360×821/font 1.0/dark after capture.
- `git diff --check`: no output.

## Final assembled-screen result

- Result: **Blocked** for final product-level parity, while the Android-owned Home dark implementation and linked routes pass the documented component checks.
- Blocker: Figma specifies a 3-tab bottom navigation plus static AD placeholder, while the shared production app contract specifies 4 routes plus live-ad behavior. This batch intentionally preserves the production contract; resolving the assembled-screen mismatch requires an explicit product decision.
- Accepted platform differences: Android status/navigation bars, Gboard versus the Figma iOS keyboard bitmap, API 35 clipboard preview, and Android Kakao/Google login providers versus the Figma Apple/Kakao/Google composition.
