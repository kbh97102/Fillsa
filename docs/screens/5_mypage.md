# My Page

## Android 구성

- Compose 위치: `presentation/ui/mypage/MyPageView.kt` 및 하위 섹션
- 상태 관리: `MyPageViewModel`
- 범위: 회원·비회원 정보, 계정 메뉴, 설정 진입

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
| 2929:11031 | MyPageContent | 5. my page_login · Light | 조사 중 |
| 2929:11102 | MyPageContent | 5. my page_login · Light | 조사 중 |
| 2929:11613 | MyPageContent | 5. my page_login · Dark | 조사 중 |
| 2929:11791 | MyPageContent | 5. my page_login · Dark | 조사 중 |
