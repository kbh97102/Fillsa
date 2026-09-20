# Figma 기반 UI 개편 워크플로우

이 문서는 기존 화면을 포함한 **모든 Android UI 수정 작업**의 필수 절차다. `AGENTS.md`의 지시에 따라 UI를 조사·구현·리뷰·검증하는 모든 작업자는 작업 전에 이 문서를 끝까지 읽어야 한다.

## 1. 기준과 완료 조건

- Figma는 UI의 유일한 정답이다. iOS 구현, 기존 Android 화면, 임의의 플랫폼 관례는 UI 판단 또는 시각 검증 기준으로 사용할 수 없다.
- UI 작업은 Figma 파일 URL과 대상 프레임/노드 ID가 화면 문서에 기록되기 전에는 시작할 수 없다.
- 완료는 “구현했다”가 아니라, 영향을 받은 모든 컴포넌트와 **전체 조립 런타임 프레임**이 Figma와 대조되어 불일치가 없다고 기록된 상태를 뜻한다. 자산·빌드·하위 컴포넌트 확인은 최종 승인에 해당하지 않는다.
- Figma에 없는 UI, 상태, 문구, 애니메이션, 상호작용은 추가하지 않는다. 디자인에서 해석할 수 없는 결정은 사용자에게 확인한다.

## 2. 작업 시작 게이트

UI 작업을 받으면 다음 순서로 준비한다.

1. 대상 화면의 `docs/screens/*.md` 문서를 찾는다. 없으면 먼저 만든다.
2. 화면 문서의 `Figma UI 기준` 섹션에 아래 정보를 기록한다.
   - Figma 파일 URL
   - 대상 프레임/노드 ID와 이름
   - 대상 기기·프레임 크기, 밀도, API 레벨, 색상 모드, 언어, 표시 상태
   - Figma MCP로 추출한 전체 프레임 기준 이미지의 경로 또는 재현 가능한 참조 정보. 루트 배경, 상태 표시줄, 안전 영역, 제스처/내비게이션 영역을 포함한다.
3. Figma MCP로 대상 프레임과 하위 계층을 확인하고, 검증에 쓸 전체 프레임 기준 이미지를 추출한다. 기준 이미지는 구현 전에 확보한다.
4. 한 프레임에 여러 상태가 있으면 각각을 검증 대상으로 목록화한다. 예: light/dark, 로그인/비로그인, 선택/비선택, 팝업 표시 상태.
5. Android Studio에서 프로젝트가 열린 상태인지 Android CLI로 확인한다.

```bash
android studio check
```

- `Fillsa`가 `READY`여야 Compose Preview 검증을 시작할 수 있다.
- `android`가 PATH에 없으면 `~/.local/bin/android`를 사용한다.
- Android Studio가 열려 있지 않거나 프로젝트가 `READY`가 아니면 Preview 검증은 `Blocked`로 기록하고, Preview 없이 컴포넌트 통과를 선언하지 않는다.

Figma URL·노드 ID·기준 이미지 중 하나라도 없으면 이 게이트는 실패다. 구현이나 추정으로 진행하지 말고 필요한 정보를 요청한다.

## 3. 컴포넌트 우선 개발

큰 화면을 통째로 구현하거나 한 번에 검증하지 않는다. Figma의 계층과 반복 구조를 기준으로 화면을 Compose 컴포넌트로 나눈다.

각 컴포넌트에는 다음을 화면 문서에 남긴다.

| 항목 | 기록 내용 |
|---|---|
| 이름 | Figma 레이어/영역과 대응되는 Compose 컴포넌트 이름 |
| Figma 범위 | 해당 컴포넌트의 노드 ID와 기준 이미지 영역 |
| 책임 | 표시하는 UI와 지원하는 Figma 상태 |
| 조립 위치 | 부모 화면 또는 상위 컴포넌트 |
| 검증 상태 | 미검증, 수정 필요, 부분 통과(최종 수용 아님), 최종 통과 중 하나 |

개발 순서는 하위·독립 컴포넌트부터 상위 컨테이너와 화면 조립 순서로 진행한다. ViewModel의 UI state·action·effect 경계도 이 UI 책임에 맞추되, 구조를 이유로 Figma 결과를 바꾸지 않는다.

