# 로그인

## Android 구성

- Compose 위치: `presentation/ui/LoginView.kt`
- 상태 관리: `LoginViewModel`
- 범위: 로그인 수단 선택, 로그인 진행·오류 상태

## Figma UI 기준

- Figma URL:
- 대상 프레임/노드:
- 대상 기기/프레임 크기:
- 검증 상태: 미검증
- 기준 이미지:
- QA 기록:

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|


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
