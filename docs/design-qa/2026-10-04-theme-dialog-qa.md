# 테마 팝업 Figma UI QA

## Reference
- Figma URL: [라이트](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-11178), [다크](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-11694)
- Target frames/nodes: `2929:11178` 라이트 선택 (360 × 634), `2929:11694` 다크 선택 (360 × 720); 팝업 `2929:11237` / `2929:11768`.
- Full-frame reference: [라이트](artifacts/2026-10-04-theme-figma/figma-2929-11178.png), [다크](artifacts/2026-10-04-theme-figma/figma-2929-11694.png). Figma가 그린 시스템 바와 화면 배경 포함.
- Preview command: `~/.local/bin/android studio render-compose-preview presentation/src/main/java/com/arakene/presentation/ui/mypage/ThemeDialog.kt ThemeItemLightPreview --project Fillsa --output-image-file=docs/design-qa/artifacts/2026-10-04-theme-figma/android-theme-item-baseline.png --print-semantics`.
- Preview image: 생성 실패. Android Studio CLI가 새 `@Preview`에 `Failed to find preview element`를 반환한다. IDE의 `find-declaration`도 새 함수를 아직 찾지 못한다.
- Preview conditions: 296 × 24dp 항목, 320 × 237dp 팝업, 한국어, fontScale 1, light/dark, 라이트 또는 다크 선택 fixture. `ThemeItemLightPreview`, `ThemeDialogLightPreview`, `ThemeDialogDarkPreview`가 production composable을 호출한다.
- Runtime target/capture: 새 `fillsa_qa_20261005` 에뮬레이터, API 35, 360 × 634px(light) / 360 × 720px(dark), override density 160dpi, fontScale 1.0. 시스템 언어 en-US, 앱 문구 한국어, 비회원 My page, 디버그 1.0.29(23). [light 전체 프레임](artifacts/2026-10-05-runtime/theme-popup-light-selected-fixed-360x634.png), [dark 전체 프레임](artifacts/2026-10-05-runtime/theme-popup-dark-border-360x720.png).

## Component inventory
| Component | Figma node | Target state | Final result |
|---|---|---|---|
| `ThemeItem` | `2929:11240`–`2929:11252`, `2929:11771`–`2929:11782` | 세 선택 항목, 라이트/다크 | 미검증 |
| `PositiveButton` | `2929:11258`, `2929:11789` | 확인 | 미검증 |
| `ThemeDialog` | `2929:11237`, `2929:11768` | 팝업 조립 | 미검증 |

## Validation rounds
### Round 1
| Scope | Difference | Fix | Result |
|---|---|---|---|
| 코드와 Figma 치수 | Figma 20px 글자·30px 항목 간격, 기존 코드 18sp·15dp | Figma 기준 20sp·30dp로 수정. 320 × 237dp 팝업 콘텐츠 Preview 추가 | 빌드 통과, 시각 검증 대기 |
| Preview CLI | IDE가 외부 변경 이전 소스를 계속 사용해 새 Preview를 찾지 못함 | `:presentation:compileDebugKotlin`, `:app:assembleDebug` 성공 후 재시도 | Blocked: IDE 새로고침 필요 |

### 2026-10-05 런타임 부분 비교
| Scope | Difference | Fix | Result |
|---|---|---|---|
| 라이트 선택 아이콘 | Figma `2929:11242`는 `#5D66FE`, 기존 Android는 다크용 `#FFCB5C` | Figma SVG와 동일한 `icn_radio_selected_light.xml` 추가, 테마별 자산 선택 | 에뮬레이터에서 파란색 선택 표시 확인 |
| 다크 팝업 테두리 | Figma `2929:11768`는 1px `#616161`, 기존 Android는 테두리 없음 | 다크 팝업에 1dp `gray_500` 테두리 | y=350 단면에서 Figma·런타임 모두 x=20/339 테두리, x=21/338 내부색 일치 |
| 팝업 크기·배치 | Figma와 런타임 모두 x=20..339, light y≈199..435, dark y=241..477 | 앞선 20sp·30dp 수정 유지 | 팝업 컴포넌트 부분 일치 |

- Full-frame side-by-side (왼쪽 Figma, 오른쪽 Android): [light](artifacts/2026-10-05-runtime/compare-theme-light.png), [dark](artifacts/2026-10-05-runtime/compare-theme-dark.png).
- 선택 저장·재진입: 라이트와 다크를 각각 확인 버튼으로 적용한 뒤 팝업을 다시 열어 해당 선택 표시가 유지됨.
- 남은 전체 프레임 차이: Figma는 로그인된 My page, 런타임은 비회원 My page이며 바 구성·광고 문구가 다르다. 다크 Dialog 외부 dim은 Figma `#212121` 80% 오버레이보다 런타임이 더 검고, 시스템 상태 표시줄의 아이콘도 일치하지 않는다. 승인 가능한 전체 프레임 비교가 아니다.

## Final assembled-screen result
- Final runtime capture: 위 에뮬레이터 전체 프레임 캡처 확보. 로그인·광고·시스템 UI 조건이 Figma와 다르다.
- Comparison: 위 side-by-side. 팝업 하위 영역은 부분 대조했으나 전체 조립 화면은 차이 존재.
- Result: Blocked (Compose Preview 미실행 및 동일 조건의 전체 프레임 불일치)
- Remaining differences: Android Studio의 이전 소스 인덱스를 새로고침한 뒤 결정적 Preview를 렌더링해야 한다. 로그인된 Figma 상태와 동일한 런타임, Dialog 스크림·시스템 바·광고 차이를 해결/검증해야 한다. Notion의 과거 18sp 축소 요청과 현재 Figma 20px가 다르므로 이번 코드는 프로젝트 지침의 Figma 기준을 적용했다.
