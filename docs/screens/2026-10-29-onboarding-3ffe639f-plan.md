# 온보딩 후속 QA 수정 계획 v1 — `3ffe639f-c7a4-805d-b27d-c6ee06e671be`

- 단계: 계획 v1 승인, 구현·자동 검증 완료, 최종 Figma 판정 Blocked. 앱 코드 수정 승인: 사용자 발언 `진행하자`(2026-10-29 KST), 이 문서의 Android 후속 댓글 범위. [QA 결과](../design-qa/2026-10-29-onboarding-3ffe639f-qa.md), [AS-IS/TO-BE 보고서](../design-qa/2026-10-29-onboarding-3ffe639f-report.html). 다음 행동: 최신 Figma 재인증 후 목표 프레임·상호작용 확인.
- Notion 원본: [온보딩 화면 안나옴](https://app.notion.com/p/3ffe639fc7a4805db27dc6ee06e671be). `In progress`, 우선순위 `중간`, Device 속성 `iOS`, 화면 `온보딩`, 담당자 `보훈 강`. 페이지 최종 수정 `2026-10-28 20:28 KST`; 댓글 `407e639f-c7a4-8045-9637-001d7e15389b` (`2026-10-28 20:28 KST`, @보훈 강). 댓글의 `[안드로이드]` 항목을 이 계획의 범위로 삼는다. 티켓 본문의 원래 “로그인 후 바로 Home 화면 이동”은 현재 댓글에서 “온보딩 화면은 적용”으로 갱신됐다.
- 코드 기준: `stage` / `7d7b915`. 기존 미커밋 `app/.DS_Store`, `fastlane/report.xml`은 범위 밖이며 보존한다.

## 원문 요구와 AS-IS

댓글은 Android에서 (1) 세 가이드의 상단 타이틀 위치 불일치, (2) 뒤로가기 아이콘을 누르면 Home으로 이동하는 문제를 지적한다. 별도로 첫 문장 `매일, 나를 위한 문장을 만나요.`의 쉼표 제거를 요청했다. 댓글은 iOS 텍스트·뒤로가기 문제도 담지만 이 Android 저장소의 구현 범위는 아니다.

재현 조건은 로그인 후 3페이지 온보딩 가이드에 진입한 상태다. Android CLI `studio render-compose-preview`로 `IntroducePage{1,2,3}DarkPreview`를 360×672dp, dark, ko, fontScale 1로 렌더링했다. semantics의 제목 상단은 1페이지 239px, 2페이지 226px, 3페이지 239px(출력 폭 945px)으로, 2페이지만 13px 위다. 현재 리비전의 Preview 이미지: [1페이지](../design-qa/assets/2026-10-29-in-progress/onboarding/fillsa-inprogress-IntroducePage1DarkPreview.png), [2페이지](../design-qa/assets/2026-10-29-in-progress/onboarding/fillsa-inprogress-onboarding-page2.png), [3페이지](../design-qa/assets/2026-10-29-in-progress/onboarding/fillsa-inprogress-IntroducePage3DarkPreview.png). Preview는 시스템 바와 실제 로그인 진입을 포함하지 않으므로 전체 화면 재현 증거는 아니다.

코드상 `IntroduceIndicatorSection.kt`의 제목 상단 padding은 2페이지에 25dp, 나머지에 30dp다. `IntroduceView.kt`의 아이콘 클릭과 `BackHandler`는 모두 `navigate(Screens.Home())`를 실행한다. `MainNavHost.kt`는 가이드에 `navigate`만 전달하고 popBackStack을 제공하지 않는다. `strings.xml`의 첫 제목에는 쉼표가 남아 있다. 뒤로가기·문구 문제는 코드 경로로 확인했으며 실제 탭 캡처는 아직 수집하지 않았다.

## Figma 기준과 미확인 사항

- 파일: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X
- 대상 Light 프레임: [1 `3088:30089`](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3088-30089), `3088:30215`, `3088:30412`; Dark 프레임: `3110:31987`, `3110:32359`, `3110:32112`. 360×720, ko. 타이틀/인디케이터는 `IntroduceIndicatorSection`, 상단 액션과 pager는 `IntroduceView`, 이미지는 `IndicatorImageSection`.
- 저장된 Figma 전체 프레임은 `docs/design-qa/artifacts/2026-10-25-full-screen-audit/figma/`의 위 노드별 PNG와 [화면 문서](0_onboarding_guide.md)에 있다. 이 캡처는 이번 댓글보다 오래됐다. 2026-10-29 Figma MCP 세션은 `UNAUTHORIZED / reauthentication required`로 실패하여, 최신 원본의 문구와 제목 좌표·프로토타입 뒤로가기 연결은 확인하지 못했다. 구현 전 최신 원본을 다시 읽어야 한다.
- 오래된 Figma 이미지에는 첫 제목에 쉼표가 있다. 댓글의 쉼표 제거 요청과 아카이브가 충돌하므로 **최신 Figma에서 제거된 사실을 확인한 뒤** 문구를 바꾼다. 최신 Figma에도 쉼표가 남아 있다면 디자인 수정 또는 명시적 예외 결정이 필요하다.
- 1페이지 아이콘의 “직전 화면”은 로그인 경로의 이전 back-stack 화면일 가능성이 높지만, 로그인 완료 후 로그인 화면으로 돌아가도 되는지 Figma prototype/제품 흐름에서 확인해야 한다.

## 해결 설계 / TO-BE

권장안은 가이드 내부 뒤로가기를 pager 상태로 처리하는 것이다. 2·3페이지에서 아이콘과 시스템 뒤로가기는 `currentPage - 1`로 이동하고, 1페이지에서는 상위 navigation의 popBackStack 계약을 사용한다. `건너뛰기`와 마지막 `필사 시작하기`는 기존 완료 흐름을 유지한다. 인위적으로 Home route를 새로 push하지 않으므로 뒤로가기 후 가이드 page가 history에 쌓이지 않는다. 1페이지 진입 경로에 이전 화면이 없는 예외는 상위 route 정책을 확인해 명시적으로 처리한다.

타이틀/서브타이틀 그룹은 페이지별 임의 padding 대신 Figma 각 프레임의 동일 기준선에 맞춘 공통 geometry로 둔다. 현재 2페이지의 25dp 분기를 제거하는 것이 최소 후보지만, 최신 Figma 전체 프레임에서 세 페이지의 실제 y·줄바꿈을 측정하고 확정한다. 이미지는 고정 visual 영역과 `ContentScale.Fit`을 보존한다. 텍스트는 최신 Figma에 반영된 문구로 `strings.xml`만 교정한다. 새로운 문구나 화면 상태를 만들지 않는다.

대안으로 뒤로가기를 모두 `navController.popBackStack()`으로 바꾸면 2·3페이지는 같은 route 안의 pager이므로 직전 페이지로 가지 못한다. 별도 화면 route로 3페이지를 나누는 방식은 navigation 변경이 지나치게 크다.

## 구현 순서와 검증

1. Figma 연결을 복구해 6개 대상 프레임의 최신 텍스트·geometry·상호작용을 확인하고 [화면 문서](0_onboarding_guide.md)에 QA 링크와 대상 상태를 갱신한다. 첫 페이지 이탈 목적지가 모호하면 제품 결정을 확인한다.
2. `IntroduceView`에 뒤로가기 callback/pager 이동을 분리하고 `MainNavHost`에서 이전 route 복귀를 연결한다. 아이콘과 시스템 뒤로가기에 동일 로직을 적용한다. 필요 시 UI action을 추출하되 ViewModel/domain/data 변경은 하지 않는다.
3. `IntroduceIndicatorSection`의 페이지별 top padding을 최신 Figma에 맞춘 공통 기준으로 정리하고, `strings.xml` 문구를 최신 원본과 맞춘다. 이미지 배치의 기존 계약은 유지한다.
4. pager 1→2→3→2→1, 아이콘·시스템 뒤로가기, 건너뛰기, 완료, 1페이지 진입/이탈을 검증한다. 두 번의 빠른 뒤로가기가 음수 페이지 또는 중복 navigation을 만들지 않는지도 확인한다.
5. Android CLI 결정적 Preview로 타이틀/인디케이터 하위 컴포넌트부터 Light/Dark, ko, 360dp를 대조한 뒤 조립 3페이지를 비교한다. 사용자가 2026-10-29에 Android QA의 시각 검증은 CLI Preview로 충분하다고 지정했으며, 시스템 바·실제 화면 전환은 별도 미검증으로 기록한다. Figma와 불일치하거나 최신 원본 접근이 안 되면 시각 검증을 `Blocked`로 남긴다.

## 위험·완료 조건

첫 페이지 popBackStack은 로그인 성공 후 로그인 UI 노출 가능성이 있다. 이 부분은 최신 Figma/제품 흐름 확인 전까지 확정 구현하지 않는다. 기존 온보딩 표시 빈도·완료 저장 정책은 건드리지 않는다. 모든 페이지의 제목 기준선·문구, 뒤로가기와 완료 흐름, 전체 프레임 Figma 비교가 통과해야 이 티켓의 Android 후속 항목을 완료로 판정한다. Notion 상태 변경은 별도 요청이 있을 때만 수행한다.
