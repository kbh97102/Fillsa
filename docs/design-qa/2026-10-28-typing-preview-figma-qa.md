# 필사 안내 Android CLI Preview / Figma QA

## Reference
- 요청: “디자인 검증은 android CLI로 preview와 피그마를 비교해보자”. 앱 소스 변경 없음.
- Notion: https://www.notion.so/3ffe639fc7a480269f40daf8cf01f20c
- Figma: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-17920
- 전체 Light `2929:17920` / Dark `3087:28815` (360×720); 안내 `3087:28754` / `3087:28984` (296×84); 안내+본문 `3087:28772` / `3087:28983` (320×164).
- [비교 HTML](2026-10-28-typing-preview-figma.html), [증거](artifacts/2026-10-28-preview-figma/verification.json).
- Studio Quail 1 / 2026.1.1, Fillsa READY. HEAD `1d95ff1`.
- 명령 전체는 `preview-commands.json`. 공통: `~/.local/bin/android studio render-compose-preview <file> <Preview> --project Fillsa --output-image-file=<PNG> --print-semantics`.
- `TypingQuoteGuide{Light,Dark}Preview`: 296×84; `TypingQuoteBody{Light,Dark}Preview`: 320×70/입력전; `TypingQuote{Light,Dark}Preview`: 360×672/일부입력. ko/fontScale1/명시적 uiMode. production composable 재사용.

## Component inventory / Preview 비교
| 범위 | 결과 | 근거 |
|---|---|---|
| 캐릭터·안내 | 컴포넌트 수준 일치 | 60dp 캐릭터, Pretendard Bold16/24, #5C65FF, 안내296×84, Light white/Dark #212121 |
| 본문 | 컴포넌트 수준 일치 | Pretendard20/30, 폭320/두줄60; Light 입력 #212121, Dark #FFCB5C, 미입력 #CACACA |
| 안내와 본문 조립 | 비교 영역 일치 | 간격20dp, 시스템 상단30 제외 시 안내y50/본문y154; 동일 문장·일부입력 상태에서 나란히·50% 중첩 확인 |
| 화면 전체 | 최종 승인 대상 아님 | IME 및 시스템 바 없는 콘텐츠 Preview. 기존 승인으로 LocaleSwitch 제외 |

- 원본 이미지와 semantics 모두 확인. 출력을2.625px/dp로 해석. 조립 Preview 크롭 `(x53,y131,w839,h432)`를320×164로 정규화하여 Figma 안내+본문과 비교. 소수점 반올림·래스터라이저 차이가 있어 픽셀 동일 주장 없음.
- 최신 원본 캐릭터 SVG 해시 `defb407ecd15892ac90cffcdb647fe972b835cc66dd402336a9bc07c9f178012`는 10/25 기준과 동일. production `drawable-nodpi/typing_handwriting.png`의 60dp 호출 위치와 렌더 확인.
- 모든 Preview 최종 이미지 확보. 단독 본문 Light 첫 시도는 Resources.NotFoundException/다른 TopSectionPreview 스택을 포함해 이미지 미생성. 다크 화면 첫 이미지는 저장하기 semantics는 있으나 글자 미노출. 코드 변경 없이 각각1회 재시도 후 정상. 첫 로그/이미지 보존. 도구의 exit0만으로 성공 판정하지 않음. 원인은 확정하지 않음.

## Final assembled-screen result
- 새 전체 런타임 캡처: 없음. 새 최종 Pass 부여 없음.
- Figma720높이는 시스템 영역·IME를 포함하고 Preview672는 콘텐츠만 표시하므로 저장행 절대y/빈공간 높이는 동일 조건 비교 불가.
- 안내·본문 검증은 중간 증거이며 기존10/25 제한 범위 승인을 확대하지 않는다. 실제 시스템 바/IME/내비게이션/리소스 qualifier의 최종 조립 검증은 별도 필요.
