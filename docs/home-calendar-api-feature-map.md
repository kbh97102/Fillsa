# Android Home·Calendar 기능-API 연결 문서

## 문서 개요

- 기준 코드: `codex/home-calendar-api-ui-integration` (`483ef93`)
- 작성일: 2026-09-13
- 범위: Android Home, Calendar, 그리고 두 화면에서 진입하는 필사 흐름
- 목적: 각 화면 기능이 어떤 API와 연결되는지, 회원·비회원에서 어떻게 달라지는지를 한 곳에 정리한다.

> 현재 구현과 테스트는 로컬 통합 브랜치에 있다. GitHub 원격 `develop`과 `release`에는 `483ef93`이 포함되어 있지 않으므로, 배포 기준으로는 아직 미반영 상태다.

---

## 1. Home 기능 리스트

### 1.1 전체 기능 맵

| # | Home 기능 | 회원 처리 | 비회원 처리 | API 호출 여부 |
|---:|---|---|---|---|
| 1 | Home 최초 진입·복귀 | 주간 명언과 streak을 서버에서 조회 | 선택 날짜의 명언만 조회하고 streak·완료일은 로컬에서 조회 | 회원 weekly 1회 + streak 1~2회 가능, 비회원 API 1건 |
| 2 | 7일 주간 날짜·명언 표시 | weekly 응답 `days`를 표시 | 일간 API 응답과 로컬 기록을 조합 | Home 로드 시 조회, 표시 자체는 무호출 |
| 3 | 주간 범위 내 날짜 선택 | 캐시된 `days`에서 선택 | 선택 날짜의 v1 일간 명언을 재조회 | 회원 무호출, 비회원 GET |
| 4 | 이전·다음 주간 이동 | 캐시가 없는 7일 창에서만 weekly 재호출 | 해당 날짜의 v1 일간 명언 조회 | 조건부 GET |
| 5 | Home 내 inline calendar 열기·월 이동 | 달력 UI만 변경, 날짜 선택 시 weekly 캐시/재조회 규칙 사용 | 날짜 선택 시 v1 일간 조회 | 열기·월 표시는 무호출 |
| 6 | 한국어·영어 명언/질문 전환 | 이미 받은 `kor*`, `eng*`, `question*` 필드를 전환 | 이미 받은 일간 응답을 전환 | 무호출 |
| 7 | 질문 답변 기록·수정 | 답변 POST 성공 후 메모리 반영, daily GET으로 보조 재조정 | 현재 화면 세션에만 저장 | 회원 POST + 성공 시 GET |
| 8 | 좋아요 선택·해제 | 선택 명언의 좋아요 API 호출 | Room 로컬 기록 변경 | 회원 POST, 비회원 무호출 |
| 9 | 이미지 보기 | weekly 응답의 `imagePath`를 표시 | 로그인 안내 다이얼로그 표시 | 보기 자체는 무호출 |
| 10 | 이미지 등록·변경 | multipart 이미지 업로드 후 `imagePath` 갱신 | 지원하지 않음, 로그인 유도 | 회원 POST |
| 11 | 이미지 삭제 | 삭제 API 성공 후 선택 날짜의 `imagePath` 제거 | 지원하지 않음 | 회원 DELETE |
| 12 | 필사 화면 진입 | Home에서는 선택 명언을 route로 전달 | 동일 | 진입 자체는 무호출 |
| 13 | 필사 기존 내용 불러오기 | 필사 화면에서 서버 필사 내용 조회 | Room 조회 | 회원 GET |
| 14 | 필사 저장 | 필사 화면에서 서버로 저장 | Room과 로컬 streak 저장 | 회원 POST |
| 15 | 명언 복사 | OS 클립보드에 복사하고 snackbar 표시 | 동일 | 무호출 |
| 16 | 명언 공유 | Share route로 명언·저자 전달 | 동일 | 무호출 |
| 17 | 저자 정보 열기 | 응답 `authorUrl`을 외부 URI로 열기 | 동일 | Fillsa API 무호출 |
| 18 | streak 표시·안내 | 현재 연속 필사 일수를 서버에서 조회 | 로컬 streak 조회 | 회원 GET |
| 19 | 프로필·Calendar 이동 | 해당 route로 이동 | 동일 | 무호출 |

