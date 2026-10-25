# Android 기능 회귀 검증 — 2026-10-13

## 대상과 결론

- 대상: `stage`의 1.0.31 (25)에 로컬 필사 저장 응답 수정 및 아직 커밋되지 않은 필사 결과 화면 작업을 더한 작업 트리.
- 기기: `fillsa_qa_20261005` 에뮬레이터, Android API 35.
- 자동 검증: Debug·Release JVM 테스트 320건, 연결 기기 테스트 8건 모두 실패 0건. Release APK 조립 성공.
- 판정: **자동 회귀 테스트 통과, 비회원 주요 수동 경로 14개 확인. 전체 비회원 기능 검증은 아직 완료되지 않았다.** 회원 OAuth·서버 저장, 실제 알림 수신, 위젯의 다음 날 자동 갱신 등은 확인하지 못했다.
- Release APK를 QA 에뮬레이터에 새로 설치해 알림 권한 허용, 비회원 진입, 온보딩 건너뛰기와 전체 3단계 진행을 확인했다. 아래 비회원 수동 검증은 이 로컬 APK에서 수행했으며, 원격 `stage`에 배포된 바이너리의 결과로 간주하지 않는다.

## 필사 저장 오류 조사

서버의 `POST /api/v2/member-quotes/{dailyQuoteSeq}/typing`은 HTTP 200과 함께 `memberQuoteSeq`, `completedChanged`, `todayCompletedChanged`가 있는 객체를 반환했다. Android Retrofit 선언은 `Response<Int>`였으므로 성공 응답의 JSON 파싱 중 예외가 발생했다. `safeApi`가 이를 실패로 바꾸고 `BaseViewModel`의 오류 흐름이 팝업을 표시했다. 서버 저장 성공과 앱의 실패 팝업이 동시에 발생한 원인이다.

- 재현: 실제 응답 구조를 MockWebServer로 제공한 `FillsaApiContractTest.postTyping_acceptsSuccessfulObjectResponse`가 수정 전 실패했다.
- 수정: 응답을 `TypingSaveResponse`로 역직렬화하고 API·Repository의 반환형을 일치시켰다. 성공 팝업은 기존 ViewModel의 저장 성공 분기를 따른다.
- 재검증: 위 계약 테스트와 회원 저장 성공·실패·중복 탭·비회원 저장을 다루는 `TypingSaveOutcomeTest`가 통과했다.
- 제공된 로그에는 액세스 토큰 원문도 출력됐다. `TokenInterceptor`의 해당 로그를 제거하고 인증 헤더 전송은 유지했다.

## 실행 결과

| 명령·범위 | 결과 |
|---|---|
| `./gradlew test --offline --quiet` | Debug 160건, Release 160건 통과 |
| `./gradlew connectedDebugAndroidTest --offline --quiet` | app 4건, data 1건, domain 1건, presentation 2건 통과 |
| `./gradlew :app:assembleRelease -x :app:uploadCrashlyticsMappingFileRelease --offline --quiet` | 성공, APK 메타데이터 1.0.31 (25) |

전체 연결 테스트 실행 전에는 위젯 WorkManager 테스트가 완료를 기다리지 않아 실패했고, 라이브러리 테스트 APK에는 AdMob 테스트 ID와 올바른 테스트 패키지명이 없었다. 테스트 전용 설정과 대기 조건을 수정한 뒤 전체 연결 테스트를 다시 실행해 통과했다.

## 비회원 에뮬레이터 수동 검증

