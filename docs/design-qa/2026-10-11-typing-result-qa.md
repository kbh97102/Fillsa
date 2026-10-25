# 필사 저장 결과 Figma UI QA

## Reference

- Figma 파일: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X
- 전체 프레임 360×720: 라이트 `2988:23906`(오늘도 이어서), `2988:23962`(연속 완료), `2988:24015`(미완료); 다크 `2929:9801`, `2929:9844`, `2929:9884`.
- 팝업 노드: 라이트 `2988:24099`, `3039:28683`, `3039:28709`; 다크 `3025:24679`, `3025:24705`, `3025:24729`.
- 전체 프레임 기준 이미지: [artifacts/2026-10-11-typing-result](artifacts/2026-10-11-typing-result/)의 `figma-<노드 ID>.png` 6개. 배경·상태 표시줄·안전 영역·하단 영역을 포함한다.
- 컴포넌트 Preview: 같은 폴더의 `preview-{light,dark}-{continue,streak,incomplete}.png` 6개. `320×243dp`(라이트) 또는 `320×245dp`(다크), `ko`, fontScale 1, 고정 상태.
- Preview 명령: `~/.local/bin/android studio render-compose-preview presentation/src/main/java/com/arakene/presentation/ui/home/TypingResultDialog.kt TypingContinueLightPreview --project Fillsa --output-image-file=docs/design-qa/artifacts/2026-10-11-typing-result/preview-light-continue.png --print-semantics` (나머지 5개도 대응하는 Preview 이름과 출력 파일 사용). `android studio check` 결과 `READY Fillsa`.
- 입력 본문 Preview: 같은 폴더의 `preview-typing-body-{light,dark}.png` 2개. `320×70dp`, `ko`, fontScale 1, 고정된 예시 문장. 팝업 하위 컴포넌트 검증 후 조립 화면을 검증했다.
- 런타임: Android API 35 에뮬레이터 `fillsa_qa_20261005`, `adb shell wm size 360x720`, `wm density 160`, 앱 한국어, 앱 테마 라이트/다크. 전체 360×720 캡처는 같은 폴더의 `runtime-*.png`.
- 승인된 예외: 사용자는 2026-10-11 Figma의 iOS 시스템 영역과 실제 Android 시스템 영역, Figma 예시 문장과 서비스에서 받은 실제 문장의 차이를 승인했다. 회원 저장 검증은 실계정 대신 목데이터 사용을 지정했다.

## Component inventory

| 컴포넌트 | Figma 노드 | 상태 | 결과 |
|---|---|---|---|
| `TypingResultContent` | 위 팝업 노드 6개 | 라이트/다크 × 완료/연속/미완료 | 최종 통과: Preview 6종과 런타임 6종의 문구·자산·크기·색상 대조 |
| `TypingQuoteBodySection` | 위 전체 프레임의 입력 본문 | 라이트/다크 × 입력 완료·미완료 | 최종 통과: Preview 2종 후 런타임 6종 대조. 실제 문장 차이는 승인된 예외 |
| `TypingQuoteView` + `TypingResultDialog` | 위 전체 프레임 6개 | 팝업 오버레이, dim, 시스템 영역 | 최종 통과: 런타임 6종 전체 프레임 대조. OS 시스템 영역 차이는 승인된 예외 |
| `TypingViewModel` 저장·이동 | 완료/미완료 프레임 | 비회원 완료·미완료, 회원 목데이터 성공·실패 | 최종 통과: 에뮬레이터 비회원 동작·회원 fake repository 테스트 |

## Validation rounds

### Round 1

| 범위 | 발견 사항 | 수정 | 결과 |
|---|---|---|---|
| 팝업 Preview 6종 | SVG를 Coil 비동기 로드하면 CLI Preview 캡처에 캐릭터가 없었음 | Figma SVG 원본을 보존하고 동일 자산을 `drawable-nodpi` PNG로 래스터화해 동기 렌더링 | [라이트 비교](artifacts/2026-10-11-typing-result/compare-light-all.png), [다크 비교](artifacts/2026-10-11-typing-result/compare-dark-all.png)에서 6종 내용 확인 |
| 비회원 완료 저장 | 저장 후 팝업 확인에도 필사 화면에 머묾 | 성공 저장 결과 팝업의 `확인`에서 기존 Home으로 pop | [완료 팝업](artifacts/2026-10-11-typing-result/runtime-complete-dark.png) → [Home](artifacts/2026-10-11-typing-result/runtime-home-return.png) 확인 |
| 비회원 빈 입력 재저장 | 기존 초안 삭제 후 조기 반환해 미완료 팝업이 누락됨 | 저장 버튼일 때 초안 삭제 후에도 미완료 팝업 표시 | JVM 회귀 테스트 통과 |
| 회원 저장 실패 | 실패 응답에도 성공 팝업 가능 | 성공 응답에서만 결과 팝업 표시 | JVM 회귀 테스트 통과. 실제 스테이지 계정·실패 응답은 미검증 |

