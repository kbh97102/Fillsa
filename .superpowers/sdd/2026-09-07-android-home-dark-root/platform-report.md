# Android platform report — Home dark root

Date: 2026-09-07
Worktree: `/Users/gangbohun/AndroidStudioProjects/Fillsa/.worktrees/home-dark-root`
Branch: `codex/home-dark-root`
Documentation baseline: `77af9fe`

## Outcome

All six Android briefs were implemented as one Home/platform integration batch, then corrected through two scoped independent-review rounds. Home-owned dark surfaces and linked image, Typing, login, and share routes were exercised on an Android API 35 emulator at 360×821 and the required compact 360×720 Share viewport. The documented Android component checks pass within their captured states. Product-level assembled-screen acceptance remains **Blocked** by the pre-existing discrepancy between Figma's 3-tab/static-ad composition and the production app's shared 4-route/live-ad contract; expected OS/provider differences and production-only outcome flows are separately listed as concerns rather than hidden by that blocker.

## Figma render nodes read

Every item below was read with individual `get_design_context` calls using `clientLanguages="kotlin"`, `clientFrameworks="compose"`, and `skillNames="figma-design-to-code"`; root metadata was used only as the inventory.

- Home/calendar/week: `3039:26518`, `3139:1753`, `3039:28099`, `3039:28101`, `3039:28627`, `3039:28629`, `3039:28631`
- Tooltip/copy/selected actions: `3039:26778`, `3039:26996`, `3039:27295`
- Question: `3139:1454`, `3139:1478`, `3139:1466`, `3139:910`, `3136:1198`, `3139:1061`, `3139:1238`
- Image: `3223:5985`, `3223:6126`, `3223:6435`, `3223:6600`, `3223:6912`
- Typing and outcomes: `3087:28815`, `2929:9764`, `2929:9801`, `2929:9844`, `2929:9884`, `2929:10996`, `3025:24578`, `3025:24618`
- Login/share/templates: `2929:9931`, `2929:10788`, `2929:10884`, `2929:10850`, `2929:10923`, `2929:10927`, `2929:10931`, `2929:10936`, `2929:10941`, `2929:10945`, `2929:10949`, `2929:10954`, `2929:10963`, `2929:10968`

## Implementation decisions

- Reused `FigmaHomeContent`, `HomeView`, existing theme typography/colors, existing navigation destinations, `HomeViewModel` actions/effects, image use cases, and auth/share integrations. No dark-only reducer, persistence model, route, API, or provider was added.
- Aligned the Home landmarks to the render nodes: quote y168/h150, actions y328/h42, divider y370, question y389, field y438/h174, CTA y637/h50.
- Implemented selected like/image presentation using the existing loaded background URL. The registered action renders a 28dp rounded thumbnail plus yellow `이미지 보기`; like icon and label use `#FFCB5C`.
- Rebuilt the zero-streak tooltip as the measured 231×76 cream callout at x92/y85, including shadow, 21×18 caret, and linked Calendar text.
- Refined the inline calendar to 248×335 at x20/y126 with 14sp dates/weekdays and 65/61dp selectors. The runtime fixture keeps Aug 16 selected because the Figma open state internally conflicts with its own Aug 16 header by selecting Aug 25.
- Replaced the question-focus `offset(-78.dp)`/`requiredHeight(638.dp)` workaround with an opaque fixed header and a parent-constrained scroll body. IME visibility scrolls to the body's measured maximum; full Gboard, the 174dp field, complete CTA, and a successful CTA tap were captured at 360×821.
- Kept question answers in existing UI-session state. Copy always emits the required app snackbar; Android 13+ may additionally render its OS clipboard preview.
- Restored content-driven sizing as the shared `CommonDialog` default. Exact 181/165dp geometry is an explicit `HomeDarkMeasured` mode enabled only for dark Home at font scale 1.0; light, accessibility font scale, and multiline error callers grow intrinsically.
- Restored intrinsic sibling measurement in Share: title and controls measure first, the dark pager receives the remaining constraint with a 481dp maximum, and light keeps its baseline weighted pager without that cap. The 360×821 dark card stays 270×400; at 360×720/font scale 1.5 the 270×377 card leaves all three labels visible.
- Reworked dark `ImageDialog` so unweighted header/actions reserve their height and the middle text uses `weight(fill=false)` plus bounded scrolling. Runtime proves the short font-1.0 surface remains 320×373 and an extreme font-1.5 fixture scrolls through the wrapped author while both buttons remain exposed and clickable.
- Added the exact Figma Typing handwriting SVG and aligned the dark Typing/share/login geometry. Explicit `darkMode` branches cover the Typing ornament/heading, Login gaps, Home focus/landmarks, inline-calendar metrics, and measured Home dialog; captured light Home/calendar/login/Typing references provide the scoped regression evidence.
- Debug-only intent extras provide deterministic QA states. Fixture mode intercepts Home upload/delete/like and Typing save/like/back-save mutation callbacks; production callbacks are unchanged when the fixture is absent.
- The template fixture now renders Figma's delete affordance. A delete tap dismisses the preview, waits one Compose frame, and opens the measured confirmation; confirming runs an empty fixture callback and closes the dialog. This does not call the production uploaded-image deletion API.
- Preserved the shared 4-route bottom bar and live-ad behavior. The Figma 3-tab/static-ad discrepancy is intentionally recorded as Blocked, not papered over in Home code.

