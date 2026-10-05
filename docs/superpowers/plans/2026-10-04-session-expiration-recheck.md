# 로그인 시간 만료 반복 재확인 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 과거의 반복 만료 팝업이 현재 앱에서 해결됐는지 재설치 직후와 토큰 만료 뒤의 두 경로로 판정한다.

**Architecture:** 기존 `AuthAuthenticator` → `safeApi` → `TokenExpiredError` → `WithBaseErrorHandling` → `LogoutUseCase` 흐름을 검증한다. 오류가 재현되기 전에는 인증 동작이나 팝업 UI를 변경하지 않는다.

**Tech Stack:** Kotlin, OkHttp/Retrofit, DataStore, Navigation Compose, JUnit 4, Android CLI

**Spec:** [Notion QA 항목 정리](../specs/2026-10-04-notion-qa-backlog-resolution.md), [기존 세션 만료 처리 계획](2026-09-19-android-session-expired-logout.md)

## Global Constraints

- Notion 신고자의 재설치 사례를 취소 버튼 문제로 단정하지 않는다.
- 민감한 토큰 값은 로그나 QA 기록에 남기지 않는다.
- 팝업 UI 수정이 필요해지면 로그인 화면 Figma URL·노드 ID를 확보하고 `docs/ui-redesign-workflow.md`를 따른다.

## Review Focus

- 만료 access token과 유효 refresh token: 재발급 뒤 원 요청을 한 번 재시도하고 로그인 화면으로 보내지 않는다.
- 만료 access/refresh token: 토큰을 비우고 로그인 화면으로 한 번 이동하며 Back으로 인증 화면에 돌아가지 않는다.
- 재설치 직후 토큰 없음: refresh 호출이나 만료 팝업 반복 없이 비회원/로그인 경로로 진입한다.

---

### Task 1: 기존 수정의 실행 경로 확인

**Files:**
- Inspect: `data/src/main/java/com/arakene/data/util/AuthAuthenticator.kt`, `SafeApi.kt`, `LocalRepositoryImpl.kt`
- Inspect: `presentation/src/main/java/com/arakene/presentation/ui/common/WithBaseErrorHandling.kt`, `presentation/src/main/java/com/arakene/presentation/util/ComposeExtension.kt`
- Evidence: `docs/design-qa/YYYY-MM-DD-session-expiration-recheck-qa.md`

**Interfaces:**
- Consumes: 현재 stage 및 운영 versionCode/versionName, 테스트 계정과 재현 조건.
- Produces: 세 경로의 요청/refresh 횟수, 토큰 저장 상태, 화면 전환 증거.

- [ ] **Step 1:** 현재 설치 빌드를 식별하고 하루 뒤 사례의 요청·응답 코드, refresh 횟수, 최종 화면을 기록한다.
- [ ] **Step 2:** 앱 데이터 삭제 또는 재설치 후 첫 실행을 같은 방식으로 기록한다.
- [ ] **Step 3:** `HandleError`의 lifecycle 재시작 시 collector 누적 여부를 확인한다. 동일 오류 하나에 다이얼로그·이동이 여러 번 발생하는지 관찰한다.
- [ ] **Step 4:** 세 경로에서 기대한 refresh·토큰·화면 결과를 운영 빌드로 확인하면 증거와 확인 범위를 기록한다. 하나라도 실패하면 최초 실패 경계를 기록하고 Task 2로 진행한다.

### Task 2: 재현된 실패만 수정

**Files:**
- Modify: Task 1에서 특정한 최초 실패 지점의 파일만
- Test: `data/src/test/java/com/arakene/data/util/AuthAuthenticatorTest.kt`, `presentation/src/test/java/com/arakene/presentation/util/SessionExpirationTest.kt` 또는 실제 실패 경계의 테스트

**Interfaces:**
- Consumes: Task 1의 재현 단계와 원인.
- Produces: 실패→통과 회귀 테스트, 필요한 최소 수정, 동일 경로 런타임 재검증.

- [ ] **Step 1:** 재현된 경로를 실패시키는 테스트를 작성하고 RED를 확인한다. `presentation` 테스트를 쓴다면 기존 `HomeViewModelIntegrationTest`/fake의 컴파일 오류를 먼저 복구한다.
- [ ] **Step 2:** 원인 파일만 수정하고 해당 테스트, 관련 모듈 테스트, `:app:assembleDebug`를 실행한다.
- [ ] **Step 3:** 원래 기기 재현 단계를 다시 실행한다. 성공 시에만 QA 기록과 Notion 상태를 갱신한다.
