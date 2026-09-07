# SDD ledger — plan: docs/superpowers/plans/2026-09-07-android-home-dark-root.md

## Preflight

| Tasks | Shared file/interface | Check |
|---|---|---|
| 1 ↔ 2 | `FigmaHomeContent.kt`, `HomeView.kt` | Task 1 owns base dark shell/palette; Task 2 owns overlay/action states and consumes the same palette. |
| 1 ↔ 3 | `FigmaHomeContent.kt`, `HomeView.kt` | Task 3 consumes Task 1 shell dimensions and only refines the question family. |
| 2 ↔ 3 | Shared SnackbarHost | Copy and question-success messages are separate target states and must not overwrite each other's styling contract. |
| 2 ↔ 4 | Home image action | Task 2 owns registered thumbnail/label; Task 4 owns image dialog and upload/delete actions. |
| 3 ↔ 4 | `HomeView.kt` state assembly | IME/question and image modal are validated as separate runtime states; no duplicated ViewModel state. |
| 4 ↔ 5 | Existing navigation/auth/share contracts | Task 4 consumes existing auth/image flows; Task 5 only aligns linked route surfaces. |
| 1–5 ↔ 6 | Runtime state and capture contract | Task 6 consumes every prior state at logical 360×821dp and may change code only through the QA loop. |
| 1 | Files/tests internally consistent | Existing palette/calendar interfaces are preserved; tests are post-implementation per user ruling. |
| 2 | Files/tests internally consistent | Existing action/effect contracts cover tooltip/copy/like/image states. |
| 3 | Files/tests internally consistent | Existing session-only question state matches the spec; no persistence layer is added. |
| 4 | Files/tests internally consistent | Existing image picker/use cases remain authoritative; fixtures are capture-only. |
| 5 | Files/tests internally consistent | Linked screens are route-level regressions, not new Home state. |
| 6 | Files/tests internally consistent | 4-route/live-ad versus Figma 3-tab/static-ad stays a recorded blocker. |

Ruling: the plan's skill-generated TDD convention is overridden by the user's explicit no-TDD requirement; tests run after implementation. Cost if wrong: regressions are detected later in the task instead of by red/green sequencing.

## Baseline

- Worktree: `/Users/gangbohun/AndroidStudioProjects/Fillsa/.worktrees/home-dark-root`
- Branch: `codex/home-dark-root`
- `./gradlew :presentation:testDebugUnitTest --console=plain`: BUILD SUCCESSFUL (2026-09-07, 48 tasks).
- Documentation/reference baseline commit: `77af9fe`.

## Independent review debugging — Phase 1–3 evidence (before fixes)

The user explicitly requires implementation-first verification, so systematic-debugging Phase 4's TDD step is overridden. Regression tests are added/run after the root-cause fixes.

### 1. Home question focus / IME

- **Phase 1 — reproduce and trace:** `runtime-android-dark-question-focus-ime.png` consistently shows the quote/card drawing through the transparent fixed header/status area and the record CTA under the production bottom navigation. The path is `MainActivity.kt` whole-app `.imePadding()` → Scaffold bottom bar → `FigmaHomeContent.kt` IME branch at the body `offset(-78.dp)` and `requiredHeight(638.dp)`. The constants attempt to compensate for an observed device pan, but do not use the actual Home viewport or scroll range.
- **Phase 2 — working pattern:** the shared Scaffold already computes the safe viewport and places bottom navigation above the IME. A fixed opaque header plus a body constrained by `weight(1f)` and `verticalScroll` can consume that viewport; scrolling to the body's measured maximum exposes the focused answer/CTA without drawing through the header. This is the same ownership split used by other fixed-header/scroll-body Compose screens.
- **Phase 3 — single hypothesis:** the overlap/clipping is caused by bypassing parent constraints with `requiredHeight` and device-specific translation. Replacing those constants with an opaque fixed header and parent-sized scroll body, then scrolling to `maxValue` when IME becomes visible, will protect the header and keep the CTA fully visible/clickable across the available height.

### 2. Shared CommonDialog clipping

