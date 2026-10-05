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
