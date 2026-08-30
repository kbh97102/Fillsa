# Calendar Figma UI QA

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2985-21952
- Target section/frames: light section `2929:13366`; basic `2985:21952` (360 × 816), selected/completed `2985:22510` (360 × 1101), and expanded scroll `2987:22796` (360 × 1101). The three render frames were directly queried.
- Figma reference exports: `/private/tmp/fillsa-calendar-figma-basic-2985-21952.png`; `/private/tmp/fillsa-calendar-figma-selected-2985-22510.png`; `/private/tmp/fillsa-calendar-figma-expanded-2987-22796.png`.
- Runtime target: Android Emulator `emulator-5556`, controlled 360dp configuration; basic empty and completed selected states require real monthly data and are never fabricated.

## Component inventory

| Component | Figma node | Target state | Final result |
|---|---|---|---|
| Calendar header | top_mobile descendants | logo, real streak, profile action | Build/test passed; live empty capture exists |
| Calendar shell | `2985:21954` | fixed 320 × 396 month/grid/selected date | Build/test passed; live empty capture exists |
| Daily indicators | `2985:22041`, `2985:22044` | completion/like mapping | Partial — genuine RED→GREEN unit test |
| Legend | Calendar frame descendants | existing like/streak aggregate counts | Partial — Figma heart/fire assets in runtime capture |
| Selected-date companion | `2985:21952`, `2985:22510`, `2987:22796` | empty/complete/expanded | Mapper test passed; runtime completed data capture pending |
| Full assembled frame | basic/selected/expanded light frames | light | Blocked pending controlled runtime captures |

## Known contract limits

- Calendar's existing data supplies date, quote, author, `completed`, `todayCompleted`, and `likeYn`, but no question/answer record contract. The Figma question text keeps the established Home placeholder and enforces the existing 200-grapheme UI cap; CTA goes to the existing selected-date typing flow and does not persist a prompt answer.
- Copy uses the established clipboard behavior and share uses the existing Share route. Like/image enter the existing selected-date Home flow because Calendar owns no direct like/image contract; no photo modal or new storage/API was created.
- Figma has three bottom tabs while Android preserves the existing shared four-route navigation and live ad behavior. That product-level discrepancy is outside Calendar scope.

## Validation rounds

### Round 2 — Calendar reimplementation

| Scope | Difference | Fix | Result |
|---|---|---|---|
| Reference acquisition | Light/dark full-frame exports saved locally | N/A | Complete |
| Detail-state behavior | Quote string was incorrectly sufficient to choose the detail state | RED failed on missing `calendarSelectedDayPresentation`; GREEN asserts null/noncompleted → Empty, `todayCompleted` → Completed, completed+expanded → Expanded | Complete |
| Header | Legacy `HomeTopSection` could include a warning/popup | Dedicated 50dp Figma header uses only durable logo, genuine supplied streak, and profile action | Build passed |
| Calendar shell | Library fill mode produced variable grid geometry | Replaced only Calendar screen shell with fixed 36dp × 50dp cells, 11dp gaps, six rows and 320 × 396 dimensions; ViewModel month/date constraints preserved | Build passed |
| Empty/detail companion | Empty state keyed off selected quote string and omitted its quote card | Completion now resolves from `completed || todayCompleted`; empty has 100dp Figma character plus tappable 80dp quote card, completed has 133dp detail/action card then 200-grapheme answer UI | Build passed |

- Focused RED: `./gradlew :presentation:testDebugUnitTest --tests com.arakene.presentation.ui.calendar.CalendarRecordIndicatorTest` failed with unresolved `CalendarSelectedDayPresentation` / `calendarSelectedDayPresentation`.
- Focused GREEN: the same command passed after the completion mapper was implemented.
- Durable Figma SVG assets: existing `presentation/src/main/assets/figma/calendar/` provides direct Figma arrow/fire/heart/empty-character bytes; existing `figma/home/` supplies the direct Figma logo/profile/streak/action/CTA bytes reused by the Calendar header/detail controls. All are vector assets, so no density bucket is applicable.

## Final assembled-screen result

- Prior runtime capture paths document the pre-reimplementation screen only and are not evidence for this round.
- Controlled reimplementation basic capture: `/private/tmp/fillsa-calendar-rework-basic-light-360x816.png`, Android Emulator `emulator-5556`, temporary 360 × 816 / 160dpi and the existing app Light theme. It includes status/safe/nav/ad surfaces and was taken from the live empty selection; the emulator was restored to its prior Dark setting and physical 1280 × 2856 / 480dpi configuration afterward.
- A completed-detail capture was not made: the live August-2026 monthly data contains no `completed || todayCompleted` selected record. No QA data was fabricated or persisted.
- Result: Blocked.
- Remaining differences: (1) Figma's 3-tab surface differs from Android's preserved shared 4-tab/live-ad surface, which reduces the visible basic quote-card space; (2) live data/date text differs from the March-2025 reference and is not fabricated; (3) Calendar has no persistent question-answer or direct image-modal contract; (4) the live completed-detail state and pixel overlay remain unavailable without genuine completed emulator data.
