# 공통 UI

## Android 구성

- Compose 위치: `presentation/ui/common`, `presentation/ui/BottomMenu.kt`
- 범위: 공통 헤더, 하단 메뉴, 다이얼로그, 입력 필드, 로딩·광고 영역

## Figma UI 기준

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X/%E2%9C%92%EF%B8%8F%ED%95%84%EC%82%AC?node-id=2985-22510
- 대상 프레임/노드: Calendar completed/answered frame `2985:22510`; bottom navigation `3087:29220`
- 대상 기기/프레임 크기: 360 × 1101 px light/ko-KR; navigation 360 × 60dp, three equal 120dp items
- 검증 상태: 하단 내비게이션 컴포넌트 통과 — Home/Calendar/My page 3탭, 32dp icon, 12sp label, light selected/unselected 색상 확인. 광고와 시스템 navigation surface는 별도 범위다.
- 기준 이미지: `docs/design-qa/assets/calendar-figma-2929-13366/2026-09-08/figma-answered-image-2985-22510.png`
- QA 기록: `docs/design-qa/2026-09-16-android-three-tab-bottom-navigation-qa.md`

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| `BottomNavigationBar` | `3087:29220` | Home/Calendar/My page 3탭, 선택 상태와 root navigation | app `Scaffold.bottomBar` | 통과 |
