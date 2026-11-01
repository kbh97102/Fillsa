# 필사 작성 화면 캐릭터 QA 독립 재검증 계획 v1

## 선택·승인 기록

- Notion: [필사 작성 화면 > 캐릭터 미적용](https://app.notion.com/p/3ffe639fc7a480269f40daf8cf01f20c), `3ffe639f-c7a4-8026-9f40-daf8cf01f20c`.
- Notion 상태/우선순위/기기: In progress / 낮음 / Android, iOS. 최근 수정: 2026-10-29 06:32 UTC.
- 계획 버전/단계: v1 / 기존 Android 구현 재검증 승인. 새 코드 수정은 계획하지 않는다.
- 이번 승인: 사용자의 2026-11-01 발언 “좋아 세가지 모두 진행, stage로부터 각자의 워크트리를 만들어서 진행하자”. 이 v1의 Android 캐릭터 독립 재검증·보고를 `stage`의 `c14b4e6`에서 만든 `codex/qa-typing-character-3ffe639f` 워크트리에서 진행한다.
- 기존 Android 승인 범위: [화면 문서](2_typing.md)에 사용자가 필사 입력 화면의 캐릭터·안내·본문 간격 및 다크 배경 수정을 승인했다고 기록되어 있다. [기존 QA](../design-qa/2026-10-25-typing-guide-qa.md)에 언어 전환 스위치를 Preview 비교에서 제외하라는 사용자 요청과 실제 앱에서는 유지한 범위가 기록되어 있다. 이번 계획은 그 구현을 새로 승인된 작업으로 간주하지 않으며 현재 상태만 독립 검증한다.
- 코드 기준: `c14b4e6`. `TypingQuoteGuide.kt`, `TypingQuoteView.kt`, `typing_handwriting.png`의 마지막 변경은 `3b3e664`(2026-10-25)이다. 시작 시 무관한 미커밋 `app/.DS_Store`, `fastlane/report.xml`이 있었다.

## 원문·AS-IS·기준

- Notion 원문은 Android/iOS 수정 전 캡처와 캐릭터·안내 문구가 있는 목표 캡처를 제공한다. 페이지 본문에는 iOS와 Android의 과거 분석·수정 기록이 혼재한다. [기존 보존 원문/첨부](../design-qa/artifacts/2026-10-25-android-new-qa/notion-typing-original.md)를 참조한다.
- 2026-10-29 SooJi 댓글은 캐릭터가 이제 표시된다고 말하며 iOS 안내 Bold와 iOS 복사 버튼 문제를 추가했다. 두 후속 항목은 Android 캐릭터 적용 범위 밖이며 본 이슈의 Android 완료와 Notion 전체 완료를 구분한다.
- 수정 전 Android 재현은 [2026-10-25 원인 분석과 증거](../design-qa/2026-10-25-android-new-qa-report.html#typing)에 보관되어 있다. 당시 `TypingQuoteView`의 TopSection과 BodySection 사이에 안내 컴포넌트가 없었다.
- Figma: [Light `2929:17920`](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-17920), [Dark `3087:28815`](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3087-28815), 모두 360×720. 안내 그룹 Light `3087:28754`, Dark `3087:28984`; 60×60 캐릭터와 Pretendard Bold 16/24 `#5C65FF` 문구. [현재 Light](../design-qa/artifacts/2026-11-01-qa-typing-character/figma-light-2929-17920.png)와 [Dark](../design-qa/artifacts/2026-11-01-qa-typing-character/figma-dark-3087-28815.png) 전체 기준 이미지는 2026-10-25 추출본과 각각 SHA-256이 동일하다.

## 해결 설계와 현재 구현

- 기존 수정은 정적 `TypingQuoteGuide`가 원본 Handwriting 캐릭터 래스터와 안내 문구를 표시하고, `TypingQuoteContent`가 TopSection 다음에 이를 배치하며 20dp 아래 BodySection을 놓는 방식이다. 새 UI state/action/effect/API/data/domain 계약은 없다.
- 현재 구현에서 `TypingQuoteGuide.kt`의 캐릭터는 `typing_handwriting` drawable, 문구는 `R.string.typing_guide`, 색상은 `R.color.purple01`이며 Bold 16/24 테마 서체를 사용한다. `TypingQuoteView.kt`에 조립되어 있다.
- 기존 해결 대안인 BodySection 내부 직접 삽입 대신 독립 안내 컴포넌트를 유지한다. Light/Dark 자산이 동일함은 기존 QA에서 검증됐다. 이번 재검증은 변경 필요성을 발견하지 않는 한 코드 수정 없이 결과를 보고한다.

## 검증과 완료 조건

1. 현재 소스의 안내 하위 Preview Light/Dark와 조립 Preview Light/Dark를 CLI headless로 렌더해 캐릭터·문구·위치를 Figma와 비교한다. 언어 전환 스위치는 과거 사용자 요청에 따른 Preview 제외 범위를 유지한다.
2. `:app:assembleDebug`와 `TypingSaveOutcomeTest`를 실행해 입력/저장 회귀를 확인한다. 2026-11-01 실행은 빌드 성공, 테스트 6/6 통과다.
3. 동일 UI 코드 변경 `3b3e664` 이후의 [기존 런타임 전체 프레임](../design-qa/artifacts/2026-10-25-typing-guide/runtime-final-light.png)과 Dark·IME 증거를 현재 Figma와 대조한다. 새 런타임 캡처를 확보하지 못한 상태를 별도 표시한다.
4. 이 이슈만의 QA 기록과 HTML 보고서를 작성한다. Android 캐릭터 적용 결과와 iOS 후속 댓글/Notion In progress 상태를 구분한다. Notion 속성은 사용자가 요청하지 않았으므로 수정하지 않는다.

## 위험과 다음 행동

- 현재 에뮬레이터는 기존 검증 환경 `emulator-5554`와 달리 `emulator-5556`(1080×2340/440dpi)이다. 새 동일 조건 360×720 앱 전체 프레임은 이번 재검증에서 확보하지 못했다. 과거 캡처의 코드 동일성 및 현재 Preview는 확인했으나 시스템 바·IME·전환의 현재 버전 독립 재실행은 미검증이다.
- 새 코드 변경이 필요한 차이를 발견하면 이 계획을 갱신해 변경 범위를 별도 검토받는다. 현재는 수정 계획이 없다.
