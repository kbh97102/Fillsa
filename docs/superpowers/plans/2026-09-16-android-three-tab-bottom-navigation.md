# Android Three-Tab Bottom Navigation Implementation Plan

> **For agentic workers:** Execute sequentially and stop for user confirmation on any product decision not represented by the cited Figma node.

**Goal:** Replace the Android four-tab root navigation with the Figma Home/Calendar/My page three-tab composition.

**Architecture:** Keep QuoteList and its NavHost destination as a Calendar-owned child route, but remove it from the shared root navigation model. Render the shared navigation with explicit Compose geometry instead of Material3's default container height.

**Tech Stack:** Kotlin, Jetpack Compose, Navigation Compose, JUnit4

**Spec:** `docs/screens/common.md`, Figma frame `2985:22510`, bottom navigation `3087:29220`

## Global constraints

- Figma is the visual source of truth.
- Keep Home, Calendar and My page routes and navigation behavior.
- Keep QuoteList as an internal destination; do not expose it as a bottom tab.
- Do not change APIs, domain/data layers, or live-ad policy.
- Work in an isolated branch and do not merge without separate approval.

## Tasks

- [x] Add failing tests for exact visible tabs, order, icon mapping and QuoteList exclusion.
- [x] Introduce `MainBottomNavigationItem`, `mainBottomNavigationItems`, and `shouldShowMainBottomNavigation`.
- [x] Render a 60dp three-column bar with 32dp icons and body4 labels.
- [x] Use the shared visibility predicate from `MainActivity` so QuoteList no longer receives the root bar.
- [x] Run focused and full presentation unit tests.
- [x] Assemble the debug app.
- [x] Capture the Calendar-selected runtime state at 360 × 1101 and compare it with Figma.
- [x] Record the verification evidence in the common, Home and Calendar planning documents.
