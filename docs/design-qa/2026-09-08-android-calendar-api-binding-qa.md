# Calendar API binding QA — Task 5

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X/%E2%9C%92%EF%B8%8F%ED%95%84%EC%82%AC?node-id=2929-13366
- Section: `2929:13366`; frames: `2985:21952`, `2987:22796`, `2985:22510`.
- Prompt components: `2987:22977`, `2985:22670`; registered thumbnail: `3207:2974`.
- Full-frame references: `assets/calendar-figma-2929-13366/2026-09-08/figma-basic-2985-21952.png`, `figma-unanswered-2987-22796.png`, `figma-answered-image-2985-22510.png` in the same directory. These are the existing Figma MCP exports including system surfaces.
- Runtime target: Medium_Phone_API_35, API 35, 160dpi, 360dp width, light, ko-KR; 360×816 basic and 360×1101 completed states.
- This task changes data/action binding only. Dimensions, colors, typography, hierarchy, and 28dp AsyncImage thumbnail remain unchanged.

## Component inventory

| Component | Figma node | Target state | Task 5 result |
|---|---|---|---|
| Selected monthly detail | `2987:22950`, `3207:2950` | API answer/image metadata, completion unchanged | Unit-tested and runtime-revalidated through production mapper |
| Prompt answer | `2987:22977`, `2985:22670` | API localized question; ViewModel input/save/edit | Unit-tested and mapped unanswered/answered runtime states captured |
| Calendar grid/count | `2987:22799`, `2987:23117` | Cache-only selection, unchanged completion/summary | Regression-tested; no visual changes |
| Assembled Calendar | Three referenced frames | Basic, member unanswered, member answered/image | Full-frame runtime evidence captured; literal result Blocked only by shared/system boundaries |

## Validation rounds

Task 5 does not claim a new visual validation round. No new full-frame runtime comparison was performed by this worker. Prior reference/runtime assets remain available; the parent must validate the assembled build after integration. Automated tests do not constitute Figma visual acceptance.

Task 6 performs an integration revalidation rather than changing or extending the five-round correction workflow. It compares the final mapper-backed build against all three existing Figma exports at full bounds.

## Behavioral evidence

- Production monthly response mapping feeds Korean/English question, answer, answeredAt, imagePath, English quote/author/URL.
- POST-first save rejects blank/over-limit/missing-target/duplicate/guest member requests; success patches originating record answer/time only.
- Failed POST preserves draft and emits no daily request; failed daily reconciliation preserves POST state.
- Auth generation, request revision, origin date/sequence, monthly refresh revision, and post-success draft changes are guarded.
- Monthly summary/completion/like/image remain unchanged by answer POST and daily reconciliation.
- Guest-only design question fallback was explicitly confirmed by the parent; members use API question exclusively.
- Figma fixture local-resource thumbnail and fixture-only displayed count remain scoped to the existing QA fixture.
- Coroutine-level `CalendarViewModel` tests use counted Home/Calendar repository fakes to verify monthly invocation, cache-only `SelectDay` with zero network calls, POST-before-daily order, silent daily failure, guest isolation, unchanged summary/completion/like/image fields, and exact Calendar-to-Home target emission.
- Retrofit contract tests independently verify typing GET uses `/api/v1/member-quotes/{dailyQuoteSeq}/typing` with an empty request body while typing POST uses `/api/v2/member-quotes/{dailyQuoteSeq}/typing` with its JSON body.

## Final assembled-screen result

- Final runtime captures: `assets/calendar-figma-2929-13366/2026-09-08-integration/runtime-basic.png` (360 × 816), `runtime-unanswered.png` (360 × 1101), and `runtime-answered-image.png` (360 × 1101).
- Production mapper boundary: each selected fixture record passes through `calendarSelectedDayPresentation`; only the documented Figma-only date/image/count overrides follow it.
- Result: Calendar-owned mapped content revalidated. Literal full-frame result remains **Blocked only by shared/system product boundaries**: Android system surfaces, shared four-tab navigation, omitted nonnetwork-fixture ad, and the recorded synthetic weekday discrepancy.
