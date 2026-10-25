# 공유 화면·공유 카드

## Figma UI 기준

- 기준 페이지: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-3720
- 이번 범위: 기존 production UI를 호출하는 Preview와 시각 대조. 제품 UI 수정은 사용자 결정 후 진행.
- 조건: ko, fontScale 1.0, Light/Dark. 전체 화면 Preview는 OS 바 48dp를 제외하며 광고를 호출하지 않는다.
- 상태별 원본·크기·이미지: [screen-map.json](../design-qa/artifacts/2026-10-25-full-screen-audit/screen-map.json)
- QA 기록: [HTML 비교 보고서](../design-qa/2026-10-25-full-screen-audit.html)
- 최종 조립 검증: 미승인. 설치된 에뮬레이터 화면은 보조 증거이며 이번 소스 빌드 검증과 구분한다.

## 컴포넌트 분해

| 노드 | 컴포넌트 | 상태 | 검증 |
|---|---|---|---|
| [2929:10850](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-10850) | ShareContent | 2-4. img_share / Dark | 보고서 참조 · 최종 미승인 |
| [2929:10884](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-10884) | ShareContent | 2-4.first_img_share / Dark | 보고서 참조 · 최종 미승인 |
| [2929:18239](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-18239) | ShareContent | 2-4.first_img_share / Light | 보고서 참조 · 최종 미승인 |
| [2929:18543](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-18543) | ShareContent | 2-4. img_share / Light | 보고서 참조 · 최종 미승인 |
| [2929:10923](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-10923) | ShareItem | 01_share.img / Dark | 보고서 참조 · 최종 미승인 |
| [2929:10927](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-10927) | ShareItem | 02_share.img / Dark | 보고서 참조 · 최종 미승인 |
| [2929:10931](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-10931) | ShareItem | 03_share.img / Dark | 보고서 참조 · 최종 미승인 |
| [2929:10936](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-10936) | ShareItem | 10_share.img / Dark | 보고서 참조 · 최종 미승인 |
| [2929:10941](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-10941) | ShareItem | 08_share.img / Dark | 보고서 참조 · 최종 미승인 |
| [2929:10945](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-10945) | ShareItem | 09_share.img / Dark | 보고서 참조 · 최종 미승인 |
| [2929:10949](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-10949) | ShareItem | 04_share.img / Dark | 보고서 참조 · 최종 미승인 |
| [2929:10954](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-10954) | ShareItem | 06_share.img / Dark | 보고서 참조 · 최종 미승인 |
| [2929:10963](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-10963) | ShareItem | 05_share.img / Dark | 보고서 참조 · 최종 미승인 |
| [2929:10968](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-10968) | ShareItem | 07_share.img / Dark | 보고서 참조 · 최종 미승인 |
| [2929:18577](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-18577) | ShareItem | 01_share.img / Light | 보고서 참조 · 최종 미승인 |
| [2929:18581](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-18581) | ShareItem | 02_share.img / Light | 보고서 참조 · 최종 미승인 |
| [2929:18585](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-18585) | ShareItem | 03_share.img / Light | 보고서 참조 · 최종 미승인 |
| [2929:18589](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-18589) | ShareItem | 10_share.img / Light | 보고서 참조 · 최종 미승인 |
| [2929:18593](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-18593) | ShareItem | 08_share.img / Light | 보고서 참조 · 최종 미승인 |
| [2929:18597](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-18597) | ShareItem | 09_share.img / Light | 보고서 참조 · 최종 미승인 |
| [2929:18601](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-18601) | ShareItem | 04_share.img / Light | 보고서 참조 · 최종 미승인 |
| [2929:18605](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-18605) | ShareItem | 06_share.img / Light | 보고서 참조 · 최종 미승인 |
| [2929:18611](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-18611) | ShareItem | 05_share.img / Light | 보고서 참조 · 최종 미승인 |
| [2929:18615](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-18615) | ShareItem | 07_share.img / Light | 보고서 참조 · 최종 미승인 |
