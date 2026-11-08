# Android 로그인 설명 중앙 정렬 QA · 2026-11-08

## 원문·범위

- [Notion 원문](https://app.notion.com/p/3ffe639fc7a480b0b19cf56f597ddba9), `3ffe639f-c7a4-80b0-b19c-f56f597ddba9`, 2026-11-08 재조회. 본문: “구글 계정으로 시작하기” 누락, Fillsa 로고 교체, 이용약관·개인정보 링크 미적용의 세 체크 항목과 AS-IS/TO-BE 첨부. 후속 댓글 2026-10-31 다영 이: “로그인 후, 나만의 필사를 안전하게 저장할 수 있습니다.” 중앙 정렬이 다른 기기에서도 깨지는지 확인 요청. 2026-11-02 SooJi: 같은 증상 확인. 2026-11-04 SooJi: iOS 중앙 정렬 재제보와 “Android도 중앙정렬이 깨지고 있습니다”. 댓글 4개, 더보기 없음.
- 원문 이미지 보존: [Notion AS-IS](artifacts/2026-11-08-login-description-center/notion-original-as-is.png), [Notion TO-BE](artifacts/2026-11-08-login-description-center/notion-original-to-be.png), [중앙 정렬 댓글](artifacts/2026-11-08-login-description-center/notion-comment.png). 이미지의 플랫폼과 촬영 시점은 원본 제보 기준이며 이번 Android 수정 후 화면으로 사용하지 않는다.
- 사용자 승인: “Android 로그인: LoginContent 설명 Text를 화면 폭에 맞춰 가운데 정렬하고 Figma Light/Dark의 줄바꿈과 전체 프레임을 재검증한다.” [계획 v1](../screens/2026-11-08-login-description-3ffe639f-plan.md).
- 이번 수정은 Android 설명 문단만 담당한다. 인증·로고·링크의 기존 상태는 건드리지 않았다. Notion 상태도 변경하지 않았다.

## Reference

- Figma 파일: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X
- Light 전체 `2929:3723`/설명 `2929:3744`: [360×720 기준 이미지](artifacts/2026-10-25-full-screen-audit/figma/2929-3723.png).
- Dark 전체 `2929:5815`/설명 `2929:5836`: [360×720 기준 이미지](artifacts/2026-10-25-full-screen-audit/figma/2929-5815.png).
- Figma MCP `get_design_context` 재확인: 두 설명 모두 좌우 20dp의 320dp 폭, Pretendard Regular 16px/24px, `text-center`, 같은 문구와 두 줄. 참조 프레임은 루트 배경·status bar·하단 영역을 포함한다.
- Preview 조건: `AuditFigma29293723`/`AuditFigma29295815`, 360×672dp 콘텐츠, API 35 Android Studio Quail 1 READY, ko, fontScale 1.0, Light/Dark, `LoginContent(isOnboarding=false)`.
- Runtime 조건: Medium Phone API 35 에뮬레이터, 임시 `wm size 360x720`, `wm density 160`, ko, fontScale 1.0, 실제 My Page → 로그인 경로의 `isOnboarding=true`. 전체 360×720 캡처에 status/navigation bar 포함. 테스트 전 화면 override는 360×1101/160이며 종료 시 복원했다.

## 컴포넌트와 코드 변경

| 컴포넌트 | Figma 노드 | AS-IS 원인 | 변경 | 확인 |
|---|---|---|---|---|
| `LoginContent` 설명 `Text` | `2929:3744`, `2929:5836` | `LoginView.kt:386–390`에 폭·`textAlign` 없음. 360dp의 둘째 줄 `니다.`가 왼쪽으로 붙음 | `Modifier.fillMaxWidth().padding(top=...)`와 `textAlign = TextAlign.Center`. 320dp 부모 폭을 차지해 각 줄을 중앙에 배치 | Light/Dark CLI Preview와 실행 화면에서 2줄·중앙 정렬 확인 |
| 로그인 전체 `LoginContent` | `2929:3723`, `2929:5815` | 이전부터 로고·버튼 세로 간격 등 Figma 차이 있음 | 변경 없음 | 전체 프레임은 미수용; 아래 차이 기록 |

같은 `LoginContent`를 `LoginView`와 기존 Audit Preview가 호출한다. 문자열 리소스, `LoginButton`, 로그인 action/API·오류 처리·회원 데이터는 변경하지 않았다.

## 검증

| 항목 | 증거 | 결과 |
|---|---|---|
| Android Studio | `~/.local/bin/android studio check` → `READY Fillsa` | Pass |
| Debug 빌드 | `./gradlew :app:assembleDebug` → `BUILD SUCCESSFUL in 4s` | Pass |
| Light Preview | `android preview render :presentation --fqn=com.arakene.presentation.ui.audit.LoginScreenAuditPreviewsKt.AuditFigma29293723 --component-tree --output=docs/design-qa/artifacts/2026-11-08-login-description-center/preview/light` · [PNG](artifacts/2026-11-08-login-description-center/preview/light/com/arakene/presentation/ui/audit/LoginScreenAuditPreviewsKt/AuditFigma29293723_0.png) · [tree](artifacts/2026-11-08-login-description-center/preview/light/com/arakene/presentation/ui/audit/LoginScreenAuditPreviewsKt/AuditFigma29293723_0.json) | 설명 2줄·중앙 정렬 Pass |
| Dark Preview | 같은 명령의 `AuditFigma29295815` · [PNG](artifacts/2026-11-08-login-description-center/preview/dark/com/arakene/presentation/ui/audit/LoginScreenAuditPreviewsKt/AuditFigma29295815_0.png) · [tree](artifacts/2026-11-08-login-description-center/preview/dark/com/arakene/presentation/ui/audit/LoginScreenAuditPreviewsKt/AuditFigma29295815_0.json) | 설명 2줄·중앙 정렬 Pass |
| Preview semantics | 설명 노드 bounds Light `(53,1008)-(892,1121)`, Dark `(53,756)-(892,869)`, 둘 다 너비 839px ≈ 320dp, 원문 문자열 일치 | Pass |
| 실행 화면 | 새 Debug APK를 에뮬레이터에 데이터 보존 설치, My Page → 로그인. [Light 전체](artifacts/2026-11-08-login-description-center/runtime-light.png) · [Dark 전체](artifacts/2026-11-08-login-description-center/runtime-dark.png) | 설명 2줄·중앙 정렬 Pass; 전체 화면 Blocked |

### 1차 비교 · 컴포넌트

- Figma 설명의 첫 줄은 “로그인 후, 나만의 필사를 안전하게 저장할 수 있습”, 둘째 줄은 “니다.”이며 Light/Dark 모두 320dp 안에서 두 줄의 수평 중심이 같다. 수정 후 Preview와 런타임도 같은 문구·줄바꿈·중심을 보였다. 360dp보다 넓은 Galaxy에서는 한 줄일 수 있으므로 360dp 에뮬레이터를 사용했다.
- 코드 변경은 수평 제약과 정렬만 추가한다. 높이와 위쪽 패딩 값은 유지해 다음 버튼의 기준 간격을 임의로 바꾸지 않았다.

### 1차 비교 · 전체 조립 화면

- Figma 전체 프레임과 360×720 런타임 전체 프레임을 같은 경계로 나란히 확인했다. 설명 컴포넌트는 통과했으나 런타임 진입은 `isOnboarding=true`라 상단 닫기 아이콘과 Kakao/Google만 보이고, Figma `2929:3723`/`2929:5815`는 비회원 버튼을 포함하는 `isOnboarding=false` 상태다. 현재 런타임은 하단 내용도 화면 아래로 넘어간다. Figma의 Apple 버튼이 있는 별도 onboarding 프레임과도 Android의 공급자 구성이 다르다.
- status bar의 실제 Android 시간·통신 아이콘도 Figma 합성 9:41 프레임과 다르다. 이 상태·시스템 영역 차이를 설명 정렬 변경의 성과로 처리하지 않는다. 전체 프레임의 최종 Figma 수용은 **Blocked**다. 5차 수정 루프는 다른 화면/제품 결정으로 범위가 커지므로 시작하지 않았다.

## 최종 판정

- **요청한 설명 문단 수정: Pass.** Light/Dark 360dp Preview와 실제 360×720 에뮬레이터에서 같은 2줄·가로 중앙 정렬.
- **전체 조립 화면 Figma 최종 승인: Blocked.** 런타임과 참조 프레임의 로그인 상태/공급자 버튼·하단 배치가 다르다. 기존 전체 화면 불일치의 별도 범위 결정과 재검증이 필요하다.
- 계정 OAuth는 사용자 제외 범위여서 실행하지 않았다. 실기기 Galaxy에는 새 APK를 설치하지 않았다. 기존 Galaxy 데이터·설정은 변경하지 않았다.

[AS-IS/TO-BE HTML 보고서](2026-11-08-login-description-center-report.html)