## Files changed

### Runtime implementation and tests

- `presentation/src/main/java/com/arakene/presentation/MainActivity.kt`
- `presentation/src/main/java/com/arakene/presentation/ui/LoginView.kt`
- `presentation/src/main/java/com/arakene/presentation/ui/common/CommonDialog.kt`
- `presentation/src/main/java/com/arakene/presentation/ui/common/NegativeButton.kt`
- `presentation/src/main/java/com/arakene/presentation/ui/common/PositiveButton.kt`
- `presentation/src/main/java/com/arakene/presentation/ui/home/FigmaHomeContent.kt`
- `presentation/src/main/java/com/arakene/presentation/ui/home/HomeInlineCalendar.kt`
- `presentation/src/main/java/com/arakene/presentation/ui/home/HomeView.kt`
- `presentation/src/main/java/com/arakene/presentation/ui/home/ImageDialog.kt`
- `presentation/src/main/java/com/arakene/presentation/ui/home/ShareView.kt`
- `presentation/src/main/java/com/arakene/presentation/ui/home/TypingQuoteBodySection.kt`
- `presentation/src/main/java/com/arakene/presentation/ui/home/TypingQuoteView.kt`
- `presentation/src/main/java/com/arakene/presentation/util/ComposeExtension.kt`
- `presentation/src/main/java/com/arakene/presentation/util/DialogData.kt`
- `presentation/src/main/java/com/arakene/presentation/util/SnackbarContent.kt`
- `presentation/src/main/java/com/arakene/presentation/viewmodel/HomeViewModel.kt`
- `presentation/src/main/java/com/arakene/presentation/ui/common/DialogSection.kt`
- `presentation/src/test/java/com/arakene/presentation/ui/home/HomeLikeIconTest.kt`
- `presentation/src/test/java/com/arakene/presentation/ui/common/CommonDialogLayoutTest.kt`
- `presentation/src/test/java/com/arakene/presentation/ui/home/ImageDialogLayoutTest.kt`
- `presentation/src/test/java/com/arakene/presentation/ui/home/ShareLayoutTest.kt`
- `presentation/src/test/java/com/arakene/presentation/ui/home/TypingDarkModeTest.kt`
- `presentation/src/main/assets/figma/typing/typing_handwriting_dark.svg`
- `presentation/src/main/res/drawable/home_registered_image_fixture.png`

### QA and specification

- `docs/design-qa/2026-09-07-android-home-dark-root-qa.md`
- `docs/screens/2_home_dark.md`
- `docs/design-qa/assets/home-figma-2929-9603/2026-09-07/runtime-android-dark-*.png` (19 runtime captures)
- `docs/design-qa/assets/home-figma-2929-9603/2026-09-07/runtime-review-*.png` (19 independent-review before/after captures)
- `docs/design-qa/assets/home-figma-2929-9603/2026-09-07/runtime-review2-*.png` (11 captures at 360×821 and 2 Share captures at 360×720)
- `docs/design-qa/assets/home-figma-2929-9603/2026-09-07/runtime-review3-*.png` (5 architecture-hypothesis captures at 360×821)
- `.superpowers/sdd/2026-09-07-android-home-dark-root/platform-report.md`
- `.superpowers/sdd/2026-09-07-android-home-dark-root/progress.md`

## Commands and exact results

