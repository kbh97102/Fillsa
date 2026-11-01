# Home 날짜 영역 스크롤 QA

- Notion: https://app.notion.com/p/Home-407e639fc7a48023a54fece5aa7bb156
- 계획: [v1](../screens/2026-11-01-home-week-scroll-407e639f-plan.md)
- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-13556
- 대상 전체 프레임/노드: Light `2929:13556`, 날짜 줄 `3204:2435`. [전체 기준 이미지](artifacts/2026-11-01-qa-home-week-scroll/figma-home-light-2929-13556.png), 360×821, 상태바·루트·하단 영역 포함.
- 수정 전 Preview 명령: `~/.local/bin/android preview render :presentation --fqn=com.arakene.presentation.ui.home.FigmaHomeContentKt.HomeDateWeekSpacingPreview --component-tree --output=/tmp/fillsa-qa-check-home-week`
- 수정 전 결과: [날짜 줄 이미지](artifacts/2026-11-01-qa-home-week-scroll/android-before-week-preview.png), [component tree](artifacts/2026-11-01-qa-home-week-scroll/android-before-week-component-tree.json). 360×50dp, Light, ko, fontScale 1.0, 2026-08-16/완료 10·11일 fixture. 일반 `Row`의 7일 칩이며 스크롤 입력 처리가 없다.
- 승인: 2026-11-01 사용자의 “좋아 세가지 모두 진행, stage로부터 각자의 워크트리를 만들어서 진행하자”. `stage` `c14b4e6` 기반 `codex/qa-home-week-scroll-407e639f`에서 v1 Android 범위를 구현.

## 수정

- `FigmaHomeContent.kt`: 기존 7칸 `Row`를 7칸 viewport의 `LazyRow`로 바꾸고 서비스 시작일~서버 기준 오늘의 날짜를 표시한다. 초기에는 오늘이 오른쪽 끝에 있다. 월 카드는 스크롤 밖에 고정하고 날짜 탭 callback은 유지한다.
- `HomeView.kt`: 스크롤 표시 범위와 선택 날짜를 분리해 전달한다. 회원의 완료 배지는 캐시된 주간 응답만으로 표시하며 비회원은 기존 로컬 완료일을 쓴다.
- `HomeViewModel.kt`, `HomeMemberQuoteWindow.kt`, `Action.kt`: 스크롤 범위의 미조회 주간만 기존 weekly API로 읽기 전용 캐시에 저장한다. 드래그 중 과도한 요청을 막기 위해 180ms 동안 표시 범위가 안정된 뒤 조회한다. 스크롤 자체는 선택 날짜·명언·질문을 바꾸지 않는다. 주간 응답 실패 시 선택과 기존 완료 표시를 보존하고, 늦은 히스토리 응답이 이미 선택 경로에서 채운 최신 캐시를 덮지 않게 한다. 인증 세션 전환 시 진행 중 읽기를 취소한다.

## 컴포넌트 Preview와 조립 검증

| 범위 | Figma | 조건·이미지 | 결과 |
|---|---|---|---|
| 날짜 줄 Light | `3204:2435` | [360×50](artifacts/2026-11-01-qa-home-week-scroll/HomeDateWeekSpacingPreview/com/arakene/presentation/ui/home/FigmaHomeContentKt/HomeDateWeekSpacingPreview_0.png), ko, fontScale 1, 2026-08-16 fixture | 10–16을 7칸으로 표시하고 16이 오른쪽 끝. 월 카드 x20..93, 날짜 줄 x102부터. CLI Preview에서는 완료 배지 SVG가 빠진다. |
| 날짜 줄 Dark | `3039:26518`의 대응 영역 | [360×50](artifacts/2026-11-01-qa-home-week-scroll/HomeDateWeekSpacingDarkPreview/com/arakene/presentation/ui/home/FigmaHomeContentKt/HomeDateWeekSpacingDarkPreview_0.png), Dark, 동일 fixture | 날짜 간격·경계 확인. |
| Home 조립 Light | `2929:13556` | [조립 Preview](artifacts/2026-11-01-qa-home-week-scroll/AuditFigma292913556/com/arakene/presentation/ui/audit/HomeAuditPreviewsKt/AuditFigma292913556_0.png) 및 인접 component tree JSON | 초기 날짜 줄은 Figma와 맞는다. CLI Preview의 일부 SVG·시스템 영역은 런타임으로 별도 확인. |
| Home 조립 Dark | `3039:26518` | [조립 Preview](artifacts/2026-11-01-qa-home-week-scroll/AuditFigma303926518/com/arakene/presentation/ui/audit/HomeAuditPreviewsKt/AuditFigma303926518_0.png) 및 인접 component tree JSON | 초기 7칸 확인. |