### 1.2 Home 회원 최초 로드

Home이 회원 세션으로 열리면 Home 로드 자체는 다음 두 조회를 시작한다.

| 순서 | API | 파라미터 | 용도 |
|---:|---|---|---|
| 1 | `GET /api/v2/member-quotes/weekly` | 최초 호출에서 `endDate` 생략 | 7일 날짜, 선택 명언, 질문·답변, 좋아요, 이미지, 필사 완료 상태 조회 |
| 2 | `GET /api/v1/member-streaks` | 없음 | Home 헤더의 현재 연속 필사 일수 조회 |

앱 공통 `MainActivityViewModel`도 Home·Calendar·Quote List route 변경 시 `GET /api/v1/member-streaks`를 다시 호출한다. 현재 새 Home 헤더가 표시하는 값은 `HomeViewModel.streakInfo`가 아니라 `MainActivityViewModel`이 제공하는 `StreakProvider`이다. 따라서 Home 최초 진입에서 streak API가 중복 호출될 수 있다.

`weekly.days` 필드는 다음과 같이 Home에 연결된다.

| 응답 필드 | Home 사용처 |
|---|---|
| `date`, `dayOfWeek`, `state`, `completed` | 주간 날짜, 오늘/과거/완료 표시, 선택 가능 범위 |
| `dailyQuoteSeq` | 좋아요·이미지·필사·답변 mutation의 path 값 |
| `korQuote`, `engQuote` | 한국어·영어 명언 카드 |
| `korAuthor`, `engAuthor`, `authorUrl` | 저자 표시와 외부 링크 |
| `questionKo`, `questionEn` | 선택 언어의 오늘의 질문 |
| `answer`, `answeredAt` | 답변 기록 완료·수정 상태 |
| `likeYn` | 좋아요 선택 상태 |
| `imagePath` | 이미지 미리보기·보기 상태 |

Home 내의 주간 범위로 날짜를 전환하면 추가 API를 호출하지 않는다. 선택한 날짜가 주간 캐시 밖에 있을 때만 `endDate` 값을 넣어 weekly API를 다시 호출한다.

Calendar에서 과거 날짜를 선택하고 Home으로 이동하는 경우는 먼저 `endDate`가 없는 weekly 호출로 서버 기준일을 확정한다. 대상 날짜가 첫 응답 밖이면 해당 날짜가 들어가는 7일 창을 한 번 더 조회한다.

### 1.3 Home 질문 답변 저장

#### 회원

1. 선택한 `MemberQuoteDay` 안의 `dailyQuoteSeq`를 저장 대상으로 사용한다.
2. 공백만 있는 답변, 200 grapheme 초과 답변, `dailyQuoteSeq == null`, 이미 저장 중인 중복 요청은 전송하지 않는다.
3. `POST /api/v2/member-quotes/{dailyQuoteSeq}/answer`를 `{"answer":"..."}` body로 호출한다.
4. POST 성공 응답의 `answer`, `answeredAt`을 선택 날짜와 weekly 캐시에 반영한다.
5. 성공 snackbar를 표시한 후 `GET /api/v2/member-quotes/daily?quoteDate=yyyy-MM-dd`를 보조적으로 호출한다.
6. 보조 daily GET이 실패해도 이미 성공한 POST 결과는 되돌리지 않는다.
7. 답변 저장은 `completed`, `state`, streak를 변경하지 않는다.

#### 비회원

- answer API를 호출하지 않는다.
- 답변은 현재 Home 화면 세션에만 유지된다.
- 앱 재실행·프로세스 종료 후까지 유지되는 영구 저장은 아니다.

### 1.4 Home 좋아요

