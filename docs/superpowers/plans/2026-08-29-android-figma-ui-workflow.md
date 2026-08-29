# Android Figma UI 문서 체계 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Android 프로젝트에 iOS와 동등한 Figma 기반 UI 작업·검증·QA 문서 체계를 도입한다.

**Architecture:** Android 루트 `AGENTS.md`가 UI 작업의 진입 규칙을 강제하고, `docs/ui-redesign-workflow.md`가 실행 절차와 QA 형식을 정의한다. `docs/planning.md`는 화면별 문서의 인덱스이며, 각 `docs/screens/*.md` 파일은 해당 화면의 기능·상태와 Figma 기준·컴포넌트 분해 기록을 함께 보관한다.

**Tech Stack:** Kotlin, Jetpack Compose, ViewModel, Android Clean Architecture, Markdown, Figma MCP

**Spec:** `docs/superpowers/specs/2026-08-29-android-figma-ui-workflow-design.md`

## Global Constraints

- Figma는 Android와 iOS UI의 유일한 시각·상호작용 기준이다.
- UI 작업은 Figma URL, 대상 프레임/노드 ID, 재현 가능한 기준 이미지 또는 Figma MCP 참조가 준비된 뒤에만 시작한다.
- Android UI 코드는 Compose 컴포넌트, ViewModel, UI state/action/effect 경계를 유지한다.
- 이번 변경은 문서·개발 규칙에 한정하며 Android·iOS 소스 코드, 리소스, 테스트를 수정하지 않는다.
- 기존 사용자 로컬 변경인 `app/.DS_Store`, 루트 `.DS_Store`, iOS Xcode 사용자 상태 파일을 스테이징하거나 커밋하지 않는다.

---

### Task 1: Android UI 작업 규칙과 워크플로 정의

**Files:**
- Create: `AGENTS.md`
- Create: `docs/ui-redesign-workflow.md`
- Modify: `README.md`

**Interfaces:**
- Consumes: `docs/superpowers/specs/2026-08-29-android-figma-ui-workflow-design.md`
- Produces: Android UI 작업 전 읽을 규칙과 화면·컴포넌트·QA 검증 절차

- [x] **Step 1: 작성 전 검증 기준 확인**

확인할 요구사항:

```text
Figma URL + 노드 ID + 기준 이미지가 없으면 UI 구현을 시작하지 않는다.
Compose 하위 컴포넌트를 먼저 검증하고, 조립 화면을 별도 검증한다.
완전한 검증은 최대 5회이며 증거는 QA 기록에 남긴다.
```

- [x] **Step 2: Android 규칙과 워크플로 작성**

`AGENTS.md`에는 Clean Architecture 모듈, Compose/ViewModel 상태 경계, 화면 문서 선행, Figma 단일 기준, 위임 필수 정보를 작성한다. `docs/ui-redesign-workflow.md`에는 시작 게이트, 컴포넌트 우선 개발, Android 런타임 캡처 비교, 최대 5회 규칙, QA 템플릿, 위임 규칙, 화면 문서 템플릿을 작성한다.

- [x] **Step 3: README에서 문서로 연결**

README에 다음 링크를 추가한다.

```markdown
## UI 개편 문서

- [Figma 기반 UI 개편 워크플로우](docs/ui-redesign-workflow.md)
- [화면별 UI 기획](docs/planning.md)
```

- [x] **Step 4: Markdown 구조 검증**

Run: `rg -n "Figma.*(URL|노드)|5회|QA|ViewModel|Compose" AGENTS.md docs/ui-redesign-workflow.md README.md`

Expected: 세 파일에서 Figma 시작 게이트, 최대 5회 QA, Android Compose/ViewModel 기준, 문서 링크를 확인한다.

- [x] **Step 5: Commit**

```bash
git add AGENTS.md docs/ui-redesign-workflow.md README.md
git commit -m "docs: add Android Figma UI workflow"
```

### Task 2: 화면별 기획과 QA 기록 구조 생성

**Files:**
- Create: `docs/planning.md`
- Create: `docs/screens/common.md`
- Create: `docs/screens/0_onboarding.md`
- Create: `docs/screens/0_onboarding_guide.md`
- Create: `docs/screens/1_login.md`
- Create: `docs/screens/2_home.md`
- Create: `docs/screens/3_calendar.md`
- Create: `docs/screens/4_list.md`
- Create: `docs/screens/5_mypage.md`
- Create: `docs/screens/5_1_notice.md`
- Create: `docs/screens/5_2_inform.md`
- Create: `docs/screens/5_3_theme.md`
- Create: `docs/design-qa/README.md`

