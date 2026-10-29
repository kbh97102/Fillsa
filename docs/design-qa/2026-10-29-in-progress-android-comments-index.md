# Notion QA `In progress` Android 후속 댓글 — 2026-10-29

조회 기준: Notion 페이지와 페이지 댓글을 2026-10-29에 다시 읽었다. `Device`가 iOS여도 댓글 본문이 Android 수정 요청을 명시한 두 티켓을 Android 저장소의 작업 후보로 삼았다. 티켓 상태는 그대로다.

| 이슈 | 원본 | 우선순위 | Android 댓글 요구 | 계획/단계 |
|---|---|---|---|---|
| 온보딩 화면 안나옴 `3ffe639f-c7a4-805d-b27d-c6ee06e671be` | [Notion](https://app.notion.com/p/3ffe639fc7a4805db27dc6ee06e671be) | 중간 | 제목 위치, 뒤로가기, 쉼표 제거 | [계획 v1](../screens/2026-10-29-onboarding-3ffe639f-plan.md) · [QA](2026-10-29-onboarding-3ffe639f-qa.md) · [AS-IS/TO-BE](2026-10-29-onboarding-3ffe639f-report.html) · 구현/자동 검증 완료, Figma 최종 판정 Blocked |
| 캘린더 > 복사, 공유, 이미지 등록 `3f4e639f-c7a4-80de-8625-cff94440d08d` | [Notion](https://app.notion.com/p/3f4e639fc7a480de8625cff94440d08d) | 높음 | 좋아요·이미지 등록/보기의 Home 이동 제거, Calendar 내부 동작 | [계획 v1](../screens/2026-10-29-calendar-actions-3f4e639f-plan.md) · [QA](2026-10-29-calendar-actions-3f4e639f-qa.md) · [AS-IS/TO-BE](2026-10-29-calendar-actions-3f4e639f-report.html) · 구현/자동 검증 완료, 실연동·Figma 최종 판정 Blocked |

다른 새 @보훈 댓글 두 건은 iOS 필사 작성 화면의 문구 굵기·복사·색상([원본](https://app.notion.com/p/3ffe639fc7a480269f40daf8cf01f20c))과 iOS 저장 팝업의 문구 굵기·캐릭터([원본](https://app.notion.com/p/3ffe639fc7a48056b876d1ab3e28f520))를 요청한다. 이 Android 작업의 변경 범위에는 넣지 않았다.

Figma 파일 `VdFocqyqTgevMVCQxwAQ2X`는 2026-10-29 MCP 재조회 시에도 재인증 오류가 나서 최신 원본을 열지 못했다. 보고서는 Notion 페이지·댓글을 19:29 KST에 재조회한 원문, 기존 Figma 캡처, 현재 코드와 Android CLI 1.1 headless Preview에 근거한다. 실연동·실제 화면 전환은 Preview 범위 밖이다. Notion 상태는 `In progress` 그대로다.
