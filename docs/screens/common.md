# 공통 UI

## Android 구성

- Compose 위치: `presentation/ui/common`, `presentation/ui/BottomMenu.kt`
- 범위: 공통 헤더, 하단 메뉴, 다이얼로그, 입력 필드, 로딩·광고 영역

## Figma UI 기준

- Figma URL (전역 로딩): https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X/%E2%9C%92%EF%B8%8F%ED%95%84%EC%82%AC?node-id=2929-6086
- 대상 프레임/노드: global loading example `2929:6086`; loading dim `2929:6175`; progress indicator `2929:6176`; bottom navigation `3087:29220`
- 대상 기기/프레임 크기: global loading 360 × 720 px light/ko-KR; navigation 360 × 60dp, three equal 120dp items
- 검증 상태: 하단 내비게이션 컴포넌트 통과 — Home/Calendar/My page 3탭, 32dp icon, 12sp label, light selected/unselected 색상 확인. 광고와 시스템 navigation surface는 별도 범위다.
- 기준 이미지: `docs/design-qa/assets/global-loading-spinner-2929-6086/reference-full.png`
- QA 기록: `docs/design-qa/2026-09-16-android-three-tab-bottom-navigation-qa.md`, `docs/design-qa/2026-09-21-android-global-loading-spinner-qa.md`

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| `BottomNavigationBar` | `3087:29220` | Home/Calendar/My page 3탭, 선택 상태와 root navigation | app `Scaffold.bottomBar` | 통과 |
| `CircleLoadingSpinner` | `2929:6175`, `2929:6176` | 전체 화면 80% dim, 중앙 120dp progress indicator, 입력 차단 | `MainActivity` root overlay | 시각 QA 통과, 기능 QA 사용자 확인 대기 |

### 전역 로딩 디자인 계약

- 전체 화면을 `#212121` 80% 투명도로 덮는다.
- 중앙 컨테이너는 120 × 120dp이며, 내부 Figma SVG의 실제 시각 크기는 112 × 112dp다.
- track은 `#D3D5FF`, 진행 arc는 `#5C65FF`, stroke는 12dp, 끝은 round cap이다.
- 로딩 중 하위 화면 입력을 차단하고 접근성에는 indeterminate progress로 노출한다.
- 화면 진입 시 발생하는 Home, Calendar, Typing, Login API와 QuoteList/Notice 최초 Paging refresh에 전역 로딩을 연결한다. 좋아요·답변·이미지·메모·광고처럼 화면 전환과 무관한 background/mutation 요청은 전역 overlay 범위에서 제외한다.
