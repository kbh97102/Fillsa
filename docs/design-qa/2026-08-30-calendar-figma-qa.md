# Calendar Figma UI QA

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2985-21952
- Target frames/nodes: light `2985:21952` (`3. calendar`); dark `3039:28370` (`3. calendar`). Sections `2929:13366` and `2929:6874` are orientation-only.
- Reference images: `docs/design-qa/assets/calendar-figma-2985-21952/reference-light-full.png`; `docs/design-qa/assets/calendar-figma-2985-21952/reference-dark-full.png`.
- Full-frame reference: both references include Figma system status, safe, navigation, and gesture surfaces at 360 × 821 px.
- Runtime target: Android Emulator `emulator-5556`, controlled 360 × 821 / 160dpi if capture is available; light and dark system modes, Korean locale, existing Calendar data state.

## Component inventory

| Component | Figma node | Target state | Final result |
|---|---|---|---|
| Calendar shell | `2985:21954`; dark equivalent | month, grid, selected date | Partial — runtime captures, no matching-size overlay |
| Daily indicators | `2985:22041`, `2985:22044` | completion/like mapping | Partial — genuine RED→GREEN unit test |
| Legend | Calendar frame descendants | existing like/streak aggregate counts | Partial — Figma heart/fire assets in runtime capture |
| Selected-date companion | light/dark root frame | established quote or empty companion | Partial — photo-modal state blocked |
| Full assembled frame | `2985:21952`, `3039:28370` | light/dark | Blocked |

## Known contract limits

- The dark Figma target `3039:28370` is a photo-record detail modal state over its calendar. Current Calendar state exposes only monthly quote text, completion/like flags, and counts; it has no photo-record payload, image-change/delete action, or modal action contract. This work must not invent one, so that exact modal is Blocked pending a product/data contract.
- Figma has three bottom tabs while Android preserves the existing shared four-route navigation and live ad behavior. That product-level discrepancy is outside Calendar scope.

## Validation rounds

### Round 1

| Scope | Difference | Fix | Result |
|---|---|---|---|
| Reference acquisition | Light/dark full-frame exports saved locally | N/A | Complete |
| Daily indicator behavior | No pure Figma record-indicator mapping existed | Added `calendarRecordIndicators(MemberQuotesData?)`; RED was unresolved function, then GREEN passed `CalendarRecordIndicatorTest` | Complete |
| Calendar shell | Existing card had non-Figma height, shape, dark background, and legacy drawable icons | 320dp-width 12dp shell, Figma colors, and durable Figma SVG arrow/fire/heart assets | Partial — runtime captured |
| Empty/detail companion | Existing empty selected day was a blank card | Added the Figma empty companion with existing blank-quote state; existing nonblank quote destination remains clickable | Partial — dark photo modal cannot be represented by current data contract |

- Focused RED: `./gradlew :presentation:testDebugUnitTest --tests com.arakene.presentation.ui.calendar.CalendarRecordIndicatorTest --console=plain` failed because `calendarRecordIndicators` did not exist.
- Focused GREEN: the same command passed after the mapper was implemented.
- Durable Figma SVG assets: `presentation/src/main/assets/figma/calendar/` contains `calendar_arrow.svg`, `calendar_record_fire.svg`, `calendar_record_heart.svg`, and `calendar_empty_handwriting.svg`. `presentation/src/main/assets/figma/calendar-night/` contains the dark-export empty companion plus byte-identical direct Figma arrow/fire/heart assets. SVGs are density-independent and render at Figma 24dp navigation, 12dp cell, 16dp legend, and 100dp companion sizes.

## Final assembled-screen result

- Final runtime capture: `docs/design-qa/assets/calendar-figma-2985-21952/runtime-light-emulator-1280x2856.png` and `docs/design-qa/assets/calendar-figma-2985-21952/runtime-dark-emulator-1280x2856.png`, captured on Android Emulator `emulator-5556` (physical 1280 × 2856 px, 480dpi). Light was selected through the existing My page theme UI for capture, then the pre-existing in-app Dark theme was restored; system mode remains light.
- Comparison: both captures are full-device frames including status, safe, bottom-navigation, ad, and gesture surfaces, but their 1280 × 2856 / 480dpi target does not match the Figma 360 × 821 reference. No controlled matching-size overlay exists.
- Result: Blocked.
- Remaining differences: (1) dark Figma `3039:28370` is a photo-record modal state for which Calendar has no photo/modal/delete/change data/action contract; (2) Figma light's March-2025 empty-state data differs from the real August-2026 runtime data and cannot be fabricated; (3) shared Android 4-tab/live-ad surface differs from Figma's 3 tabs; (4) matching 360 × 821 / 160dpi capture and overlay are pending.
