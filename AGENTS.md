# AGENTS.md

## Project Context

- Fillsa Android 앱은 Jetpack Compose 기반의 기존 운영 앱이다.
- Figma는 모든 UI 결정의 단일 기준이다. 레이아웃, 문구, 자산, 컴포넌트 계층, 표시 상태, 상호작용은 대상 Figma 디자인을 따른다.
- iOS 구현이나 기존 Android 구현은 UI 판단 또는 시각 검증의 기준으로 사용하지 않는다.

## Architecture

- `app`, `presentation`, `domain`, `data` 모듈의 Clean Architecture 경계를 유지한다.
- UI는 Compose 컴포넌트, ViewModel, UI state, action, effect의 책임을 분리한다.
- UI 구조를 이유로 Figma의 시각 결과나 상호작용을 바꾸지 않는다.

## Figma UI Source-Of-Truth Rules

- 모든 UI 변경은 참조한 Figma 디자인과 일치해야 한다. 예외는 사용자가 명시적으로 승인한 경우에만 허용한다.
- Figma에 없는 UI 요소, 상태, 문구, 애니메이션, 상호작용을 추가하지 않는다.
- UI 작업을 시작하려면 Figma 파일 URL과 대상 프레임/노드 ID가 필요하다. 둘 중 하나라도 없으면 요청하고 구현을 시작하지 않는다.
- UI 조사·구현·리뷰·검증 전에는 `docs/ui-redesign-workflow.md`를 끝까지 읽는다.

## Planning Documents

- 전체 화면 인덱스는 `docs/planning.md`에서 관리한다.
- 화면별 기획 문서는 `docs/screens/`에 있다.
- UI 개편 절차와 QA 형식은 `docs/ui-redesign-workflow.md`에 있다.
- UI 수정 전 대상 화면 문서를 찾고, 없으면 먼저 만든다.
- UI 수정 전 화면 문서에 Figma URL, 대상 프레임/노드 ID, 컴포넌트 분해, 대상 상태, QA 기록 링크를 최신화한다.

## Workflow Expectations

- Figma 계층과 반복 구조를 기준으로 Compose UI를 컴포넌트 단위로 구현·검증한다.
- 하위 컴포넌트 검증을 통과한 뒤에만 상위 컨테이너와 조립 화면 검증으로 진행한다.
- 영향을 받은 컴포넌트와 조립 화면이 최종 Figma 검증을 통과하고 QA 증거가 기록되기 전에는 UI 작업을 완료로 표시하지 않는다.
- Figma나 화면 문서로 판단할 수 없는 제품 결정은 사용자에게 확인한다.

## Agent Collaboration

- UI 작업을 위임할 때는 Figma URL, 프레임/노드 ID, 담당 컴포넌트 범위, 대상 상태를 전달한다.
- 작업자는 이 문서와 `docs/ui-redesign-workflow.md`를 읽고 자신에게 맡겨진 컴포넌트 범위만 구현·검증한다.
- 부모 작업자는 컴포넌트별 검증 증거를 취합한 뒤 조립 화면을 별도로 검증한다.
