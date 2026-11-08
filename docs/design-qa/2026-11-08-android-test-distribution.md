# Android 1.0.36 (30) 테스트 배포 · 2026-11-08

- 소스: `stage` 커밋 `30609c0` (`fix(android): center login description for test build`). 코드·버전·로그인 QA 증거와 [알려진 이슈 메모](2026-11-08-android-test-release-notes.txt)를 함께 커밋했다.
- 산출물: `app/build/outputs/apk/release/app-release.apk`, SHA-256 `e4b518f71404c82e2d43ffc93fabb836dbb2e4946916b68be6ca4fb36ddd323b`, 28,193,094 bytes. `aapt`: `com.arakene.fillsa`, `versionName=1.0.36`, `versionCode=30`, minSdk 28, targetSdk 36. `apksigner verify`: v2 서명 true, signer 1명.
- 검증: `./gradlew :app:assembleRelease :presentation:testDebugUnitTest :data:testDebugUnitTest :domain:testDebugUnitTest :app:testDebugUnitTest` → `BUILD SUCCESSFUL in 1m 6s`; 단위 테스트 179/179 (presentation 154, data 14, domain 10, app 1), 실패 0. 로그인 설명 Light/Dark Preview·360×720 에뮬레이터 [QA](2026-11-08-login-description-center-qa.md) 통과.
- 배포: Firebase App Distribution 프로젝트 `fillsa-91e50`, Android app `1:49392670537:android:d235a6a14a796d87b12c98`, 기존 `fillsa` 그룹(조회 당시 테스터 1명). CLI가 바이너리 업로드, 메모 추가, 테스터 그룹 배포를 모두 성공으로 반환했다. 릴리스 ID `7h95ghmlcke20`.
- [Firebase 콘솔 릴리스](https://console.firebase.google.com/project/fillsa-91e50/appdistribution/app/android:com.arakene.fillsa/releases/7h95ghmlcke20) · [테스터 설치 페이지](https://appdistribution.firebase.google.com/testerapps/1:49392670537:android:d235a6a14a796d87b12c98/releases/7h95ghmlcke20)
- iOS TestFlight: 이 Mac에 Apple Distribution identity와 provisioning profile이 없어 Archive/업로드를 실행하지 못했다. 이번 Firebase 배포는 Android만 해당한다.
- 운영 Play Store 배포는 실행하지 않았다. Notion 완료 상태도 변경하지 않았다. 알려진 이슈의 해결 및 전체 Figma 화면 승인은 별도 QA다.