| 구분 | 처리 |
|---|---|
| 회원 | `POST /api/v1/member-quotes/{dailyQuoteSeq}/like`, body `{"likeYn":"Y"}` 또는 `{"likeYn":"N"}` |
| 비회원 | API를 호출하지 않고 Room의 해당 명언 좋아요 상태를 변경 |

회원 좋아요는 화면에서 먼저 선택 상태를 바꾸고 API를 호출한다. 같은 날짜에서 연속으로 누른 요청은 순서대로 처리하며, 가장 최근의 사용자 선택만 weekly 캐시에 확정한다.

### 1.5 Home 이미지

| 기능 | API | 성공 후 처리 |
|---|---|---|
| 등록·변경 | `POST /api/v1/member-quotes/{dailyQuoteSeq}/images` | multipart key `image`로 전송하고 응답 `imagePath`를 선택 날짜와 weekly 캐시에 반영 |
| 삭제 | `DELETE /api/v1/member-quotes/{dailyQuoteSeq}/images` | 선택 날짜와 weekly 캐시의 `imagePath`를 제거 |
| 보기 | 없음 | weekly 응답에 포함된 `imagePath`를 이미지 다이얼로그에서 표시 |

이미지 기능은 회원 전용이다. 비회원이 누르면 이미지 API 대신 로그인 안내를 표시한다.

### 1.6 Home에서 진입하는 필사 흐름

Home의 명언 카드를 누르는 순간에는 API를 호출하지 않고 선택 명언 DTO를 필사 route로 전달한다. API는 필사 화면에서 작동한다.

| 기능 | 회원 | 비회원 |
|---|---|---|
| 기존 필사 불러오기 | `GET /api/v1/member-quotes/{dailyQuoteSeq}/typing` | Room 조회 |
| 필사 저장 | `POST /api/v2/member-quotes/{dailyQuoteSeq}/typing`, body `typingKorQuote`, `typingEngQuote` | Room 저장 |
| 필사 화면 좋아요 | Home과 같은 v1 like API | Room 변경 |
| 완료 판정 | 입력과 원문을 앱에서 비교해 완료 다이얼로그 표시 | 동일한 비교 후 로컬 streak 기록 |

### 1.7 Home에서 API를 호출하지 않는 기능

- 명언 복사: OS 클립보드와 snackbar만 사용한다.
- 명언 공유: Share 화면으로 명언·저자를 전달한다.
- 언어 전환: 이미 조회한 한국어·영어 필드를 바꾼다.
- inline calendar 열기·닫기·월 표시: UI 상태만 변경한다.
- streak tooltip 열기·닫기: UI 상태만 변경한다.
- 프로필·Calendar 이동: 앱 라우트만 변경한다.

---

## 2. Calendar 기능 리스트

### 2.1 전체 기능 맵

| # | Calendar 기능 | 회원 처리 | 비회원 처리 | API 호출 여부 |
|---:|---|---|---|---|
| 1 | Calendar 최초 진입 | 현재 월의 회원 월간 데이터 조회 | 비회원 월간 명언 조회 + 로컬 기록 병합 | GET 1회 |
| 2 | 이전·다음 월 이동 | 해당 `yearMonth`의 회원 월간 데이터 재조회 | 해당 월 비회원 API 재조회 + 로컬 병합 | GET 1회/월 변경 |
| 3 | 날짜 선택 | 이미 받은 `memberQuotes`에서 선택 | 이미 조합한 월간 데이터에서 선택 | 무호출 |
| 4 | 날짜별 필사·좋아요 표시 | `completed`, `todayCompleted`, `likeYn` 표시 | Room 기록을 월간 명언과 병합 | 추가 무호출 |
| 5 | 월간 필사·좋아요 개수 | `monthlySummary.typingCount`, `likeCount` 표시 | Room 기록으로 앱에서 집계 | 추가 무호출 |
| 6 | 선택 날짜 명언·질문·답변·이미지 | 월간 응답 필드를 상세 영역에 표시 | 명언과 로컬 완료 상태를 표시, 질문은 기존 기본 문구 사용 | 추가 무호출 |
| 7 | 질문 답변 기록·수정 | Home과 같은 answer POST + daily GET | Calendar 세션에만 저장 | 회원 POST + 성공 시 GET |
| 8 | 명언 복사 | OS 클립보드에 복사 | 동일 | 무호출 |
| 9 | 명언 공유 | Share route로 이동 | 동일 | 무호출 |
| 10 | 좋아요·이미지·명언 카드 클릭 | 해당 날짜를 지정해 Home으로 이동 | 동일 | Calendar에서는 무호출 |
| 11 | 월간 개수 영역 클릭 | 선택 월을 지정해 Quote List로 이동 | 로컬 Quote List로 이동 | Calendar에서는 무호출 |

