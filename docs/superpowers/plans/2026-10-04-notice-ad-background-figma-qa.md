# 공지사항 광고 배경 Figma UI 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 공지 목록·상세의 광고 배경을 대상 Figma의 light/dark 상태와 대조하고 차이가 있는 곳만 수정한다.

**Architecture:** 기존 공통 `SingleLineAdSection`의 경로별 색상 분기를 유지하며, Figma 차이가 확인된 표시 영역만 변경한다. 광고 로딩 동작은 유지한다.

**Tech Stack:** Kotlin, Jetpack Compose, Android CLI Compose Preview, Native Ads

**Spec:** [Notion QA 항목 정리](../specs/2026-10-04-notion-qa-backlog-resolution.md), [UI 개편 워크플로우](../../ui-redesign-workflow.md)

## Global Constraints

- `docs/screens/5_1_notice.md`에 Figma URL·노드 ID·기준 이미지·상태를 기록하기 전에는 UI 수정/시각 검증을 시작하지 않는다.
- 광고 데이터나 다른 화면의 배경색 동작을 추정으로 변경하지 않는다.
- 하위 Preview 뒤 실제 목록·상세 전체 프레임을 비교한다.

## Review Focus

- 목록/상세, light/dark에서 광고가 있을 때의 배경 영역이 Figma와 일치한다.
- 광고 미수신 시에도 Figma의 화면 배경·여백과 일치한다.
- 공지 이외 경로의 광고 배경은 의도하지 않게 바뀌지 않는다.

---

### Task 1: 색상 분기 판정과 필요한 수정

**Files:**
- Inspect/Modify: `presentation/src/main/java/com/arakene/presentation/ui/common/SingleLineAdSection.kt`, `presentation/src/main/java/com/arakene/presentation/ui/mypage/NoticeView.kt`
- Modify: `docs/screens/5_1_notice.md`
- Create: `docs/design-qa/YYYY-MM-DD-notice-ad-background-qa.md`

**Interfaces:**
- Consumes: 공지 목록·상세의 Figma URL·노드 ID와 광고 표시/미표시 상태.
- Produces: 색상·영역 비교, 필요한 최소 수정, 상태별 전체 프레임 증거.

- [x] **Step 1:** 화면 문서에 Figma 기준을 기록하고 현재 `yellow01`/`gray_600` 분기가 적용되는 범위를 확인한다.
- [ ] **Step 2:** 광고 표시·미표시의 결정적 하위 Preview를 Figma 영역과 대조한다.
- [ ] **Step 3:** Figma에서 확인한 빈 상태 불일치를 수정했다. `:app:assembleDebug` 통과. 하위 Preview 재비교는 IDE 인덱스가 이전 소스를 사용해 대기 중이다.
- [ ] **Step 4:** 실제 기기/에뮬레이터에서 목록·상세, light/dark 전체 프레임을 대조해 기록한다. 일치할 때만 Notion `Completed`로 옮긴다.
