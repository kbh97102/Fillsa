# Android 로그인 설명 중앙 정렬 · 계획 v1

- Notion: [로그인 > 구글 계정으로 시작하기 누락](https://app.notion.com/p/3ffe639fc7a480b0b19cf56f597ddba9) (`3ffe639f-c7a4-80b0-b19c-f56f597ddba9`). 후속 댓글의 Android 안내문 중앙 정렬 제보.
- 승인: 사용자가 2026-11-08에 “Android 로그인: LoginContent 설명 Text를 화면 폭에 맞춰 가운데 정렬하고 Figma Light/Dark의 줄바꿈과 전체 프레임을 재검증한다”라고 이 수정안 자체를 지시했다.
- Figma: [Light `2929:3723`](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-3723) 설명 `2929:3744`, [Dark `2929:5815`](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-5815) 설명 `2929:5836`. 360×720 전체 프레임, 좌우 20dp 안의 320dp 설명 영역, Pretendard Regular 16/24, 두 줄 모두 중앙 정렬.
- AS-IS: `LoginView.kt`의 `LoginContent` 설명 `Text`에는 폭·`textAlign`이 없어 360dp에서 둘째 줄이 왼쪽에 붙는다. [수정 전 Preview](../design-qa/artifacts/2026-11-08-login-description-center/asis-light.png).
- 변경: 해당 `Text`에 `Modifier.fillMaxWidth()`와 `TextAlign.Center`를 적용한다. 문구, 글꼴, 간격, 로그인 action/API, 기타 화면은 변경하지 않는다. `LoginView`와 기존 Audit Preview가 같은 `LoginContent`를 호출하므로 한 곳에서 수정한다.
- 검증: Android CLI의 Light/Dark 360×672 Preview PNG·tree로 문구 두 줄과 가로 중심을 확인한 뒤 360dp 실행 화면 전체 프레임을 Figma와 비교한다. 실제 런타임 조건 차이 또는 기존 다른 불일치는 별도로 기록한다. QA는 [로그인 설명 중앙 정렬 QA](../design-qa/2026-11-08-login-description-center-qa.md)에 남긴다.

[HTML 계획](2026-11-08-login-description-3ffe639f-plan.html)
