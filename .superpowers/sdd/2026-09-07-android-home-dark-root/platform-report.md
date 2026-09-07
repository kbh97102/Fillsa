# Android platform report — Home dark root

Date: 2026-09-07  
Worktree: `/Users/gangbohun/AndroidStudioProjects/Fillsa/.worktrees/home-dark-root`  
Branch: `codex/home-dark-root`  
Documentation baseline: `77af9fe`

## Outcome

All six Android briefs were implemented as one Home/platform integration batch. Home-owned dark surfaces and the linked image, Typing, login, and share routes were exercised on an Android API 35 emulator. Final assembled-screen acceptance remains **Blocked** only by the pre-existing product discrepancy between Figma's 3-tab/static-ad composition and the production app's shared 4-route/live-ad contract.

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
- Added focused-question interaction tracking and purple border. With the IME visible, the body has an authored -78dp offset plus Android's observed 131dp focus pan, yielding the Figma effective -209dp shift and preserving the 174dp answer box.
- Kept question answers in existing UI-session state. Copy always emits the required app snackbar; Android 13+ may additionally render its OS clipboard preview.
- Reused the existing `ImageDialog` contracts while aligning the 320×373 preview, typography, controls, uploaded ocean fixture, template preview, and delete confirmation. Deletion dismisses the preview before the existing confirmation effect.
- Added the exact Figma Typing handwriting SVG and aligned the Typing/share/login dark geometry. Android retains Gboard and Kakao/Google providers rather than copying iOS keyboard pixels or adding Apple login.
- Debug-only intent extras provide deterministic, non-network QA states. The fixture bypasses Splash/API quote loading only in `BuildConfig.DEBUG`; production contracts are unchanged.
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
- `presentation/src/main/java/com/arakene/presentation/util/SnackbarContent.kt`
- `presentation/src/main/java/com/arakene/presentation/viewmodel/HomeViewModel.kt`
- `presentation/src/test/java/com/arakene/presentation/ui/home/HomeLikeIconTest.kt`
- `presentation/src/main/assets/figma/typing/typing_handwriting_dark.svg`
- `presentation/src/main/res/drawable/home_registered_image_fixture.png`

### QA and specification

- `docs/design-qa/2026-09-07-android-home-dark-root-qa.md`
- `docs/screens/2_home_dark.md`
- `docs/design-qa/assets/home-figma-2929-9603/2026-09-07/runtime-android-dark-*.png` (19 runtime captures)
- `.superpowers/sdd/2026-09-07-android-home-dark-root/platform-report.md`

## Commands and exact results

- `git status --short && git branch --show-current`: started clean on `codex/home-dark-root`; baseline documentation commit was `77af9fe`.
- `curl -L <Figma asset URL> -o <temporary path>`: first sandbox attempt failed with DNS resolution; the policy-required escalated retry succeeded for both assets. `file` reported SVG text for the handwriting asset and a 1024×1024 PNG for the ocean image.
- Initial `./gradlew :app:assembleDebug --console=plain`: failed because the ignored worktree-local `app/google-services.json` was absent. The existing ignored file was copied from the parent checkout into this worktree; it was not staged or committed.
- Final `./gradlew :presentation:testDebugUnitTest :presentation:assembleDebug --console=plain`: `BUILD SUCCESSFUL in 2s`, 63 actionable tasks (6 executed, 57 up-to-date). XML results contain 29 tests, 0 skipped, 0 failures, 0 errors.
- Final `./gradlew :app:assembleDebug --console=plain`: `BUILD SUCCESSFUL in 5s`, 135 actionable tasks (12 executed, 123 up-to-date).
- `adb install -r app/build/outputs/apk/debug/app-debug.apk`: `Success`.
- Emulator configuration used escalated ADB because sandbox daemon startup was denied: `wm size 360x821`, density 160, `cmd uimode night yes`, font scale 1, and animation scales 0. `adb shell wm size` reported physical 1080×2400 and override 360×821 at final capture time.
- `file docs/design-qa/assets/home-figma-2929-9603/2026-09-07/runtime-android-dark-*.png`: primary files report 360×821 PNG; explicitly suffixed supplemental files report 360×720 PNG.
- `git diff --check`: no output before both implementation and QA commits.

## Runtime captures and comparison findings

Capture directory: `docs/design-qa/assets/home-figma-2929-9603/2026-09-07/`

- `runtime-android-dark-home-default.png`: Home-owned content aligns to the default node landmarks; production 4-route bar is visible instead of the Figma 3-tab/static-ad area.
- `runtime-android-dark-home-calendar-open.png`: popup x20/y126, 248×335; Aug 10/11 completed, Aug 16 selected.
- `runtime-android-dark-home-streak-tooltip.png`: callout x92/y85, 231×76 with caret.
- `runtime-android-dark-home-copy-toast.png`: app snackbar matches; API 35 native clipboard overlay is extra OS UI.
- `runtime-android-dark-home-selected.png`: selected heart/label and exact ocean thumbnail appear.
- `runtime-android-dark-home-recorded-toast.png`: recorded answer/session snackbar state appears.
- `runtime-android-dark-question-focus-ime.png`: action row/question match the focus-node vertical composition and the 174dp field remains intact above Gboard.
- `runtime-android-dark-image-preview.png`: uploaded ocean preview, delete, quote/author, and controls align in a centered 320×373 modal.
- `runtime-android-dark-image-template.png`: template preview uses the existing gradient background and aligned controls.
- `runtime-android-dark-image-delete-confirmation.png`: preview closes before the 320×165 confirmation; full labels fit.
- `runtime-android-dark-typing-ime.png`: full Android IME state plus exact Figma handwriting character/title and typed-prefix styling.
- `runtime-android-dark-login-modal.png`, `runtime-android-dark-login-full.png`: 320×181 prompt and full Android provider route align; the final login label is not clipped.
- `runtime-android-dark-share-first.png`, `runtime-android-dark-share-carousel.png`: 270×400 card at x45/y168, action circles at y598, first guide and paging verified.
- `*-360x720.png`: supplemental direct captures for 360×720 Typing/login/share render nodes; primary acceptance still uses the requested 360×821 emulator override.

## Commits

- `2ad2ccb` — `feat(android): align dark Home states with Figma`
- `1a9e24f` — `docs(android): record dark Home runtime parity`
- Platform report commit: the commit containing this file (listed in the parent handoff because a commit cannot include its own SHA).

## Concerns / blockers

1. **Blocked product decision:** Figma 3-tab/static AD versus shared app 4-route/live AD. No Android-only contract fork was introduced.
2. **Expected platform surfaces:** Android status/navigation bars, Gboard instead of the Figma iOS keyboard bitmap, and API 35 clipboard preview cannot be pixel-identical to those iOS-authored references.
3. **Provider contract:** Android intentionally remains Kakao/Google; the Figma full-login Apple row is not applicable without a product/auth decision.
4. **Production-side effects avoided:** quote-saving outcome modals and production image/auth API mutations were not triggered solely for screenshots. Their render-node contexts were read and existing route/effect contracts were preserved; active Typing, question, image, login, and share states were captured safely.
5. The ignored `app/google-services.json` must exist locally for `:app:assembleDebug`; it is not part of these commits.