- `git status --short && git branch --show-current`: started clean on `codex/home-dark-root`; baseline documentation commit was `77af9fe`.
- `curl -L <Figma asset URL> -o <temporary path>`: first sandbox attempt failed with DNS resolution; the policy-required escalated retry succeeded for both assets. `file` reported SVG text for the handwriting asset and a 1024×1024 PNG for the ocean image.
- Initial `./gradlew :app:assembleDebug --console=plain`: failed because the ignored worktree-local `app/google-services.json` was absent. The existing ignored file was copied from the parent checkout into this worktree; it was not staged or committed.
- Independent-review implementation check `./gradlew :presentation:testDebugUnitTest --console=plain`: `BUILD SUCCESSFUL in 2s`, 48 actionable tasks (10 executed, 38 up-to-date).
- Pre-capture APK `./gradlew :app:assembleDebug --console=plain`: `BUILD SUCCESSFUL in 4s`, 135 actionable tasks (11 executed, 124 up-to-date).
- Final `./gradlew :presentation:testDebugUnitTest :presentation:assembleDebug --console=plain`: `BUILD SUCCESSFUL in 1s`, 63 actionable tasks (3 executed, 60 up-to-date). XML results contain 34 tests, 0 skipped, 0 failures, 0 errors.
- Final `./gradlew :app:assembleDebug --console=plain`: `BUILD SUCCESSFUL in 883ms`, 135 actionable tasks (135 up-to-date).
- Second-review implementation build first failed in `:presentation:compileDebugKotlin` with unresolved `launch` and a resulting suspend-call diagnostic after the one-frame fixture sequence was added. Importing the existing `kotlinx.coroutines.launch` extension was the only correction; the immediate retry succeeded.
- Second-review targeted `./gradlew :presentation:testDebugUnitTest --tests 'com.arakene.presentation.ui.home.ImageDialogLayoutTest' --tests 'com.arakene.presentation.ui.home.ShareLayoutTest' --console=plain --quiet`: exit code 0; the only output was the existing google-services plugin warning.
- Second-review final `./gradlew :presentation:testDebugUnitTest :presentation:assembleDebug --console=plain`: `BUILD SUCCESSFUL in 3s`, 63 actionable tasks (6 executed, 57 up-to-date). XML results contain 36 tests, 0 skipped, 0 failures, 0 errors.
- Second-review final `./gradlew :app:assembleDebug --console=plain`: `BUILD SUCCESSFUL in 1s`, 135 actionable tasks (135 up-to-date).
- `adb install -r app/build/outputs/apk/debug/app-debug.apk`: `Success`.
- Emulator configuration used escalated ADB because sandbox daemon startup was denied: `wm size 360x821`, density 160, `cmd uimode night yes`, font scale 1, and animation scales 0. `adb shell wm size` reported physical 1080×2400 and override 360×821 at final capture time.
- Focused-question verification additionally reported `mCurMethodId=com.google.android.inputmethod.latin/com.android.inputmethod.latin.LatinIME` and `mInputShown=true`; resetting the test-emulator Gboard package restored the full keyboard after its one-handed toolbar state became stuck.
- `file docs/design-qa/assets/home-figma-2929-9603/2026-09-07/runtime-review-*.png`: 17 independent-review files report 360×821 PNG and 2 Share files report 360×720 PNG.
- `file .../runtime-review2-*.png .../runtime-review3-*.png`: second/third-round evidence reports 16 files at 360×821 and 2 Share files at 360×720.
- Final `adb shell wm size`, `wm density`, font-scale, and UI-mode reads: override `360x821`, override density `160`, font scale `1.0`, `Night mode: yes`.
- `git diff --check`: no output before commits.

## Runtime captures and comparison findings

Capture directory: `docs/design-qa/assets/home-figma-2929-9603/2026-09-07/`

