# Fillsa 전체 화면 Figma 대조 실행 계획

> **For agentic workers:** 이 계획은 작업 단위별로 실행한다. 실행 방법이 별도로 승인되면 superpowers:subagent-driven-development 또는 superpowers:executing-plans 절차를 적용한다. 아래 단계는 checkbox로 관리한다. 1차 비교 보고서를 작성했으며 제품 UI 수정은 사용자 검토 후 별도 작업이다.

**Goal:** 리뉴얼 ver.1.0의 기능별 디자인과 현재 Android 앱의 동일 상태 Preview를 연결한 HTML 보고서를 만들어 사용자가 수정할 화면을 결정할 수 있게 한다.

**Architecture:** Figma 화면 목록과 Android 화면 목록을 각각 수집하고, 화면·상태·테마별 비교 ID로 연결한다. 기존 production UI에 고정 fixture를 주입하는 Preview만 마련하고 렌더링한다. Figma 이미지·Android 이미지·차이·미매칭 이유를 한 보고서에 누적한다.

**Tech Stack:** Kotlin, Jetpack Compose, Figma MCP, Android CLI Compose Preview, JSON, 정적 HTML.

**Spec:** 사용자 요청 및 `AGENTS.md`, `docs/ui-redesign-workflow.md`. 기준은 https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-3720 의 `리뉴얼 ver.1.0` 페이지.

## Global Constraints

- 이번 산출물은 대조·비교 보고서다. 색상·문구·배치·자산·상호작용 수정은 보고서 검토 후 사용자가 선택한 화면에서 진행한다. 중단된 달력 화살표 수정도 차이 목록으로 옮긴다.
- 다른 Figma 페이지, 이전 HTML 승인본, 기존 화면 문서의 노드 ID를 자동으로 정답 처리하지 않는다. 지정 페이지 내 소속과 실제 보이는 상태를 새로 확인한다.
- Android에 없는 디자인, Figma에서 찾지 못한 앱 화면, 플랫폼 전용 화면을 누락 없이 별도 목록에 남긴다.
- Light/Dark, 회원/비회원, 한/영, 빈 값/데이터 있음, 입력 전/중/후, 팝업 표시 등 실제 디자인·앱에 존재하는 상태를 열거한다. 존재하지 않는 조합을 만들지 않는다.
- 현재 작업 트리에는 미커밋 앱 변경이 있다. 기준 commit과 전체 소스 fingerprint를 기록하고 비교 도중 상태가 바뀌면 해당 렌더에 새 기준을 기록한다. 임의 커밋·리셋·배포·노션 수정 없음.
- Preview는 production composable 재사용이 원칙이다. ViewModel·Hilt·네트워크·현재 시각·랜덤·저장된 개인 데이터 의존을 fixture로 분리한다. Preview 전용 가짜 화면을 복제하지 않는다.
- CLI Preview는 중간 비교 자료다. 실제 시스템 바·IME·시스템 다이얼로그·Glance 런처는 에뮬레이터 증거가 필요하며 Preview 성공을 최종 UI 승인으로 표시하지 않는다.

## Review Focus

1. 같은 이름의 다른 화면: 이름만으로 매칭하지 않고 기능·테마·본문·overlay·크기를 확인한다. 매칭 목록에 선택 이유를 기록한다.
2. 720/821/1101 높이: 전체 화면/스크롤 콘텐츠/팝업/시스템 영역을 구분한다. 높이를 강제로 늘이거나 줄여 일치시키지 않는다.
3. Preview와 앱의 분리: 실제 production callsite를 기록한다. 샘플이 런타임 UI와 다른 컴포넌트를 부르면 비교 불가로 판정한다.
4. SVG·이미지 미표시 및 Hilt 오류: placeholder/빈 이미지/렌더 오류를 실제 UI 차이와 구분하고 원인을 기록한다.
5. 디자인 부재·중복·플랫폼 차이: 미매칭 표에 남긴다. 이전 iOS 시스템 바 등 예외는 승인 내역과 적용 범위를 기록하고 전체 화면으로 확대하지 않는다.

