# 테마 선택 팝업 Figma UI 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 라이트·다크·시스템 테마 팝업의 글자와 간격을 대상 Figma와 일치시킨다.

**Architecture:** `ThemeDialog`와 `ThemeItem` 표시값만 필요한 만큼 변경한다. `MyPageViewModel`의 선택·저장 동작은 현재 계약을 사용한다.

**Tech Stack:** Kotlin, Jetpack Compose, Android CLI Compose Preview

**Spec:** [Notion QA 항목 정리](../specs/2026-10-04-notion-qa-backlog-resolution.md), [UI 개편 워크플로우](../../ui-redesign-workflow.md)

## Global Constraints

- `docs/screens/5_3_theme.md`에 Figma URL·노드 ID·기준 이미지·상태를 기록하기 전에는 UI 수정/시각 검증을 시작하지 않는다.
- Notion의 ‘글자 약 2dp 축소, 간격 30dp’ 요청과 Figma가 다르면 Figma를 따른다.
- 하위 Preview 뒤 동일 조건의 전체 프레임을 검증한다.

## Review Focus

- light/dark 및 세 선택 상태에서 모든 항목과 확인 버튼이 보인다.
- 큰 글꼴에서 텍스트가 잘리거나 항목 터치 영역이 겹치지 않는다.
- 테마 선택 후 저장·재진입 시 선택 상태가 유지된다.

---

### Task 1: 기준 고정과 치수 수정

**Files:**
- Modify: `docs/screens/5_3_theme.md`, `presentation/src/main/java/com/arakene/presentation/ui/mypage/ThemeDialog.kt`
- Create: `docs/design-qa/YYYY-MM-DD-theme-dialog-qa.md`

**Interfaces:**
- Consumes: 테마 팝업 Figma URL·대상 노드 ID와 light/dark 상태.
- Produces: Figma와 대응하는 결정적 `@Preview`, 컴포넌트 및 조립 화면 증거.

- [x] **Step 1:** Figma 파일 URL·노드 ID, 전체 프레임, 선택 상태, 기기/locale 조건을 화면 문서에 기록한다.
- [ ] **Step 2:** 기존 18sp 글자와 15dp 항목 간격을 결정적 Preview로 렌더링하고 Figma 차이를 QA 기록에 남긴다.
- [x] **Step 3:** 확인한 Figma 치수에 맞춰 `ThemeDialog`의 글자·간격을 수정한다(20sp·30dp). `:app:assembleDebug` 통과, Preview는 IDE 인덱스가 이전 소스를 사용해 차단됨.
- [ ] **Step 4:** 각 상태의 하위 Preview 이미지와 semantics를 비교한 뒤 마이페이지 전체 프레임을 실제 기기/에뮬레이터에서 캡처한다. 워크플로우의 최대 5회 규칙을 따른다.
- [ ] **Step 5:** 선택·저장·재진입 동작과 최종 캡처를 기록하고, 일치할 때만 Notion `Completed`로 옮긴다.
