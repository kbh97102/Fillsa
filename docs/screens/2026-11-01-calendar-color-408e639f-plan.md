# Calendar 미필사 안내 색상 QA 계획 v1

## 선택·승인 기록

- Notion: [필사하지 않음 > text, area 컬ㄹ러](https://app.notion.com/p/text-area-408e639fc7a480da9407d6a75c64df65), `408e639f-c7a4-80da-9407-d6a75c64df65`.
- Notion 상태/우선순위/기기: Not started / 중간 / Android. 최근 수정: 2026-10-29 07:19 UTC.
- 계획 버전/단계: v1 / 승인, 수정·검증 진행.
- 구현 승인: 사용자의 2026-11-01 발언 “좋아 세가지 모두 진행, stage로부터 각자의 워크트리를 만들어서 진행하자”. 승인 범위는 이 v1의 Android Calendar 색상 수정·검증·독립 보고이며 `stage`의 `c14b4e6`에서 만든 `codex/qa-calendar-color-408e639f` 워크트리에서 수행한다.
- 코드 기준: `c14b4e6`. 시작 시 기존 미커밋 변경 `app/.DS_Store`, `fastlane/report.xml`이 있었으며 이 이슈와 무관하다.

## 원문과 재현 근거

- 본문은 비어 있다. SooJi의 댓글은 Android의 해당 영역 색상이 Figma와 다르다고 제보하고 Figma 캡처를 첨부했다. [제보 캡처](../design-qa/artifacts/2026-11-01-qa-calendar-color/notion-figma-attachment.png).
- 대상: Calendar에서 필사하지 않은 날짜를 선택한 다크 상태. Light의 같은 영역은 이번 제보 대상이 아니다.
- AS-IS: 현재 소스를 Android CLI로 `AuditFigma303924906` Preview 렌더했다. [수정 전 이미지](../design-qa/artifacts/2026-11-01-qa-calendar-color/android-before-preview.png), [component tree](../design-qa/artifacts/2026-11-01-qa-calendar-color/android-before-component-tree.json). 미필사 안내는 흰색이고 아래 명언 카드 테두리는 보라색이다. SVG 캐릭터와 일부 아이콘이 이 Preview에서 비어 있어 해당 자산의 시각 결과는 이 증거로 판단하지 않는다.
- Figma: [다크 기본 전체 프레임 `3039:24906`](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3039-24906), 360×816, ko-KR, Dark. [직접 추출한 전체 기준 이미지](../design-qa/artifacts/2026-11-01-qa-calendar-color/figma-dark-basic-3039-24906.png). 미필사 안내 `3039:25079`/문구 `3039:25096`은 `#FFCB5C`, 카드 `3039:25073`/내부 `3039:25074`는 `#424242` 바탕과 `#FFCB5C` 1px 테두리다. Figma design context에서 확인했다.
- 재현 순서: 다크 모드의 Calendar에서 필사하지 않은 날짜를 선택하고 미필사 안내와 명언 카드의 색상을 Figma와 비교한다. 동일 조건의 앱 전체 런타임 캡처는 이번 계획 작성 단계에서 새로 확보하지 않았으며 Preview가 현재 코드 불일치를 증명한다.

## 원인 분석

- `CalendarQuoteSection.kt`의 `CalendarEmptyDay`는 두 안내 `Text`에 다크 모드일 때 `Color.White`를 지정한다.
- 같은 파일의 `CalendarQuotePreviewCard`는 테마와 무관하게 `R.color.purple01`을 테두리에 지정한다.
- `R.color.yellow02`는 이미 `#FFCB5C`로 정의되어 있고 다크 테마의 `tertiary`에도 연결되어 있다. 색상 누락은 자산이나 API 문제가 아니라 두 Compose 색상 분기/지정의 문제다.
- 카드 날짜·명언의 흰 텍스트와 `#424242` 표면은 Figma의 대상 노드와 일치하므로 변경 대상이 아니다.

## 해결 설계 / TO-BE

- 권장안: `CalendarEmptyDay`의 다크 안내 문구 2개와 `CalendarQuotePreviewCard`의 다크 테두리에 기존 `R.color.yellow02`를 사용한다. Light 분기는 현재 보라색을 유지한다.
- 컴포넌트 흐름: `CalendarQuoteSection`이 `presentation.completed == false`일 때 미필사 안내와 80dp 명언 카드를 조립한다. `darkMode`를 두 하위 컴포넌트에 전달하는 현재 계약을 그대로 사용한다. 상태·액션·ViewModel·domain/data/API 변경은 없다.
- 대안: 화면별 `Color(0xFFFFCB5C)` 상수를 직접 추가할 수 있으나 기존 동일 토큰과 중복된다. 테마의 공통 `tertiary`를 사용해도 되지만 이번 컴포넌트의 색상 의도를 명시적으로 유지하려면 기존 리소스가 더 좁은 변경이다.
- 경계: 필사 완료 날짜의 카드/액션, Light 미필사 상태, 다른 Calendar 색상, SVG 자산은 이 이슈 범위에서 수정하지 않는다. 공유 색상 토큰의 값은 바꾸지 않는다.

## 변경 순서와 검증

1. `presentation/ui/calendar/CalendarQuoteSection.kt`에서 두 색상 지정만 다크/라이트 조건으로 맞춘다.
2. 다크 미필사 안내·카드 하위 Preview를 결정적 fixture로 준비해 Figma 노드 `3039:25079`, `3039:25073`과 대조한다. 같은 컴포넌트의 Light Preview `2985:22183`, `2985:22145`로 회귀 확인한다.
3. 하위 결과가 맞으면 360×816 다크 Calendar 조립 Preview와 Figma 전체 기준 이미지를 비교하고 component tree를 보관한다. 프로젝트 워크플로우에 따라 동일 조건의 실제 에뮬레이터 전체 프레임도 확인한다. 시스템 바/캐릭터 SVG가 Preview에서 빠지는 문제는 런타임 비교에 기록한다.
4. 관련 빌드와 Calendar 회귀 검증을 실행하고 `docs/design-qa/2026-11-01-calendar-color-408e639f-qa.md`에 결과를 기록한다. 결과가 확보되면 이 이슈만의 AS-IS/TO-BE HTML 보고서를 작성한다.

## 완료 조건·위험

- 다크 안내 글자와 카드 테두리가 Figma `#FFCB5C`이고, Light의 기존 보라색이 유지되어야 한다. 클릭 영역·명언 텍스트·날짜 상태는 변하지 않아야 한다.
- 전체 런타임 프레임을 Figma와 비교하지 못하면 UI 최종 Pass로 처리하지 않는다. Figma와 Notion 제보의 기준 버전이 달라지면 구현 전에 차이를 다시 확인한다.
- 복구는 이 이슈에서 변경한 색상 분기만 되돌린다. 기존 사용자 변경은 건드리지 않는다.
- 제품 결정 질문: 현재 확인한 Figma 노드와 Notion 캡처 사이의 대상 색상 충돌은 없다.
