# 8번 한/A 토글 · Android 수정 QA

## Reference

- Notion: [Home > 한영변환 버튼 클릭반경 확대요청](https://app.notion.com/p/Home-40ce639fc7a4804981e2f4b81e2c775d), `40ce639f-c7a4-8049-81e2-f4b81e2c775d`.
- Figma: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X. Home Light/Dark 전체 `2929:13556`/`3039:26518`, 토글 `2929:13611`; Typing Light/Dark 전체 `2929:17920`/`3087:28815`, 토글 `2929:18075`/`3087:28821`. [Home 전체 기준](assets/home-figma-2929-13556/reference-full.png) · [Typing Light 전체 기준](artifacts/2026-11-01-qa-typing-character/figma-light-2929-17920.png) · [Typing Dark 전체 기준](artifacts/2026-11-01-qa-typing-character/figma-dark-3087-28815.png).
- User-approved Figma exception: Typing toggle uses the same yellow Home style rather than the Figma Typing lavender toggle. This scope was approved by “필사화면 토글 디자인을 홈화면과 동일하게 하자” followed by “진행”.
- Preview target: `:presentation`, `TypingQuoteTopSectionPreview` and `TypingQuoteLightPreview`/`TypingQuoteDarkPreview`, `medium_phone` 1080px at 420dpi, fontScale 1.0. [Top section PNG](artifacts/2026-11-08-home-locale-40ce639f/top-section/com/arakene/presentation/ui/home/TypingQuoteViewKt/TypingQuoteTopSectionPreview_0.png) · [tree JSON](artifacts/2026-11-08-home-locale-40ce639f/top-section/com/arakene/presentation/ui/home/TypingQuoteViewKt/TypingQuoteTopSectionPreview_0.json), [Light](artifacts/2026-11-08-home-locale-40ce639f/typing-light/com/arakene/presentation/ui/home/TypingQuoteViewKt/TypingQuoteLightPreview_0.png) · [Dark](artifacts/2026-11-08-home-locale-40ce639f/typing-dark/com/arakene/presentation/ui/home/TypingQuoteViewKt/TypingQuoteDarkPreview_0.png).
- Runtime: connected Galaxy S25 Ultra (`SM-S938N`), Android 17, 1080×2340px/450dpi, app Debug 1.0.35(29), Korean, Light, real Home/Typing. [Home before tap](artifacts/2026-11-08-home-locale-40ce639f/android-after-home-before-tap.png), [Home left edge after](artifacts/2026-11-08-home-locale-40ce639f/android-after-home-left-edge-tap.png), [Typing before tap](artifacts/2026-11-08-home-locale-40ce639f/android-after-typing-light-kor.png), [Typing left edge after](artifacts/2026-11-08-home-locale-40ce639f/android-after-typing-visible-left.png).
- Comparison method: full uncropped Figma/Preview/runtime side by side, top-right toggle crop considered as component. Figma fixture sentence, device status/IME/ad validator differ; full assembled Figma pixel Pass is not claimed.

## Component inventory

| Component | Figma node | Target state | Result |
|---|---|---|---|
| `LocaleSwitch` | Home `2929:13611` | 한/A, capsule edges | Component tree has `OnClick` on the colored Row's full bounds; physical edges Pass |
| `TypingQuoteTopSection` | Typing `2929:18075`/`3087:28821` | Light/Dark Home-style exception | Preview Light/Dark shows yellow toggle; physical Light edges Pass |
| Home/Typing assembly | `2929:13556`, `3039:26518`, `2929:17920`, `3087:28815` | card boundary, keyboard | Feature interaction Pass on physical Light; full Figma acceptance Blocked |
| Quote detail shared caller | `LocaleSwitch` | unchanged visual, click modifier | Debug APK compiled; physical route not reached; Preview blocked by `ListViewModel` constructor |

## Validation rounds

### Round 1 · 수정 전

- Galaxy visible Home capsule x=865..1023 at y≈425..494. Tap `(865,460)` did not switch, while center tap did. Clickable bounds started x=871 because `LocaleSwitch.kt` applied `padding(rootPadding)` before `noEffectClickable`. [Previous physical AS-IS](../../../../iosProjects/Fiilsa/docs/design-qa/assets/2026-11-08-cross-platform-home-locale/android-before-ko.png) is kept in the cross-platform report.
- Typing called `LocaleSwitch` with default lavender plus larger text/padding. Historical [physical pre-change Typing screen](../../../../iosProjects/Fiilsa/docs/design-qa/assets/2026-11-08-cross-platform-typing-result/android-incomplete-live-device.png) shows the lavender toggle under a result popup.

### Round 2 · 수정 후

- `LocaleSwitch.kt`: `background → noEffectClickable → padding(rootPadding)` so the whole colored Row is clickable. `TypingQuoteView.kt`: Home's default text/root/content padding plus `#FFCB5C` outer, white selected, `#212121` text. Removed the previous Preview-only toggle omission; the real shared control now appears in `TypingQuoteTopSectionPreview`.
- `android studio check` returned Fillsa `READY`. `android preview render :presentation --fqn=com.arakene.presentation.ui.home.TypingQuoteViewKt.TypingQuoteTopSectionPreview --component-tree --output=docs/design-qa/artifacts/2026-11-08-home-locale-40ce639f/top-section` passed; Light/Dark full Typing Preview commands with the corresponding FQNs passed. Tree JSON shows colored Row and `OnClick` share bounds `[932,35]..[1072,97]` in Preview pixels.
- `./gradlew :app:assembleDebug` passed. `adb install -r app/build/outputs/apk/debug/app-debug.apk` succeeded on the connected Galaxy.
- Galaxy Home: `(865,460)` switched 한→A. Right `(1020,460)`, top `(945,425)`, bottom `(945,492)` each switched A↔한 in sequence; tapping card `(940,650)` opened Typing. Captures are stored for each coordinate in the artifact directory.
- Galaxy Typing: corrected raw-screen coordinates left `(882,166)`, right `(1030,166)`, top `(960,137)`, bottom `(960,198)` switched 한→A→한→A→한. Initial y=250 trial tapped the input area and is excluded from the edge result. Preview and physical screen both display the yellow Home-style toggle.

## Final assembled-screen result

- Functional result: **Pass** for the reported Home edge on connected Android hardware and Typing visible edges in Light mode. Back/card navigation stayed distinct.
- UI result: **Blocked** for full Figma acceptance because the real sentence, system bars, IME/ad validator, and source frame conditions differ. Typing toggle's color/size difference is specifically the user-approved exception.
- Dark mode: source Preview rendered and inspected; physical Dark mode not executed. Quote detail Preview attempt failed before rendering with `NoSuchMethodException: ListViewModel.<init>()`; that route is build-only regression evidence.
