# Home Locale Toggle Figma UI QA

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-19887
- Target frames/nodes: `2929:19887` (`Toggle_off_S`), light Home `2929:13611`, dark Home `3039:26574`
- Reference image: `docs/design-qa/assets/home-locale-toggle-2929-19887/reference.png`
- Full-frame reference: `docs/design-qa/assets/home-figma-2929-13556/reference-full.png`
- Runtime target: SM-S938N, 1080 × 2340 px, 450 dpi, API 36, dark mode, Korean locale, Home default state
- Capture platform/device/emulator: physical Android device SM-S938N

## Component inventory

| Component | Figma node | Target state | Final result |
|---|---|---|---|
| Shared `LocaleSwitch` in Home | `2929:19887`, `3039:26574` | Korean selected | Pass |
| Shared `LocaleSwitch` in Home | interaction counterpart | English selected | Pass |
| Assembled Home screen | `3039:26518` | default dark Home | Blocked outside component scope; dynamic quote/question/ad data differ from the static Figma frame |

## Measured design contract

- Layout size is derived from measured text plus padding; the implementation must not set a fixed toggle width/height or use weighted segments.
- Each label uses horizontal `8dp` and vertical `2dp` content padding.
- The outer container uses start `4dp`, top `3dp`, bottom `3dp`, and no end padding.
- Colors: outer `#FFCB5C`, selected `#FFFFFF`, text `#212121`.
- Figma metadata at the reference font render: root `60 × 28`, selected surface `28 × 22` at `x=4, y=3`, labels `12 × 18` at `y=5`.

## Validation rounds

### Round 1

| Scope | Difference | Fix | Result |
|---|---|---|---|
| Home locale toggle, Korean selected | Home-specific component fixed the root at `60 × 28dp`, split labels with `weight(1f)`, and started the selected surface at the outer left edge | Reused shared `LocaleSwitch`; removed fixed root size and weights; applied start `4dp`, vertical `3dp`, item horizontal `8dp`, vertical `2dp` | Pass — selected surface follows the text padding and retains the Figma colors/alignment |
| Home locale toggle, English selected | Home-specific component never applied the white selected surface to `A` | Shared `LocaleSwitch` applies the same selected item implementation to both locales | Pass — English selected surface and padding match the Korean state behavior |
| Shared consumers | Adding Home colors could have changed Typing/Quote detail | Added color parameters with the previous purple/white/gray resources as defaults | Pass by full presentation compile/unit tests; no existing call-site changes |

- Comparison: side-by-side inspection of the Figma component PNG/metadata and physical-device captures. Figma light and dark instances have the same `60 × 28` reference geometry and colors. Runtime before: `docs/design-qa/assets/home-locale-toggle-2929-19887/runtime-before.png`. Runtime after: `runtime-kor-after.png`, `runtime-eng-after.png` in the same directory.

## Final assembled-screen result

- Final runtime capture: `docs/design-qa/assets/home-locale-toggle-2929-19887/runtime-kor-after.png`, `docs/design-qa/assets/home-locale-toggle-2929-19887/runtime-eng-after.png`
- Comparison: affected component and surrounding Home row compared side-by-side against Figma `3039:26574`; Korean and English interaction states were both captured after installing the built debug APK.
- Result: Locale toggle component Pass. Full assembled Home remains Blocked under the pre-existing Home QA because live quote/question/ad content differs from the static Figma frame.
- Remaining differences: none in the locale toggle component; full-screen differences are dynamic content/live-ad scope already tracked by Home QA.