### 2.2 Calendar 월간 조회

#### 회원

`GET /api/v2/member-quotes/monthly?yearMonth=yyyy-MM`를 Calendar 진입과 월 변경 때 호출한다.

| 응답 영역 | Calendar 사용처 |
|---|---|
| `memberQuotes[]` | 날짜 그리드와 선택 날짜 상세 |
| `monthlySummary.typingCount` | 월간 필사 수 |
| `monthlySummary.likeCount` | 월간 좋아요 수 |
| `monthlySummary.streakCount` | 응답 DTO에는 보존되지만 현재 Calendar 개수 영역에서는 typing/like만 직접 표시 |

`memberQuotes[]`의 `questionKo`, `questionEn`, `answer`, `answeredAt`, `imagePath`, `engQuote`, `engAuthor`, `authorUrl`도 선택 날짜 상세에 직접 연결된다. 날짜를 클릭할 때는 월간 응답 캐시에서 데이터를 찾으므로 추가 daily GET을 호출하지 않는다.

#### 비회원

1. `GET /api/v1/quotes/monthly?yearMonth=yyyy-MM`로 월간 명언을 조회한다.
2. Room의 명언·필사·좋아요 기록을 `dailyQuoteSeq`로 매칭한다.
3. 병합 결과로 `completed`, `todayCompleted`, `likeYn`, 월간 필사·좋아요 개수를 앱에서 만든다.

### 2.3 Calendar 질문 답변 저장

Calendar 회원 답변도 Home과 같은 API와 UseCase를 사용한다.

1. `POST /api/v2/member-quotes/{dailyQuoteSeq}/answer`로 답변을 저장한다.
2. POST 성공 결과의 `answer`, `answeredAt`만 해당 월간 메모리 레코드에 반영한다.
3. `GET /api/v2/member-quotes/daily?quoteDate=yyyy-MM-dd`로 해당 날짜의 답변 필드를 보조 재조정한다.
4. daily GET이 실패해도 POST 성공 결과를 유지한다.
5. 월간 summary, 필사 완료, 좋아요, 이미지 필드는 answer 저장으로 변경하지 않는다.

비회원은 answer API를 호출하지 않고 Calendar 화면 세션에만 답변을 유지한다.

### 2.4 Calendar 액션 버튼의 실제 동작

| Calendar 액션 | 실제 동작 | Calendar 내 직접 API |
|---|---|---|
| 복사 | 선택 명언·저자를 OS 클립보드에 복사 | 없음 |
| 공유 | Share route로 명언·저자 전달 | 없음 |
| 좋아요 | 선택 날짜를 포함한 Home route로 이동 | 없음; 이동 후 Home에서 사용자가 액션을 실행할 때 like API 호출 |
| 이미지 | 선택 날짜를 포함한 Home route로 이동 | 없음; 이동 후 Home의 이미지 기능이 API 담당 |
| 명언 카드 | 선택 날짜를 포함한 Home route로 이동 | 없음; Home이 필요하면 weekly 창 조회 |
| 월간 개수 | Quote List route로 선택 월 전달 | Calendar에서는 없음; 회원 Quote List가 열리면 `GET /api/v2/member-quotes` 페이징 조회 |

---

## 3. API 엔드포인트-기능 대응표

