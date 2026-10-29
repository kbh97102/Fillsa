# 온보딩 Android 후속 댓글 QA · 3ffe639f

- 출처: [Notion 원본](https://app.notion.com/p/3ffe639fc7a4805db27dc6ee06e671be), 댓글 `407e639f-c7a4-8045-9637-001d7e15389b` (SooJi, 2026-10-28 20:28 KST). 페이지와 댓글을 2026-10-29 19:21 KST에 재조회했고, [댓글 첨부](assets/2026-10-29-in-progress/onboarding/notion-comment-407e639f-attachment.png)를 보존했다.
- 기준: [계획 v1](../screens/2026-10-29-onboarding-3ffe639f-plan.md), 승인 발언 `진행하자`, 시작 HEAD `7d7b915`. Figma 파일 `VdFocqyqTgevMVCQxwAQ2X`, Dark 프레임 `3110:31987`, `3110:32359`, `3110:32112`.
- 범위: Android 가이드 3페이지 제목 정렬, 첫 문구 쉼표 제거, 아이콘·시스템 뒤로가기. iOS 요구는 이 저장소 범위 밖이다.

## AS-IS → TO-BE

| 항목 | AS-IS | TO-BE / 확인 |
|---|---|---|
| 제목 위치 | Dark CLI Preview semantics의 제목 상단: 1페이지 239px, 2페이지 226px, 3페이지 239px | 최신 headless CLI Preview: 3페이지 모두 239px. [1](assets/2026-10-29-in-progress/onboarding/IntroducePage1DarkPreview-headless.png) · [2](assets/2026-10-29-in-progress/onboarding/IntroducePage2DarkPreview-headless.png) · [3](assets/2026-10-29-in-progress/onboarding/IntroducePage3DarkPreview-headless.png), 각 component tree JSON 포함 |
| 첫 문구 | `매일, 나를 위한 문장을 만나요.` | `매일 나를 위한 문장을 만나요.`; 1페이지 Preview semantics 및 Notion 첨부와 일치 |
| 뒤로가기 | 아이콘·시스템 뒤로가기가 항상 Home 이동 | 2·3페이지는 직전 pager 페이지, 1페이지는 상위 back stack pop. 코드 경로 확인; 실제 화면 전환은 미검증 |

`IntroduceIndicatorSection`의 2페이지만 다른 제목 padding을 공통 30dp로 바꿨고, `IntroduceView`의 pager 뒤로가기와 `MainNavHost`의 상위 복귀를 연결했다. `strings.xml`에서 첫 문구의 쉼표를 제거했다. 기존 건너뛰기/완료 경로는 그대로다.

## 검증과 제한

- Android CLI `studio render-compose-preview`를 360dp, dark, ko의 결정적 `IntroducePage1/2/3DarkPreview`에 각각 실행했다. 첫 렌더에서 2페이지가 이전 소스를 보여 사용자에게 빌드를 요청했고, 빌드 완료 후 재렌더에서 모두 239px을 확인했다. CLI 1.1.16717390으로 갱신한 뒤 `android preview render :presentation --fqn=... --component-tree`로 소스 직접 빌드/렌더를 재실행했으며, JSON에서 세 제목 상단 239px·쉼표 없는 문구를 확인했다. 이후에는 이 headless 경로를 우선 사용한다.
- `./gradlew :presentation:testDebugUnitTest --offline`: 전체 138건 통과(0 실패/오류/skip). `./gradlew :app:assembleDebug --offline`: 성공. 이는 UI 클릭의 실제 화면 전환을 증명하지 않는다.
- 저장된 2026-10-25 Figma 캡처는 댓글보다 오래되어 첫 문구에 쉼표가 있다. 최신 Figma MCP는 `UNAUTHORIZED / reauthentication required`로 조회 불가다. 새 댓글 첨부에서는 쉼표 없는 문구를 직접 확인했지만 최신 6개 Figma 프레임의 좌표·상호작용은 확인하지 못했다. **최종 Figma UI 판정: Blocked.**
- Android CLI Preview는 시스템 바·IME·실제 뒤로가기 전환을 렌더링하지 않는다. 사용자 요청에 따라 별도 Computer Use/기기 캡처는 하지 않았다. 기능 경로는 코드에서 확인했으나 실제 탭 동작은 미검증이다.

Notion 티켓 상태는 `In progress` 그대로이며, 보고서: [AS-IS/TO-BE HTML](2026-10-29-onboarding-3ffe639f-report.html).
