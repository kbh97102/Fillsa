# 위젯 Figma 및 운영 QA

## Reference
- Figma URL: [위젯 디자인 섹션](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2355-2140)
- Target frames/nodes: [화면 문서](../screens/6_widget.md)에 열거한 2×2·4×2 최소/최대, 긴 글, 연속 필사 알림 12개 노드.
- Reference images: [프로젝트에 저장한 개별 Figma PNG](artifacts/2026-10-04-widget-figma/).
- Preview command/image/conditions: 미실시. 구성 화면의 선택 미리보기 대상은 독립 Figma 프레임 확인 후 결정.
- Runtime target/capture: 운영 앱의 실제 versionName/versionCode, Android 런처/기기/해상도/밀도/API 레벨 확인 필요.

## Component inventory
| Component | Figma node | Target state | Final result |
|---|---|---|---|
| `MyWidget` | `2416:2839`, `2354:1832`, `2354:1825`, `2398:2250` 및 각 변형 | 2×2/4×2 × 짧은/긴 글/알림 | 미검증 |
| 구성 화면 선택 미리보기 | 제공된 섹션에 대상 프레임 없음 | 현재 선택 비율 | Figma 프레임 필요 |

## Validation rounds
- 코드 사전 조사: `MyWidget`의 루트에 `MainActivity` 실행 action이 연결되어 있고, 구성 화면은 `sizeType == "SMALL"`에 따라 두 미리보기의 `isCurrent`를 분기한다. 이는 운영 동작과 화면 증거를 대신하지 않는다.

## Final assembled-screen result
- Final runtime capture: 미실시
- Comparison: 미실시
- Result: Blocked (운영 앱/런처 전체 프레임 미검증)
- Remaining differences: 운영 빌드 및 구성 화면의 독립 Figma 프레임 확인 필요. `2398:2828`은 위젯 크기 표이며 선택 테두리 기준이 아니다.
