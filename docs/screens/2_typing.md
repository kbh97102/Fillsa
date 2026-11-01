# 필사 입력·저장 결과

## 2026-11-01 캐릭터 적용 Android 독립 재검증

- [Notion 캐릭터 이슈 독립 계획 v1 HTML](2026-11-01-typing-character-3ffe639f-plan.html) · [Markdown](2026-11-01-typing-character-3ffe639f-plan.md) · [QA 기록](../design-qa/2026-11-01-typing-character-3ffe639f-qa.md).
- Figma Light `2929:17920`/안내 `3087:28754`, Dark `3087:28815`/안내 `3087:28984`; 360×720, ko-KR, 입력 화면. 현재 전체 기준 이미지는 QA 기록에 보관했고 2026-10-25 기준과 SHA-256이 동일하다.
- 컴포넌트: `TypingQuoteGuide`(캐릭터·문구), `TypingQuoteBodySection`(입력 본문), `TypingQuoteContent`(조립). 이번 독립 워크트리에서 Light/Dark 하위·조립 Preview와 360×720 API 35 실제 앱의 IME 열림/닫힘 전체 프레임을 새로 확인했다. 캐릭터 적용은 유지되며 키보드/시스템 영역의 Figma 차이는 [이번 QA 기록](../design-qa/2026-11-01-typing-character-3ffe639f-qa.md)에 남겼다.

## Android CLI Preview / Figma 재대조 · 2026-10-28

- [비교 HTML](../design-qa/2026-10-28-typing-preview-figma.html) · [QA 기록](../design-qa/2026-10-28-typing-preview-figma-qa.md).
- 안내·본문 Light/Dark는 크기·간격·문구·색상·줄바꿈 비교 일치. 6개 Preview 최종 확보, 첫 리소스 오류/저장 글자 누락은 재시도 후 정상. 새 전체 런타임 승인은 아님.
- 검증만 수행했으며 앱 소스를 수정하지 않았다. 시스템 영역·IME를 포함한 전체 런타임 검증은 별도다.

## 현재 상태 재검토 · 2026-10-28