| Method | Endpoint | 주요 파라미터/body | 연결 기능 | 호출 위치 | 상태 |
|---|---|---|---|---|---|
| GET | `/api/v2/member-quotes/weekly` | optional `endDate=yyyy-MM-dd` | 회원 Home 7일 데이터, 날짜 이동, Calendar→Home 날짜 정렬 | Home | 신규 반영 |
| GET | `/api/v2/member-quotes/daily` | `quoteDate=yyyy-MM-dd` | 답변 POST 성공 후 해당 날짜 보조 재조정 | Home, Calendar | 신규 반영 |
| POST | `/api/v2/member-quotes/{dailyQuoteSeq}/answer` | `{"answer":"..."}` | Home·Calendar 회원 질문 답변 기록·수정 | Home, Calendar | 신규 반영 |
| GET | `/api/v2/member-quotes/monthly` | `yearMonth=yyyy-MM` | 회원 Calendar 월간 그리드, 상세, 집계 | Calendar | 신규 필드 연결 |
| GET | `/api/v1/quotes/daily` | `quoteDate=yyyy-MM-dd` | 비회원 Home 날짜별 명언 | Home | 기존 유지 |
| GET | `/api/v1/quotes/monthly` | `yearMonth=yyyy-MM` | 비회원 Calendar 월간 명언 | Calendar | 기존 유지 |
| POST | `/api/v1/member-quotes/{dailyQuoteSeq}/like` | `{"likeYn":"Y|N"}` | Home·필사 회원 좋아요 | Home, Typing | 기존 유지 |
| POST | `/api/v1/member-quotes/{dailyQuoteSeq}/images` | multipart `image` | Home 회원 이미지 등록·변경 | Home | 기존 유지 |
| DELETE | `/api/v1/member-quotes/{dailyQuoteSeq}/images` | path `dailyQuoteSeq` | Home 회원 이미지 삭제 | Home | 기존 유지 |
| GET | `/api/v1/member-streaks` | 없음 | Home·공통 헤더·필사 결과의 회원 streak | Home, app scaffold, Typing | 기존 유지 |
| GET | `/api/v1/member-quotes/{dailyQuoteSeq}/typing` | path `dailyQuoteSeq` | 회원 필사 기존 내용 조회 | Typing | v1 유지 |
| POST | `/api/v2/member-quotes/{dailyQuoteSeq}/typing` | `typingKorQuote`, `typingEngQuote` | 회원 필사 저장 | Typing | POST만 v2 |
| GET | `/api/v2/member-quotes` | `size`, `page`, `likeYn`, `startDate`, `endDate` | Calendar 월간 개수 클릭 후 회원 Quote List 페이징 | Quote List | 기존 v2 |

### 레거시 회원 daily API

`GET /api/v1/member-quotes/daily?quoteDate=` 인터페이스와 UseCase는 아직 코드에 남아 있지만, 리뉴얼된 회원 Home 로드는 weekly v2를 사용하므로 현재 Home 흐름에서는 직접 호출되지 않는다.

---

## 4. 회원·비회원 요약

| 화면 | 회원 | 비회원 |
|---|---|---|
| Home 조회 | weekly v2 + streak v1 | daily quote v1 + Room/DataStore |
| Home 날짜 선택 | 주간 캐시, 경계에서만 weekly v2 | 날짜별 daily quote v1 |
| Calendar 조회 | monthly v2 | monthly quotes v1 + Room |
| 질문 답변 | answer v2 + daily v2 보조 조회 | 현재 화면 세션 |
| 좋아요 | like v1 | Room |
| 이미지 | image v1 | 로그인 유도 |
| 필사 | typing GET v1 / POST v2 | Room |
| streak | member-streaks v1 | Room/DataStore |

---

## 5. 캐시·오류·세션 처리

### 캐시

- Home 회원은 weekly 응답을 `endDate`별 7일 창으로 메모리에 유지한다.
- Home의 같은 7일 창 내 선택은 네트워크를 사용하지 않는다.
- Calendar는 현재 월의 `memberQuotes`를 메모리에 유지하고 날짜 선택 시 재사용한다.

### 오류

