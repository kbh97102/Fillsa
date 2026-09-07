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

## Component inventory

| Component/state family | Figma node | Runtime result |
|---|---|---|
| Home shell | `3039:26518` | Pass for Home-owned geometry, palette, quote, question, and action content. Global bottom navigation/ad composition remains Blocked below. |
| Inline calendar | `3139:1753`, `3039:28099`, `3039:28101` | Pass. Popup is 248×335 at x20/y126; selectors, 14sp dates, and selected/today markers match. Runtime keeps selected day 16 consistent with the Home header; the Figma open-calendar sample internally highlights 25. |
| Week strip | `3039:28627`, `3039:28629`, `3039:28631` | Pass. Aug 10/11 completed markers and Aug 16 selected/today state are present in the fixture. |
| Streak tooltip | `3039:26778` | Pass. 231×76 cream surface at x92/y85, 4dp radius, shadow, caret, title, and Calendar link match. |
| Copy/liked/image actions | `3039:26996`, `3039:27295` | Pass for app-owned UI. Registered thumbnail is 28×28 with yellow label; selected heart and label use `#FFCB5C`; snackbar width/gap match. API 35 additionally shows the native clipboard preview. |
| Question flow | `3139:1454`, `3139:1478`, `3139:1466`, `3139:910`, `3136:1198`, `3139:1061`, `3139:1238` | Pass for before/focus/snackbar/done UI-session states. Focus uses a purple border and an IME-aware -209dp effective body shift; answer field remains 174dp instead of collapsing under `adjustResize`. |
| Image flow | `3223:5985`, `3223:6126`, `3223:6435`, `3223:6600`, `3223:6912` | Pass for unregistered/registered actions, uploaded preview, template preview, and delete confirmation. Existing picker/delete contracts are unchanged. |
| Typing | `3087:28815` | Pass for active typing geometry and exact handwriting asset. Android Gboard is retained rather than reproducing the Figma iOS keyboard bitmap. API-backed success/outcome dialogs were code-audited but not invoked against production services during QA. |
| Login | `2929:9931`, `2929:10788` | Pass for modal and Android login route. Existing Kakao/Google providers are preserved; the Figma Apple row is intentionally not added to Android. |
| Share | `2929:10884`, `2929:10850`, template nodes | Pass. Card x45/y168 at 270×400, first-load guide, carousel paging, and dark 48dp action circles match while existing save/copy/Kakao behavior is preserved. |

## Validation rounds

### Round 1 — Home assembly

- Configured the API 35 emulator with `wm size 360x821`, density 160, dark UI mode, font scale 1, and disabled animation scales.
- The deterministic fixture renders Aug 16, 2026, the exact Figma quote/author, 100-day streak, and completed-day markers for Aug 10/11 without issuing Home API writes.
- Measured runtime landmarks match the render-node context: quote card y168/h150, action row y328/h42, divider y370, question group y389, answer field y438/h174, and CTA y637/h50.

### Round 2 — overlays and state variants

- Exercised inline calendar, zero-streak tooltip, copied snackbar, selected like/image action, recorded-answer snackbar, uploaded image preview, template preview, and delete confirmation.
- Corrected the login dialog button padding after the first capture exposed clipped `로그인 하기`; the final 320×181 modal shows the complete label.
- API 35's native clipboard preview coexists with the required app snackbar and is treated as an OS-owned difference.

### Round 3 — IME and linked routes

- The first focused-question capture exposed `adjustResize` squeezing the answer field. The final implementation keeps the 174dp field and aligns action row/question landmarks to the `3136:1198` focus composition while the app header stays fixed.
- Captured full Android Gboard states for Home question and Typing. The keyboard top/keys differ from the Figma iOS keyboard asset by platform, while app-owned content above it is aligned.
- Captured login modal/full route and the share guide/carousel. A signed-in emulator datastore was left intact; deterministic debug flags only select visual states and do not replace auth/domain/API contracts.

## Verification

- `./gradlew :presentation:testDebugUnitTest :presentation:assembleDebug --console=plain`: BUILD SUCCESSFUL; 29 tests, 0 failures/errors.
- `./gradlew :app:assembleDebug --console=plain`: BUILD SUCCESSFUL.
- `git diff --check`: no output.

## Final assembled-screen result

- Result: **Blocked** for final product-level parity, while the Android-owned Home dark implementation and linked routes pass the documented component checks.
- Blocker: Figma specifies a 3-tab bottom navigation plus static AD placeholder, while the shared production app contract specifies 4 routes plus live-ad behavior. This batch intentionally preserves the production contract; resolving the assembled-screen mismatch requires an explicit product decision.
- Accepted platform differences: Android status/navigation bars, Gboard versus the Figma iOS keyboard bitmap, API 35 clipboard preview, and Android Kakao/Google login providers versus the Figma Apple/Kakao/Google composition.