각 컴포넌트는 구현 직후 Android CLI로 Compose Preview를 렌더링하고 해당 Figma 영역과 먼저 비교·수정한다. 개별 검증을 통과하지 않은 컴포넌트를 화면에 조립한 뒤 전체 화면 검증으로 넘기지 않는다.

### Compose Preview 검증

- 대상 컴포넌트 또는 화면에는 검증할 Figma 상태마다 결정적 `@Preview`를 둔다. `widthDp`, `heightDp`, `uiMode`, `locale`, `fontScale`을 대상 조건에 맞추고, 네트워크·현재 시각·사용자 저장 데이터에 의존하지 않는 fixture state를 사용한다.
- Preview 전용 UI를 따로 복제하지 않는다. Preview는 production composable에 상태와 callback만 주입한다.
- 다음 명령으로 이미지와 semantics를 함께 확인한다.

```bash
android studio render-compose-preview \
  <compose-file-path> <preview-composable-name> \
  --project Fillsa \
  --output-image-file=<persistent-preview-png-path> \
  --print-semantics
```

- 출력 이미지는 `docs/design-qa/artifacts/<date>-<screen>-<topic>/`처럼 재검증 가능한 프로젝트 경로에 저장한다.
- Preview로 레이아웃, 문구, 이미지, 색상, typography, light/dark 및 정적 표시 상태를 먼저 검증한다.
- Preview는 실제 시스템 바, 제스처/내비게이션 영역, IME, 권한 UI, Android 버전별 동작, navigation transition, 런타임 리소스 선택을 최종 증명하지 않는다. 이 항목과 전체 조립 화면은 실제 기기 또는 에뮬레이터 캡처로 검증한다.

## 4. 검증과 수정 루프

검증은 매 라운드에서 다음 순서를 지킨다.

1. 영향을 받은 각 컴포넌트와 화면 상태의 Compose Preview를 Android CLI로 렌더링하고 Figma의 대응 영역과 비교한다.
2. Preview에서 확인한 문구, 이미지, 정렬, 여백, 크기, 색상, typography, 표시 상태의 불일치를 먼저 수정한다.
3. 하위 Preview가 통과하면 Figma 기준 이미지와 같은 대상 기기 크기·밀도·API 레벨·색상 모드·언어·상태로 Android 화면의 **전체 프레임**을 캡처한다. 기준·런타임 이미지 모두 루트 배경, 상태 표시줄, 안전 영역, 제스처/내비게이션 영역을 포함한다.
4. 영향을 받은 컴포넌트와 조립 화면 전체를 오버레이 또는 나란히 비교한다. 크롭 경계를 기록하고 시스템 표면 색상·상태/내비게이션 바 아이콘 모양도 Figma와 비교한다.
5. 불일치를 화면 문서와 QA 기록에 구체적으로 남긴다. 대상 노드/컴포넌트, Preview 명령·이미지, 런타임 캡처, 차이, 수정 위치, 수정 결과를 포함한다.
6. 불일치가 있으면 해당 컴포넌트를 수정하고 1번부터 재검증한다.

비교 항목은 최소한 다음을 포함한다.

- 프레임 크기, 시스템 바와 제스처 영역을 제외한 콘텐츠 시작 위치, 정렬, 여백, 크기, 스크롤 범위
- 색상, 투명도, 테두리, 그림자, 모서리 반경
- 폰트, 크기, 무게, 줄 높이, 줄바꿈, 문구
- 아이콘·이미지의 원본 자산, 크기, 위치, 상태별 변형
- 루트 배경, 상태/내비게이션 바 색상, 상태/내비게이션 바 아이콘의 밝기·모양, 안전 영역과 제스처 영역
- 표시/숨김 조건, 선택 상태, 다이얼로그·바텀시트 등 Figma에 정의된 모든 대상 상태

### 최대 5회 규칙

