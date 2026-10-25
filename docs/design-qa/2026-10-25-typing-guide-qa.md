# 필사 입력 안내 복원 QA

## Reference

- Light: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-17920
- Dark: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3087-28815
- 안내 그룹: Light `3087:28754`, Dark `3087:28984`.
- 전체 프레임 기준: [Light](artifacts/2026-10-25-typing-guide/figma-light.png), [Dark](artifacts/2026-10-25-typing-guide/figma-dark.png). 각각 360×720, 시스템 영역·키보드 포함.
- 안내 기준: [296×84 안내 그룹](artifacts/2026-10-25-typing-guide/figma-guide.png).
- Figma MCP의 design context를 같은 근거 폴더의 `figma-light-context.txt`, `figma-dark-context.txt`에 보관했다.
- Preview 조건: 안내 296×84dp, 한국어, fontScale 1.0, Light/Dark. 전체 입력 화면은 ViewModel을 사용하지 않는 TypingQuoteLightPreview / TypingQuoteDarkPreview(360×672dp)를 추가했다.
- 런타임 대상: emulator-5554, API 35, 360×720px / 160dpi. 기존 승인된 Android 시스템 영역과 실제 문장 차이 적용.

## Component inventory

| 컴포넌트 | Figma | 변경 | 검증 상태 |
|---|---|---|---|
| TypingQuoteGuide | 3087:28754 / 3087:28984 | 60×60 캐릭터, 안내 문구 16/24 Bold, #5C65FF, 중앙 정렬 | 부분 통과: 안내 Preview Light/Dark |
| 안내와 입력 본문 | 3087:28772 / 3087:28983 | 기존 상단 28dp 제거, 안내 아래 20dp | 부분 통과: 안내·본문 단독 Preview |
| TypingQuoteContent 배경 | 3087:28815 | gray_600 → gray_700 (#212121) | 실행 화면 색상 확인, 전체 Preview 차단 |

## 원본 자산

- MCP가 제공한 캐릭터 SVG 두 개를 `handwriting-light.svg`, `handwriting-dark.svg`로 보관했다.
- 두 원본을 Sharp로 4배 래스터화한 RGBA 픽셀은 동일했다. 원본의 루트 크기는 60×60이고 수정하지 않았다.
- `presentation/src/main/res/drawable-nodpi/typing_handwriting.png`는 이 원본의 240×240 래스터이며, production `Image`에서 60dp로 표시한다. 화면 캡처를 자산으로 사용하지 않았다.
- 새로운 의존성을 앱에 추가하지 않았다.

## 검증 진행

- 수정 전 CLI Preview에서 캐릭터·안내 누락, 높은 본문 위치, 다크 배경 차이를 확인했다.
- `:presentation:compileDebugKotlin`, `:app:assembleDebug` 성공.
- `:presentation:testDebugUnitTest --tests com.arakene.presentation.viewmodel.TypingSaveOutcomeTest`: 6개 통과.
- 안내 단독 Preview 렌더링은 IDE의 이전 R 목록 때문에 `typing_handwriting`, `typing_guide`의 unresolved reference로 차단됨. Gradle 빌드는 정상이며, IDE Analyze에는 해당 코드 오류가 없다.
- Android Studio의 Assemble Project를 사용자에게 요청했다. Computer Use는 권한 미부여로 IDE 빌드를 실행할 수 없었다.
- 하위 Preview 통과 이전이므로 상위·전체 화면 시각 검증을 통과로 처리하지 않는다.

### IDE 빌드 갱신 후 실행할 명령

```sh
~/.local/bin/android studio render-compose-preview presentation/src/main/java/com/arakene/presentation/ui/home/TypingQuoteGuide.kt TypingQuoteGuideLightPreview --project Fillsa --output-image-file=docs/design-qa/artifacts/2026-10-25-typing-guide/guide-light.png --print-semantics
~/.local/bin/android studio render-compose-preview presentation/src/main/java/com/arakene/presentation/ui/home/TypingQuoteGuide.kt TypingQuoteGuideDarkPreview --project Fillsa --output-image-file=docs/design-qa/artifacts/2026-10-25-typing-guide/guide-dark.png --print-semantics
```

## Final assembled-screen result

- 현재 상태: **수정 범위 검증 통과 — 언어 전환 스위치 Preview 제외 승인 적용**. 아래 Blocked 기록은 제외 승인 전의 조사 이력이다.
- 한계: 언어 전환 스위치 자체의 Preview 검증은 사용자 승인으로 제외. 영어 문장 입력·한글 조합·긴 문장 전체 회귀는 미실시(기존 시각 fixture에는 영문 문장이 없음).
- 기존 전체 화면 비교 보고서와 수정 후보 보고서는 SHA-256 대조로 변경 없음을 확인했다.


## 클린 빌드 후 재검증

- 사용자의 요청으로 `./gradlew clean :app:assembleDebug :presentation:testDebugUnitTest --tests com.arakene.presentation.viewmodel.TypingSaveOutcomeTest` 실행. 최종 clean 빌드 20초, 149개 task 실행, 저장 테스트 6개 통과. [로그](artifacts/2026-10-25-typing-guide/clean-build.log).
- 리소스 unresolved reference 해소. 안내 Light/Dark 렌더링 성공.
- 문구의 기본 line-height trimming 때문에 Figma보다 2px 위에 있었음. `LineHeightStyle.Trim.None` 적용 후 1배 크기 문구의 세로 픽셀 범위가 양쪽 모두 y=65..78로 일치. 수평 마지막 픽셀 1px 차이는 플랫폼 글꼴 래스터화 차이.
- 캐릭터 노드 자체도 MCP로 재추출: `handwriting-node.png` / `handwriting-node.svg`. source SVG를 래스터화한 것과 같은 자산임을 확인. 60dp 표시, 안내 총 높이 84dp 확인.
- 본문도 20sp/30sp 줄높이를 유지하도록 trimming을 해제. 본문 Preview 두 줄 높이 60dp 확인. 상단 영역은 Figma의 50dp로 고정하여 안내의 시작 위치를 맞춤.
- 마지막 간격 수정 뒤 Debug 빌드 및 저장 테스트 6개 재통과. [로그](artifacts/2026-10-25-typing-guide/layout-build.log).
- 안내: [Light](artifacts/2026-10-25-typing-guide/guide-light.png) / [Dark](artifacts/2026-10-25-typing-guide/guide-dark.png).
- 본문: [Light](artifacts/2026-10-25-typing-guide/body-light.png) / [Dark](artifacts/2026-10-25-typing-guide/body-dark.png). 이미지와 같은 이름의 `-semantics.txt`에 트리 기록.
- 전체 화면 Preview는 `LocaleSwitch-KP6Xqw0` 메서드의 `NoSuchMethodError`로 실패. 의존 컴포넌트 단독 렌더는 성공하지만 조립 렌더는 실패하므로 IDE의 클래스/Compose 참조 불일치로 추정. Gradle Sync 요청. 실패 전의 오래된 전체 Preview는 `stale-screen-*-NOT-VALID.png`로 명시하고 현재 구현 증거로 사용하지 않음.

### 에뮬레이터 전체 프레임

- emulator-5554, API 35, 360×720, 160dpi, 기본 글자 배율, 기존 debug Home fixture. 앱 데이터를 지우지 않고 기존 설치와 같은 인증서로 테스트 APK를 설치함. 배포용 설정은 변경하지 않음.
- 최종 조립 캡처: [Light](artifacts/2026-10-25-typing-guide/runtime-light.png), [Dark](artifacts/2026-10-25-typing-guide/runtime-dark.png), [Light IME](artifacts/2026-10-25-typing-guide/runtime-light-ime.png), [Dark IME](artifacts/2026-10-25-typing-guide/runtime-dark-ime.png).
- 양 테마에서 원본 캐릭터·안내가 표시되고 키보드를 열어도 본문과 저장 버튼이 가려지지 않음. Dark 배경 #212121 확인. 언어 토글로 안내가 사라지지 않고 한국어로 복귀 시 입력 내용 유지 확인.
- Figma와 OS 상태바 높이가 6dp 다르므로 콘텐츠 시작 y좌표에는 해당 차이가 남음. Android 시스템 바·키보드와 입력 fixture 상태는 기존 승인된 예외로 별도 표기. 결과 팝업 자체는 이번 수정 범위에 포함하지 않음.
- **런타임 캡처는 확보했으나 전체 화면 Preview 게이트가 남아 있으므로 최종 Pass로 표시하지 않는다.**

### 전체 프레임 비교 파일

- [Light 나란히 비교](artifacts/2026-10-25-typing-guide/compare-light.png), [Dark 나란히 비교](artifacts/2026-10-25-typing-guide/compare-dark.png). 양쪽 원본 360×720 전체 프레임을 유지했다.
- 변경 대상인 안내 자산·문구·중앙 정렬·간격과 다크 배경을 확인했다. 시스템 영역 차이를 제외하면 안내 그룹은 Figma와 같은 크기다.
- 잔여 차이: 본문 줄바꿈은 Figma 첫 줄이 ‘좋’에서 끝나고 런타임은 ‘좋은’에서 끝난다. 기본 폰트 배율 1.0과 160dpi를 확인했으며, 이 차이는 최종 통과로 처리하지 않았다. 전체 Preview가 복구되면 동일 상태에서 글꼴·행폭을 추가 대조해야 한다.


## Sync 이후 조사 및 줄바꿈 수정

- 사용자 Gradle Sync 완료 이후에도 전체 Preview의 LocaleSwitch NoSuchMethodError가 재현됐다.
- `javap`로 Gradle 클래스의 메서드는 `LocaleSwitch-zhKChko`, IDE 임시 TypingQuoteView 클래스의 호출은 `LocaleSwitch-KP6Xqw0`임을 확인했다. 앱 소스의 공통 LocaleSwitch API를 바꾸거나 검증용 UI를 복제하지 않았다.
- 이 화면의 IDE 임시 overlay 디렉터리 4개만 `/tmp/fillsa-typing-preview-overlay-backup`에 백업·이동했다. 이후 오류가 새 Preview 메서드를 찾지 못하는 NoSuchMethodException으로 바뀌어, IDE가 현재 Gradle 출력과 다른 클래스를 메모리에 유지하는 것으로 판단했다. Android Studio 완전 재시작 요청. Gradle/앱 파일은 이 캐시 작업에서 변경하지 않았다.
- 본문 글꼴은 Pretendard Regular, 20sp, 행폭 320dp, fontScale 1.0이 맞다. 동일 OTF의 글자 폭을 비교하면 20px 정수 힌팅에서는 첫 줄 후보 ‘…좋은’이 319px이지만, 소수점 폭을 유지하면 약 324.1px이어서 320px에 들어가지 않는다. 이것이 Figma·고밀도 Preview와 160dpi 런타임의 줄바꿈 차이 원인이다.
- `TypingQuoteBodySection`의 입력·표시 공통 TextStyle에 `TextMotion.Animated`를 지정해 소수점 글자 폭을 보존했다. 애니메이션을 추가한 것이 아니며 입력·저장 로직은 변경하지 않았다. [Compose API](https://developer.android.com/reference/kotlin/androidx/compose/ui/text/style/TextMotion), [AndroidX의 linear/subpixel 설정](https://android.googlesource.com/platform/frameworks/support/+/c258e5a5a308f583cd11d5fd1e18358832184a00%5E%21/).
- 수정 후 160dpi 실행 화면에서도 첫 줄은 ‘좋’, 둘째 줄은 ‘은 상황을 맞는다.’로 Figma와 일치. 안내·본문 하위 Preview Light/Dark 정상. Debug 빌드와 저장 테스트 6개 재통과.
- 전체 화면 Preview는 재시작 후 재검증 전까지 여전히 Blocked다. 이전 절의 줄바꿈 잔여 차이는 이번 수정으로 해소했다.


### 재시작 완료 응답 후 재확인

- CLI는 여전히 Android Studio PID 2279에 연결됨. `ps`에서 해당 프로세스 시작 시각은 2026-09-25 11:06:21로 확인되어, 이 도구가 연결한 IDE 프로세스에는 완전 종료가 반영되지 않은 상태다.
- 현재 전체 Preview 오류는 `TypingQuoteLightPreview not found`이며 새 PNG는 생성되지 않았다. Gradle 출력에 해당 메서드가 있는 것은 앞선 javap 검사로 확인했다.
- 코드·에뮬레이터 검증 결과는 유지하고, 실제 프로세스 재시작이 확인되기 전 최종 Pass로 바꾸지 않는다.


### 추가 완료 응답 후 재검증 (22:22)

- CLI에서 새 `TypingQuoteLightPreview` 함수를 다시 인식했다. 현재 오류는 함수 미발견이 아니라 `LocaleSwitch-KP6Xqw0` 호출의 `NoSuchMethodError`다.
- IDE 로그에는 22:22:51 FastPreviewManager의 별도 컴파일 성공이 기록됐지만, Gradle 클래스의 메서드는 여전히 `LocaleSwitch-zhKChko`로 서로 다르다. Fast Preview 컴파일과 Gradle 클래스 간 불일치는 확인됐으며, 이를 일으킨 정확한 IDE 설정/의존성 원인은 미확정이다.
- CLI 연결은 PID 2279 / Fillsa 프로젝트 한 개다. 사용자가 보고 있는 IDE 화면을 Computer Use로 확인하려 했으나 `Computer Use permissions are not granted`로 차단됐다.
- 같은 재시작을 다시 요청하지 않고, 사용자 IDE의 실제 Preview 결과를 확인하여 CLI 연결 문제와 IDE 자체 렌더 오류를 구분해야 한다.
- 이번 확인에서는 앱 코드 변경 없음. `git diff --check` 통과. 전체 Preview 이미지가 생성되지 않았으므로 최종 상태는 Blocked 유지.


## 언어 전환 스위치 Preview 제외 승인

- 사용자: “그냥 그쪽만 프리뷰빼봐 그거 지금 수정하는건인가?”
- `LocaleSwitch`는 이번 UI 변경 대상이 아니다. 필사 화면의 `LocalInspectionMode.current`가 참일 때만 언어 전환 스위치 호출을 생략한다. 실제 앱에서는 기존 스위치를 표시한다. Preview의 오른쪽 언어 전환 영역은 비교 대상에서 제외한다.
- 조사 결과: Gradle의 클래스/compile jar/runtime jar에서 모두 `LocaleSwitch-zhKChko`가 존재하지만, Fast Preview는 `LocaleSwitch-KP6Xqw0`를 호출한다. Kotlin의 현재/구형 inline class mangling 계산으로 두 이름을 정확히 재현했다. 새 진단 Preview가 기존 LocaleSwitch를 직접 호출해도 동일 오류가 나므로 필사 화면 레이아웃과 독립적으로 재현된다.
- Kotlin 컴파일러에는 참조 메서드 확인 실패 시 구형 mangling으로 재시도하는 경로가 있다. 이번 Fast Preview에서 그 조회가 실패하는 세부 원인은 미확정이며, 사용자 요청으로 조사를 종료했다. 근거: https://github.com/JetBrains/kotlin/blob/v2.3.10/compiler/ir/backend.jvm/src/org/jetbrains/kotlin/backend/jvm/MemoizedInlineClassReplacements.kt .
- 진단용 Kotlin 파일은 main/debug 소스에서 모두 제거한 뒤 Debug APK와 저장 테스트를 다시 빌드했다. 성공: 6초, 144 tasks. 진단 파일은 앱에 남기지 않는다.

### 제외 적용 후 결과

- 상단 Preview 성공: [스위치 제외 상단](artifacts/2026-10-25-typing-guide/top-preview-switch-excluded.png). 고정 50dp 높이 유지.
- 조립 Preview 성공: [Light](artifacts/2026-10-25-typing-guide/screen-light-switch-excluded.png), [Dark](artifacts/2026-10-25-typing-guide/screen-dark-switch-excluded.png). 각 이미지와 같은 이름의 `-semantics.txt`에 트리 보관.
- 360×672dp / ko / fontScale 1, 출력 945×1764px. Preview는 시스템 바·IME가 없는 조건이며 우측 언어 전환 영역이 생략돼 있다. Figma의 상태바 30px를 제외한 콘텐츠 기준으로 안내 y=50dp, 캐릭터 60dp, 문구 24dp, 본문까지 20dp를 확인했다. 첫 줄 ‘좋’, 둘째 줄 ‘은 상황을 맞는다.’ 및 Light/Dark 색상도 대조했다.
- 전체 시스템 영역·IME 및 스위치가 포함된 실제 앱의 시각 증거는 앞서 확보한 `runtime-*.png` / `compare-*.png`를 사용한다. 마지막 변경은 Preview 분기에만 적용되므로 런타임 레이아웃은 그대로다.
- Dark 첫 시도에는 `CompositionErrorContextKt` 로딩 오류가 발생했지만 동일 소스 재시도에서 정상 렌더링됨. 이 재시도는 소스 변경 없이 수행했다.
- Debug 빌드·저장 테스트 6개(실패 0) 통과, 진단용 파일 제거. `git diff --check` 및 원본 비교 보고서 2개 SHA-256 검증 통과.

## 최신 APK 최종 실행 확인 및 결과 보고서

- Preview 제외 분기를 포함한 최신 APK를 emulator-5554에 기존 데이터 유지 방식으로 재설치했다.
- Light/Dark 각각 키보드 열림·닫힘 전체 프레임을 다시 캡처했다: `runtime-final-light.png`, `runtime-final-light-ime.png`, `runtime-final-dark.png`, `runtime-final-dark-ime.png`. 언어 전환 스위치는 실행 화면에 정상 표시된다.
- Figma 전체 프레임 대조: 승인된 시스템 영역 차이를 제외하고 수정한 안내 자산·문구·중앙 정렬·간격·본문 줄바꿈·다크 배경을 확인했다. 저장 버튼이 키보드 위에 표시되는 것도 확인했다.
- 결과: 수정 범위 Pass, 언어 전환 스위치 Preview 제외 승인 적용. 실서버 저장과 영문·한글 조합·긴 문장 전체 회귀는 이번 시각 라운드 범위 밖.
- [최종 HTML 보고서](2026-10-25-typing-ui-result.html): Figma/실행 화면 비교, Preview, 변경 내용, 검증 조건과 한계 포함. 이미지 8개를 내장한 단일 파일. HTML 이미지 검증 및 Chrome 렌더링 확인 완료.
