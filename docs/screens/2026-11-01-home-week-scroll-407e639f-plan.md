# Home 날짜 영역 좌우 스크롤 QA 계획 v1

## 선택·승인 기록

- Notion: [Home > 날짜 영역 > 스크롤 필요](https://app.notion.com/p/Home-407e639fc7a48023a54fece5aa7bb156), `407e639f-c7a4-8023-a54f-ece5aa7bb156`.
- Notion 상태/우선순위/기기: Not started / 중간 / iOS, Android. 최근 수정: 2026-10-29 06:25 UTC.
- 계획 버전/단계: v1 / Android 수정·검증 승인. 구현 승인: 사용자의 2026-11-01 발언 “좋아 세가지 모두 진행, stage로부터 각자의 워크트리를 만들어서 진행하자”. 승인 범위는 이 v1의 Android Home 날짜 줄 스크롤·검증·독립 보고이며 `stage`의 `c14b4e6`에서 만든 `codex/qa-home-week-scroll-407e639f` 워크트리에서 수행한다.
- 코드 기준: `c14b4e6`; 시작 시 기존 미커밋 `app/.DS_Store`, `fastlane/report.xml`은 무관하므로 보존한다.

## 원문·Figma·AS-IS

- 본문은 비어 있다. SooJi 댓글은 Home 날짜 영역의 좌우 스크롤을 요구하고 [Figma 노드 `2929:13556`](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556)를 연결했다. [댓글 첨부](../design-qa/artifacts/2026-11-01-qa-home-week-scroll/notion-attachment.png)는 개발 주석과 날짜 영역을 보여준다.
- Figma 전체 프레임: [Light `2929:13556`, 360×821](../design-qa/artifacts/2026-11-01-qa-home-week-scroll/figma-home-light-2929-13556.png), ko-KR, 기본 Home. 날짜 영역 `3204:2435`의 Figma 주석: 10(월)~16(일)을 한 주로 노출, 오늘은 가장 오른쪽, 과거 필사 기록 탐색을 위해 좌우 스크롤 가능. 현재 날짜 칩의 크기·색·배열은 유지한다.
- 현재 `HomeDateWeekSection`은 월 카드와 `HomeWeekStrip`을 나란히 배치한다. `HomeWeekStrip`은 전달된 7개 `HomeWeekDayState`를 일반 `Row`에 그려 각 날짜 탭만 처리한다. `horizontalScroll`/`LazyRow`와 스크롤 이벤트가 없다.
- [수정 전 날짜 Preview](../design-qa/artifacts/2026-11-01-qa-home-week-scroll/android-before-week-preview.png), [component tree](../design-qa/artifacts/2026-11-01-qa-home-week-scroll/android-before-week-component-tree.json). 360×50dp, Light, ko, fontScale 1.0, 고정 2026-08-16 fixture. Preview는 정지 화면이므로 제스처 부재는 코드 경로로 확인했고 실제 터치 재현은 아직 수행하지 않았다.
- 회원 주간 응답 `HomeMemberQuoteWindow`는 현재 7일 창과 선택 날짜를 가진다. 날짜 탭과 명언 카드 스와이프는 `HomeViewModel.selectDate`/`selectAdjacentDate`를 통해 화면의 선택 날짜를 바꾸며, 창 경계에서는 weekly API를 불러온다. 기존 카드 스와이프는 날짜 줄 제스처와 별개의 영역이다.

## 원인과 해결 설계 / TO-BE

- 원인: 날짜 줄의 7칸 고정 `Row`와 주간 표시 창이 선택 날짜에 묶인 구조. 화면에 스크롤 가능한 날짜 목록이나 범위 확장 이벤트가 없다.
- 권장 동작: Home 최초 표시에서 오늘을 날짜 줄 오른쪽 끝에 두고, 날짜 줄만 좌우로 드래그해 과거 날짜를 탐색한다. 드래그 자체는 선택된 날짜·명언·질문을 바꾸지 않고, 날짜 탭만 기존 선택 이벤트를 보낸다. 오늘보다 미래의 날짜와 서비스 시작일 이전 날짜는 노출하지 않는다. 월 카드는 스크롤 영역 밖에 고정한다.
- `HomeWeekStrip`은 날짜 칩을 재사용하는 가로 스크롤 컨테이너가 된다. viewport와 현재 탐색 범위는 Home UI state로 분리하고, `HomeViewModel`은 회원의 추가 주간 창을 기존 weekly API와 cache에 읽되 현재 선택을 유지한다. 비회원은 기존 로컬 완료일 집합에서 표시 상태를 만든다. 현재 선택 날짜/명언의 상태 계약은 그대로 둔다.
- 회원 추가 주간 로드가 실패하면 이미 로드된 칩은 유지하고 실패한 범위에 완료 표시를 추정하지 않는다. 중복/역순 응답은 기존 request generation/cache 규칙을 따라 폐기한다. 날짜 탭 후 답변 편집·좋아요·이미지 상태는 해당 날짜의 기존 선택 로직을 사용한다.
- 대안: 한 번 드래그할 때 7일씩 이동하고 오른쪽 끝 날짜를 자동 선택하면 기존 window API를 더 재사용할 수 있다. 하지만 스크롤만으로 명언/질문이 바뀌어 Figma 주석의 '히스토리 살펴보기'와 날짜 탭의 역할이 섞인다. 권장안은 탐색과 선택을 분리한다.
- 제품 결정 확인: Figma는 드래그가 선택 날짜를 바꾸는지, 연속 스크롤인지 페이지 단위인지까지 지정하지 않는다. 위 권장안인 **날짜 줄 연속 스크롤, 탭할 때만 선택**을 계획 승인 범위로 제안한다.

## 변경 순서

1. `FigmaHomeContent.kt`의 날짜 칩 표시를 독립 컴포넌트로 유지하며 가로 스크롤 viewport와 초기 오른쪽 정렬을 구현한다. 기존 30dp 칩·5dp 간격·완료 배지는 Figma 노드 `3204:2435`에 맞춘다.
2. `HomeView.kt`가 탐색 범위/스크롤 요청과 날짜 선택 callback을 구분해 전달한다. 명언 카드의 기존 드래그 처리에는 영향을 주지 않는다.
3. `HomeViewModel.kt` 및 필요한 Home UI state/모델에서 회원 과거 주간 읽기와 완료 표시 cache를 현재 선택과 분리한다. 기존 weekly API 계약만 사용하고 domain/data 호출 규약은 유지한다. 비회원은 로컬 완료일 정보를 재사용한다.
4. 화면 문서·이 이슈의 QA 기록을 갱신하고, 독립 AS-IS/TO-BE HTML 보고서를 만든다.

## 검증·완료 조건·위험

- 결정적 날짜 줄 Preview로 360dp/Light·Dark/ko/기본 글자 배율에서 최초 7칸과 오른쪽 오늘 위치를 Figma와 비교한다. 스크롤 후 과거 범위·완료 배지·선택 표시의 하위 상태를 각각 확인한다.
- 손가락 드래그로 과거를 탐색하고 다시 오늘까지 복귀하는 실제 에뮬레이터 동작을 검증한다. 날짜 줄 스크롤 중 월 카드/명언이 바뀌지 않고, 날짜 탭 후에는 해당 날짜의 명언·질문·완료 표시가 갱신되어야 한다. 회원 창 경계의 API 성공/실패·비회원·서비스 시작일·오늘 경계를 검증한다.
- 하위 Preview 통과 뒤 Home 전체 360×821 조건의 Figma/실행 화면을 비교한다. 시스템 영역·오프라인 fixture 한계를 기록하며 전체 프레임을 확인하지 못하면 최종 Pass로 표시하지 않는다.
- 위험: 추가 weekly 조회의 빈도와 진행 중 답변 draft 보존, 로딩 중 반복 드래그, quote card 스와이프와의 터치 경계. 이 이슈 외의 Home 레이아웃·색상과 Calendar 화면은 수정하지 않는다. 복구는 날짜 줄 탐색 state와 관련 변경만 되돌린다.