- `runtime-android-dark-home-default.png`: Home-owned content aligns to the default node landmarks; production 4-route bar is visible instead of the Figma 3-tab/static-ad area.
- `runtime-android-dark-home-calendar-open.png`: popup x20/y126, 248×335; Aug 10/11 completed, Aug 16 selected.
- `runtime-android-dark-home-streak-tooltip.png`: callout x92/y85, 231×76 with caret.
- `runtime-android-dark-home-copy-toast.png`: app snackbar matches; API 35 native clipboard overlay is extra OS UI.
- `runtime-android-dark-home-selected.png`: selected heart/label and exact ocean thumbnail appear.
- `runtime-android-dark-home-recorded-toast.png`: recorded answer/session snackbar state appears.
- `runtime-android-dark-question-focus-ime.png`: pre-review capture showing the original overlap/CTA-clipping defect; it is retained as before evidence, not a Pass.
- `runtime-review-question-focus-after.png`: full Gboard (`mInputShown=true`), fixed opaque header, complete 174dp field/counter/CTA, and no header/status overlap. The shared production bottom navigation remains above Gboard, consistent with Scaffold ownership and the Figma focus layout.
- `runtime-review-question-cta-clicked-after.png`: keyboard closes and CTA changes to `내 답변 수정하기`, proving the visible action is clickable.
- `runtime-android-dark-image-preview.png`: uploaded ocean preview, delete, quote/author, and controls align in a centered 320×373 modal.
- `runtime-android-dark-image-template.png`: template preview uses the existing gradient background and aligned controls.
- `runtime-review-image-template-delete-after.png` / `runtime-review-image-template-delete-dismissed-after.png`: retained historical before/dismissed frames; this pair alone does not prove a single-tap transition.
- `runtime-android-dark-image-delete-confirmation.png`: preview closes before the 320×165 confirmation; full labels fit.
- `runtime-android-dark-typing-ime.png`: full Android IME state plus exact Figma handwriting character/title and typed-prefix styling.
- `runtime-android-dark-login-modal.png`, `runtime-android-dark-login-full.png`: 320×181 prompt and full Android provider route align; the final login label is not clipped.
- `runtime-android-dark-share-first.png`, `runtime-android-dark-share-carousel.png`: 270×400 card at x45/y168, action circles at y598, first guide and paging verified.
- `runtime-review-common-dialog-font130-before.png` / `after.png`: before records the risky global cap; after shows long title/body/button complete at font scale 1.3 on the shared dialog path.
- `runtime-review-image-dialog-font130-before.png`, `runtime-review-image-dialog-short-after.png`, `runtime-review-image-dialog-long-font130-after.png`: controls change from clipped blank to complete while short geometry remains 320×373 and long/font-1.3 content grows.
- `runtime-review-share-controls-after-360x720.png` and `runtime-review-share-controls-after-360x821.png`: all three labels are visible at both heights; the compact card shrinks and the tall card remains 270×400.
- `runtime-review-light-typing-before.png` / `after.png` plus `runtime-review-dark-typing-after.png`: directly prove the ornament/heading leak was removed from light and retained in dark. `runtime-review-light-home-after.png`, `runtime-review-light-calendar-after.png`, and `runtime-review-light-login-dialog-after.png` cover the other gated metrics.
- `runtime-review2-image-short-after.png`: retained failed-hypothesis evidence; default `weight(fill=true)` expanded the short dialog to almost the available height.
- `runtime-review3-image-short-font100-after.png` / `runtime-review3-image-short-confirmed.png`: `weight(fill=false)` restores x20/y225/320×373, shows both buttons, and records the state after a successful confirm tap.
- `runtime-review3-image-extreme-font150-top.png` / `bottom.png` / `runtime-review3-image-extreme-confirmed.png`: at font scale 1.5, the bounded middle scroll reaches the wrapped author while the footer remains fixed; the final frame records a successful confirm tap.
- `runtime-review2-share-font150-360x720-before.png` / `after.png`: accessibility-scale compact comparison; the final intrinsic layout shows a 270×377 card and all three labels. `runtime-review2-share-dark-360x821-after.png` retains the dark 270×400 geometry, while `runtime-review2-share-light-360x821-after.png` records the uncapped light weighted layout.
- `runtime-review2-template-delete-before.png`, `runtime-review2-template-confirmation-after-one-tap.png`, and `runtime-review2-template-safe-dismissed-after-confirm.png`: the three-step sequence distinguishes the actionable template, the visible confirmation after one delete tap, and the safe fixture dismissal after confirmation.

## Commits

- `2ad2ccb` — `feat(android): align dark Home states with Figma`
- `1a9e24f` — `docs(android): record dark Home runtime parity`
- `038a7b0` — `docs(android): add dark Home platform report`
- `4ac2f4e` — `fix(android): address dark Home review findings`
- `641171c` — `docs(android): record dark Home review verification`
- `2723055` — `docs(android): finalize Home review report`
- `bd84a84` — `fix(android): harden adaptive Home dialogs and share`
- `64a8dc1` — `docs(android): record second Home review evidence`
- Platform report commit: the commit containing this file (listed in the parent handoff because a commit cannot include its own SHA).

## Concerns / blockers

1. **Blocked product decision:** Figma 3-tab/static AD versus shared app 4-route/live AD. No Android-only contract fork was introduced.
2. **Expected platform surfaces:** Android status/navigation bars, Gboard instead of the Figma iOS keyboard bitmap, and API 35 clipboard preview cannot be pixel-identical to those iOS-authored references.
3. **Provider contract:** Android intentionally remains Kakao/Google; the Figma full-login Apple row is not applicable without a product/auth decision.
4. **Production-side effects avoided:** quote-saving outcome modals and production image/auth API mutations were not triggered solely for screenshots. Their render-node contexts were read and existing route/effect contracts were preserved; active Typing, question, image, login, and share states were captured safely.
5. The ignored `app/google-services.json` must exist locally for `:app:assembleDebug`; it is not part of these commits.
6. The Figma template delete action is implemented only for the debug fixture because production has no separate template-removal domain contract. Production uploaded-image deletion remains unchanged.