## 초기 조사 결과

- Figma 메타데이터에 나타난 노드 7,426개, section 19개. 생략된 인스턴스 내부가 있을 수 있으므로 실제 전체 노드 수로 표현하지 않는다.
- 화면 크기 기준 후보 96개, 화면 밖 별도 노드 후보 57개. 컴포넌트 모음·중복·홍보 이미지를 포함한 후보 수이며 최종 비교 화면 수가 아니다.
- Android MainNavHost route 14개, 소스의 직접 `@Preview` 함수 79개. width/height/locale/fontScale/uiMode를 모두 명시한 것은 23개이며 실제 렌더 성공 여부는 아직 확인하지 않았다.
- 위젯·공유 이미지·시스템 창·팝업은 route 수에 포함되지 않으므로 별도 조사한다.

## 파일 구조

- 보고서: `docs/design-qa/2026-10-25-full-screen-audit.html` — 같은 파일을 단계별 갱신.
- 조사 원본: `docs/design-qa/artifacts/2026-10-25-full-screen-audit/figma-inventory.json`, `android-inventory.json`.
- 비교 기준 목록: 같은 폴더 `screen-map.json` — 기능/상태/테마별 기준과 진행 상태.
- 캡처: 같은 폴더 아래 `figma/<case-id>.png`, `android/<case-id>.png`, `semantics/<case-id>.txt`, `runtime/<case-id>.png`.
- 재생성 도구(실행 단계에서 작성): `tools/ui-audit/build_report.py`, `tools/ui-audit/render_previews.py`.
- 공통 Preview fixture: `presentation/src/debug/java/com/arakene/presentation/ui/audit/AuditFixtures.kt`.
- 기능별 Preview: 같은 경로에 `OnboardingAuditPreviews.kt`, `LoginAuditPreviews.kt`, `HomeAuditPreviews.kt`, `TypingAuditPreviews.kt`, `ImageAuditPreviews.kt`, `ShareAuditPreviews.kt`, `CalendarAuditPreviews.kt`, `RecordAuditPreviews.kt`, `MyPageAuditPreviews.kt`, `NoticeAuditPreviews.kt`, `PreferencesAuditPreviews.kt`, `CommonAuditPreviews.kt`.
- 위젯 설정 Preview: `app/src/debug/java/com/arakene/fillsa/widget/ui/WidgetAuditPreviews.kt`. 실제 Glance 화면은 런처 캡처로 보완한다.
- 기존 private content를 사용해야 하면 원본 파일에서 production content를 최소 추출하거나 같은 파일에 결정적 Preview를 추가한다. 실행 시 필요한 파일만 수정한다.

## 비교 목록 계약

각 항목은 `id`, `feature`, `screen`, `state`, `theme`, `platform`, `figma{pageId,nodeId,url,width,height,image}`, `android{route,sourceFile,productionComposable,previewFile,previewFunction,image,semantics}`, `fixture{locale,fontScale,date,text,auth}`, `mappingStatus`, `captureStatus`, `comparisonStatus`, `differences[]`, `decision`, `notes`를 가진다.

- 매칭: candidate / matched / figma_only / android_only / platform_only / component_reference.
- 캡처: pending / figma_ready / preview_ready / blocked / runtime_required.
- 비교: not_reviewed / no_observed_difference / difference_found / not_comparable. `no_observed_difference`도 Preview 기준이며 최종 승인과 다르다.
- 사용자 결정: pending / fix / keep / later. 자동으로 `fix`를 지정하지 않는다.
- HTML은 기능·테마·상태 필터, 양쪽 이미지, 원본 링크·크기·조건, 차이 목록, 사용자 메모와 결정 JSON 내보내기를 제공한다. 결정 정보는 노션 등에 자동 전송하지 않는다.

