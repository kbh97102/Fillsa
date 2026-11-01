# Calendar 미필사 안내 색상 QA

- Notion: https://app.notion.com/p/text-area-408e639fc7a480da9407d6a75c64df65
- 계획: [v1](../screens/2026-11-01-calendar-color-408e639f-plan.md)
- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3039-24906
- 대상 프레임/노드: 다크 전체 `3039:24906`, 안내 `3039:25079`, 카드 `3039:25073`; 라이트 회귀 `2985:21952`, 안내 `2985:22183`, 카드 `2985:22145`.
- 기준 이미지: [다크 전체 360×816](artifacts/2026-11-01-qa-calendar-color/figma-dark-basic-3039-24906.png). 루트·시스템 영역·하단 바 포함.
- 수정 전 Preview 명령: `~/.local/bin/android preview render :presentation --fqn=com.arakene.presentation.ui.audit.CalendarAuditPreviewsKt.AuditFigma303924906 --component-tree --output=/tmp/fillsa-qa-check-calendar-dark`
- 수정 전 Preview: [이미지](artifacts/2026-11-01-qa-calendar-color/android-before-preview.png), [component tree](artifacts/2026-11-01-qa-calendar-color/android-before-component-tree.json). 360×768dp 콘텐츠, Dark, ko, fontScale 1.0, 고정 미필사 상태.
- 결과: 다크 안내 글자 흰색·카드 테두리 보라색으로 Figma의 노란색 `#FFCB5C`와 불일치. SVG와 일부 아이콘은 Preview에서 표시되지 않아 별도 런타임 확인이 필요하다.
- 승인: 2026-11-01 사용자의 “좋아 세가지 모두 진행, stage로부터 각자의 워크트리를 만들어서 진행하자”. `stage` `c14b4e6` 기반 `codex/qa-calendar-color-408e639f`에서 v1 범위 구현.
- 변경: `CalendarQuoteSection.kt`의 다크 미필사 안내 두 문구와 미필사 명언 카드 테두리에 기존 `R.color.yellow02`를 적용했다. Light는 `purple01`을 유지한다. `MainActivity.kt`의 기존 디버그 Calendar fixture에서 Dark override를 사용할 수 있도록 해 전체 런타임 증거를 확보했다.

## 하위 컴포넌트 검증

| 컴포넌트 | Figma 노드 | 대상 상태 | Preview 이미지 | 결과 |
|---|---|---|---|---|
| `CalendarEmptyDay` | `3039:25079` | Dark, 320×100dp, ko, fontScale 1 | [안내](artifacts/2026-11-01-qa-calendar-color/after-empty-dark/com/arakene/presentation/ui/calendar/CalendarQuoteSectionKt/CalendarEmptyDayDarkQaPreview_0.png) | 두 문구 `#FFCB5C` 확인. Preview의 SVG 캐릭터는 렌더되지 않음. |
| `CalendarQuotePreviewCard` | `3039:25073` | Dark, 320×80dp, ko, fontScale 1 | [카드](artifacts/2026-11-01-qa-calendar-color/after-card-dark/com/arakene/presentation/ui/calendar/CalendarQuoteSectionKt/CalendarQuotePreviewCardDarkQaPreview_0.png) | 테두리 `#FFCB5C`, 표면 `#424242` 확인. Preview fixture는 명언 본문을 생략함. |
| `CalendarEmptyDay` | `2985:22183` | Light, 320×100dp, ko, fontScale 1 | [안내](artifacts/2026-11-01-qa-calendar-color/after-empty-light/com/arakene/presentation/ui/calendar/CalendarQuoteSectionKt/CalendarEmptyDayLightQaPreview_0.png) | 기존 보라색 유지. |
| `CalendarQuotePreviewCard` | `2985:22145` | Light, 320×80dp, ko, fontScale 1 | [카드](artifacts/2026-11-01-qa-calendar-color/after-card-light/com/arakene/presentation/ui/calendar/CalendarQuoteSectionKt/CalendarQuotePreviewCardLightQaPreview_0.png) | 기존 보라색 테두리 유지. |

렌더 명령: `~/.local/bin/android preview render :presentation --fqn=com.arakene.presentation.ui.calendar.CalendarQuoteSectionKt.<Preview 이름> --component-tree --output=docs/design-qa/artifacts/2026-11-01-qa-calendar-color/<상태>`. 각 이미지 옆에 동일 이름의 component tree JSON을 보관했다.

## 1차 조립 검증

- Dark 조립 Preview: [이미지](artifacts/2026-11-01-qa-calendar-color/after-full-dark/com/arakene/presentation/ui/audit/CalendarAuditPreviewsKt/AuditFigma303924906_0.png), [tree](artifacts/2026-11-01-qa-calendar-color/after-full-dark/com/arakene/presentation/ui/audit/CalendarAuditPreviewsKt/AuditFigma303924906_0.json). `AuditFigma303924906`, 360×768dp 콘텐츠, Dark, ko, fontScale 1, 2025-03-17 미필사 fixture.
- Light 조립 Preview: [이미지](artifacts/2026-11-01-qa-calendar-color/after-full-light/com/arakene/presentation/ui/audit/CalendarAuditPreviewsKt/AuditFigma298521952_0.png), [tree](artifacts/2026-11-01-qa-calendar-color/after-full-light/com/arakene/presentation/ui/audit/CalendarAuditPreviewsKt/AuditFigma298521952_0.json). 동일 fixture, Light.
- Dark 전체 런타임: [360×816 전체 프레임](artifacts/2026-11-01-qa-calendar-color/after-runtime-dark-360x816.png). Android Emulator `Medium_Phone_API_35`, API 35, `wm size 360x816`, `wm density 160`, ko, Dark, fontScale 1.0. Debug `CALENDAR_QA_STATE=basic`와 `HOME_QA_DARK_MODE=true`로 고정.
- 비교: [Figma Dark 전체](artifacts/2026-11-01-qa-calendar-color/figma-dark-basic-3039-24906.png)와 런타임 전체를 같은 360×816 경계로 나란히 확인했다. 대상 안내 문구·카드 테두리는 Figma의 노란색과 일치하고, Preview에서 누락된 캐릭터 SVG는 런타임에서 표시된다. 기존 달력 카드 표면색, 광고 영역, 시스템 바·하단 제스처 영역은 Figma와 차이가 남는다. 그 차이는 이 색상 이슈의 변경 전에도 존재하며 이번 범위에서 수정하지 않았다.
- 빌드/회귀: `./gradlew :presentation:testDebugUnitTest --console=plain` 기준 통과; 변경 후 `./gradlew :app:assembleDebug :presentation:testDebugUnitTest --console=plain` 통과. 첫 앱 빌드 시 워크트리에 ignored `app/google-services.json`이 없어 실패했으며 기존 로컬 설정 파일을 복사한 뒤 재실행해 통과했다.
- 최종 결과: 대상 색상 수정과 Light 회귀 확인. 전체 조립 화면은 기존의 다른 Figma 차이로 최종 `Pass` 보류. Notion 상태는 변경하지 않았다.