| 경로 | 확인 결과 | 증거 |
|---|---|---|
| 첫 실행·비회원 진입 | 권한 허용 후 비회원 진입, 온보딩 건너뛰기와 3단계 전체 진행 모두 Home으로 연결 | [1단계](artifacts/2026-10-13-functional-regression/guest-onboarding-first.png) · [2단계](artifacts/2026-10-13-functional-regression/guest-onboarding-second.png) · [3단계](artifacts/2026-10-13-functional-regression/guest-onboarding-third.png) |
| Home 날짜·언어 | 주간 날짜 선택과 월 선택 팝업에서 9월 30일 이동, 한국어·영어 문구 전환 확인 | [9월 30일](artifacts/2026-10-13-functional-regression/guest-home-sept30.png) |
| 필사 미완료·임시 입력 | 빈 입력과 일부 입력 저장 모두 미완료 팝업 확인. 영어 일부 입력은 뒤로 나갔다가 재진입한 뒤 영어 탭으로 돌아오면 유지됐다. 미완료 팝업 취소와 Home 복귀 확인 | [빈 입력 팝업](artifacts/2026-10-13-functional-regression/guest-incomplete-popup.png) · [일부 입력 팝업](artifacts/2026-10-13-functional-regression/guest-typing-partial-result.png) · [재진입한 영어 입력](artifacts/2026-10-13-functional-regression/guest-typing-draft-persisted.png) |
| 필사 완료 | 10월 12일 영어 원문 전체 입력 후 완료 팝업과 Home 복귀 확인. 재진입·앱 재시작 후 입력 내용과 1일 연속 기록 유지 | [완료 팝업](artifacts/2026-10-13-functional-regression/guest-completed-popup.png) |
| 좋아요 | Home에서 색상과 목록 항목이 바뀌고 재시작 뒤에도 유지. 필사 화면의 좋아요도 Home에 반영 | [Home 좋아요](artifacts/2026-10-13-functional-regression/guest-home-liked.png) · [필사 좋아요](artifacts/2026-10-13-functional-regression/guest-typing-like.png) |
| 오늘의 답변 | 비회원 답변 입력·기록·수정은 즉시 Home에 반영되고 앱 재시작 후 사라짐. 사용자가 **현재 세션만 유지**하는 정책을 확정했으므로 정상 동작으로 판정 | [기록 직후](artifacts/2026-10-13-functional-regression/guest-answer-recorded.png) · [수정 후](artifacts/2026-10-13-functional-regression/guest-home-answer-edited.png) |
| 캘린더 | 날짜별 좋아요·필사 표시, 날짜 선택 후 문구 카드와 질문 확인. 비회원 답변 기록·수정 후 날짜를 바꿨다가 돌아와도 캘린더 화면에서 유지. 사용자가 **Home과 캘린더 답변은 각 화면에서 따로 유지**한다고 확정했으므로 Home의 같은 날짜에 반영되지 않는 동작은 정상으로 판정 | [날짜 선택](artifacts/2026-10-13-functional-regression/guest-calendar-selected.png) · [답변 수정 후 재선택](artifacts/2026-10-13-functional-regression/guest-calendar-answer-edited.png) |
| 기록 목록·메모 | 캘린더 건수에서 목록 진입, 날짜 범위 변경, 문구 상세·메모 입력. 재시작 후 메모 유지, 비우면 목록의 메모 표시 제거. 비좋아요 필사 기록 1건에서 좋아요 필터를 켜면 빈 목록, 끄면 해당 기록 표시 | [비좋아요 기록](artifacts/2026-10-13-functional-regression/guest-list-unliked.png) · [필터 결과](artifacts/2026-10-13-functional-regression/guest-list-liked-filter-empty.png) · [재시작 후 메모](artifacts/2026-10-13-functional-regression/guest-memo-after-restart.png) · [메모 삭제](artifacts/2026-10-13-functional-regression/guest-memo-cleared.png) |
| 공유 | Home의 공유 화면에서 복사 시 시스템 클립보드 표시, 저장 시 성공 표시와 MediaStore에 `quote_*.jpg` 생성, 카카오톡 버튼에서 Android 공유 시트 표시. 필사 화면에서도 공유 화면 이동과 다른 배경 선택·저장 성공 확인. 에뮬레이터에 카카오톡은 설치되지 않음 | [저장 성공](artifacts/2026-10-13-functional-regression/guest-share-save.png) · [공유 시트](artifacts/2026-10-13-functional-regression/guest-system-share.png) · [두 번째 배경 저장](artifacts/2026-10-13-functional-regression/guest-share-second-background-save.png) |
| 회원 전용 진입 | 이미지 등록 시 로그인 안내 팝업, My Page 로그인 버튼에서 OAuth 선택 화면 진입·닫기 확인 | [로그인 화면](artifacts/2026-10-13-functional-regression/guest-login-entry.png) |
| My Page·공지 | 비회원 카드와 버전 1.0.31 표시, 공지 3건 목록과 각 상세 본문 확인 | [My Page](artifacts/2026-10-13-functional-regression/guest-my-page.png) · [첫 상세](artifacts/2026-10-13-functional-regression/guest-notice-detail.png) · [둘째 상세](artifacts/2026-10-13-functional-regression/guest-notice-second-detail.png) · [셋째 상세](artifacts/2026-10-13-functional-regression/guest-notice-third-detail.png) |
| 알림 설정 | 켜짐→꺼짐 변경 후 재진입 시 꺼짐 유지, 다시 켜짐으로 복구. 실제 예약 시각의 알림 수신은 미확인 | 설정 화면 수동 재진입 확인 |
| 테마 | 시스템·다크·라이트 선택과 즉시 적용, 다크 상태에서 재시작 후 유지 확인. 마지막에 시스템으로 복구 | [다크](artifacts/2026-10-13-functional-regression/guest-my-page-dark.png) · [라이트](artifacts/2026-10-13-functional-regression/guest-my-page-light.png) |
| 위젯 | 런처에서 2×2 위젯 추가, 오늘 문구 표시, 탭으로 앱 실행, 설정에서 영어로 변경 후 문구 갱신 확인. 마지막에 한국어로 복구. 다음 날 자동 갱신은 미확인 | [추가](artifacts/2026-10-13-functional-regression/guest-widget-added.png) · [영어 설정](artifacts/2026-10-13-functional-regression/guest-widget-english.png) |