## Task 1: 기능별 추출 대상과 매칭 목록 확정

- [x] 지정 Figma 페이지 구조 수집 및 Android route/Preview 목록 조사.
- [x] 13개 기능군의 화면 후보를 읽고 상태 이름을 부여한다. 중복·플랫폼 전용·컴포넌트·홍보 자산을 표시한다.
- [x] Figma 계층·추출 텍스트와 원본 PNG로 동일 이름 후보의 실제 내용을 확인한다. 특히 `write-as is/to be`, 동일 이름 Calendar, My Page 로그인 상태를 확인한다.
- [x] `screen-map.json`에 모든 route와 popup/공유/위젯 항목을 연결한다. 미매칭 항목도 이유와 함께 포함한다.
- [x] 검증: Figma node가 지정 페이지 안에 있는지, 한 case-id에 한 상태만 있는지, 모든 route가 최소 한 항목 또는 미매칭 사유를 가지는지 확인한다.
- 산출: 기능별 추출 목록과 확정된 비교 수. 이 단계 전에는 96개를 최종 화면 수로 고정하지 않는다.

## Task 2: Figma 기준 이미지 추출

- [x] 확정된 case 순서로 전체 frame과 필요한 popup/component를 PNG로 저장한다. 상하단을 임의 자르지 않는다.
- [x] Figma의 텍스트·날짜·선택 상태·이미지를 fixture 명세에 기록한다. 필요 자산은 원본을 로컬 저장한다.
- [x] 각 이미지의 크기·node ID·해시·추출 시각을 기록하고 HTML의 Figma 칸을 채운다.
- [x] 검증: 잘못된 frame, 빈 PNG, 누락된 overlay, 같은 그림의 잘못된 재사용을 확인한다.
- 순서: 온보딩·로그인 → Home·필사 → Calendar·기록 → My Page·공지·설정 → 공통 상태·공유·이미지·위젯.

## Task 3: 앱의 동일 상태 Preview 마련

- [x] 기존 Preview마다 호출 경로와 렌더 의존을 확인한다. 79개를 모두 사용 가능으로 가정하지 않는다.
- [x] 가능한 화면은 production stateless content를 직접 호출한다. Home은 `FigmaHomeContent`, 다른 화면은 실제 content를 재사용한다.
- [x] Hilt에 묶인 화면만 화면 표시 부분을 분리해 state/callback을 받도록 한다. 이벤트·repository·navigation 동작과 시각 결과를 바꾸지 않는다.
- [x] widthDp/heightDp/uiMode/locale/fontScale과 고정 fixture를 명시한다. Figma와 동일하지 않은 날짜·완료 상태·사진, 토스트·IME 제약은 각 항목에 기록한다.
- [x] 공통 root 배경·하단 메뉴는 production 컴포넌트를 호출한다. 광고/OS 바는 Preview에서 제외하고, 전체 조립 승인이 아님을 표시한다.
- [x] 검증: 컴파일 성공, fixture의 네트워크 호출 없음, 실제 UI 재사용. 이 작업에서 발견한 UI 결함은 수정하지 않고 보고서에 남긴다.

## Task 4: Android CLI 렌더링과 비교

- [x] `~/.local/bin/android studio check`에서 Fillsa READY 확인.
- [x] 하위 컴포넌트 → 전체 화면 순으로 `render-compose-preview <file> <function> --project Fillsa --output-image-file=<path> --print-semantics` 실행.
- [x] 성공 이미지/semantics와 실패 로그를 각각 case에 연결한다. 실패는 빈칸 대신 원인과 다음 조치를 표시한다.
- [x] Figma와 Preview의 dp/px 배율을 기록하고 동일 콘텐츠 영역으로 나란히 비교한다. 원본 전체 frame은 유지한다.
- [x] 차이는 배치·크기·간격·색상·글꼴·문구·아이콘·상태 누락으로 기록하고 근거를 표시한다. 시스템·콘텐츠 길이 차이는 별도 기록한다.
- [x] 렌더 불가 항목(native dialog/IME/Glance 등)은 가능한 실제 화면 캡처를 추가하고 Preview와 출처를 명확히 구분한다.

