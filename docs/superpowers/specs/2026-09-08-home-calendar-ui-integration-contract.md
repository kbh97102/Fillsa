# Home·Calendar UI 연동 계약

## 목적

완성된 Home·Calendar Figma UI에 2026-09-03 운영 배포된 리뉴얼 API를 연결한다. 화면의 시각 구조와 문구는 바꾸지 않고, 기존 fixture/session 데이터 자리를 서버 응답과 명시적인 화면 상태로 교체한다.

## 기준 자료

- Notion: `홈·캘린더 리뉴얼 API 연동 가이드`
- Notion page ID: `3d0e639f-c7a4-8058-9e90-d3245234ee3a`
- Home Figma: `https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X/2.home?node-id=2929-13556`
- Calendar Figma: `https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X/✒️필사?node-id=2929-13366`
- 화면 명세: `docs/screens/2_home.md`, `docs/screens/3_calendar.md`
- UI 검증 절차: `docs/ui-redesign-workflow.md`

## 브랜치 기준

- iOS UI 기준: `codex/calendar-screen-root` (`effd700`)
- iOS API/UseCase: `codex/member-api-usecases` (`876656c` 이후)
- Android UI 기준: `codex/calendar-screen-root` (`d06110d`)
- Android API/UseCase: `codex/member-api-usecases` (`a300cc0` 이후)
- 구현은 각 OS별 새 `codex/home-calendar-api-ui-integration` worktree에서 수행한다.

## 회원 Home 계약

1. 최초 진입은 `GET /api/v2/member-quotes/weekly`를 호출하며 `endDate`를 보내지 않는다.
2. 응답 `days`가 날짜 스트립, 선택 카드, 질문, 답변, 좋아요, 이미지, 완료 표시의 단일 원천이다.
3. 현재 창 안의 날짜 탭과 카드 스와이프는 캐시된 `days`만 선택하며 네트워크를 호출하지 않는다.
4. 이전/다음 창이 필요할 때만 현재 응답 `endDate`에 각각 -7/+7일을 적용해 주간 API를 호출한다.
5. 기준 날짜 계산은 기기 오늘이 아니라 최초 응답의 `endDate`를 사용한다. 서버 `state == "today"` 판정을 앱에서 다시 만들지 않는다.
6. 연월 배지는 선택 날짜에서 `yyyy.MM`로 계산한다. 월 경계에서도 별도 호출하지 않는다.
7. `state == "done"` 또는 `completed == true`인 날짜만 완료 표시를 사용한다. 답변 유무는 완료 표시 조건이 아니다.
8. `dailyQuoteSeq == null`인 칸은 선택할 수 있지만 좋아요·이미지·타이핑·답변 mutation은 실행하지 않는다.
9. Calendar에서 과거 날짜를 지정해 Home에 진입하면 먼저 `endDate` 없는 weekly로 서버 기준 창을 얻는다. 대상 날짜가 그 창 밖이면 `floor((anchorEndDate - targetDate) / 7)`만큼 7일 단위로 뺀 `endDate`를 사용해 정렬된 대상 창을 한 번 더 조회한다.

## 질문·답변 계약

1. 질문은 선택된 날의 `questionKo`/`questionEn`을 화면 언어에 맞춰 표시한다.
2. 입력은 기존 UI처럼 최대 200 grapheme으로 제한하고, 공백-only 입력은 전송하지 않는다.
3. 저장/수정은 `POST /api/v2/member-quotes/{dailyQuoteSeq}/answer` 하나를 사용한다.
4. 저장 중 중복 탭을 막는다. 성공 전에는 기록 완료 상태나 성공 토스트를 표시하지 않는다.
5. 성공 시 서버가 정리한 `answer`와 `answeredAt`을 현재 주간/월간 메모리 레코드에 반영한 뒤 기존 성공 토스트를 표시한다.
6. 성공 후 `GET /api/v2/member-quotes/daily?quoteDate=`를 보조 호출해 해당 날짜만 서버 상태로 재조정한다. 보조 호출이 실패해도 POST 성공 상태는 되돌리지 않는다.
7. 실패 시 입력과 편집 상태를 유지하고 성공 토스트를 표시하지 않는다. Figma에 없는 오류 문구나 화면을 새로 추가하지 않는다.
8. 답변 저장은 `completed`, `state`, 완료 날짜, 월간 필사 수, streak를 변경하지 않는다.

