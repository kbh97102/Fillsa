# 위젯 디자인 운영 검증 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 1.0.22 (18) 스테이지에서 확인된 위젯 탭 동작과 선택 테두리가 운영 빌드에서도 동작하는지 검증한다.

**Architecture:** 구성 화면은 Compose Preview로, 실제 Glance 위젯은 런처에 배치해 확인한다. Figma와 비교할 대상 URL·노드 ID를 화면 문서에 고정한 뒤 상태별로 대조한다.

**Tech Stack:** Kotlin, Jetpack Compose, Glance, Android 런처, Figma MCP

**Spec:** [Notion QA 항목 정리](../specs/2026-10-04-notion-qa-backlog-resolution.md), [UI 개편 워크플로우](../../ui-redesign-workflow.md)

## Global Constraints

- 운영 앱/런처와 Figma URL·노드 ID가 없는 상태에서 시각 완료 판정을 하지 않는다.
- 스테이지 확인 결과를 운영 확인으로 대체하지 않는다.
- 운영에서 차이가 확인될 때만 컴포넌트를 수정한다.

## Review Focus

- 2×2/4×2 위젯을 탭하면 운영 앱이 열린다.
- 현재 선택 비율만 Figma의 선택 테두리로 표시된다.
- 런처의 실제 크기·light/dark 상태에서 잘림이나 색상 차이가 없다.

---

### Task 1: 운영 런처 위젯 대조

**Files:**
- Inspect: `app/src/main/java/com/arakene/fillsa/widget/ui/AppWidget.kt`, `WidgetPreview2x2.kt`, `WidgetPreview4x2.kt`
- Create: `docs/screens/6_widget.md`
- Modify: `docs/planning.md`
- Create: `docs/design-qa/YYYY-MM-DD-widget-production-qa.md`

**Interfaces:**
- Consumes: 위젯 Figma URL·노드 ID, 운영 앱 versionCode/versionName, 실제 런처.
- Produces: 두 위젯 크기의 탭·선택·표시 상태 증거.

- [ ] **Step 1:** Figma의 두 크기와 변형은 확보해 `docs/screens/6_widget.md`를 만들고 `docs/planning.md`에 연결했다. 제공된 섹션에는 구성 화면 선택 테두리 프레임이 없으므로 그 노드 링크가 필요하다.
- [ ] **Step 2:** 운영 앱 버전을 기록하고 2×2/4×2 위젯을 런처에 배치한다.
- [ ] **Step 3:** 위젯 탭 → 앱 진입을 각 크기에서 확인한다. 설정 화면의 선택 테두리는 Compose Preview 및 실제 화면으로 비교한다.
- [ ] **Step 4:** 런처 전체 프레임을 Figma와 비교해 기록한다. 일치하면 Notion `Completed`로 옮기고, 차이가 있으면 별도 수정 계획을 만든다.