- Preview 비교는 팝업 경계 전체 `320×243/245`를 Figma 팝업 크롭과 나란히 대조했다. 이미지 6종과 semantics의 제목·버튼을 확인했다.
- 최초 런타임 비교는 각 전체 프레임의 `x=20..340`, `y=238..483`(다크) 또는 `y=239..482`(라이트) 팝업 영역을 기준 이미지와 나란히 대조했다: [다크 완료](artifacts/2026-10-11-typing-result/compare-runtime-dark-continue.png), [다크 미완료](artifacts/2026-10-11-typing-result/compare-runtime-dark-incomplete.png), [라이트 연속](artifacts/2026-10-11-typing-result/compare-runtime-light-streak.png), [라이트 미완료](artifacts/2026-10-11-typing-result/compare-runtime-light-incomplete.png). 팝업의 자산·문구·버튼·위치가 기준과 근접했다.
- `필사하기`를 누르면 팝업이 닫히고 필사 입력 화면에 머무는 것을 에뮬레이터에서 확인했다. 완료 상태에서 전체 영문 문장이 일치하는 입력을 저장하고 `확인`을 눌러 Home 복귀를 확인했다.
- 회원 성공·실패는 로그인 상태와 `POST` 결과를 제어하는 `FakeTypingRepository` 목데이터로 검증했다. 성공에서 결과 팝업 `확인`이 Home 이동 효과를 내고, 실패에서 결과 팝업을 내지 않으며, 연속 저장 탭은 한 요청만 전송한다. 이 검증은 실제 스테이지 서버의 가용성이나 인증 상태를 증명하지 않는다.

### Round 2 — 전체 화면 조립

| 범위 | 발견 사항 | 수정 | 결과 |
|---|---|---|---|
| 라이트·다크 입력 본문 | 다크 화면의 부가 그림·문구와 본문 상단 간격이 Figma 전체 프레임과 달랐음 | Figma에 없는 장식을 제거하고 본문 상단 간격을 28dp로 맞춤 | [라이트 Preview](artifacts/2026-10-11-typing-result/preview-typing-body-light.png), [다크 Preview](artifacts/2026-10-11-typing-result/preview-typing-body-dark.png) 및 전체 프레임 대조 |
| 다크 배경과 Dialog dim | 팝업 밖 배경이 기준보다 밝거나 어두웠음 | 다크 입력 화면 배경 및 라이트·다크 dim 양을 조정 | 최종 배경 샘플 `x=10,y=500`: 다크 기준·런타임 RGB `(34,34,34)`, 라이트 기준 `(78,78,78)`·런타임 `(77,77,77)` |
| 조립 6개 상태 | 일부 런타임 상태 캡처가 없었음 | 라이트·다크 × 완료·연속·미완료를 같은 360×720 조건에서 재캡처 | 아래 6개 전체 프레임 비교 완료 |

- 조립 비교: 각 `compare-full-{light,dark}-{continue,streak,incomplete}.png`의 왼쪽은 Figma, 오른쪽은 Android 전체 `x=0..359,y=0..719`이다. 팝업 경계·색상·캐릭터·제목·버튼, dim, 입력 본문, 하단 동작을 모두 확인했다. 예시 문장과 Android 시스템 바·하단 안전 영역의 차이는 위 승인된 예외에 포함한다.
- 검증 명령: `./gradlew :presentation:testDebugUnitTest :app:assembleDebug --console=plain` 성공. `TypingSaveOutcomeTest` 6개 포함. `git diff --check` 통과.

## Final assembled-screen result

- 전체 런타임 캡처: 라이트 [완료](artifacts/2026-10-11-typing-result/runtime-complete-light.png) · [연속](artifacts/2026-10-11-typing-result/runtime-streak-light.png) · [미완료](artifacts/2026-10-11-typing-result/runtime-incomplete-light.png), 다크 [완료](artifacts/2026-10-11-typing-result/runtime-complete-dark.png) · [연속](artifacts/2026-10-11-typing-result/runtime-streak-dark.png) · [미완료](artifacts/2026-10-11-typing-result/runtime-incomplete-dark.png), [Home 복귀](artifacts/2026-10-11-typing-result/runtime-home-return.png).
- 전체 비교: 라이트 [완료](artifacts/2026-10-11-typing-result/compare-full-light-continue.png) · [연속](artifacts/2026-10-11-typing-result/compare-full-light-streak.png) · [미완료](artifacts/2026-10-11-typing-result/compare-full-light-incomplete.png), 다크 [완료](artifacts/2026-10-11-typing-result/compare-full-dark-continue.png) · [연속](artifacts/2026-10-11-typing-result/compare-full-dark-streak.png) · [미완료](artifacts/2026-10-11-typing-result/compare-full-dark-incomplete.png). 비교 범위는 양쪽 모두 360×720 전체다.
- 결과: **Pass (명시 승인된 예외 적용)**. 모든 팝업 상태의 Compose Preview와 전체 Android 프레임, 비회원 완료 후 Home 복귀, 회원 목데이터 성공·실패·중복 저장을 확인했다.
- 남은 차이: Figma의 iOS 상태·제스처 영역 및 예시 문장은 Android 실제 시스템 영역 및 서비스 문장과 다르다. 사용자가 승인했다. 서버와 연결한 회원 스테이지 검증은 목데이터 선택에 따라 이번 판정 범위에 포함하지 않았다.