- **Phase 1 — reproduce and trace:** `runtime-review-common-dialog-font130-before.png` records the fixed 181dp Home login modal at font scale 1.3; code inspection shows every `WithBaseErrorHandling` title/body also flows through `DialogSection.kt` into the same `CommonDialog.kt` `.height(if (hasBody) 165.dp else 181.dp)`. Multiline service/maintenance/custom messages have no escape from that hard cap.
- **Phase 2 — working pattern:** `DialogWIthImage`, `ThemeDialog`, and baseline `77af9fe` `CommonDialog` all use content-driven Columns. Exact dark Home geometry is a caller-specific requirement, while error dialogs require intrinsic height and large-font growth.
- **Phase 3 — single hypothesis:** global fixed height is the clipping root cause. A `DialogLayout.Content` default plus an explicit `HomeDarkMeasured` variant, activated only for dark Home login/delete dialogs, will restore multiline accessibility without losing the measured 181/165dp Home targets.

### 3. Share controls at 360×720

- **Phase 1 — reproduce and trace:** `runtime-android-dark-share-first-360x720.png` shows the three 48dp action circles but all labels are outside the frame. `ShareView.kt` consumes a fixed 481dp pager plus a 50dp bottom padding after the title block; the Column has no remaining height for the label rows.
- **Phase 2 — working pattern:** baseline `77af9fe` gives the pager `weight(1f)`, which yields space after measuring fixed controls. The dark card requires a 481dp maximum pager and 45dp horizontal inset, but those do not require a fixed height on shorter viewports.
- **Phase 3 — single hypothesis:** measuring the pager before reserving the complete 77dp control row and bottom safe gap causes label loss. Computing a pager height from `BoxWithConstraints.maxHeight` after subtracting the title/control/bottom budgets, capped at 481dp, will preserve the 360×821 target while shrinking safely at 360×720.

### 4. ImageDialog growth

- **Phase 1 — reproduce and trace:** `runtime-review-image-dialog-font130-before.png` at 360×821/font scale 1.3 shows both button labels clipped away. The path is `ImageDialog.kt` fixed `.height(373.dp)` plus fixed quote top 90dp and button top 98dp; quote/author growth only pushes controls beyond the clipped Box.
- **Phase 2 — working pattern:** baseline `77af9fe` used content-driven height, and shared dialogs anchor actions after content. A minimum-height surface with a weighted flexible spacer can retain the short-fixture 373dp geometry while allowing text to consume additional height.
- **Phase 3 — single hypothesis:** the hard surface height and fixed control gap, not the button itself, cause clipping. Replacing the hard height with `heightIn(min=373.dp)`, bounding it by the available screen, and anchoring controls at the bottom with a shrinking spacer will preserve short content and grow for long/large text.

### 5. Dark visual leakage into light

- **Phase 1 — reproduce and trace:** `runtime-review-light-typing-before.png` shows the dark Figma handwriting asset and heading in light Typing. `git show 77af9fe:.../TypingQuoteView.kt` confirms light previously began directly with the quote at 20dp. The same batch changed Login top gaps, Home vertical landmarks, calendar typography/position, and shared dialog geometry without a dark predicate.
- **Phase 2 — working pattern:** `IsDarkMode` already gates backgrounds, assets, and Share action surfaces. Baseline code provides the light metrics; behavior such as image registration state and navigation may remain shared, but render-node-specific geometry/assets must branch on `darkMode`.
- **Phase 3 — single hypothesis:** unconditional application of dark render-node deltas caused the regression. Gating the Typing ornament/heading, Login gaps, Home dark landmark deltas, calendar dark metrics, and measured dialog mode on `darkMode` will restore baseline light rendering while preserving shared behavior.

### Fixture safety and template-delete gap

- **Phase 1:** Home fixture callbacks still reach `HomeViewModel` upload/delete/like effects; calling them can reach real use cases even though quote loading is bypassed. Template preview hides delete because `backgroundImageUrl.isEmpty()`, while Figma `3223:6600` includes a delete affordance.
- **Phase 2:** production callbacks must remain authoritative outside QA. Fixture-only state can provide deterministic no-op/visual callbacks. The existing delete API represents uploaded server imagery and has no defined template-removal contract.
- **Phase 3 hypotheses:** intercepting mutation callbacks only when `LocalHomeRuntimeQaFixture` is non-null will make capture deterministic without changing production. The template affordance can be rendered without inventing a template-removal API if its fixture-only callback closes the preview deterministically; production uploaded-image deletion remains on the existing domain path.

## Independent review implementation results

### Fix 1 result — hypothesis confirmed

