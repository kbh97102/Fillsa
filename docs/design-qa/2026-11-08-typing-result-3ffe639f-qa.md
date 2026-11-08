# 11번 필사 결과 팝업 · Android Figma UI QA

## Reference

- Notion: [필사 저장하기 팝업 안뜸](https://app.notion.com/p/3ffe639fc7a48056b876d1ab3e28f520), `3ffe639f-c7a4-8056-b876-d1ab3e28f520`, In progress. 2026-11-04 댓글은 iOS/Android 네 종류 팝업을 요청한다.
- Figma: [Light 전체 `3566:2674`](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3566-2674), 카드 `3566:2737`, 캐릭터 `3566:2743`; [Dark 전체 `3566:2808`](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3566-2808), 카드 `3566:2885`, 캐릭터 `3566:2888`. 360×720, ko-KR. 전체 기준 이미지는 iOS 저장소 `docs/design-qa/assets/2026-11-03-typing-result-3ffe639f/figma-completed-new-light-3566-2674.png`와 `docs/design-qa/assets/2026-11-08-cross-platform-typing-result/figma-completed-dark-3566-2808.png` (루트·시스템 바·제스처 영역 포함).
- 화면 문서: [2_typing.md](../screens/2_typing.md). 승인 계획: [v2](../../../../iosProjects/Fiilsa/docs/screens/2026-11-08-typing-result-3ffe639f-cross-platform-plan.md). 보고서: [교차 플랫폼 HTML](../../../../iosProjects/Fiilsa/docs/design-qa/2026-11-08-typing-result-cross-platform-report.html).
- Android Studio CLI `android studio check`: Fillsa `READY`. Preview 조건: `widthDp=320`, `heightDp=243/245`, Light/Dark, ko, fontScale 1, 결정적 `Completed` fixture. 저장 경로는 Preview가 아니라 단위 테스트와 기기 조작으로 검증.
- Galaxy S25 Ultra SM-S938N, Android 17, 1080×2340 / 450dpi, Light, 2026-11-08 21:23–21:24 KST, USB ADB, 현 소스 Debug APK. 계정 경로 제외.

## Component inventory

| Component | Figma node | Target state | Result |
|---|---|---|---|
| `TypingResultCharacter` | `3566:2743` / `3566:2888` | Greeting Light/Dark | Preview 부분 통과: Figma SVG에서 3x PNG 생성, 양 모드에 동일 원본 이미지 표시 |
| `TypingResultContent` | `3566:2737` / `3566:2885` | 일반 완료 카드 | Preview 부분 통과: 320dp 카드, 캐릭터·Bold 제목·확인 버튼 시각 비교 |
| `TypingResultDialog` + `TypingQuoteView` | 전체 `3566:2674` / `3566:2808` | 저장 후 조립 화면 | Blocked: 일반 완료를 실계정 없이 런타임에서 만들 수 없음. Figma 360×720과 Galaxy 1080×2340의 전체 프레임 동등 비교 불가 |
| `TypingViewModel` | 결과 4종 | 완료 상태 분기 | 단위 11/11 Pass, Galaxy 비회원 미완료·오늘 연속 완료 Pass. 회원 실서버 미실행 |

## Validation rounds

### Component Preview attempt (round 1 incomplete)

- 명령: `android preview render :presentation --fqn=com.arakene.presentation.ui.home.TypingResultDialogKt.TypingCompletedLightPreview --fqn=com.arakene.presentation.ui.home.TypingResultDialogKt.TypingCompletedDarkPreview --component-tree --output=docs/design-qa/artifacts/2026-11-08-typing-result-3ffe639f/completed-preview`.
- 최초 Coil SVG 로딩으로는 headless Preview의 캐릭터가 비었다. Figma 원본 `presentation/src/main/assets/figma/typing-result/completed-greeting.svg`를 `sips`로 360×360 PNG 리소스로 변환하고 `TypingResultCharacter`에서 그리도록 수정했다. 같은 명령 재실행의 [Light PNG](artifacts/2026-11-08-typing-result-3ffe639f/completed-preview/com/arakene/presentation/ui/home/TypingResultDialogKt/TypingCompletedLightPreview_0.png) · [Dark PNG](artifacts/2026-11-08-typing-result-3ffe639f/completed-preview/com/arakene/presentation/ui/home/TypingResultDialogKt/TypingCompletedDarkPreview_0.png) · 같은 폴더의 JSON에 표시됨. 출력 840×638px, 각각 320×243/245dp 카드 Preview다.
- Figma 카드 영역과 나란히 확인한 요소: 인사 캐릭터 형태·색, ‘필사 완료!’ Bold 제목, Light 노란/Dark 보라 버튼, 카드 폭·높이. Preview는 전체 화면 캡처가 아니므로 최종 UI Pass가 아니다.

### Galaxy runtime (round 1 incomplete)

- `./gradlew :app:assembleDebug` 및 `adb install -r app/build/outputs/apk/debug/app-debug.apk` Pass.
- 비회원 오늘 문장, 빈 입력 → [미완료 전체 캡처](artifacts/2026-11-08-typing-result-3ffe639f/galaxy-guest-incomplete-after.png) 1080×2340 Pass. 취소 → [편집 화면 유지](artifacts/2026-11-08-typing-result-3ffe639f/galaxy-guest-after-cancel.png) Pass.
- 비회원 오늘 영어 문장 전체 입력 → [연속 필사 완료 전체 캡처](artifacts/2026-11-08-typing-result-3ffe639f/galaxy-guest-streak-after.png) Pass. 확인 → [홈 복귀, 연속 1일](artifacts/2026-11-08-typing-result-3ffe639f/galaxy-guest-after-streak-confirm.png) Pass.
- Figma 전체 프레임 비교: Galaxy 해상도·상태 바·입력 문장·광고·레이아웃과 Figma 360×720 fixture가 달라 동일 조건 비교 불가. 일반 완료 회원 상태는 사용자 제외 범위. 완전한 1차 검증 라운드로 세지 않는다.

## Functional checks

- `./gradlew :presentation:testDebugUnitTest --tests com.arakene.presentation.viewmodel.TypingSaveOutcomeTest`: [11 tests, 0 failures](../../../../iosProjects/Fiilsa/docs/design-qa/assets/2026-11-08-cross-platform-typing-result/android-test-log.txt). 저장 실패, 오늘 완료, 과거 완료+오늘 작성/미작성, 미완료, 재저장 변경 플래그 false, daily 실패/ID·날짜 불일치, streak 실패, 비회원, 중복 탭을 포함한다.
- API 계약: `POST /api/v2/member-quotes/{seq}/typing` 성공 후 `GET /api/v2/member-quotes/daily?quoteDate=...`의 `dailyQuoteSeq`·`date` 일치와 `completed` 확인. 과거 완료일 때만 저장 후 `GET /api/v1/member-streaks`의 `isTodayWritten`을 새로 확인. POST의 `completedChanged`·`todayCompletedChanged`는 현재 완료 상태로 쓰지 않는다.

## Final assembled-screen result

- **Blocked.** 일반 완료 팝업의 조립 런타임 캡처 및 동일 조건 Figma 전체 프레임 비교가 없다. 회원 저장 실서버도 사용자 요청으로 제외했다. 캐릭터·카드 Preview와 비회원 기기 흐름, 모의 응답 단위 테스트는 부분 통과로 기록한다.
- 남은 차이/한계: 실계정 완료 API 응답·Dark 실기기·일반 완료 조립 전체 프레임은 미검증. Notion 상태 변경 없음.
