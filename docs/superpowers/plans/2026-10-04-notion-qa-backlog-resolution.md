# Notion QA Checklist 계획 인덱스

미완료 5건은 서로 다른 원인과 완료 증거를 가지므로 각각 실행한다. 현재 근거와 공통 수용 기준은 [QA 항목 정리](../specs/2026-10-04-notion-qa-backlog-resolution.md)에 있다.

| 항목 | 계획 | 현재 게이트 |
|---|---|---|
| 로그인 시간 만료 팝업 | [로그인 만료 재확인](2026-10-04-session-expiration-recheck.md) | 원래 재설치/하루 뒤 증상 재현 또는 운영 증거 |
| 테마 선택 팝업 크기조정 | [테마 팝업 UI](2026-10-04-theme-dialog-figma-qa.md) | Figma URL·대상 노드 ID |
| 공지사항 화면 광고 배경색상 수정 | [공지사항 광고 UI](2026-10-04-notice-ad-background-figma-qa.md) | Figma URL·대상 노드 ID |
| 팝업 디자인 | [팝업 운영 검증](2026-10-04-popup-production-verification.md) | 운영 빌드/기기와 대상 팝업 상태 |
| 위젯 디자인 | [위젯 운영 검증](2026-10-04-widget-production-verification.md) | 운영 빌드/런처와 Figma URL·노드 ID |

항목별 증거를 `docs/design-qa/`에 남긴 뒤 수용 기준을 충족한 항목만 Notion `Completed`로 변경한다.