- Replaced the device-specific `offset(-78.dp)`/`requiredHeight(638.dp)` branch with an opaque fixed header and a body sized by parent `weight(1f)` plus `verticalScroll`; IME visibility scrolls to the measured `ScrollState.maxValue`.
- `runtime-review-question-focus-after.png` at 360×821 shows full Gboard, protected header, full 174dp answer field, counter, and the complete CTA immediately above the production bottom bar. `runtime-review-question-cta-clicked-after.png` records the CTA transition to the recorded/edit state, confirming it is clickable.
- The app bottom navigation remains above Gboard because it is owned by the shared Scaffold and the Figma focus composition also retains navigation above its keyboard. Its 4-route composition remains part of the existing product blocker, not an IME-layout regression.

### Fix 2 result — hypothesis confirmed

- Added `DialogLayoutMode.Content` as the shared default and scoped `HomeDarkMeasured` to Home login/delete. The measured mode is enabled only in dark appearance at font scale 1.0; accessibility font scales use intrinsic content sizing.
- `runtime-review-common-dialog-font130-after.png` renders a deliberately long title/body at font scale 1.3 with the single button fully visible. This exercises the same `DialogSection`/`CommonDialog` path used by `WithBaseErrorHandling`, so multiline error content is no longer subject to the 165/181dp cap.

### Fix 3 result — hypothesis confirmed

- `ShareView` now derives pager height from `BoxWithConstraints.maxHeight` after reserving 51dp title content, the complete 77dp controls, and a mode-specific bottom gap; 481dp is a maximum rather than an unconditional height. Dark horizontal geometry remains 45dp while light retains its baseline 60dp inset.
- At 360×720, `runtime-review-share-after-360x720.png` and `runtime-review-share-controls-after-360x720.png` show the guide plus all `저장`/`복사`/`카카오톡` labels. The card adapts to 389dp tall. At 360×821, `runtime-review-share-controls-after-360x821.png` retains the 400dp card and complete labels.

### Fix 4 result — hypothesis confirmed

- Dark `ImageDialog` is now a minimum-373dp, available-screen-bounded surface with header/text/controls arranged as three `SpaceBetween` regions. Light mode keeps the baseline content-driven 90/86dp spacing and body typography.
- `runtime-review-image-dialog-short-after.png` retains the short 320×373 target. `runtime-review-image-dialog-long-font130-after.png` uses a long quote plus wrapped author at font scale 1.3; the surface grows to approximately 391dp and both button labels remain complete. The pre-fix `runtime-review-image-dialog-font130-before.png` shows the same controls clipped blank at the hard cap.

### Fix 5 result — hypothesis confirmed

- Gated the dark Typing SVG/heading and top metric, Login gaps, Home landmark deltas/focus border/selected-action styling, inline-calendar typography/selector widths, tooltip variant, and measured dialog mode by `darkMode`.
- `runtime-review-light-typing-before.png` contains the leaked dark ornament; `runtime-review-light-typing-after.png` restores the baseline direct-to-quote composition, while `runtime-review-dark-typing-after.png` proves the ornament remains in dark. Light Home/calendar/login references also match baseline ownership and content-driven dialog sizing.
- The fixed-header/scroll-body behavior remains shared because it is an inset/accessibility correction rather than a dark visual. No light-specific reducer, route, or behavior fork was added.

### Fixture safety and template action result — hypothesis confirmed

- Fixture mode intercepts Home upload/delete/like and Typing save/like/back-save mutations; production paths remain byte-for-byte selected whenever `LocalHomeRuntimeQaFixture` is absent.
- `runtime-review-image-template-delete-after.png` shows the Figma delete affordance on the template fixture. Tapping it dismisses the preview via a fixture-only callback and does not call the production image-delete use case, so no new domain/API contract was invented.

## Independent review final verification

- `./gradlew :presentation:testDebugUnitTest :presentation:assembleDebug --console=plain`: `BUILD SUCCESSFUL in 1s`; 34 tests, 0 skipped/failures/errors.
- `./gradlew :app:assembleDebug --console=plain`: `BUILD SUCCESSFUL in 883ms`.
- Emulator final reads: override 360×821, density 160, font scale 1.0, dark appearance; Share additionally captured at 360×720. Full Gboard focus reported `mInputShown=true`.
- `git diff --check`: no output. The pre-existing 3-tab/static-ad versus 4-route/live-ad product blocker remains unchanged.

## Second scoped review — Phase 1–3 before changes

