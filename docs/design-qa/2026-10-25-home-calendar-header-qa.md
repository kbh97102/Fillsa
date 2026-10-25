# Home 달력 연도·월 버튼 UI QA

## Reference and scope

- Figma: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-16228
- Light header `2929:16228`, Dark header `3039:28108`. Full frames `3139:1501` / `3139:1753`, 360×821.
- Evidence folder: `artifacts/2026-10-25-home-calendar-header/`.
- Reference: `figma-header-light.png`, `figma-header-dark.png` and matching `.txt` MCP context; `figma-full-light.png`, `figma-full-dark.png` retained from the earlier MCP export.
- User request: Home 달력의 연도·월 버튼 아이콘 및 중앙 정렬 수정. 기존 전체 비교 보고서와 원본 자료는 수정하지 않았다.

## Cause and changes

- `‹`, `›`, `⌄` Unicode 글리프를 화살표로 사용하여 폰트 기준선과 자형에 따라 모양·중심이 달랐다.
- Light는 가변 폭 / 12sp, Dark는 65·61dp / 14sp였다. 높이31·간격5·radius7·border #E7E1D6도 Figma와 달랐다.
- Figma SVG 원본을 보관하고 동일 형상의 xxxhdpi PNG로 사용한다. 좌우 화살표 16dp / 아래 화살표12dp, 양쪽 버튼32dp, 아이콘 수직·수평 중앙 정렬.
- 연도65×32dp / 월63×32dp, 간격6dp, radius8dp, border #E5E5E5, Inter Medium 14sp / lineHeight20sp, 글자와 아이콘 수직 중앙 정렬. 월 텍스트의 수평 시작 정렬은 Figma 명세를 따른다.
- 달력 내부12dp 패딩으로 헤더 폭224dp 확보. 시스템 연·월 선택기 및 확인·취소 callback은 유지한다.

## Component evidence

| Component | Figma | State | Result |
|---|---|---|---|
| HomeCalendarSelector | 2929:16232 / 16236, 3039:28112 / 28116 | Light/Dark | 원본 아래 화살표, 버튼 치수 및 수직 정렬 확인 |
| HomeCalendarArrow | 2929:16229 / 16240 | 이전/다음, 경계 비활성 | 원본 chevron과 32dp 중앙 배치 확인 |
| HomeCalendarHeader | 2929:16228 / 3039:28108 | 2026-08 | Light/Dark Preview 및 런타임 확인, 전체 화면 최종 승인은 아님 |

## Validation round 1

- Before: `before-light.png`에서 문자 화살표와 폭·정렬 차이 재현.
- CLI command: `~/.local/bin/android studio render-compose-preview presentation/src/main/java/com/arakene/presentation/ui/home/HomeInlineCalendar.kt <PreviewName> --project Fillsa --output-image-file=<evidence PNG> --print-semantics`.
- Header Preview: `HomeCalendarHeaderLightPreview`, `HomeCalendarHeaderDarkPreview`; 224×36dp, ko, fontScale1, 2026-08 / 상한2026-10-25. `header-light.png`, `header-dark.png` 및 semantics 저장. 실제 헤더 높이32dp, Preview는 그림자 여백 포함36dp.
- Calendar Preview: `HomeInlineCalendarLightPreview`, `HomeInlineCalendarDarkPreview`; 248×335dp, ko, fontScale1, 선택일/상한2026-08-16. `calendar-light.png`, `calendar-dark.png` 및 semantics 저장. 마지막 월의 다음 화살표 비활성 표시 확인.
- CLI는 최초에 오래된 Fast Preview overlay를 읽어 신규 Preview에 NoSuchMethodException을 반환했다. 사용자 IDE 빌드 후에도 동일했다. 실제 Gradle class에는 메서드가 있고 임시 overlay class에는 없는 것을 javap로 확인했다. 해당 클래스26개만 `/tmp/fillsa-calendar-render/stale-preview-backup`에 분리한 후 4개 Preview가 정상 렌더링됐다. 기존 함수를 사용해 나온 오래된 렌더는 새 결과로 교체했다.
- Header semantics: 좌우 버튼과 텍스트 중심이 약48px(출력 밀도2.625)로 일치한다. SVG 원본과 런타임 자산의 존재·호출 위치·실효 크기를 확인했다.
- Debug/Release Kotlin 및 APK 빌드 성공. `HomeYearMonthDialogTest` 계측4개 통과(`instrumentation.txt`): 연도/월 진입, 취소 보존, 확인 후 월 변경, 경계 월 제한, 내부/외부 탭 회귀.

## Final assembled-screen evidence

- API35 emulator-5554, 360×821px/dp, density160, ko, fontScale1, 비회원. Release APK를 기존 서명으로 `install -r`; 앱 데이터 유지. 물리 기기에는 설치하지 않았다.
- `runtime-light.png` / `runtime-dark.png`: 전체 프레임(시스템 영역 포함), 2026-08을 표시한 달력. `runtime-picker-light.png` / `runtime-picker-dark.png`: 두 선택 버튼의 native 선택창 진입 확인.
- 비교는 Figma 원본과 Preview·전체 런타임을 나란히 놓고 헤더를 확인했다. 원본 헤더224×36과 Preview588×95는 동일 dp로 환산한다. 전체 프레임의 절대 위치·높이 차이를 헤더 정렬 문제와 혼동하지 않는다.
- 검증 후 emulator size360×720 / night=no 복구.
- 요청 범위 결과: 아이콘 대체와 중앙 정렬 수정, 빌드·Preview·실제 에뮬레이터 확인.
- 전체 조립 화면 최종 Figma 승인: **Blocked / 미승인**. 기존 팝업 위치, 달력 배경·날짜 셀·요일 간격, 플랫폼 시스템 바, 서버 문장·날짜·광고는 범위 밖 차이로 남는다. Android elevation 그림자도 Figma CSS shadow와 픽셀 단위 동일하다고 주장하지 않는다.
- 실제 회원 기기, OEM별 표시 및 전체 Home 재설계는 검증하지 않았다. 상세 결과와 APK/source hash는 `verification.json`.
