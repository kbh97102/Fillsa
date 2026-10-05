# 공지사항

## Android 구성

- Compose 위치: `presentation/ui/mypage/NoticeView.kt`, `NoticeListSection.kt`, `NoticeDetailView.kt`
- 상태 관리: `MyPageViewModel`
- 범위: 공지 목록과 공지 상세

## Figma UI 기준

- Figma URL: [라이트 목록](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-11354), [라이트 상세](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-11389), [라이트 빈 상태](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-11409), [다크 목록](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-11529), [다크 상세](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-11568), [다크 빈 상태](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-11588)
- 대상 프레임/노드: `2929:11354`, `2929:11389`, `2929:11409`, `2929:11529`, `2929:11568`, `2929:11588`. 광고 영역은 각각 `2929:11383`, `2929:11403`, `2929:11428`, `2929:11562`, `2929:11582`, `2929:11607`.
- 대상 기기/프레임 크기: 모든 프레임 360 × 720 px, 한국어, light/dark × 목록/상세/빈 상태. 밀도·API 레벨은 Figma에 명시되지 않았으며 런타임 검증 조건에 별도 기록한다.
- 검증 상태: 에뮬레이터에서 light/dark 빈 상태 부분 비교 완료. Compose Preview 및 목록·상세의 전체 프레임 검증 대기
- 기준 이미지: [라이트 목록](../design-qa/artifacts/2026-10-04-notice-figma/figma-2929-11354.png), [라이트 상세](../design-qa/artifacts/2026-10-04-notice-figma/figma-2929-11389.png), [라이트 빈 상태](../design-qa/artifacts/2026-10-04-notice-figma/figma-2929-11409.png), [다크 목록](../design-qa/artifacts/2026-10-04-notice-figma/figma-2929-11529.png), [다크 상세](../design-qa/artifacts/2026-10-04-notice-figma/figma-2929-11568.png), [다크 빈 상태](../design-qa/artifacts/2026-10-04-notice-figma/figma-2929-11588.png)
- QA 기록: [공지 광고 배경 QA](../design-qa/2026-10-04-notice-ad-background-qa.md)

목록·상세의 콘텐츠와 광고 영역은 라이트 `#FFF7E6`, 다크 `#424242`이다. 빈 상태의 전체 화면과 광고 영역은 라이트 `#FFEFCC`, 다크 `#212121`이다. 광고가 없는 상태도 같은 바탕색을 유지해야 한다.

2026-10-05 에뮬레이터의 네트워크 오류 경로에서 빈 상태를 재현해 배경과 아이콘·문구 위치를 부분 비교했다. 공지 화면은 광고 미수신 시에도 Figma의 35px 광고 배경 영역을 유지한다. 온라인 목록·상세에서도 light/dark 광고 배경 표본이 Figma와 일치했다. 다크 상태 표시줄 아이콘은 앱 테마를 따르도록 수정했다. 실제 공지 데이터·광고 내용·OS 아이콘 구성이 Figma와 달라 전체 화면 승인은 보류한다. 상세 증거는 QA 기록을 따른다.

### 컴포넌트 분해
| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| `NoticeItem` / `NoticeListSection` | `2929:11361`–`2929:11381`, `2929:11535`–`2929:11559` | 목록 항목 | `NoticeView` | 미검증 |
| `NoticeDetailView` 본문 | `2929:11395`–`2929:11401`, `2929:11569`–`2929:11579` | 상세 내용 | `NoticeDetailView` | 미검증 |
| `EmptyNoticeSection` | `2929:11409`, `2929:11588`의 중앙 빈 상태 | 목록이 없는 상태 | `NoticeView` | 에뮬레이터 부분 비교, Preview 대기 |
| `SingleLineAdSection` 배경 | `2929:11383`, `2929:11403`, `2929:11428`, `2929:11562`, `2929:11582`, `2929:11607` | 상태별 광고 띠 색 | `BottomNavigationBar` | 6개 상태의 배경 표본 일치, Preview·전체 화면 대기 |
