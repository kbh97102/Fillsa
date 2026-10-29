# Calendar 액션 후속 QA 수정 계획 v1 — `3f4e639f-c7a4-80de-8625-cff94440d08d`

- 단계: 계획 v1 승인, 구현·자동 검증 완료, 최종 Figma·실연동 판정 Blocked. 앱 코드 수정 승인: 사용자 발언 `진행하자`(2026-10-29 KST), 이 문서의 Android 후속 댓글 범위. [QA 결과](../design-qa/2026-10-29-calendar-actions-3f4e639f-qa.md), [AS-IS/TO-BE 보고서](../design-qa/2026-10-29-calendar-actions-3f4e639f-report.html). 다음 행동: 최신 Figma 재인증 및 실제 API/갤러리 동작 확인.
- Notion 원본: [캘린더 > 복사, 공유, 이미지 등록](https://app.notion.com/p/3f4e639fc7a480de8625cff94440d08d). `In progress`, 우선순위 `높음`, Device 속성 `iOS`, Category `기능`. 댓글 `407e639f-c7a4-8027-b429-001d64515583` (`2026-10-28 19:59 KST`, @보훈 강)은 Android의 좋아요·이미지 등록·이미지 보기가 Home으로 이동하며 “캘린더 화면 내에서 작동”해야 한다고 명시한다. 원래 Figma QA 링크는 [노드 `3493:1347`](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3493-1347). 페이지 최종 수정 `2026-10-28 19:52 KST`이며 댓글은 그 7분 뒤 작성됐다.
- 코드 기준: `stage` / `7d7b915`. 기존 미커밋 `app/.DS_Store`, `fastlane/report.xml`은 범위 밖이며 보존한다.

## 원문 요구와 AS-IS

재현 조건은 Calendar에서 필사 완료 날짜를 선택해 하단 명언 카드를 보이는 상태다. 좋아요 또는 이미지 등록/보기를 누르면 Calendar에 남아 상태가 바뀌거나 이미지 modal이 떠야 하는데 현재 Home으로 이동한다. `CalendarView.kt`에서 세 버튼의 `onLike`와 `onImage`가 모두 `CalendarAction.ClickBottomQuote`를 보낸다. `CalendarViewModel.kt`의 해당 action은 선택 날짜를 담은 `Screens.Home` 이동 effect를 낸다. 따라서 코드상 이동 경로는 확정이다. 제보 당시 실기기 탭 캡처와 서버 연동 재현은 아직 없으며, 현재 에뮬레이터에서 동일 데이터 상태도 재현하지 않았다.

`docs/screens/3_calendar.md`의 기존 API 계약은 이 Home 이동을 명시적으로 유지한다고 기록한다. 새 QA 댓글과 충돌하므로 승인 후 계약도 갱신해야 한다. `CalendarQuoteSection`은 복사·공유·좋아요·이미지 콜백을 이미 분리했고, 완료 카드의 `likeYn`과 `imagePath`를 이미 표시한다. 원인 후보는 UI 버튼의 공통 Home 액션 연결이며, 실제 mutation 경로는 Calendar에 없다. Home에는 좋아요 API/로컬 저장, 이미지 dialog·업로드·삭제가 이미 있으나 Home ViewModel을 Calendar에서 직접 호출하면 화면 상태가 결합된다.

## Figma 기준과 미확인 사항

- 파일: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X
- Calendar 대상: [완료·이미지 없음 `2987:22796`](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2987-22796), [완료·이미지 있음 `2985:22510`](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2985-22510), 액션 행 `2987:22950`, `3207:2950`, 이미지 보기 `3207:2973`. 360×1101, light, ko. 화면 출처와 기존 Figma 캡처는 [Calendar 화면 문서](3_calendar.md) 및 `docs/design-qa/assets/calendar-figma-2929-13366/2026-09-08/`에 있다.
- 이미지 modal 후보는 [Home 이미지 보기 `3223:6435`](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3223-6435)와 [템플릿 보기 `3223:6600`](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3223-6600)이다. Calendar 전용 modal이 있는지는 확인이 필요하다. 2026-10-29 Figma MCP는 재인증 오류로 최신 원본을 읽지 못했다. 구현 전 Calendar 버튼과 modal·삭제 확인 상태를 Figma에서 확인한다.

## 해결 설계 / TO-BE

권장안은 Calendar가 선택 날짜의 액션을 직접 소유하게 하는 것이다. 좋아요는 선택 날짜/`dailyQuoteSeq`로 회원 API `PostLikeUseCase`를 호출하고, 비회원은 기존 로컬 좋아요 UseCase를 사용한다. 성공 시 Calendar 월 캐시의 해당 record `likeYn`과 월간 좋아요 집계를 일관되게 갱신하고, 날짜를 바꾸거나 인증 상태가 바뀐 뒤 늦게 도착한 응답이 다른 날짜를 수정하지 않도록 요청 토큰/대상을 고정한다. 실패 시 기존 표시를 복구하거나 오류를 표시하며 무조건 성공 상태로 남기지 않는다. Calendar를 떠났다가 돌아와도 서버/로컬 재조회 결과가 일치해야 한다.

이미지 등록/보기는 선택 날짜의 명언·저자·`imagePath`로 Figma에 맞는 이미지 dialog를 Calendar 위에 표시한다. 등록/변경은 picker→이미지 파일 가공→기존 `PostUploadImageUseCase`를, 삭제는 확인 dialog→`DeleteUploadImageUseCase`를 사용한다. 성공 후 선택 record의 `imagePath`와 액션의 썸네일/라벨을 갱신한다. 비회원 접근 제약은 Home과 동일한 로그인 안내 계약을 Calendar 안에서 먼저 처리하되, 로그인 버튼을 사용자가 명시적으로 누르기 전 Home으로 이동하지 않는다. `ImageDialog` 같은 Compose 표시는 재사용할 수 있으나 Calendar ViewModel에 Home ViewModel을 주입하지 않는다. 기존 복사·공유는 이 티켓의 새 Android 댓글 범위 밖이고 현재 흐름을 유지한다.

대안인 Home 화면 위에 Calendar처럼 보이는 overlay를 얹는 방식은 navigation과 상태가 실제 Calendar가 아니어서 QA의 “캘린더 화면 내” 요구를 만족하지 않는다. 즉시 UI만 좋아요로 바꾸고 저장을 생략하면 재진입·월간 집계에서 불일치한다.

## 구현 순서와 검증

1. Figma 원본의 Calendar 2상태, 이미지 modal/확인 상태를 확인하고 [화면 문서](3_calendar.md)의 QA 링크·대상 상태·제품 계약에서 Home 이동 문구를 갱신한다. Calendar 전용 modal/비회원 동작이 Figma에서 불분명하면 제품 결정을 확인한다.
2. `CalendarAction`에 좋아요·이미지 열기·업로드·삭제 등 각 사용자 이벤트를 분리한다. `CalendarViewModel`이 선택 날짜와 quote ID, 로그인 상태, 월 캐시를 소유하도록 한다. 필요한 UseCase는 domain 경계로 주입하고 data repository의 기존 API 구현을 재사용한다.
3. `CalendarView`가 기존 `CalendarQuoteSection` 콜백을 새 action에 연결하고 이미지 dialog/picker 및 effect를 처리한다. `onOpenQuote`의 선택 문장 Home 진입은 별도 액션으로 유지한다. Home 코드와 공유할 파일 처리 함수는 필요한 경우에만 추출한다.
4. 회원/비회원 좋아요 토글, 이미지 등록·보기·변경·삭제, 취소·API 실패·날짜 변경 중 응답·연타·인증 변경·재진입을 테스트한다. 기존 `selected Calendar day → Home` 테스트는 명언 카드 탭에만 남기고, 좋아요/이미지 탭이 이동 effect를 내지 않는 테스트를 추가한다.
5. Android CLI로 액션 행과 이미지 dialog를 결정적 Preview에서 먼저 Figma와 대조한다. 이후 360dp light의 완료/이미지 없음·있음 조립 Preview를 비교한다. 사용자가 2026-10-29에 Android QA의 시각 검증은 CLI Preview로 충분하다고 지정했으며, 실제 화면 전환은 별도 미검증으로 기록한다. 회원 API 실연동을 검증하지 못하면 기능 완료로 판정하지 않는다.

## 위험·완료 조건

월간 캐시와 Home 캐시가 서로 다른 시점의 좋아요/이미지를 보여줄 수 있다. Calendar 재진입 또는 명시적 refresh를 포함해 동기화를 검증한다. 업로드 대상은 클릭 당시 선택 날짜로 고정해야 다른 날짜로 잘못 업로드되지 않는다. 권한·picker 취소·파일 변환 실패도 기존 image dialog 상태를 망가뜨리지 않아야 한다. Figma 최신 원본과 실제 mutation 검증이 모두 통과해야 Android 후속 항목을 완료로 판단한다. Notion 상태 변경은 별도 요청이 있을 때만 수행한다.
