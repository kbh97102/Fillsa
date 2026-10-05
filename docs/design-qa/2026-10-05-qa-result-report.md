# QA Checklist 진행 결과 보고서

- 작성일: 2026-10-05
- 범위: Notion [QA Checklist](https://app.notion.com/p/QA-Checklist-20fe639fc7a4801896aacef91539661e)의 이전 미완료 5건과 이번 Figma 기반 Android 검증
- 판정 기준: [UI 개편 워크플로우](../ui-redesign-workflow.md). Compose Preview와 동일 조건의 실제 기기·에뮬레이터 전체 프레임이 모두 Figma와 일치해야 UI 항목을 완료한다.
- 코드 상태: 로컬 작업 트리의 미커밋 변경. 운영 배포 확인 결과가 아니다.

## 현재 결론

테마 팝업과 공지사항의 확인된 시각 차이를 수정하고 Android 에뮬레이터에서 부분 대조했다. `:app:assembleDebug`는 통과했다. Android CLI의 Compose Preview는 기존 컴포넌트에서 정상 동작하지만 이번에 추가한 Preview는 Android Studio가 이전 소스를 참조해 찾지 못한다. 전체 프레임에도 Figma와 다른 로그인·공지 데이터·광고·시스템 UI 조건이 남았다. 따라서 아래 미완료 5건의 Notion 상태는 변경하지 않았다.

## Notion 상태와 항목별 결과

2026-10-05 Notion MCP로 QA Checklist 데이터 소스를 다시 조회했다.

| 항목 | Notion 상태 | 이번 확인 결과 | 완료 전 필요한 증거·입력 |
|---|---|---|---|
| [팝업 디자인](https://app.notion.com/p/2b5e639fc7a480e49107c81bd535132d) | In progress | [QA 노드 `2554:2201`](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2554-2201)는 사진을 담은 QA 섹션이다. 실제 v3.0 디자인 프레임과 표시 조건은 확인되지 않았다. | v3.0 팝업의 Figma 프레임/노드, 표시 경로와 운영 빌드 캡처. [별도 계획](../superpowers/plans/2026-10-04-popup-production-verification.md) |
| [테마 선택 팝업 크기조정](https://app.notion.com/p/276e639fc7a480f78ef7f0ce85525ad5) | In progress | Figma 기준 20sp 글자·30dp 간격, 라이트 선택 아이콘과 다크 테두리를 적용했다. 360 × 634/720 에뮬레이터에서 팝업 위치·크기와 선택 저장·재진입을 부분 확인했다. | 새 `@Preview` 렌더링, 로그인 상태·광고·Dialog dim·시스템 UI까지 같은 전체 프레임 비교. [QA 기록](2026-10-04-theme-dialog-qa.md) |
| [위젯 디자인](https://app.notion.com/p/276e639fc7a48077a891fcea28baf4e1) | In progress | 제공된 [Figma 위젯 섹션](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2355-2140)의 2×2/4×2 변형을 정리했다. 런처·구성 화면 운영 검증은 아직 없다. | 선택 미리보기 테두리의 정확한 Figma 프레임, 운영 빌드와 런처 캡처. [QA 기록](2026-10-04-widget-production-qa.md) |
| [로그인 시간 만료 팝업](https://app.notion.com/p/26be639fc7a48046b4afe4cfb5bf0d98) | Not started | 표준 HTTP 401/403에서 세션 삭제와 로그인 이동으로 연결되는 코드 경로 및 빈 refresh token 방어를 확인했다. 반복 현상의 운영 재현·해결은 확인하지 못했다. | 하루 뒤·재설치 직후 사례의 재현 조건과 운영 로그/캡처. [조사 기록](2026-10-04-session-expiration-recheck-qa.md) |
| [공지사항 화면 광고 배경색상 수정](https://app.notion.com/p/25de639fc7a480d48d47ea7d07663793) | In progress | 빈 상태 light/dark 배경·아이콘 위치를 조정했다. 실제 목록·상세·빈 상태 6개 화면에서 배경색 표본을 Figma와 대조했고 다크 상태 표시줄 아이콘을 수정했다. | 새 `@Preview` 렌더링, 실제 공지·광고 상태와 Figma 전체 화면의 차이 판정. [QA 기록](2026-10-04-notice-ad-background-qa.md) |

앞서 `Completed`로 옮긴 `연속필사 안내`와 `테마 추가`는 이번 조회에서도 `Completed`다. 이번 보고서 작성으로 추가 변경한 Notion 상태는 없다.

## Android CLI Compose Preview 실행 결과

Android Studio Quail 1 (2026.1.1), PID 2279에서 `~/.local/bin/android studio check` 결과 `Fillsa`는 `READY`였다. CLI 렌더러 자체를 확인하기 위해 기존 `MyPageItemPreview`를 실행했고 [1080 × 157px 이미지](artifacts/2026-10-05-runtime/preview-cli-control-mypage-item.png)와 `TextView ... text="공지사항"`를 포함한 semantics를 얻었다. 이 대조군은 이번 수정 컴포넌트의 검증 결과가 아니다.

```bash
~/.local/bin/android studio render-compose-preview \
  presentation/src/main/java/com/arakene/presentation/ui/mypage/MyPageItem.kt \
  MyPageItemPreview --project Fillsa --pid 2279 \
  --output-image-file=docs/design-qa/artifacts/2026-10-05-runtime/preview-cli-control-mypage-item.png \
  --print-semantics
```

대상 Preview에는 동일한 기능 명령을 사용했다.

| 대상 | 명령의 파일·함수 인자 | 결과 |
|---|---|---|
| 테마 항목 light | `ThemeDialog.kt ThemeItemLightPreview` | `Error: Failed to find preview element.`; PNG 없음 |
| 테마 팝업 light | `ThemeDialog.kt ThemeDialogLightPreview` | `Error: Failed to find preview element.`; PNG 없음 |
| 공지 빈 상태 광고 light | `SingleLineAdSection.kt NoticeEmptyAdLightPreview` | `Error: Failed to find preview element.`; PNG 없음 |

각 명령에는 `--project Fillsa`, `--output-image-file=docs/design-qa/artifacts/2026-10-05-runtime/preview-<대상>.png`, `--print-semantics`를 전달했다. CLI는 오류 문자열을 출력하면서도 프로세스 종료 코드를 `0`으로 반환하므로, PNG 존재 여부와 출력 메시지를 함께 확인했다. `find-declaration ThemeItemLightPreview`도 `No declaration found`였다. `analyze-file ThemeDialog.kt`는 현재 파일과 다른 이전 소스의 경고를 반환했고, `open-file`은 해당 파일에 `File not found`를 반환했다. 빌드는 통과하므로 Studio의 소스 인덱스가 로컬 파일 변경을 아직 반영하지 않은 것으로 판단한다.

Studio의 저장되지 않은 편집을 확인한 뒤 프로젝트 소스 동기화 또는 재열기, `android studio check` 재확인, 하위 Preview의 이미지·semantics 재추출 순서가 필요하다. 그 전에는 Preview 통과로 판정하지 않는다.

## 런타임 대조 증거와 남은 차이

- 테마 팝업: [light 나란히 비교](artifacts/2026-10-05-runtime/compare-theme-light.png), [dark 나란히 비교](artifacts/2026-10-05-runtime/compare-theme-dark.png). 팝업 자체는 부분 일치하나 Figma의 로그인된 My page와 테스트 앱의 비회원 My page, 광고, Dialog dim 및 OS 아이콘 구성이 다르다.
- 공지 빈 상태: [light 나란히 비교](artifacts/2026-10-05-runtime/compare-notice-light-empty.png), [dark 나란히 비교](artifacts/2026-10-05-runtime/compare-notice-dark-empty.png). 아이콘·문구 위치와 배경색은 부분 일치한다. 실제 공지·광고 내용과 OS 상태 표시줄의 구성은 Figma 예시와 다르다.
- 공지 목록·상세: [light 목록](artifacts/2026-10-05-runtime/notice-light-list-live-360x720.png), [light 상세](artifacts/2026-10-05-runtime/notice-light-detail-live-360x720.png), [dark 목록](artifacts/2026-10-05-runtime/notice-dark-list-live-360x720.png), [dark 상세](artifacts/2026-10-05-runtime/notice-dark-detail-live-360x720.png). 광고 영역과 본문 배경의 표본 색상은 해당 Figma 프레임과 일치한다. 실제 공지 3건의 데이터는 Figma 예시와 다르다.

런타임은 별도 QA 에뮬레이터 API 35, 360 × 634/720px, 160dpi, fontScale 1.0, 디버그 1.0.29(23)에서 캡처했다. 위 비교는 컴포넌트 및 색상에 관한 부분 증거이며 최종 조립 화면의 승인 증거는 아니다.

## 다음 검증 순서

1. Android Studio가 최신 로컬 소스를 읽도록 동기화한 뒤 테마 항목·팝업과 공지 빈 상태·광고 배경의 결정적 Preview 이미지와 semantics를 얻는다.
2. 각 하위 Preview를 해당 Figma 영역과 비교하고 남은 차이를 수정한다.
3. 동일 상태의 전체 런타임 프레임을 다시 캡처해 로그인·실제 광고/공지·시스템 바 차이를 항목별로 판정한다.
4. 운영 확인 대상인 팝업·위젯·로그인 만료는 별도 계획의 실제 운영 증거를 확보한다. 모든 수용 조건을 충족한 항목만 Notion `Completed`로 옮긴다.
