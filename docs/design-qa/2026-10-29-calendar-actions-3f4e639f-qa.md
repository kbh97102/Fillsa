# Calendar 액션 Android 후속 댓글 QA · 3f4e639f

- 출처: [Notion 원본](https://app.notion.com/p/3f4e639fc7a480de8625cff94440d08d), 원래 Figma QA 링크 댓글 `3f4e639f-c7a4-80b8-8b6c-001d8f216c19` 및 Android 후속 댓글 `407e639f-c7a4-8027-b429-001d64515583` (SooJi, 2026-10-28 19:59 KST). 페이지·댓글 2건을 2026-10-29 19:21 KST에 재조회했다. 첨부 없음.
- 기준: [계획 v1](../screens/2026-10-29-calendar-actions-3f4e639f-plan.md), 승인 발언 `진행하자`, 시작 HEAD `7d7b915`. Figma 완료 프레임 `2987:22796`(이미지 없음), `2985:22510`(이미지 있음), 이미지 보기 `3223:6435`/`3223:6600`.
- 범위: 완료 날짜 카드의 좋아요·이미지 등록/보기. 복사·공유와 카드 자체의 Home 이동은 변경하지 않았다.

## AS-IS → TO-BE

| 항목 | AS-IS | TO-BE / 확인 |
|---|---|---|
| 세 액션 탭 | `CalendarView`의 좋아요·이미지 콜백이 `ClickBottomQuote`를 전달하여 `Screens.Home`으로 이동 | 각각 `ClickLike`, `ClickImage`로 분리. 새 액션의 ViewModel 단위 테스트에서 Home 이동 effect 없음 |
| 좋아요 | Calendar에서 mutation 없음 | 회원은 기존 like API, 비회원은 로컬 저장소 호출. 성공 시 선택 날짜와 월간 좋아요 수 갱신; 실패는 표시 유지. 회원/비회원 테스트 통과 |
| 이미지 등록/보기 | Calendar에서 mutation/dialog 없음 | 선택 날짜 명언의 공유 `ImageDialog`를 Calendar 위에 표시. 이미지 picker→가공→업로드 및 삭제 확인→삭제; 성공 시 월 캐시 반영. ViewModel 업로드/삭제 테스트 통과. 버튼 전체 탭에서 picker를 열도록 공유 대화상자의 버튼 callback 교정 |

Android CLI headless 렌더에서 360dp light/ko의 완료·이미지 없음 [Preview](assets/2026-10-29-in-progress/calendar/AuditFigma298722796-headless.png)과 완료·이미지 있음 [Preview](assets/2026-10-29-in-progress/calendar/AuditFigma298522510-headless.png)를 소스 직접 빌드 후 렌더링했다. Component tree에는 두 상태의 `좋아요`와 각각 `이미지 등록`/`이미지 보기`가 나타난다. 공유 [이미지 대화상자 content Preview](assets/2026-10-29-in-progress/calendar/ImageDialogPreview-headless.png)도 360dp dark/ko에서 정상 렌더링했다. 액션 연결은 Preview 이미지로 관찰할 수 없어 ViewModel 테스트로 검증했다.

## 검증과 제한

- 공유 대화상자 버튼 변경과 진단용 Preview 제거 후 `./gradlew :presentation:testDebugUnitTest --offline`: 전체 138건 통과(0 실패/오류/skip), Calendar 통합 테스트 8건 포함. `./gradlew :app:assembleDebug --offline`: 성공. `android preview render :presentation`에서 2개 Calendar 상태와 대화상자 PNG/JSON 생성 성공.
- 기존 Studio 연결 렌더는 새 Preview 함수를 찾지 못하고 빈 PNG를 반환했다. IDE 인덱스/빌드 상태에 의존하는 경로 대신 CLI 1.1.16717390의 headless `android preview render`를 사용하니 새 함수와 변경된 대화상자가 정상 렌더링됐다. Studio CLI가 오류 문구를 내면서 종료 코드 0을 반환했으므로 PNG·component tree 내용까지 검증한다.
- 저장된 2026-09-08 Figma 2개 완료 프레임과 현재 Preview를 비교했다. CLI Preview의 일부 SVG 아이콘과 썸네일이 렌더되지 않아 해당 시각 요소의 일치 판정은 못 한다. 최신 Figma MCP는 재인증 오류로 접근 불가다. **최종 Figma UI 판정: Blocked.**
- 실제 회원 API/갤러리 선택/파일 변환/화면 전환 및 Calendar 재진입 상태는 CLI Preview와 fake 기반 테스트만으로 검증되지 않았다. **기능은 자동 테스트 통과, 실연동은 미검증.** 사용자 요청에 따라 Computer Use는 사용하지 않았다.

Notion 티켓 상태는 `In progress` 그대로이며, 보고서: [AS-IS/TO-BE HTML](2026-10-29-calendar-actions-3f4e639f-report.html).