- answer POST가 실패하면 입력 초안과 편집 상태를 유지하고 성공 snackbar를 표시하지 않는다.
- answer POST 성공 후 daily GET이 실패하면 POST로 받은 답변을 유지한다.
- 느린 weekly/monthly/daily 응답이 나중의 선택과 상태를 덮어쓰지 못하도록 request revision을 검증한다.
- 실패한 GET으로 이미 표시 중인 성공 데이터를 지우지 않는다.

### 인증 세션

- 명시적인 로그인·로그아웃 상태 전환은 이전 회원 weekly/monthly 캐시와 진행 중 요청을 무효화한다.
- 정상적인 access token 갱신은 동일 논리 회원 세션으로 보고 진행 중인 answer POST 결과를 폐기하지 않는다.
- QA fixture는 프로세스 로컬 상태만 변경하며 운영 API mutation을 호출하지 않는다.

### 현재 구현 주의사항

- `GET /api/v1/member-streaks`는 Home 진입 시 `HomeViewModel`과 앱 공통 `MainActivityViewModel`에서 모두 요청될 수 있다. 표시값의 실제 소스는 공통 `StreakProvider`이므로 Home 전용 조회는 중복 후보다.
- Home 회원 좋아요는 optimistic UI를 적용한다. API 실패 시 현재 세션의 `isLike` 표시값을 즉시 원복하는 분기는 없고, 다음 서버 재조회 때 재정렬된다.
- 리뉴얼 전 회원 daily v1 UseCase가 `HomeViewModel`에 주입되어 있지만 현재 회원 Home 로드에서는 사용되지 않는다.

---

## 6. 코드 위치

| 영역 | 주요 파일 |
|---|---|
| Retrofit endpoint | `data/src/main/java/com/arakene/data/network/ApiEndPoint.kt`, `FillsaApi.kt`, `FillsaNoTokenApi.kt` |
| Repository | `data/src/main/java/com/arakene/data/repository/HomeRepositoryImpl.kt`, `CalendarRepositoryImpl.kt`, `TypingRepositoryImpl.kt`, `CommonRepositoryImpl.kt` |
| Domain model | `domain/src/main/java/com/arakene/domain/responses/MemberQuoteDay.kt`, `MemberQuotesData.kt`, `MemberMonthlyQuoteResponse.kt`, `AnswerResponse.kt` |
| Home state/action | `presentation/src/main/java/com/arakene/presentation/viewmodel/HomeViewModel.kt`, `model/HomeMemberQuoteWindow.kt`, `util/HomeAnswerDraft.kt` |
| Calendar state/action | `presentation/src/main/java/com/arakene/presentation/viewmodel/CalendarViewModel.kt`, `model/CalendarAnswerState.kt`, `ui/calendar/CalendarPresentation.kt` |
| Typing state/action | `presentation/src/main/java/com/arakene/presentation/viewmodel/TypingViewModel.kt` |

---

## 7. 검증 결과와 한계

### 확인된 내용

- Retrofit 실제 생성 URL, optional `endDate`, path substitution, answer JSON body, typing GET v1/POST v2를 MockWebServer 계약 테스트로 검증했다.
- Home ViewModel의 회원 weekly 최초 호출, 캐시 선택 무호출, answer POST→daily GET 순서, 비회원 격리를 검증했다.
- Calendar ViewModel의 monthly 1회 호출, 날짜 선택 무호출, answer POST→daily GET, summary/완료/좋아요/이미지 보존을 검증했다.
- 2026-09-13 기준 `data`, `domain`, `presentation` Debug 테스트를 `--rerun-tasks`로 재실행했으며 135개, failures 0, errors 0, skipped 0이다.

### 아직 확인하지 않은 범위

- 실제 회원 access token과 운영/스테이지 서버 데이터를 사용한 end-to-end 호출은 수행하지 않았다.
- 원격 `develop`·`release`에 통합되지 않았으므로 실제 배포 앱의 동작을 보증하지 않는다.
- 전체 화면의 Figma 시각 QA blocker는 API 연결 검증과 별개다.