## 회원 Calendar 계약

1. 진입/월 변경 시 `GET /api/v2/member-quotes/monthly?yearMonth=`를 1회 호출한다.
2. 날짜 탭은 캐시된 `memberQuotes`에서 상세를 선택하며 추가 GET을 호출하지 않는다.
3. 상세 화면은 `questionKo`, `questionEn`, `answer`, `answeredAt`, `imagePath`, `engQuote`, `engAuthor`, `authorUrl`을 월간 응답에서 읽는다.
4. Calendar 답변 저장도 Home과 같은 answer UseCase를 사용하고, 성공 후 선택 레코드를 메모리에서 갱신한다.
5. Calendar의 복사/공유는 기존 로컬 동작을 유지한다. 좋아요/이미지 버튼의 기존 선택 날짜 Home 이동 계약은 이번 리뉴얼 API 연결에서 바꾸지 않는다.
6. 월간 summary는 서버 값을 그대로 사용하며 답변 저장으로 다시 계산하지 않는다.

## 비회원 계약

1. 회원 전용 weekly/daily/answer/monthly API를 호출하지 않는다.
2. 기존 v1 비회원 일간/월간 조회, 로컬 좋아요, 로컬 필사 기록을 유지한다.
3. 현재 질문 답변의 세션-only 동작을 유지한다. 로그인 유도, 숨김, 서버 저장으로의 변경은 별도 제품 결정을 필요로 한다.

## 기존 API 보존

- 좋아요, 이미지 등록/삭제, streak GET은 기존 경로와 UseCase를 유지한다.
- 타이핑 GET은 v1을 유지하고 타이핑 POST만 v2를 사용한다.
- memo v1은 신규 Home·Calendar에서 호출하지 않는다.

## 동시성·오류·캐시

- 화면별로 진행 중인 주간/월간/단일 날짜 요청을 식별하고 최신 선택 요청만 state를 갱신한다.
- 동일한 `endDate` 주간 창은 화면 세션 메모리에 재사용한다.
- 새 날짜를 선택하면 draft/recorded-answer 상태를 그 날짜 레코드로 원자적으로 교체한다.
- 실패한 GET은 이미 표시 중인 성공 데이터를 지우지 않는다.
- 로그아웃 또는 계정 변경 시 회원 주간/월간 메모리 캐시를 폐기한다.

## 테스트 및 완료 기준

- API 계약 테스트: 실제 생성 URL, optional query, path, JSON body, v1/v2 타이핑 경로.
- 상태 테스트: 최초 `endDate` 생략, 창 내부 무호출, 경계에서 ±7 호출, 늦은 응답 무시, 날짜별 draft 격리.
- 답변 테스트: 공백/201자 차단, 중복 탭 차단, 성공 패치, 보조 GET 실패 유지, 완료/streak 불변.
- Calendar 테스트: 월간 확장 필드 표시, 날짜 탭 무호출, 월 변경 1회 호출, 답변 저장 후 선택 레코드만 갱신.
- 비회원 회귀: 회원 API 무호출과 기존 로컬 흐름 유지.
- 각 OS 전체 단위 테스트와 Debug/Release 빌드가 통과해야 한다.
- 데이터 연결로 영향을 받은 Home·Calendar 상태는 `docs/ui-redesign-workflow.md`에 따라 Figma 기준과 런타임 전체 프레임을 다시 비교한다. 기존 font/navigation/viewport blocker는 해결되지 않은 채로 Pass로 바꾸지 않는다.
