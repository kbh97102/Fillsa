# 공지사항 광고 배경 Figma UI QA

## Reference
- Figma URL: [라이트 목록](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-11354), [라이트 상세](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-11389), [라이트 빈 상태](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-11409), [다크 목록](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-11529), [다크 상세](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-11568), [다크 빈 상태](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-11588)
- Target frames/nodes: 위 6개, 각 360 × 720 px. 광고 띠 y=665–700px, 시스템/제스처 영역 포함.
- Full-frame reference: [11354](artifacts/2026-10-04-notice-figma/figma-2929-11354.png), [11389](artifacts/2026-10-04-notice-figma/figma-2929-11389.png), [11409](artifacts/2026-10-04-notice-figma/figma-2929-11409.png), [11529](artifacts/2026-10-04-notice-figma/figma-2929-11529.png), [11568](artifacts/2026-10-04-notice-figma/figma-2929-11568.png), [11588](artifacts/2026-10-04-notice-figma/figma-2929-11588.png).
- Preview command: `~/.local/bin/android studio render-compose-preview presentation/src/main/java/com/arakene/presentation/ui/common/SingleLineAdSection.kt NoticeEmptyAdLightPreview --project Fillsa --output-image-file=docs/design-qa/artifacts/2026-10-04-notice-figma/android-notice-empty-ad-light.png --print-semantics`.
- Preview image: 생성 실패. Android Studio CLI가 새 `@Preview`에 `Failed to find preview element`를 반환한다. 기존 `NoticeViewPreview`는 IDE의 이전 소스를 렌더링해 ViewModel 생성 오류를 냈다.
- Preview conditions: 광고 배경 360 × 35dp, 한국어, fontScale 1, light/dark 빈 상태 fixture. production `SingleLineAdSurface`와 실제 경로 색상 함수를 사용한다.
- Runtime target/capture: 새 `fillsa_qa_20261005` 에뮬레이터, API 35, 360 × 720px, override density 160dpi, fontScale 1.0. 시스템 언어 en-US, 앱 문구 한국어, 비회원, 디버그 1.0.29(23). 네트워크를 꺼서 빈 결과 경로를 만들고 light/dark를 각각 캡처했다. [light](artifacts/2026-10-05-runtime/notice-light-empty-ad-360x720.png), [dark](artifacts/2026-10-05-runtime/notice-dark-empty-ad-360x720.png).

## Component inventory
| Component | Figma node | Target state | Final result |
|---|---|---|---|
| `SingleLineAdSection` 배경 | `2929:11383`, `2929:11403`, `2929:11428`, `2929:11562`, `2929:11582`, `2929:11607` | light/dark × 목록/상세/빈 상태 | 미검증 |
| `NoticeView` / `NoticeDetailView` | 상위 6개 프레임 | 전체 조립 | 미검증 |

## Validation rounds
### Round 1
| Scope | Difference | Fix | Result |
|---|---|---|---|
| 코드와 Figma 색상 | 목록·상세 광고 배경은 라이트 `#FFF7E6`, 다크 `#424242`로 일치. 빈 상태는 Figma가 라이트 `#FFEFCC`, 다크 `#212121`인데 기존 광고 분기는 목록/상세 색을 사용 | `NoticeView`의 빈 상태 배경과 광고 띠를 같은 Figma 색으로 연결 | 빌드 통과, 시각 검증 대기 |
| Preview CLI | IDE가 외부 변경 이전 소스를 계속 사용해 새 Preview를 찾지 못함 | `:app:assembleDebug` 성공 후 재시도 | Blocked: IDE 새로고침 필요 |

### 2026-10-05 런타임 부분 비교
| Scope | Difference | Fix | Result |
|---|---|---|---|
| 빈 상태 본문 | 초기 런타임의 아이콘·문구가 Figma보다 약 60px 아래 | `EmptyNoticeSection`의 아이콘·문구 묶음 위치와 간격 수정 | Figma 아이콘 가시 영역 y=256..329, 최종 Android y=255..327. 다크 문구 y=358..375 대 Android y=357..374 |
| 빈 상태 배경·광고 띠 | 광고 미수신 시 기존에는 35px 띠가 사라져 중앙 정렬도 달라짐 | Notice 경로에서 광고 수신 여부와 무관하게 35dp 배경 영역 유지 | x=4, y=100/600/680 표본 색상은 light·dark 모두 Figma PNG와 동일 |
| 실제 광고 문구 | Figma는 `광고가 들어가는 영역입니다.`, Android 디버그 광고는 `Test Ad : Google Ads`; 다크 캡처는 광고 미수신 | 광고 데이터/문구는 변경하지 않음 | Figma placeholder와 동적 광고 상태의 차이 유지 |
| 실제 목록·상세 배경 | Figma와 Android의 실제 공지 내용은 다름 | light/dark × 목록/상세를 온라인 공지 데이터로 캡처 | x=4, y=600(본문)/680(광고 띠) 표본이 4개 화면 모두 각 Figma 프레임과 동일: light `#FFF8EA`, dark `#535353`(PNG의 색상 프로필 변환 후 값) |
| 다크 상태 표시줄 | 앱 다크 테마여도 OS 라이트 설정에 따라 검은 아이콘이 나옴 | `MainActivity`에서 현재 앱 테마에 맞춰 상태 표시줄 아이콘 밝기 설정 | 다크 공지 목록·빈 상태 재캡처에서 흰 아이콘 확인 |

- Full-frame side-by-side (왼쪽 Figma, 오른쪽 Android): [light 빈 상태](artifacts/2026-10-05-runtime/compare-notice-light-empty.png), [dark 빈 상태](artifacts/2026-10-05-runtime/compare-notice-dark-empty.png).
- 목록·상세 런타임 캡처: [light 목록](artifacts/2026-10-05-runtime/notice-light-list-live-360x720.png), [light 상세](artifacts/2026-10-05-runtime/notice-light-detail-live-360x720.png), [dark 목록](artifacts/2026-10-05-runtime/notice-dark-list-live-360x720.png), [dark 상세](artifacts/2026-10-05-runtime/notice-dark-detail-live-360x720.png). 실제 공지 3건의 제목·본문·날짜가 Figma 예시와 달라 전체 프레임의 동일 상태 비교는 미완료다.

## Final assembled-screen result
- Final runtime capture: 위 light/dark 빈 상태와 목록·상세 에뮬레이터 전체 프레임 캡처 확보. 다크 상태 표시줄 아이콘은 수정했으나 OS 아이콘 종류·시간 및 광고 상태는 Figma와 다르다.
- Comparison: 위 side-by-side. 배경 색상과 빈 상태 아이콘·문구는 부분 일치, 전체 프레임은 불일치.
- Result: Blocked (Compose Preview 미실행 및 동일 조건의 전체 프레임 불일치)
- Remaining differences: Android Studio 새 소스 인덱스로 하위 Preview를 렌더링해야 한다. 목록·상세의 실제 콘텐츠, OS 상태 표시줄 구성 및 Figma placeholder와 실제 광고 내용의 취급을 확인해야 한다. 광고 수신/미수신 시 배경색 표본은 확보했으나 모든 상태의 최종 조립 화면 승인은 대기 중이다.
