# Notion QA Checklist 미완료 항목 정리

## 범위와 현재 증거

2026-10-04 기준 `QA Checklist`의 `Not started`/`In progress` 5건을 다룬다. 상태 변경은 검증 증거를 남긴 뒤 수행한다.

| 항목 | Notion | 현재 근거 | 남은 결정 |
|---|---|---|---|
| 팝업 디자인 | [QA](https://app.notion.com/p/2b5e639fc7a480e49107c81bd535132d) | 1.0.22 (18) 스테이지 확인 완료. 댓글은 운영 배포 확인 후 `Completed`를 요청한다. [Figma QA 노드 `2554:2201`](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2554-2201). | 운영 빌드에서 해당 팝업 상태 확인. |
| 테마 선택 팝업 크기조정 | [QA](https://app.notion.com/p/276e639fc7a480f78ef7f0ce85525ad5) | 요청은 항목 글자 크기 약 2dp 축소와 항목 간 30dp. 현재 `ThemeDialog.kt`는 18sp/15dp이고 1.0.22 (18) 스테이지에서 미반영 판정. | Figma URL/노드와 대상 상태 확보 후 UI 수정·검증. |
| 위젯 디자인 | [QA](https://app.notion.com/p/276e639fc7a48077a891fcea28baf4e1) | 탭 시 앱 실행과 선택 테두리는 코드에 있으며 1.0.22 (18) 스테이지 확인 완료. 댓글은 운영 배포 확인 후 `Completed`를 요청한다. | 운영 빌드의 실제 런처 위젯과 설정 화면 확인. Figma URL/노드 확보. |
| 로그인 시간 만료 팝업 | [QA](https://app.notion.com/p/26be639fc7a48046b4afe4cfb5bf0d98) | 2026-07-01에 빈 refresh token의 재시도를 막았고, 2026-09-19에 `TokenExpiredError` 시 세션 삭제·로그인 이동을 추가했다. 신고 내용은 하루 뒤 또는 앱 재설치 후 반복 팝업이다. 신고자가 댓글에서 재설치 사례는 취소 버튼 가설과 다르다고 밝혔다. | 원래 재현 조건과 운영 동작 확인. 코드 수정만으로 완료 판정 금지. |
| 공지사항 화면 광고 배경색상 수정 | [QA](https://app.notion.com/p/25de639fc7a480d48d47ea7d07663793) | `SingleLineAdSection.kt`에 공지사항 경로별 색상 분기가 있다. QA 본문은 비어 있고 `docs/screens/5_1_notice.md`에 Figma 기준이 없다. | 목표 Figma URL/노드와 light/dark, 목록/상세 상태 확보 후 대조. |

## 공통 수용 기준

- UI의 유일한 판단 기준은 대상 Figma다. Figma 파일 URL, 프레임/노드 ID, 기준 이미지와 화면 문서가 준비되기 전에는 UI 구현·시각 승인에 들어가지 않는다.
- UI 수정 시 `docs/ui-redesign-workflow.md` 순서대로 하위 Compose Preview와 실제 기기/에뮬레이터 전체 프레임을 대조하고 `docs/design-qa/`에 증거를 남긴다. Glance 위젯은 실제 런처에서도 검증한다.
- 운영 확인을 요구한 팝업·위젯은 스테이지 결과만으로 `Completed` 처리하지 않는다.
- 로그인 오류는 취소 버튼 가설을 전제하지 않고 재현 및 토큰/요청 경로를 추적한다.
- Notion `Completed`는 항목별 수용 기준을 충족한 뒤 변경하고, 변경 후 다시 읽어 확인한다.

## 현재 검증 장애

- `data:AuthAuthenticatorTest`는 2026-10-04 재실행에 통과했다. `presentation:testDebugUnitTest`는 기존 `HomeViewModelIntegrationTest`와 지원 fake의 컴파일 오류로 중단된다. 인증 변경 검증에 해당 모듈 테스트가 필요할 경우 이 테스트 드리프트를 먼저 복구한다.
- 팝업·위젯의 운영 빌드/기기 증거와 테마·위젯·공지사항의 Figma 대상 노드는 현재 자료에 없다.