The user again overrides TDD sequencing: the two second hypotheses are implemented first, then covered by tests and runtime evidence. If either second hypothesis fails its target runtime state, implementation stops for architecture review rather than accumulating a third patch.

### 1. ImageDialog reserved controls / bounded scrolling

- **Phase 1 — reproduce and trace:** `runtime-review2-image-long-font150-before.png` at 360×821/font scale 1.5 shows the long author ending exactly where the action row begins. The UI hierarchy reports quote `[32,223][328,494]`, author `[32,506][328,567]`, and buttons starting at y567. In `ImageDialog.kt`, all three regions are non-weight children inside a max-height Column; `Arrangement.SpaceBetween` only distributes positive free space and does not reserve space for children measured after overflowing text.
- **Phase 2 — working pattern:** Compose layouts with persistent actions measure header and footer as unweighted children and give the variable body `weight(1f)`. `verticalScroll` on that bounded middle viewport lets text exceed its allocation while the footer keeps its intrinsic height. Centering the scroll container when its content is short preserves the existing 320×373 composition.
- **Phase 3 — second single hypothesis:** the failure risk comes from the text owning unconstrained main-axis measurement before the footer. Making only the center text region `weight(1f)` and vertically scrollable, while keeping header/footer unweighted, will guarantee button measurement at very long text/font 1.5 and preserve short geometry.

### 2. Share intrinsic title/control measurement

- **Phase 1 — reproduce and trace:** `runtime-review2-share-font150-360x720-before.png` still happens to show labels, but accessibility bounds prove the fixed budget is already inaccurate: title/subtitle occupy y74–133 (59px rather than 51), while action items occupy y603–672 (69px plus a 24px gap rather than the assumed 77 total). `sharePagerHeight` never observes either child and retains a 320dp minimum that can exceed truly compact remaining space.
- **Phase 2 — working pattern:** baseline `77af9fe` measures title and `ShareBottomSection` intrinsically, then allocates the rest to a `weight(1f)` pager. Dark only needs an upper cap of 481dp plus different pager padding; it does not need to replace intrinsic sibling measurement. Light should use the baseline weighted pager without a dark cap.
- **Phase 3 — second single hypothesis:** fixed sibling reservations are the root cause. An unweighted intrinsic title/footer plus `weight(fill=false)` dark pager capped at 481dp will consume only the actually remaining constraint at compact/large-font sizes, while retaining the 360×821 dark target. A separate light branch using baseline `weight(1f)` will prevent dark sizing from leaking into tall light layouts.

### Template confirmation evidence correction

- **Phase 1:** the previous pair only proves a before modal and an after-dismissed Home; it cannot prove a single delete tap or visible confirmation. Runtime tapping showed the synchronous fixture dialog swap can remove the image dialog without presenting the new CommonDialog in the captured frame.
- **Phase 2:** production deletion already emits its confirmation asynchronously through the ViewModel after the preview is dismissed. The fixture performs both state changes in one callback/recomposition.
- **Phase 3 hypothesis:** waiting one Compose frame after dismissing the fixture preview before setting the shared dialog holder will mirror the production sequencing and make the safe confirmation visibly testable without invoking a mutation.

### Second fix 1 result — extreme state passed, short-state gate failed

- Dark `ImageDialog` measured header and action row as unweighted children and assigned only the middle text viewport default `weight(1f)` plus `verticalScroll`. This fixed the extreme-content failure but forced short content to fill the available allocation.
- The extended fixture is substantially longer than the previous font-1.3 sample. At 360×821/font scale 1.5, `runtime-review2-image-extreme-font150-after-top.png` keeps both 58px-tall buttons fully visible. After scrolling, `runtime-review2-image-extreme-font150-after-bottom.png` reaches the wrapped author while button bounds remain `[32,719][171,777]` and `[181,719][328,777]`.
- The accessibility tree reports a scrollable middle viewport `[32,72][328,719]`, quote ending at y646, author `[32,658][328,719]`, and controls beginning at y719. This proves text overflow is owned by the bounded scroll region rather than consuming the controls.

### Second fix 2 result — second hypothesis confirmed