광고 SDK의 `AdMob native ad validator` 안내가 간헐적으로 하단 탭을 덮었다. 안내의 **Dismiss**로 닫은 뒤 같은 경로를 다시 실행했다. 이로 인해 탭 접근 자체를 실패로 판정하지 않았다. 위 표에서 기록한 좋아요·답변·메모는 QA 에뮬레이터의 테스트 데이터다. 전체 온보딩 재검증 전에 앱 데이터를 초기화해 이전 기록은 지워졌고, 필터 검증용 비좋아요 기록 1건을 새로 만들었다. 이후 10월 11일 좋아요, Home·캘린더 답변, 미완료 필사 입력을 추가했으므로 현재 로컬 데이터는 초기 상태가 아니다. 캡처는 모두 보존했다.

## 기능별 범위와 남은 확인

이 문서의 **14개**는 화면별 주요 경로 수다. 모든 버튼·상태·기기 조건을 완주했다는 뜻은 아니다. 추가로 확인한 세부 동작은 위 표에 합쳐 기록했다. 비회원 범위에서 아직 검증하지 못한 항목은 다음과 같다.

- 오전 9시 실제 알림 수신과 알림 권한 거부·재허용 후 동작.
- 위젯의 다음 날 자동 갱신, 4×2 크기와 글자 크기별 표시.
- 카카오톡 설치 기기에서 실제 공유 대상 전달. 공유 이미지의 나머지 6개 배경은 개별 저장하지 않았다.
- 캘린더에서 제공하는 최초·최종 월 경계, 연속 필사 0일일 때의 안내와 캘린더 이동.

비회원 답변은 사용자가 **앱 세션 동안 유지**, **Home과 캘린더에서 각각 유지**로 결정했다. 현재 에뮬레이터의 화면별 동작은 이 정책과 일치한다.

| 기능 | 이번 확인 | 남은 실제 동작 확인 |
|---|---|---|
| 로그인·토큰 | 요청 본문, 설치 ID, 토큰 재발급, 오류 1회 처리 | OAuth 로그인, 만료 후 재발급, 앱 재설치·다음 날 재현 |
| Home·오늘의 기록 | 회원 주간 데이터·날짜 전환·답변·좋아요 ViewModel 테스트. 비회원 답변의 세션 한정·화면별 분리 정책과 기록·수정 확인 | 0일 연속 필사 안내·캘린더 이동, 회원의 서버 데이터 갱신 |
| 필사 입력·저장 | 실제 형태의 200 응답 파싱, 성공·실패 팝업 분기, 중복 저장 방지. 비회원 완료·빈 입력·일부 입력·재진입한 영어 임시 입력, 필사 화면 좋아요·공유 확인 | 회원 앱에서 저장 후 완료 팝업과 Home 복귀, 서버 조회로 중복 저장 여부 확인 |
| Calendar | 월간 데이터·선택 날짜·답변 상태 테스트, 비회원 날짜별 표시와 답변 기록·수정·재선택 확인 | 회원 이미지·답변 표시, 최초·최종 월 경계 확인 |
| List·상세·메모 | 비회원 목록·상세·메모 저장 및 재시작 후 재진입, 메모 비우기와 목록 반영 확인 | 회원 서버 동기화 확인 |
| My Page·공지·테마 | 비회원 공지 3건 모두 목록/상세, 테마 적용·재시작 유지 확인 | 회원 프로필·로그아웃 |
| 알림 | 설정 저장·스케줄링 단위 테스트, 비회원 설정 토글 확인 | 실제 예약 시각 수신, 권한 거부·재허용 |
| 위젯 | Worker 성공·지연·재시도 테스트, 비회원 런처 2×2 추가·설정·앱 실행 확인 | 다음 날 자동 갱신, 4×2와 글자 크기별 표시 |
| 공유·온보딩 | 비회원 이미지 복사·저장·시스템 공유 시트, 두 가지 배경 저장, 온보딩 건너뛰기와 3단계 전체 진행 확인 | 카카오톡 설치 기기에서 대상 전달, 남은 6가지 공유 배경 저장 |

회원 E2E에 필요한 스테이지 테스트 계정 및 재현 데이터가 제공되면 위 표의 실제 동작 확인을 이어서 실행한다. 계정 없이 수행한 목 서버 테스트를 실제 서버 검증으로 간주하지 않는다.