## Task 5: 보고서 완성 및 사용자 결정

- [x] 같은 HTML에 추출/렌더/비교 진행률과 기능별 결과를 갱신한다.
- [x] 모든 항목은 비교 이미지 2개 또는 미매칭/렌더 실패/런타임 필요 이유를 가진다. 앱 화면이 목록 밖으로 누락되지 않았는지 재확인한다.
- [x] 이미지 링크·필터·사용자 결정 저장/내보내기와 데스크톱·작은 화면 렌더링을 확인한다.
- [ ] 최종 보고서에서 사용자가 `수정 / 유지 / 나중에`를 결정한다. 선택 결과로 별도 수정 계획을 만들며, 이번 대조 작업 중에는 디자인 수정을 실행하지 않는다.

## 완료 기준

사용자는 화면마다 Figma 원본, 현재 앱 이미지, 비교 조건, 차이, 보류 이유를 보고 수정 우선순위를 결정할 수 있다. 누락 없이 설명된 보고서가 이번 작업의 완료 기준이며, 앱 UI가 모두 Figma와 일치하는 것은 이번 단계의 완료 조건이 아니다.

## 1차 대조 결과 · 2026-10-25

- 171개 ledger 항목: 홍보 21개 제외, 보고서 150개 카드. 하위 참조 9개를 제외한 화면·상태 후보 141개.
- Figma PNG 131개, Android CLI Preview 129개 렌더 성공. 14개 앱 route 모두 목록화.
- 95개 항목에서 시각/조건 차이 관찰, 6개 팝업 하위 컴포넌트는 큰 배치 차이를 발견하지 못함. 49개는 디자인 부재·플랫폼/fixture/렌더 조건 차이로 직접 일치 판정 보류.
- Compile: app Debug Kotlin, presentation Release Kotlin 성공. presentation 단위 테스트 135개 성공, 실패/오류/skip 0.
- HTML 필터·검색·브라우저 결정 저장·JSON 내보내기 검사 통과. 상대 링크 957개 검사, 누락 0.
- 실제 제품 UI 변경은 적용하지 않았다. ViewModel 의존 화면과 Dialog의 표시 content를 기존 코드에서 분리하고 debug Preview가 재사용하도록 했다.

### 재현

```bash
JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home' ./gradlew :app:compileDebugKotlin :presentation:compileReleaseKotlin :presentation:testDebugUnitTest --console=plain
python3 tools/ui-audit/render_previews.py --retry --id <screen-map의-case-id>
python3 tools/ui-audit/reconcile_report.py
python3 tools/ui-audit/build_report.py
```

Preview 파일/함수의 현재 이름은 `preview-manifest.json`을 기준으로 한다. 재렌더가 진행 중일 때 `screen-map.json`을 수정하지 않는다. `review-findings.json`에 리뷰를 저장한 뒤 렌더 종료 후 reconcile한다.

### 남은 확인과 사용자 판단

- SVG와 Popup/토스트/IME는 동일 상태의 새 빌드 런타임 캡처 필요. 설치 버전 1.0.31(25)의 Home·연도월·키보드·공유창은 보조 증거다.
- 사진 선택기는 비회원 로그인 안내로 진입 불가. 실제 Glance 런처 위젯과 OS 권한창 캡처는 미확보.
- 이전/리뉴얼 변형의 최종 기준, 별밤·수평선 공유 배경 추가 여부, 기록/위젯의 새 기준 Figma는 사용자 결정 필요.
- 모든 항목은 최종 UI 미승인이다. 이 보고서는 수정 우선순위 결정용이며 기능 회귀·배포 QA 완료가 아니다.
