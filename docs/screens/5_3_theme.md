# 테마

## Android 구성

- Compose 위치: `presentation/ui/mypage/ThemeDialog.kt`, `presentation/ui/theme/`
- 상태 관리: `MyPageViewModel`
- 범위: 라이트·다크 테마 선택과 적용 상태

## Figma UI 기준

- Figma URL: [라이트](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-11178), [다크](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-11694)
- 대상 프레임/노드: `2929:11178` 라이트 선택, `2929:11694` 다크 선택. 팝업 `2929:11237` / `2929:11768`, 선택 목록 `2929:11239` / `2929:11770`, 확인 버튼 `2929:11258` / `2929:11789`.
- 대상 기기/프레임 크기: Figma 360 × 634(라이트), 360 × 720(다크) px, 한국어, light/dark. 밀도·API 레벨은 Figma에 명시되지 않았으며 런타임 검증 조건에 별도 기록한다.
- 검증 상태: 에뮬레이터에서 팝업 부분 비교 완료. Compose Preview와 동일 조건의 전체 프레임 검증 대기
- 기준 이미지: [라이트 전체 프레임](../design-qa/artifacts/2026-10-04-theme-figma/figma-2929-11178.png), [다크 전체 프레임](../design-qa/artifacts/2026-10-04-theme-figma/figma-2929-11694.png)
- QA 기록: [테마 팝업 QA](../design-qa/2026-10-04-theme-dialog-qa.md)

Figma의 선택 항목은 20px Pretendard Regular, 항목 간격 30px, 팝업 너비 320px, 내부 너비 296px이다. 라이트 선택 라디오는 `#5D66FE`, 다크 선택 라디오는 `#FFCB5C`이며 다크 팝업에는 1px `#616161` 테두리가 있다. 라이트 프레임은 세로 634px로 다크 프레임(720px)과 높이가 달라 두 전체 프레임을 각각 비교한다.

2026-10-05 에뮬레이터 부분 비교에서 팝업 위치·크기·선택 자산과 다크 테두리를 확인했다. 선택 저장 후 재진입도 확인했다. 로그인된 Figma 화면과 비회원 런타임의 배경, 광고, 시스템 바가 달라 조립 화면 승인은 보류한다. 상세 증거는 QA 기록을 따른다.

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| `ThemeItem` | `2929:11240`, `2929:11246`, `2929:11251`; 다크 `2929:11771`, `2929:11776`, `2929:11782` | 라이트·다크·시스템 선택 항목과 라디오 표시 | `ThemeDialog` | 에뮬레이터 부분 비교, Preview 대기 |
| `PositiveButton` | `2929:11258`, `2929:11789` | 선택 확정 | `ThemeDialog` | 미검증 |
| `ThemeDialog` | `2929:11237`, `2929:11768` | 세 항목 배치와 팝업 표면 | `MyPageView` | 에뮬레이터 부분 비교, 전체 화면 대기 |