- Removed `sharePagerHeight` and its 51/77dp sibling reservations. Title/subtitle and `ShareBottomSection` now measure at their real scaled heights; the pager receives the remaining Column constraint through `weight`.
- Dark uses `weight(fill=false)` plus only a 481dp maximum. `runtime-review2-share-font150-360x720-after.png` at font scale 1.5 shows all three labels fully visible while the card adapts to 270×377. `runtime-review2-share-dark-360x821-after.png` retains the target x45/y168/270×400 card and y598 controls.
- Light has no pager cap and uses the baseline `weight(1f)` plus 30dp vertical/60dp horizontal padding. `runtime-review2-share-light-360x821-after.png` records the expected tall weighted card and complete controls.

### Template confirmation result — hypothesis confirmed

- Fixture deletion now waits one Compose frame after dismissing `ImageDialog`, then presents the shared Home measured confirmation. The production ViewModel path is untouched.
- `runtime-review2-template-delete-before.png` records the actionable label; `runtime-review2-template-confirmation-after-one-tap.png` is the direct frame after one tap and visibly contains `삭제하기`/`취소`; `runtime-review2-template-safe-dismissed-after-confirm.png` records the result after tapping fixture `삭제하기`.
- The safe fixture callback is empty and CommonDialog only dismisses itself, so this sequence does not call the upload-image deletion use case.

## Architecture review — authorized third ImageDialog hypothesis

- **Second-hypothesis failure:** the first reserved-control implementation used `weight(1f)` with its default `fill=true`. Extreme font-1.5 content scrolled and retained controls, but `runtime-review2-image-short-after.png` showed short content expanding the outer surface from 320×373 to almost the full 320×773 maximum. The weighted center's forced allocation, not text size, became the short dialog's measured height.
- **Architecture pattern:** `ColumnScope.weight(weight, fill=false)` still assigns the center at most the space left after non-weight header/footer measurement, but does not force a short child to occupy that full allocation. The outer `heightIn(min=373,max=screen-48)` can therefore enforce only the 373dp minimum for short content while allowing an overflowing scroll viewport to grow up to the bounded allocation.
- **Authorized third single hypothesis:** changing only the center scroll region from `weight(1f)` to `weight(1f, fill=false)` will keep short font-1.0 geometry at 320×373 and retain the extreme font-1.5 bounded scroll/header/footer behavior. Both runtime conditions must pass before any other work resumes; one failure stops the round without another patch.

### Third ImageDialog result — architecture hypothesis confirmed

- The only layout change from the failed state was `weight(1f, fill=false)`. `runtime-review3-image-short-font100-after.png` restores the short surface to x20/y225/320×373 with both buttons visible; `runtime-review3-image-short-confirmed.png` records the Home state after tapping `확인`.
- At 360×821/font scale 1.5, `runtime-review3-image-extreme-font150-top.png` keeps header and both 58px controls fixed on the screen. After scrolling to the end, `runtime-review3-image-extreme-font150-bottom.png` and the UI tree show ScrollView `[32,72][328,719]`, wrapped author `[32,658][328,719]`, and controls starting at y719. `runtime-review3-image-extreme-confirmed.png` records the successful `확인` tap.
- Both architecture gates passed, so the round resumed with post-implementation tests, documentation correction, and commit preparation.

## Second scoped review — final verification

- The first build after adding the one-frame template confirmation failed in `:presentation:compileDebugKotlin`: `launch` was unresolved and `withFrameNanos` consequently reported a suspend-call diagnostic. The code path already used `scope`; importing `kotlinx.coroutines.launch` was the single correction, and the immediate build retry succeeded.
- Targeted post-implementation command: `./gradlew :presentation:testDebugUnitTest --tests 'com.arakene.presentation.ui.home.ImageDialogLayoutTest' --tests 'com.arakene.presentation.ui.home.ShareLayoutTest' --console=plain --quiet` exited 0. Output contained only the existing google-services plugin warning.
- Full presentation command: `./gradlew :presentation:testDebugUnitTest :presentation:assembleDebug --console=plain` → `BUILD SUCCESSFUL in 3s`, 63 actionable tasks (6 executed, 57 up-to-date). XML totals: 36 tests, 0 skipped, 0 failures, 0 errors.
- App command: `./gradlew :app:assembleDebug --console=plain` → `BUILD SUCCESSFUL in 1s`, 135 actionable tasks (135 up-to-date).
- `file` reports the original independent-review set as 17 PNGs at 360×821 and 2 at 360×720; the second/third-round set adds 16 PNGs at 360×821 and 2 at 360×720.
- Final pending gates before handoff: range `git diff --check`, logical commits, and a clean worktree.