**Interfaces:**
- Consumes: `docs/ui-redesign-workflow.md`의 화면 문서·QA 템플릿
- Produces: Android 화면별 Figma 메타데이터·컴포넌트·검증 상태를 기록할 동등한 문서 구조

- [x] **Step 1: 화면 인덱스와 공통 템플릿 작성**

`docs/planning.md`에는 화면 문서 목록과 워크플로 링크를 만들고, 모든 화면 문서에는 아래의 빈 Figma 기준 섹션을 작성한다. Figma URL·노드 ID가 제공되지 않았으므로 추정값을 넣지 않는다.

```markdown
## Figma UI 기준

- Figma URL:
- 대상 프레임/노드:
- 대상 기기/프레임 크기:
- 검증 상태: 미검증
- 기준 이미지:
- QA 기록:

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
```

- [x] **Step 2: 기존 Android 화면 구조에 맞는 초기 문서 작성**

각 화면 문서에는 Android Compose 위치와 대표 ViewModel을 아래처럼 기록한다.

```text
2_home.md: presentation/ui/home, HomeViewModel
3_calendar.md: presentation/ui/calendar, CalendarViewModel
4_list.md: presentation/ui/quotelist, ListViewModel
5_mypage.md: presentation/ui/mypage, MyPageViewModel
1_login.md: presentation/ui/LoginView.kt, LoginViewModel
```

공통 문서는 `presentation/ui/common`과 `presentation/ui/BottomMenu.kt`를 기록하고, 나머지 화면은 현재 네비게이션·기능 맥락을 간결히 설명한다. 이는 구현 판단이 아니라 문서 탐색을 위한 코드 위치 정보다.

- [x] **Step 3: QA 안내 문서 작성**

`docs/design-qa/README.md`에 QA 파일 이름 규칙 `YYYY-MM-DD-<screen>-<topic>-qa.md`와 필수 섹션(Reference, Component inventory, Validation rounds, Final assembled-screen result)을 작성한다.

- [x] **Step 4: 구조·템플릿 검증**

Run:

```bash
test -f docs/planning.md
for screen in common 0_onboarding 0_onboarding_guide 1_login 2_home 3_calendar 4_list 5_mypage 5_1_notice 5_2_inform 5_3_theme; do
  test -f "docs/screens/${screen}.md" || exit 1
  rg -q "## Figma UI 기준" "docs/screens/${screen}.md" || exit 1
  rg -q "### 컴포넌트 분해" "docs/screens/${screen}.md" || exit 1
done
test -f docs/design-qa/README.md
```

Expected: 모든 화면 문서와 QA 안내 문서가 존재하며, Figma UI 기준·컴포넌트 분해 템플릿을 포함한다.

- [x] **Step 5: Commit**

```bash
git add docs/planning.md docs/screens docs/design-qa/README.md
git commit -m "docs: add Android UI planning structure"
```

### Task 3: 양 플랫폼 문서 커밋 상태 확인

**Files:**
- Verify only: iOS `AGENTS.md`, `docs/planning.md`, `docs/ui-redesign-workflow.md`
- Verify only: Android `AGENTS.md`, `docs/planning.md`, `docs/ui-redesign-workflow.md`

**Interfaces:**
- Consumes: iOS commit `fdfd902`와 Android Tasks 1-2 문서
- Produces: 두 저장소에서 독립적으로 추적되는 Figma UI 문서 체계

- [x] **Step 1: iOS 기존 문서 커밋 확인**

Run: `git -C /Users/gangbohun/iosProjects/Fiilsa show --stat --oneline fdfd902`

Expected: `AGENTS.md`, `docs/planning.md`, `docs/ui-redesign-workflow.md`를 포함한 iOS UI 워크플로 커밋을 확인한다.

- [x] **Step 2: Android 문서 커밋 확인**

Run: `git -C /Users/gangbohun/AndroidStudioProjects/Fillsa log --oneline -3`

Expected: Android UI 워크플로와 화면 문서 구조 커밋이 `release` 브랜치의 최상단에 있다.

- [x] **Step 3: 사용자 로컬 변경 보호 확인**

Run:

```bash
git -C /Users/gangbohun/iosProjects/Fiilsa status --short
git -C /Users/gangbohun/AndroidStudioProjects/Fillsa status --short
```

Expected: iOS Xcode 사용자 상태 파일과 Android `.DS_Store`만 남아 있고, 문서 변경은 모두 커밋되어 있다.
