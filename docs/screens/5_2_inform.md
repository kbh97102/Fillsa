# 알림 설정·회원 탈퇴

## Android 구성

- Compose 위치: `presentation/ui/mypage/AlertView.kt`, `AlertSwitchSection.kt`, `MyPageBottomButtonSection.kt`
- 상태 관리: `MyPageViewModel`
- 범위: 알림 설정, 계정 관련 안내와 회원 탈퇴

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
| 2936:20033 | AlertContent | modal_delete ID · Light | 조사 중 |
| 2929:11178 | ThemeDialogContent | 5-3. theme · Light | 조사 중 |
| 2929:11260 | AlertContent | 5-2. inform · Light | 조사 중 |
| 2929:11314 | AlertContent | list-2 · Light | 조사 중 |
| 2929:11435 | AlertContent | 5-2. inform · Dark | 조사 중 |
| 2929:11489 | AlertContent | list-2 · Dark | 조사 중 |
| 2929:11694 | ThemeDialogContent | 5-3. theme · Dark | 조사 중 |
| 2936:20153 | AlertContent | modal_delete ID · Dark | 조사 중 |
