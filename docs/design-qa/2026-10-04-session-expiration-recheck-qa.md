# 로그인 시간 만료 반복 재확인 — 코드 조사

## 대상

- Notion: [로그인 시간 만료 팝업](https://app.notion.com/p/26be639fc7a48046b4afe4cfb5bf0d98)
- 신고: 하루 뒤 만료 팝업 반복, 앱 삭제·재설치 뒤에도 발생. 신고자는 재설치 사례가 취소 버튼 가설과 다르다고 기록했다.
- 조사 환경: 2026-10-04, 로컬 `stage` 브랜치, 저장소 설정 versionCode 23/versionName 1.0.29.

## 확인한 코드 경로

1. 2026-07-01 `e4b9dc2`가 빈 refresh token으로 재발급을 요청하지 않도록 하고, 유효한 새 토큰을 받았을 때만 저장하도록 바꿨다.
2. `safeApi`와 `GetQuotesListDataSource`는 HTTP 401/403을 `CommonError.TokenExpiredError`로 바꾼다.
3. 2026-09-19 `09c03cb`가 이 오류에서 logout callback, 로그인 화면 이동, back stack 제거를 추가했다. `LogoutUseCase`는 access/refresh token을 비운다.
4. `WithBaseErrorHandling`에는 `ApiFail`의 errorCode 401/403에서 만료 팝업을 표시하는 이전 분기가 남아 있다. 표준 HTTP 401/403 경로는 위의 `TokenExpiredError` 경로를 탄다.
5. `HandleError`는 lifecycle의 STARTED 반복 블록에서 composition scope에 collector를 시작한다. 재시작 시 이전 collector가 남는지, 오류 한 번에 callback이 중복 실행되는지는 런타임 확인이 필요하다.

## 실행한 검증

| 확인 | 결과 |
|---|---|
| `./gradlew :data:testDebugUnitTest --tests com.arakene.data.util.AuthAuthenticatorTest --offline --quiet` | 통과. 빈 refresh token에서 재발급을 호출하지 않는 테스트다. |
| `./gradlew :presentation:testDebugUnitTest --tests com.arakene.presentation.util.SessionExpirationTest --offline --quiet` | 테스트 실행 전 컴파일 실패. 기존 `HomeViewModelIntegrationTest`의 `TestErrorCodeUseCase` 참조·생성자 인자와 지원 fake의 `testErrorCode` override가 현재 production 인터페이스와 불일치한다. |
| `adb devices -l` | 연결된 기기 없음. 재설치·토큰 만료·운영 빌드 시나리오를 실행하지 못했다. |

## 판정

**코드 수정 확인, 신고된 반복 현상의 해결은 미확인.** 현재 표준 401/403 경로는 이전 만료 팝업 대신 세션 삭제와 로그인 이동으로 연결된다. 재설치 직후 사례와 하루 뒤 사례의 운영 런타임 증거가 없고 presentation 테스트도 실행되지 않았으므로 Notion은 `Not started`로 유지한다.

다음 검증 단계는 [별도 계획](../superpowers/plans/2026-10-04-session-expiration-recheck.md)에 있다.

## 2026-10-12 후속 확인

사용자 지시에 따라 로그인 만료 건만 진행했다. 다른 Android QA 항목은 이번 검증 대상이 아니다.

### 재현한 원인과 수정

- 최초 로그인은 Firebase Installation ID를 `loginData.deviceData.deviceId`에 넣었지만, 401 뒤 `/api/v1/auth/refresh`는 `deviceId: ""`를 전송했다. `AuthAuthenticatorTest`에서 실제 빈 값으로 재현한 뒤 두 경로가 같은 `InstallationIdProvider`를 쓰도록 수정했다.
- Firebase 설치 ID 조회 실패 시 최초 로그인에서 빈 ID로 발급 요청을 보내던 경로를 막았다. 재발급에서도 빈 ID 또는 조회 실패면 잘못된 요청을 보내지 않는다.
- 머지 전 리뷰에서 설치 ID 조회 실패가 `lastContract`를 오류 effect로 덮어써 네트워크 팝업의 재시도가 로그인 액션을 재실행하지 못하는 문제를 확인했다. 오류를 직접 전달해 원래 로그인 액션을 보존하고 회귀 테스트로 확인했다.
- `HandleError`는 화면이 다시 `STARTED`가 될 때 기존 collector를 남긴 채 새 collector를 시작했다. 에뮬레이터에서 오류 1건이 2회 처리되는 것을 재현한 뒤 lifecycle에 묶어 1회 처리되도록 수정했다.

### 검증 증거

| 확인 | 결과 |
|---|---|
| 로그인 ViewModel 단위 테스트 | 가짜 설치 ID가 로그인 요청에 담김; 빈 ID에서는 로그인 API를 호출하지 않음 |
| 로그인 재시도 단위 테스트 | 설치 ID 조회 실패 뒤 네트워크 오류가 발생해도 원래 로그인 액션이 `lastContract`에 남음 |
| 인증 API MockWebServer 계약 테스트 | `/api/v1/auth/login`의 `deviceData`, `userData`, `syncData`와 `/api/v1/auth/refresh`의 `deviceId`, `refreshToken` JSON 확인 |
| Authenticator 단위 테스트 | 재발급 요청에 설치 ID와 refresh token이 담기고 새 access token으로 원 요청을 재시도함 |
| 목 서버 연속 흐름 테스트 | 로그인 발급→보호 요청 401→재발급→원 요청 200 순서 확인. 최초 로그인과 재발급의 `deviceId`가 같고 보호 요청은 새 토큰으로 한 번만 재시도함 |
| 목 서버 재발급 거부 테스트 | refresh 403에서 보호 요청을 다시 보내지 않고 원래 401을 반환함 |
| Android 에뮬레이터 Firebase 테스트 | 설치 ID가 비어 있지 않음 (`FirebaseInstallationIdProviderTest`, 1/1 통과). 실제 ID 값은 기록하지 않음 |
| Android 에뮬레이터 오류 collector 테스트 | 수정 전 1건→2회 실패, 수정 후 1건→1회 통과 (`HandleErrorLifecycleTest`, 1/1 통과) |
| `:data:testDebugUnitTest :presentation:testDebugUnitTest :app:testDebugUnitTest :app:assembleDebug` | 통과. Google Services 및 kapt 경고는 남아 있음 |

### 목 서버 범위와 운영 QA

사용자가 이번 인증 데이터 검증 방식으로 목 서버를 선택했다. 위 테스트는 Android 클라이언트의 요청 생성·재발급·재시도 동작을 확인하며 실제 서버의 토큰 정책이나 24시간 뒤 상태는 판정하지 않는다.

운영 QA를 재개한다면 Android 테스트 계정으로 하루 뒤 만료, refresh token 만료, 삭제·재설치 결과를 확인한다. 현재 `settings` DataStore는 자동 백업 대상일 수 있어 이전 토큰 복원 여부를 확인해야 한다. [Android Auto Backup 문서](https://developer.android.com/identity/data/autobackup)에 따르면 내부 파일은 기본 백업 범위다. 실제 ID·토큰 값은 기록하지 않는다.

**판정: 코드와 에뮬레이터 단위 검증 통과, 신고 사례의 스테이지 재현 검증 전.** Notion은 Completed로 이동하지 않는다. 현재 HTTP 로그는 인증 요청·응답 본문과 토큰을 출력하므로 실제 테스트에서는 원문 Logcat을 공유하지 않고 서버의 마스킹된 요청 필드만 확인한다.
