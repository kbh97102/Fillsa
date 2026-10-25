# Home 연도·월 시스템 선택기 QA

## 범위와 승인

- 사용자 요청: “년도, 월은 일단 기본제공해주는 시스템 다이어로그 사용해보자”. 열린 선택기는 Figma 예외로 승인된 시스템 UI를 사용한다.
- 원본 QA: **Home > 달력 > 연도/월 선택안됨** / **연도/월 클릭시 팝업 닫힘**. 원본 첨부는 `artifacts/2026-10-25-android-new-qa/notion-calendar-1.heic`.
- 원인: 선택 버튼에 클릭 핸들러가 없고, 달력 빈 공간 탭이 뒤의 dismiss 영역으로 전달되었다.
- 구현: 두 버튼 모두 Android `AlertDialog` + `NumberPicker` 2개를 연다. 확인 때만 `onMonthChanged`를 호출한다. 취소/뒤로가기는 표시 월을 보존한다. 달력 자체는 열린 상태를 유지한다.
- 연도 변경 시 유효 월 범위로 조정한다. 하한은 `DateCondition.startMonth`(2025-06), 상한은 기존 `referenceDate`의 월(회원 서버 기준일 / 비회원 오늘)이다. 날짜 선택 및 서버 저장 로직은 이번 수정 대상이 아니다.
- `DisposableEffect`로 다이얼로그를 정리하고, 열림 상태와 미확정 연도·월을 `rememberSaveable`로 보존한다.
- 노션 수정 없음. 구현·검증 기록은 로컬 문서로만 남긴다.