- [Android QA 3건 AS-IS / TO-BE 보고서](../design-qa/2026-10-28-android-three-issues-report.html#typing). HEAD `1d95ff1`에 캐릭터·문구·간격 수정이 존재한다. 아래 10/25 후속 검증의 Android 수정 범위 Pass 기록을 기준으로 한다.
- 최신 Figma Light/Dark 전체 프레임은 10/25 보관본과 바이트 동일. 안내 Light/Dark Preview와 저장 단위 테스트 6건을 다시 확인했다. 공통 Notion의 과거 “앱 연결 미실시” 설명은 현재 Android 상태를 반영하지 않는다.
- 이번 작업은 분석과 재검증이며 새로운 전체 런타임 승인이나 iOS 완료 판정은 아니다. 기존 Preview 스위치 제외 승인 및 실서버·한글 조합·긴 문장 회귀 한계는 유지한다.

## 필사 입력 안내 복원 · 2026-10-25

- 사용자 승인: 현재 페이지의 필사 입력 화면에 맞춰 캐릭터·안내·본문 간격과 다크 배경 수정.
- Preview 예외 승인: 이번 수정 대상이 아닌 언어 전환 스위치(`LocaleSwitch`)는 사용자의 요청으로 필사 화면 Preview에서 제외한다. 런타임에는 그대로 표시하며, Preview 비교에서 해당 영역은 평가하지 않는다.
- Figma: [Light 2929:17920](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-17920), [Dark 3087:28815](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3087-28815).
- 조건: 360×720, 한국어, fontScale 1.0, Light/Dark, 입력 전·입력 중·IME 열림/닫힘. 기존 승인된 Android 시스템 바·키보드 및 서비스 문장 차이는 유지.
- 기준 이미지: [Light](../design-qa/artifacts/2026-10-25-typing-guide/figma-light.png), [Dark](../design-qa/artifacts/2026-10-25-typing-guide/figma-dark.png).
- QA 기록: [필사 안내 복원 QA](../design-qa/2026-10-25-typing-guide-qa.md).
- 최종 결과: [필사 화면 수정 결과 HTML](../design-qa/2026-10-25-typing-ui-result.html). 최신 APK의 Light/Dark·IME 열림/닫힘 실행 화면 재확인 완료.
- 구현·클린 빌드·저장 테스트 6개 및 안내/본문 Preview 통과. 에뮬레이터 Light/Dark·IME 캡처 확보. 사용자 승인으로 언어 전환 스위치를 제외한 라이트·다크 조립 Preview 렌더링·대조를 완료했다. 언어 전환 스위치 자체는 이번 Preview 검증 범위 밖이다. 아래 과거 결과 팝업의 완료 기록은 이번 입력 화면의 완료 판단에 사용하지 않는다.

| 컴포넌트 | Figma 범위 | 상태·책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| TypingQuoteGuide | 3087:28754 / 3087:28984 | 60×60 원본 캐릭터, 안내 문구 Pretendard Bold 16/24, #5C65FF, 중앙 정렬 | TypingQuoteContent 상단과 본문 사이 | 하위 Preview 통과 |
| 안내·입력 본문 | 3087:28772 / 3087:28983 | 안내 아래 20dp, 본문 320dp 폭 | TypingQuoteContent | 본문·조립 Preview 통과(스위치 제외 승인) |
| 전체 화면 | 2929:17920 / 3087:28815 | Light #FFFFFF / Dark #212121, IME에 따른 가용 높이 | TypingQuoteView | 런타임 확인, 조립 Preview 통과(스위치 제외 승인) |

## Figma UI 기준

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2988-23906
- 대상 프레임/노드: 라이트 `2988:23906`(오늘도 이어서), `2988:23962`(연속 완료), `2988:24015`(미완료); 다크 `2929:9801`, `2929:9844`, `2929:9884`(같은 순서). 팝업 단독 노드는 라이트 `2988:24099`, `3039:28683`, `3039:28709`; 다크 `3025:24679`, `3025:24705`, `3025:24729`.
- 대상 기기/프레임 크기: Figma 전체 화면 360×720px, 팝업 라이트 320×243px·다크 320×245px, 한국어, 기본 font scale, light/dark. 런타임 에뮬레이터 API 35·360×720px·160dpi.
- 검증 상태: 최종 통과. 팝업 Preview 6종, 입력 본문 Preview 2종, 라이트·다크 전체 프레임 6종과 비회원 Home 복귀를 확인했다. 회원 성공·실패·중복 저장은 목데이터로 검증했다. 사용자는 Figma iOS 시스템 영역·예시 문장과 Android 실제 시스템 영역·서비스 문장의 차이를 승인했다.
- 기준 이미지: [6개 전체 프레임](../design-qa/artifacts/2026-10-11-typing-result/)의 `figma-<노드 ID>.png`.
- QA 기록: [2026-10-11 필사 결과 QA](../design-qa/2026-10-11-typing-result-qa.md).

## 동작 계약

- [저장 후 필사 화면에 머묾](https://app.notion.com/p/3f4e639fc7a4805d8be2e1bcd1f3eb52): 필사를 모두 입력한 뒤 저장이 성공하면 Figma 완료 팝업을 표시하고, `확인`을 누르면 Home으로 돌아간다. 실패한 저장에서 성공 팝업·홈 이동을 실행하지 않는다.
- [필사 후 팝업](https://app.notion.com/p/3f4e639fc7a480449bbdf6879aa1b3a8): 오늘도 이어서/연속 필사 완료/필사 미완료 3개 팝업을 라이트·다크 Figma 프레임에 맞춘다. 미완료 팝업은 Figma의 `취소`·`필사하기` 동작을 유지한다.
- 저장 성공 판정은 회원 `POST` 결과 또는 비회원 로컬 저장 완료에 따른다. 저장 중 중복 탭을 차단한다.

### 컴포넌트 분해

| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| 결과 팝업 내용·자산 | 라이트 `2988:24099`, `3039:28683`, `3039:28709`; 다크 `3025:24679`, `3025:24705`, `3025:24729` | 문구, 정확한 캐릭터 자산, 버튼, 크기·색 | 필사 결과 Dialog | 최종 통과: 6개 Preview·전체 프레임 대조 |
| 필사 화면과 팝업 조립 | 라이트 `2988:23906`, `2988:23962`, `2988:24015`; 다크 `2929:9801`, `2929:9844`, `2929:9884` | 입력 화면 위 dim·팝업 표시, 시스템 영역 | `TypingQuoteView` | 최종 통과: 본문 Preview 2종·전체 프레임 6종 대조, 승인된 예외 적용 |
| 저장 후 이동 | 위 완료 상태 프레임 | 성공 후 확인 시 Home 복귀 | `TypingViewModel` / `MainNavHost` | 최종 통과: 비회원 실동작·회원 목데이터 JVM 테스트 |

기존 Home dark 상태 인벤토리는 [2_home_dark.md](2_home_dark.md)를 참조한다. 전체 비교와 예외 범위는 [QA 기록](../design-qa/2026-10-11-typing-result-qa.md)에 남겼다.


## 2026-10-25 신규 QA: 입력 화면 안내 영역

- QA: https://www.notion.so/3ffe639fc7a480269f40daf8cf01f20c (Android/iOS 공통).
- Figma: Light https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-17920 / Dark https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3087-28815.
- 안내 그룹: Light 3087:28754 / Dark 3087:28984. 캐릭터 3087:28737 / 3087:28985, 문구 3087:28753 / 3087:29001.
- 기준: 360×720, Handwriting 캐릭터 60×60, 문구 ‘오늘의 문장을 따라 써주세요.’ Pretendard Bold 16/24, #5C65FF. 안내 다음 본문까지 20. Figma MCP 원본을 2026-10-25 확인했다.
- 기준 이미지·재현 증거: [근거 폴더](../design-qa/artifacts/2026-10-25-android-new-qa/).
- 분석 보고서: [QA 원문·원인·해결 계획](../design-qa/2026-10-25-android-new-qa-report.html#typing).
- 당시 분석 상태(상단 후속 구현·검증 전): 원인 분석 완료, 앱 수정 미실시. 앞선 저장 결과 팝업의 완료 기록만으로 입력 화면 안내 영역까지 검증했다고 판단하지 않았다. 현재 안내 구현·검증 결과는 문서 상단을 따른다.
- 원인: TypingQuoteView의 TopSection 다음에 바로 BodySection을 배치하여 캐릭터·안내 영역이 없다. 기존 설치 앱에서 누락 재현.
- 계획: 안내만 표시하는 작은 Compose 컴포넌트를 만들고 Figma 원본 자산을 사용한다. 기존 28dp padding과 조립 간격을 함께 조정하며 입력·저장 계약은 보존한다.
- 검증 대상: 안내 컴포넌트 Light/Dark, 안내+본문, 360×720 전체 화면 IME 열림/닫힘, 한/영·한글 조합·긴 문장·저장 회귀. Preview와 전체 프레임 대조 전까지 이번 UI는 미완료다.


### 제공 페이지 추가 조사 · 2026-10-25

- 사용자 제공 `v. 3.0` 페이지 2438:3437의 Light 2438:12875 / Dark 2438:10034에는 안내 캐릭터·문구가 없다. 캡처로 확인했고 QA 요구를 충족하는 `리뉴얼 ver.1.0` 페이지의 기존 기준(2929:17920 / 3087:28815)을 유지한다.
- 안내 그룹(296×84) Light 3087:28754 / Dark 3087:28984의 원본 자산·문구·색상·폰트 추출 완료.
- 원본 SVG: [Light](../design-qa/artifacts/2026-10-25-android-new-qa/typing-guide-light.svg), [Dark](../design-qa/artifacts/2026-10-25-android-new-qa/typing-guide-dark.svg). 모두 width=60 height=60 viewBox=0 0 60 60. SHA-256 동일(`defb407ecd15892ac90cffcdb647fe972b835cc66dd402336a9bc07c9f178012`)하여 단일 자산으로 재사용 가능.
- 문구: 오늘의 문장을 따라 써주세요. / Pretendard Bold16, lineHeight24, #5C65FF. 화면 조립 시 안내 아래 본문 간격20.
- [버전 비교·추출 보고서](../design-qa/2026-10-25-android-new-qa-report.html#figma-extraction). 당시에는 자산 추출·보관까지 수행했다. 이후 production 코드 연결과 시각 검증 결과는 문서 상단의 안내 복원 기록을 따른다.


## 전체 화면 대조 · 2026-10-25

- 기준 페이지: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-3720
- 범위: 현재 production UI 재사용 Preview. 시각 수정은 사용자 검토 후 별도 작업.
- 조건: ko, fontScale 1.0, Light/Dark, 고정 fixture. Preview는 시스템 바·IME를 포함한 최종 승인이 아님.
- QA 기록: [전체 화면 대조 보고서](../design-qa/2026-10-25-full-screen-audit.html)
- 상태·크기·원본 이미지: [screen-map.json](../design-qa/artifacts/2026-10-25-full-screen-audit/screen-map.json)

| 대상 노드 | production 컴포넌트 | 상태 / 책임 | 검증 상태 |
|---|---|---|---|
| 3087:28815 | TypingQuoteContent | 2-3. write-to be · Dark | 조사 중 |
| 2929:9764 | TypingQuoteContent | 2-3. write_저장하기 누른 후 · Dark | 조사 중 |
| 2929:9801 | TypingQuoteContent | 2-3. write · Dark | 조사 중 |
| 2929:9844 | TypingQuoteContent | 2-3. write · Dark | 조사 중 |
| 2929:9884 | TypingQuoteContent | 2-3. write · Dark | 조사 중 |
| 2929:17920 | TypingQuoteContent | 2-3. write-as is · Light | 조사 중 |
| 2929:18278 | TypingQuoteContent | 2-3. write_저장하기 누른 후 · Light | 조사 중 |
| 2988:23906 | TypingQuoteContent | 2-3. write · Light | 조사 중 |
| 2988:23962 | TypingQuoteContent | 2-3. write · Light | 조사 중 |
| 2988:24015 | TypingQuoteContent | 2-3. write · Light | 조사 중 |
| 2929:10996 | TypingResultContent | Modal_type01_2-1 · Dark | 조사 중 |
| 3025:24578 | TypingResultContent | Modal_type01_2-1 · Dark | 조사 중 |
| 3025:24618 | TypingResultContent | Modal_type01_2-1 · Dark | 조사 중 |
| 2988:24099 | TypingResultContent | notice_popup · Light | 조사 중 |
| 2988:24127 | TypingResultContent | notice_popup · Light | 조사 중 |
| 3025:24232 | TypingResultContent | notice_popup · Light | 조사 중 |