- 최초 구현 후 비교를 **1차 검증**으로 센다. 수정 후 수행하는 완전한 재비교가 2~5차 검증이다.
- 한 차수는 영향을 받은 Preview 재검증과 전체 조립 런타임 재검증을 모두 포함한다. Preview만 확인한 시도는 차수 완료나 최종 통과로 세지 않는다.
- 한 차수의 통과 조건은 영향을 받은 컴포넌트와 조립 화면 전체에서 모두 불일치가 없는 것이다.
- `Pass`는 전체 조립 런타임 프레임 캡처와 Figma 기준의 비교가 기록된 경우에만 가능하다. 헤드리스 환경, 도달 불가 상태 등으로 캡처할 수 없으면 `Blocked`이며, 자산·빌드·부분 비교를 `Pass` 또는 최종 승인으로 기록하지 않는다.
- 5차 검증 뒤에도 차이가 있으면 작업을 완료로 표시하지 않는다. 남은 차이, 원인, 시도한 수정, Figma 기준을 QA 기록에 남기고 사용자에게 다음 판단을 요청한다.
- 새로 발생한 조립 단계의 불일치는 해당 하위 컴포넌트를 다시 검증해야 한다. 전체 화면 통과만으로 개별 컴포넌트 검증을 생략할 수 없다.

## 5. 검증 증거와 기록

각 UI 변경에는 `docs/design-qa/YYYY-MM-DD-<screen>-<topic>-qa.md` QA 기록을 새로 만들거나 기존 기록을 갱신한다. 화면 문서는 그 QA 기록으로 링크한다.

QA 기록에는 아래를 반드시 남긴다.

```markdown
# <Screen> Figma UI QA

## Reference
- Figma URL: <URL>
- Target frames/nodes: <node ID and name>
- Reference image(s): <path or reproducible Figma MCP output>
- Full-frame reference: <path; root background/status bar/safe area/gesture or navigation area included>
- Preview command: <android studio render-compose-preview command>
- Preview image(s): <persistent project path; one per target state>
- Preview conditions: <widthDp, heightDp, uiMode, locale, fontScale, fixture state>
- Runtime target: <device/emulator, resolution or dp size, density, API level, color mode, locale, state>
- Capture platform/device/emulator: <platform and device/emulator>

## Component inventory
| Component | Figma node | Target state | Final result |
|---|---|---|---|

## Validation rounds
### Round 1
| Scope | Difference | Fix | Result |
|---|---|---|---|

- Preview comparison: <Figma area / Preview image, exact differences>
- Comparison: <overlay / side-by-side, crop boundaries, exact differences>

## Final assembled-screen result
- Final runtime capture: <path; full frame including root background/status bar/safe area/gesture or navigation area>
- Comparison: <overlay / side-by-side and crop boundaries>
- Result: Pass / Blocked after round 5
- Remaining differences: none / <specific list>
```

이미지 파일은 재검증 가능한 프로젝트 내 경로 또는 유지되는 외부 산출물 경로를 기록한다. 일시 경로만 남겨 후속 검증자가 기준 또는 결과를 볼 수 없게 하지 않는다.

## 6. 에이전트 협업 규칙

- 별도 `SUBAGENTS.md`는 사용하지 않는다. `AGENTS.md`와 이 문서가 부모·하위 작업자 모두의 공통 규칙이다.
- 부모 작업자는 UI 작업을 위임할 때 Figma URL, 프레임/노드 ID, 맡길 컴포넌트 범위, 대상 상태를 전달한다.
- 작업자는 이 문서를 읽고 자신에게 할당된 컴포넌트만 구현·검증한다. Figma 범위 밖의 UI 결정을 내리지 않는다.
- 부모 작업자는 하위 컴포넌트의 검증 증거를 취합한 뒤, 조립 화면의 최종 검증을 별도로 수행한다. 하위 컴포넌트 통과만으로 화면 완료를 선언할 수 없다.
- 부모 작업자만 하위 작업 이후 전체 조립 런타임 프레임의 최종 승인(`Pass`)을 할 수 있다. 작업자의 자산·빌드·부분 컴포넌트 확인은 반드시 비최종 검증으로 표시한다.

## 7. 화면 문서에 추가할 템플릿

모든 UI 수정 전 해당 화면 문서에 아래 섹션을 추가하거나 최신화한다.

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

이 섹션은 작업의 체크리스트가 아니라 완료 판단의 증거다. 비어 있는 필수 항목이 있으면 UI 작업은 시작 또는 완료할 수 없다.