명령: `~/.local/bin/android preview render :presentation --fqn=<위 Preview FQN> --component-tree --output=docs/design-qa/artifacts/2026-11-01-qa-home-week-scroll/<Preview 이름>`. 이미지 옆에 JSON component tree를 보관했다.

## 1차 실제 앱 동작

- 환경: Android Emulator `Medium_Phone_API_35`, API 35, 360×821, 160dpi, Light, ko, fontScale 1.0, Debug `HOME_QA_FIXTURE=true`. [Figma 전체 360×821](artifacts/2026-11-01-qa-home-week-scroll/figma-home-light-2929-13556.png)과 동일 프레임 크기.
- [초기 전체 프레임](artifacts/2026-11-01-qa-home-week-scroll/after-runtime-initial-360x821.png): 10–16, 오늘 16이 오른쪽 끝. 완료 배지 10·11이 런타임에서 표시된다.
- [오른쪽으로 드래그](artifacts/2026-11-01-qa-home-week-scroll/after-runtime-scrolled-360x821.png): 4–11까지 과거 날짜 표시. 월 카드 2026.08, 명언, 질문, 선택 16은 유지된다. 카드와 날짜 줄의 제스처 영역이 분리된다.
- [10일 탭](artifacts/2026-11-01-qa-home-week-scroll/after-runtime-selected-360x821.png): 10일의 선택 원형 표시가 바뀌며 viewport는 같은 위치다. QA fixture의 각 날짜 명언은 동일한 문구라서 런타임 캡처만으로 명언 내용 차이는 판단하지 않는다.
- [왼쪽으로 드래그](artifacts/2026-11-01-qa-home-week-scroll/after-runtime-return-360x821.png): 오늘 쪽 10–16으로 복귀, 선택 10은 유지된다.
- 비교: 날짜 줄의 초기 위치·간격·표시 상태는 Figma와 일치한다. 전체 Home 프레임에는 기존 상단 0일 fixture/목표 100일, live 광고/목표 placeholder, 시스템 바·기타 자산/서체 차이가 남는다. 이번 날짜 스크롤 수정으로 발생한 차이는 아니다.

## 기능·회귀

- 기준 `./gradlew :presentation:testDebugUnitTest --console=plain` 통과.
- 변경 후 `./gradlew :app:assembleDebug :presentation:testDebugUnitTest --console=plain` 통과.
- 표시 범위 안정화 조정 후 `./gradlew :app:assembleDebug :presentation:testDebugUnitTest --tests com.arakene.presentation.viewmodel.HomeViewModelIntegrationTest --tests com.arakene.presentation.model.HomeMemberQuoteWindowTest --tests com.arakene.presentation.ui.home.HomeWeekStripStateTest --console=plain` 재통과. Light/Dark 하위·조립 Preview를 최종 소스로 다시 렌더했고, 최종 APK를 재설치해 초기·과거 드래그 전체 캡처를 갱신했다.
- 늦은 히스토리 응답 캐시 보존 테스트 추가 후 `HomeViewModelIntegrationTest` 재통과, `./gradlew :app:assembleDebug --console=plain` 통과. 최종 APK를 재설치하고 초기·과거 드래그 전체 캡처를 다시 갱신했다.
- `HomeMemberQuoteWindowTest`, `HomeWeekStripStateTest`, `HomeViewModelIntegrationTest` 통과. 서비스 시작일·서버 오늘 경계, 캐시 주간 범위, 스크롤 중 선택 명언 불변, 과거 완료일 표시, 날짜 탭 후 캐시 선택, 주간 API 실패 시 기존 선택·완료 표시 보존을 확인했다.
- 실계정으로 추가 weekly API를 실제 서버에 호출한 검증은 미실행. 실제 손가락 드래그는 Debug 회원 fixture에서 실행했다.
- 최종 결과: Android 날짜 줄 스크롤과 선택 분리 구현·기능 검증 완료. 전체 Home 화면의 Figma 최종 `Pass`는 기존 차이와 실계정 서버 검증이 남아 보류. Notion 상태는 변경하지 않았다.
