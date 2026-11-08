# 로그인

## 로그인 만료 이슈 재검토 · 2026-10-28

- [Android QA 3건 AS-IS / TO-BE 보고서](../design-qa/2026-10-28-android-three-issues-report.html#session). HEAD `1d95ff1`에서 최초 로그인·재발급 기기 ID 공통화와 오류 collector 생명주기 수정 확인.
- 인증 관련 단위 테스트 12건·오류 lifecycle 계측 1건(API31) 재통과. 실서버 하루 경과·refresh 만료·삭제/재설치·백업 복원은 미검증으로 유지한다.
- 표준 HTTP401/403의 로그인 이동과 ApiFail 본문 errorCode401/403의 기존 팝업 분기를 구분해야 한다. 추가 코드 변경은 실서버 재현 근거에 따라 결정한다.

## Android 구성

- Compose 위치: `presentation/ui/LoginView.kt`
- 상태 관리: `LoginViewModel`
- 범위: 로그인 수단 선택, 로그인 진행·오류 상태

## Figma UI 기준

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X
- 대상 프레임/노드: Light `2929:3723` (`1.login`, 설명 `2929:3744`), Dark `2929:5815` (`1.login_dark`, 설명 `2929:5836`).
- 대상 기기/프레임 크기: Figma 360×720, 한국어, 글꼴 배율 1.0, 비회원 로그인 진입 상태. Android Preview 콘텐츠는 상태/내비게이션 바를 제외한 360×672dp. 에뮬레이터 전체 프레임은 360×720px/160dpi/API 35로 별도 기록했다.
- 검증 상태: 설명 중앙 정렬 Light/Dark Preview·360×720 실행 화면 Pass. 로그인 상태·버튼·하단 배치가 다른 전체 조립 화면 Figma 수용은 Blocked. [차이 기록](../design-qa/2026-11-08-login-description-center-qa.md).
- 기준 이미지: [Light 전체 프레임](../design-qa/artifacts/2026-10-25-full-screen-audit/figma/2929-3723.png), [Dark 전체 프레임](../design-qa/artifacts/2026-10-25-full-screen-audit/figma/2929-5815.png). Figma MCP `get_design_context`로 두 프레임과 설명 노드의 `text-center`, 320px 폭, 16px Pretendard/24px 줄높이를 2026-11-08 재확인.
- QA 기록: [로그인 설명 정렬 QA](../design-qa/2026-11-08-login-description-center-qa.md).

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| 로그인 안내 설명 `Text` | Light `2929:3744`, Dark `2929:5836` | 320dp 폭에서 같은 문구를 두 줄로 중앙 정렬 | `LoginContent`의 로고와 버튼 그룹 사이 | 컴포넌트 Pass |
| 로그인 전체 `LoginContent` | Light `2929:3723`, Dark `2929:5815` | 설명과 기존 로고·버튼·약관 조립 | `Screens.Login` | 전체 프레임 Blocked |


## 전체 화면 대조 · 2026-10-25

- 기준 페이지: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-3720
- 범위: 현재 production UI 재사용 Preview. 시각 수정은 사용자 검토 후 별도 작업.
- 조건: ko, fontScale 1.0, Light/Dark, 고정 fixture. Preview는 시스템 바·IME를 포함한 최종 승인이 아님.
- QA 기록: [전체 화면 대조 보고서](../design-qa/2026-10-25-full-screen-audit.html)
- 상태·크기·원본 이미지: [screen-map.json](../design-qa/artifacts/2026-10-25-full-screen-audit/screen-map.json)

| 대상 노드 | production 컴포넌트 | 상태 / 책임 | 검증 상태 |
|---|---|---|---|
| 2929:3723 | LoginContent | 1.login · Light | 조사 중 |
| 2929:4254 | LoginContent | 1.login-apple · Light | 조사 중 |
| 2929:5815 | LoginContent | 1.login_dark · Dark | 조사 중 |
| 2929:5878 | LoginContent | 1.login_dark-apple · Dark | 조사 중 |
| 2929:9931 | CommonDialog | 2-1. modal_login · Dark | 조사 중 |
| 2929:10788 | LoginContent | 1.login_dark · Dark | 조사 중 |
| 2929:18449 | LoginContent | 1.login · Light | 조사 중 |
| 2929:19027 | CommonDialog | 2-1. modal_login · Light | 조사 중 |
