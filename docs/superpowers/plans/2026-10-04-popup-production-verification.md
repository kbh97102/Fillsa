# 팝업 디자인 운영 검증 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 1.0.22 (18) 스테이지에서 확인된 팝업 디자인이 운영 빌드에도 반영됐는지 검증한다.

**Architecture:** [Figma QA 노드 `2554:2201`](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2554-2201)는 팝업 사례를 담은 QA 섹션이다. 실제 v3.0 대상 디자인 프레임과 표시 조건을 식별한 뒤 운영 앱을 같은 조건으로 캡처한다. 일치하면 코드 수정 없이 증거만 기록한다.

**Tech Stack:** Figma MCP, Android 기기/에뮬레이터, Notion MCP

**Spec:** [Notion QA 항목 정리](../specs/2026-10-04-notion-qa-backlog-resolution.md), [UI 개편 워크플로우](../../ui-redesign-workflow.md)

## Global Constraints

- 스테이지 확인 결과를 운영 확인으로 대체하지 않는다.
- 팝업 이름·대상 프레임·표시 조건을 식별하고 화면 문서에 기록한 뒤 대조한다.
- `2554:2201`의 사진 3개는 비교 증거이며, 구현 기준인 v3.0 디자인 프레임은 아직 확보되지 않았다.
- 운영에서 차이가 확인될 때만 별도의 수정 작업을 시작한다.

## Review Focus

- 팝업의 문구·크기·버튼·닫기 상태가 Figma 대상 상태와 일치한다.
- 운영 빌드의 versionCode/versionName이 캡처와 함께 기록된다.

---

### Task 1: 운영 팝업 대조

**Files:**
- Modify: 식별된 팝업의 `docs/screens/*.md`
- Create: `docs/design-qa/YYYY-MM-DD-popup-production-qa.md`

**Interfaces:**
- Consumes: Figma `2554:2201`의 정확한 대상 상태와 운영 빌드/기기.
- Produces: 운영 팝업의 Figma 대조 결과.

- [ ] **Step 1:** Figma 대상 팝업 노드와 light/dark·열림/닫힘 상태를 식별해 화면 문서에 기록한다.
- [ ] **Step 2:** 운영 앱의 versionCode/versionName, 기기 조건, 재현 경로를 기록한다.
- [ ] **Step 3:** 팝업의 하위 Preview와 운영 전체 프레임을 같은 상태로 캡처하고 Figma와 비교한다.
- [ ] **Step 4:** 일치하면 QA 기록 후 Notion `Completed`로 옮긴다. 차이가 있으면 원인과 대상 컴포넌트를 기록하고 별도 수정 계획을 만든다.