## Reference

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3139-1501
- 전체 Light `3139:1501`, Dark `3139:1753`(360×821), 연도 `2929:16232`, 월 `2929:16236`.
- 전체 기준 이미지: `artifacts/2026-10-25-home-year-month/figma-full-light.png`, `figma-full-dark.png`.
- 기존 달력 부분 기준: `artifacts/2026-10-25-android-new-qa/figma-calendar.png`.
- [Android DatePicker](https://developer.android.com/reference/android/widget/DatePicker)는 연·월·일 선택기이며 공개 API에 연·월 전용 모드가 없다. [NumberPicker](https://developer.android.com/reference/android/widget/NumberPicker)의 공개 API를 사용했다. 숨겨진 내부 day 뷰 ID나 reflection에 의존하지 않는다.

## Component inventory

| 컴포넌트 | Figma / 기준 | 검증 상태 |
|---|---|---|
| `HomeCalendarSelector` | 2929:16232 / 2929:16236 | 기존 외형 유지, 클릭·Button semantics 확인 |
| `HomeYearMonthDialog` | 사용자 승인 시스템 UI 예외 | 시스템 선택기 Light/Dark Preview 확인, 실제 화면 검증 별도 |
| `HomeInlineCalendar` | 기존 달력 프레임 | 내부/외부 탭 회귀 테스트 통과, 기존 시각 차이 있음 |

## Preview

- Android Studio Fillsa `READY` 확인.
- 경로 공통: `presentation/src/main/java/com/arakene/presentation/ui/home/`.
- 명령: `~/.local/bin/android studio render-compose-preview <파일> <Preview> --project Fillsa --output-image-file=<아래 PNG> --print-semantics`.
- 결과 폴더: `artifacts/2026-10-25-home-year-month/`. 각 PNG와 같은 이름의 `-semantics.txt`를 보관한다.

| 파일 / Preview | 크기 | 상태 | PNG |
|---|---|---|---|
| HomeYearMonthDialog.kt / HomeYearMonthPickerLightPreview | 280×180dp | Light / 2026-08, 상한 2026-10 | picker-light.png |
| HomeYearMonthDialog.kt / HomeYearMonthPickerDarkPreview | 280×180dp | Dark / 동일 | picker-dark.png |
| HomeInlineCalendar.kt / HomeInlineCalendarLightPreview | 248×335dp | Light / 2026-08, 선택·기준일 08-16 | calendar-light.png |
| HomeInlineCalendar.kt / HomeInlineCalendarDarkPreview | 248×335dp | Dark / 동일 | calendar-dark.png |

- 모두 ko / fontScale 1 / 명시적 uiMode / 고정 날짜 fixture. Studio 출력 밀도 2.625: 선택기 735×473px, 달력 651×879px.
- 선택기 Preview에서 연도·월만 노출되고 각 선택 값에 년/월 단위가 있다. Dark에서는 시스템 테마 배경 및 흰 글자를 확인했다.
- 전체 native AlertDialog의 제목·버튼·시스템 창은 Preview로 검증할 수 없으므로 런타임으로 별도 확인한다.

## 테스트

- 수정 전 회귀 테스트: 연도 버튼 클릭 후 `연도` NumberPicker가 없어 `NoMatchingViewException` 발생. 실패를 확인한 후 구현했다.
- `./gradlew testDebugUnitTest`: 160 tests, failures 0, errors 0, skipped 0.
- `ANDROID_SERIAL=emulator-5554 ./gradlew :presentation:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.arakene.presentation.ui.home.HomeYearMonthDialogTest`: 4 tests 통과.
  1. 연도·월 각각 진입, 취소 시 표시 월 보존, 확인 시 표시 월만 변경(날짜 선택 callback 0회).
  2. 2026-01 → 2025년 선택 시 6월로 조정, 2025-12 → 2026년 선택 시 10월로 조정.
  3. 시작·마지막 연도가 같은 경우 6~9월 제한, Dark 상태에서 뒤로가기 취소.
  4. 실제 `FigmaHomeContent` 조립 상태에서 달력 내부 빈 공간 탭 유지 / 외부 탭 닫힘.
- 테스트는 native accessibility provider의 실제 스크롤 동작을 사용한다. 초기에 Espresso 기본 root 선택/NumberPicker host 접근을 바로잡았으며 최종 결과에는 수정된 테스트를 사용한다.

## 최종 조립 화면

- Release 빌드 성공, 기존 앱에 `adb install -r` 성공. 설치 APK SHA256은 `artifacts/2026-10-25-home-year-month/verification.json`에 기록했다. 기존 작업 트리 변경을 포함한 빌드이며 이번 수정만 별도 커밋한 빌드는 아니다.
- 대상: emulator-5554 / API 35 / 360×720dp / density 160 / ko-KR / fontScale 1 / 비회원. 실제 서버의 읽기 데이터를 표시하며 별도 목 데이터는 추가하지 않았다.
- 전체 프레임 캡처(상태바·제스처 영역 포함), XML 함께 보관:
  - `runtime-picker-light.png`: 2026년 10월, 시스템 제목·취소·확인, 일 선택 없음.
  - `runtime-picker-august.png`: 시스템 컨트롤로 8월로 변경한 미확정 상태.
  - `runtime-confirm-august.png`: 확인 후 달력은 2026-08을 표시하고 Home의 실제 선택일은 2026-10-25 유지.
  - `runtime-cancel-keeps-august.png`: 월 버튼으로 다시 진입하여 2025년으로 바꾼 뒤 취소, 표시 월 2026-08 유지.
  - `runtime-picker-dark.png`: Dark 앱 전체 프레임 위에 Dark 시스템 선택기.
  - `runtime-dark-inside-keeps-calendar.png`: 뒤로가기로 다이얼로그 취소 후 내부 여백 탭, 달력 유지.
  - `runtime-dark-outside-closes-calendar.png`: 외부 탭 후 달력 닫힘.
  - `runtime-date-selected.png`: 기존 날짜 선택도 확인. 2026-10-20을 누르면 달력이 닫히고 Home 날짜·문장이 변경된다.
- 시스템 야간 모드는 검증 뒤 기존 `no`로 복구했다. AdMob 테스트 광고 검증 안내는 화면에서 닫고 캡처했다.

## 시각 비교와 판정

- 비교: 전체 프레임을 나란히 비교했다. Figma는 360×821의 iOS 시스템 영역, 런타임은 360×720의 Android 시스템 영역이다. 영역을 잘라 전체 프레임 일치로 간주하지 않았다. Preview 컴포넌트는 출력 밀도 2.625를 dp로 환산하여 비교했다.
- 시스템 선택기: 사용자 승인된 Figma 예외. 연·월만 표시 / native 확인·취소 / Light·Dark 표시 확인. 기능 검증 통과.
- 기존 달력: Figma의 선택기 65×32 / 63×32, 간격6 / radius8 / border #E5E5E5와 기존 Android의 Light 가변 폭 / 높이31 / 간격5 / radius7 / border #E7E1D6 차이가 남는다. 달력 바탕·날짜 원형 선택 및 수직 간격에도 기존 차이가 있다. 이번 수정에서는 기존 외형을 바꾸지 않았다.
- 전체 조립 UI 최종 Figma 승인: **미완료(Blocked)**. 사용자 승인 시스템 다이얼로그 예외를 전체 달력의 기존 시각 차이 승인으로 확대하지 않는다. 완전 일치 또는 승인 없이 Notion Completed로 변경하지 않는다.
- 한계: OEM 실제 기기 및 회원 실계정의 서버 기준일을 사용한 수동 검증은 하지 않았다. 회원 상한 전달 경로를 유지했고, 경계 월은 고정 기준일을 주입하는 계측 테스트로 검증했다.
- 결과: 연·월 선택 기능 수정, 테스트·실제 에뮬레이터 동작 확인 완료. 기존 달력 전체의 Figma 외형 정합성은 별도 남은 작업이다.
