# 홈 달력 Android CLI Preview / Figma QA

## Reference
- 요청: “디자인 검증은 android CLI로 preview와 피그마를 비교해보자”. 검증만 수행, 앱 소스 수정 없음.
- Notion: https://www.notion.so/3ffe639fc7a48005b57ff2fe3eeeb952
- Figma: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-16227
- 본체 Light `2929:16227`, Dark `3039:28107`; 헤더 `2929:16228` / `3039:28108`; 전체 `3139:1501` / `3139:1753` (360×821).
- [비교 HTML](2026-10-28-calendar-preview-figma.html), [증거](artifacts/2026-10-28-preview-figma/verification.json).
- Studio Quail 1 / 2026.1.1, Fillsa READY. HEAD `1d95ff1`.
- 명령: `~/.local/bin/android studio render-compose-preview presentation/src/main/java/com/arakene/presentation/ui/home/HomeInlineCalendar.kt <PreviewName> --project Fillsa --output-image-file=<PNG> --print-semantics`.
- Preview: `HomeCalendarHeader{Light,Dark}Preview` 224×36dp, 2026-08/상한2026-10-25; `HomeInlineCalendar{Light,Dark}Preview` 248×335dp, 2026-08/선택·상한2026-08-16. 모두 ko/fontScale1, 명시적 uiMode.
- 원본 PNG·semantics·Figma context·metadata·전체 프레임을 증거 폴더에 저장. 약2.625px/dp 결과를 선언 dp 크기로 축소해 나란히 비교. Figma 본체 이미지는 그림자 포함254×341, 내용248×335이며 외곽을 보존.

## Component inventory / Preview 비교
| 범위 | 결과 | 근거 |
|---|---|---|
| 헤더 버튼 | 치수·정렬 일치, 그림자 차이 | 65/63×32, 간격6, Inter Medium14/20, 원본 chevron12/16; Android elevation1dp가 Figma shadow보다 더 두꺼워 보임 |
| 달력 표면 | 수정 필요 | Figma white/#E5E5E5 vs 코드 #FFFDFC/#E7E1D6, 본체 shadow 없음 |
| 요일 | 수정 필요 | Figma 헤더 아래16, y60/행21; Light 아래8/y≈52, Dark 아래16/y≈60, 텍스트 typography가 테마별로 다름 |
| 날짜 | 수정 필요 | Figma Inter Regular14/20, 현재 Light Pretendard12/18 · Dark14/21 |
| 선택 셀 | 수정 필요 | Figma32×32/radius8 vs 현재31×31/원형 radius99 |

- Figma 선택일25 vs fixture16, 다음 달 활성 vs 현재 상한으로 인한 비활성은 입력 조건 차이로 분리했다. 수정 대상으로 단정하지 않음.
- 하위 헤더는 그림자까지 완전 일치하지 않으므로 상위 승인 게이트를 통과시키지 않았다. 본체 렌더는 잔여 차이 진단 증거이며 승인용 검증 완료가 아니다.
- 수정 위치: `HomeInlineCalendar.kt` 달력 Column/요일 Row·Text/날짜 Box·Text/selector shadow.

## Final assembled-screen result
- 새 런타임 캡처: 없음. 전체 Figma 프레임만 최신 확보.
- 결과: **Blocked / 전체 승인 보류**. Preview에서 확인된 차이가 남아 있다.
- 시스템 연월 선택창은 기존 승인된 Figma 예외. 이번 닫힌 달력 스타일 검증과 별개.
- 새 수정 또는 전체 런타임 검증이 없으므로 완전한 검증 차수는 증가시키지 않음.
