# Fillsa Android 화면별 기획

Android 앱의 화면별 기능·상태·Figma UI 기준은 `docs/screens/`에서 관리한다. 모든 UI 변경은 [Figma 기반 UI 개편 워크플로우](ui-redesign-workflow.md)를 따른다.

## 화면별 기획 문서

| 파일 | 화면 | Android 구현 위치 |
|---|---|---|
| [common.md](screens/common.md) | 공통 UI | `presentation/ui/common`, `presentation/ui/BottomMenu.kt` |
| [0_onboarding.md](screens/0_onboarding.md) | 스플래시·온보딩 진입 | `presentation/ui/common/SplashView.kt`, `SplashViewModel` |
| [0_onboarding_guide.md](screens/0_onboarding_guide.md) | 온보딩 가이드 | `presentation/ui/common/IntroduceView.kt` |
| [1_login.md](screens/1_login.md) | 로그인 | `presentation/ui/LoginView.kt`, `LoginViewModel` |
| [2_home.md](screens/2_home.md) | Home | `presentation/ui/home`, `HomeViewModel`, `TypingViewModel`, `ShareViewModel` |
| [3_calendar.md](screens/3_calendar.md) | Calendar | `presentation/ui/calendar`, `CalendarViewModel` |
| [4_list.md](screens/4_list.md) | List·상세·메모 | `presentation/ui/quotelist`, `ListViewModel` |
| [5_mypage.md](screens/5_mypage.md) | My Page | `presentation/ui/mypage`, `MyPageViewModel` |
| [5_1_notice.md](screens/5_1_notice.md) | 공지사항 | `presentation/ui/mypage`, `MyPageViewModel` |
| [5_2_inform.md](screens/5_2_inform.md) | 알림 설정·회원 탈퇴 | `presentation/ui/mypage`, `MyPageViewModel` |
| [5_3_theme.md](screens/5_3_theme.md) | 테마 | `presentation/ui/mypage/ThemeDialog.kt`, `MyPageViewModel` |

## 문서 작성 규칙

- Figma URL·프레임/노드·기준 이미지가 준비되기 전에는 `Figma UI 기준`의 빈 항목을 추정값으로 채우지 않는다.
- UI 변경 전 대상 화면의 기능·상태·Compose 구성 위치를 최신화하고 컴포넌트 분해를 작성한다.
- UI 변경 후에는 `docs/design-qa/`의 QA 기록으로 링크하고 검증 상태를 갱신한다.

## Home / Calendar API UI 연동 검증 (2026-09-08)

- Home의 주간·일간·답변 API 상태와 Calendar의 월간·일간·답변 API 상태를 기존 Compose UI에 연결한 최종 회귀 검증을 완료했다.
- 코루틴 ViewModel 테스트는 실제 production 인터페이스를 통과하는 counted fake로 API 호출 횟수·순서·실패 정책과 Calendar `SelectDay`의 무네트워크 정책을 검증한다.
- Retrofit 계약 테스트는 typing 조회의 GET v1과 저장의 POST v2 경로·method·body를 각각 검증한다.
- 최종 런타임 증거와 판정은 [Home 상호작용 QA](design-qa/2026-09-07-android-home-interactions-qa.md), [Home 답변 연동 QA](design-qa/2026-09-08-android-home-answer-integration-qa.md), [Calendar 화면 QA](design-qa/2026-09-08-android-calendar-screen-root-qa.md), [Calendar API 바인딩 QA](design-qa/2026-09-08-android-calendar-api-binding-qa.md)에 기록했다.
- Home/Calendar 소유 영역의 production mapper 결과는 재검증되었다. 다만 전체 프레임의 literal 판정은 기존 공유/시스템 경계(시스템 UI, 4-tab 대 Figma 3-tab, 광고)와 Home의 정확한 IME·인증 이미지·실완료 데이터 증거 부족 때문에 **Blocked**를 유지한다.
