# Android Three-Tab Bottom Navigation Figma UI QA

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X/%E2%9C%92%EF%B8%8F%ED%95%84%EC%82%AC?node-id=2985-22510
- Target frame/node: `2985:22510` (`3. calendar`), bottom navigation `3087:29220`
- Reference image: `docs/design-qa/assets/calendar-figma-2929-13366/2026-09-08/figma-answered-image-2985-22510.png`
- Runtime target: `Medium_Phone_API_35`, API 35, 360 × 1101 logical px, 160dpi, light, ko-KR, Calendar selected
- Runtime capture: `docs/design-qa/assets/bottom-navigation-figma-2985-22510/2026-09-16/runtime-calendar-selected.png`

## Component inventory

| Component | Figma node | Target state | Final result |
|---|---|---|---|
| Three equal navigation items | `3087:29220` | Home / Calendar selected / My page | Pass |
| Navigation icon | `3087:29223`, `3087:29227`, `3087:29231` | 32 × 32dp local vectors | Pass |
| Navigation label | `3087:29222`, `3087:29226`, `3087:29230` | Pretendard regular 12sp, 18sp line height | Pass |
| Selection color | Calendar instance | selected `#5C65FF`, unselected `#212121` | Pass |
| QuoteList route boundary | not a Figma bottom tab | destination retained; bottom bar hidden | Pass |

## Validation rounds

### Round 1

| Scope | Difference | Fix | Result |
|---|---|---|---|
| Item composition | Android exposed Home/List/Calendar/My page as four equal items | Removed QuoteList from the main navigation specification | Pass |
| Geometry | Material3 `NavigationBar` used framework container geometry instead of the 60dp frame | Replaced it with a 60dp `Row`; each item uses `weight(1f)`, 32dp icon, 2dp gap and body4 label | Pass |
| Root route visibility | QuoteList was treated as a bottom-navigation root | Shared visibility predicate now contains only Home, Calendar and My page | Pass |

- Comparison: side-by-side full-frame inspection against the stored 360 × 1101 reference. The navigation component has three equal items and the same icon/label/color contract.
- Interaction: starting from the Home QA fixture, tapping all three items changed the selected accessibility node to Home `[0,1017][120,1077]`, Calendar `[120,1017][240,1077]`, and My page `[240,1017][360,1077]`. Each runtime item therefore occupies exactly 120 × 60 logical pixels.
- The Calendar process-local fixture intentionally suppresses the live advertising request. The missing static advertisement in the runtime capture is outside this navigation change and is not counted as a navigation failure.

## Automated verification

- RED: focused test failed because `mainBottomNavigationItems` and `shouldShowMainBottomNavigation` did not exist.
- GREEN: `./gradlew :presentation:testDebugUnitTest --tests com.arakene.presentation.ui.BottomNavigationSpecTest --console=plain` — successful, 4 focused tests.
- Regression: `./gradlew :presentation:testDebugUnitTest --console=plain` — successful, 124 tests.
- Assembly: `./gradlew :app:assembleDebug --console=plain` — successful after copying the repository's gitignored `app/google-services.json` into the isolated worktree.

## Final assembled-screen result

- Final runtime capture: `docs/design-qa/assets/bottom-navigation-figma-2985-22510/2026-09-16/runtime-calendar-selected.png`
- Comparison: full-frame side-by-side; bottom navigation only
- Result: Pass for the shared bottom-navigation component
- Remaining differences: Android status/gesture surfaces and fixture-suppressed live ad remain outside this component scope.
