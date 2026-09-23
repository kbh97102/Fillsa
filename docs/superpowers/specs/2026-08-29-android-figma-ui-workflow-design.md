# Android Figma 기반 UI 개편 문서 체계 설계

## 목표

Android 프로젝트에 iOS와 동등한 Figma 기반 UI 작업 절차와 문서 구조를 도입한다. 이후 두 플랫폼은 같은 Figma 프레임·상태·자산을 기준으로 UI를 구현하고, 각 플랫폼의 런타임 결과와 QA 증거를 별도로 남긴다.

## 범위

- Android 루트 `AGENTS.md`에 UI 작업의 공통 규칙을 추가한다.
- `docs/ui-redesign-workflow.md`에 Android Compose UI 작업 절차를 만든다.
- `docs/planning.md`와 `docs/screens/`에 화면별 기획·Figma 기준을 기록할 구조와 초기 문서를 만든다.
- `docs/design-qa/`에 화면별 Figma UI QA 기록을 보관하는 규칙을 정의한다.
- `README.md`에서 새 문서 체계로 연결한다.

## 범위 제외

- Android Compose 코드, ViewModel, 네비게이션, 리소스, 테스트의 수정
- iOS 문서 또는 소스 코드의 변경
- Figma에 없는 UI·상태·상호작용의 추가
- Figma URL·노드 ID가 없는 특정 화면의 UI 구현 또는 검증

## 기준 원칙

- Figma는 Android와 iOS UI의 유일한 시각·상호작용 기준이다.
- 한 플랫폼의 구현 결과는 다른 플랫폼의 UI 판단 기준이 될 수 없다. 플랫폼별 차이는 시스템 안전 영역, 상태 표시줄, 입력 방식 등 Figma 또는 화면 문서에서 명시한 항목만 허용한다.
- UI 작업은 Figma URL, 대상 프레임/노드 ID, 재현 가능한 기준 이미지 또는 Figma MCP 참조가 화면 문서에 준비된 뒤에만 시작한다.
- 완료는 코드 작성이 아니라, 영향 컴포넌트와 조립 화면이 Figma에 대조되어 통과한 QA 기록을 뜻한다.

## 문서 구조

```text
AGENTS.md
docs/
  planning.md
  ui-redesign-workflow.md
  screens/
    common.md
    0_onboarding.md
    0_onboarding_guide.md
    1_login.md
    2_home.md
    3_calendar.md
    4_list.md
    5_mypage.md
    5_1_notice.md
    5_2_inform.md
    5_3_theme.md
  design-qa/
    YYYY-MM-DD-<screen>-<topic>-qa.md
```

화면 파일 이름은 iOS 기획 문서와 맞춘다. 문서의 기능·상태 설명은 Android의 실제 화면 구조에 맞게 유지하되, `Figma UI 기준` 섹션과 컴포넌트 분해 표는 양 플랫폼에서 같은 의미를 가진다.

## AGENTS.md 규칙

Android용 `AGENTS.md`는 다음을 명시한다.

- 앱은 Clean Architecture의 `app`, `presentation`, `domain`, `data` 모듈 구조를 유지한다.
- UI는 Compose 컴포넌트, ViewModel, UI state/action/effect 경계를 사용한다.
- UI 조사·구현·리뷰·검증 전에 `docs/ui-redesign-workflow.md`를 끝까지 읽는다.
- 작업 전 대상 화면 문서를 찾고, 없으면 먼저 만든다.
- Figma URL과 노드 ID가 없으면 구현을 중단하고 요청한다.
- 위임 작업에는 Figma URL, 노드 ID, 담당 컴포넌트 범위, 대상 상태를 전달한다.

## Android UI 개편 워크플로

`docs/ui-redesign-workflow.md`는 iOS 절차와 같은 일곱 단계로 구성한다.

1. 기준과 완료 조건: Figma 단일 기준과 완료 정의
2. 작업 시작 게이트: 화면 문서, Figma URL·노드 ID·기준 이미지, 대상 디바이스·화면 상태 확보
3. 컴포넌트 우선 개발: Compose 컴포넌트를 하위 영역부터 구현·개별 검증하고 ViewModel의 상태·액션·이펙트 경계를 UI 책임에 맞춤
4. 검증과 수정 루프: 동일한 기기 크기, 밀도, API 레벨, 색상 모드, 언어, 상태에서 런타임 캡처를 Figma와 비교
5. 최대 5회 검증: 컴포넌트와 조립 화면이 모두 통과해야 하며, 5회 뒤에도 차이가 남으면 차단 상태로 기록
6. 증거와 기록: 화면 문서와 `docs/design-qa/` QA 기록에 기준·차이·수정·결과를 남김
7. 에이전트 협업과 화면 문서 템플릿: 위임 단위와 필수 메타데이터를 고정

## 화면 문서와 QA 기록

각 화면 문서에는 아래 섹션을 둔다.

```markdown
## Figma UI 기준

- Figma URL:
- 대상 프레임/노드:
- 대상 기기/프레임 크기:
- 검증 상태:
- 기준 이미지:
- QA 기록:

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
```

QA 기록은 Figma 참조, 런타임 대상, 컴포넌트 인벤토리, 라운드별 차이와 수정, 최종 화면 캡처와 결과를 포함한다. 기준 이미지와 캡처는 재검증 가능한 프로젝트 경로 또는 유지되는 외부 산출물 경로로 남긴다.

## 검증

문서 정비 완료 시 다음을 확인한다.

- `AGENTS.md`, `README.md`, `planning.md`, 워크플로 문서의 링크가 모두 유효하다.
- `planning.md`에 열거한 모든 화면 문서가 존재한다.
- 모든 화면 문서에 Figma UI 기준과 컴포넌트 분해 표가 있다.
- 워크플로 문서의 QA 템플릿과 화면 문서의 링크 규칙이 일치한다.
- 기존 Android 코드와 사용자 로컬 변경(`.DS_Store`)은 수정하지 않는다.

## 성공 기준

Android에서 새 UI 작업을 시작하는 사람은 문서만으로 Figma 기준 준비, 컴포넌트 단위 구현, 최대 5회 검증, QA 증거 기록을 수행할 수 있다. iOS와 Android는 같은 화면 문서 이름·Figma 메타데이터·QA 기록 형태를 사용하지만, 각자 플랫폼 런타임 결과를 독립적으로 검증한다.
