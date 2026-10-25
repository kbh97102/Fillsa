# 이미지 등록·보기·삭제

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
| [3223:5985](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3223-5985) | ImageDialog | 1.이미지-등록전 / Dark | 보고서 참조 · 최종 미승인 |
| [3223:6126](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3223-6126) | ImageDialog | 2.이미지-등록후 / Dark | 보고서 참조 · 최종 미승인 |
| [3223:6435](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3223-6435) | ImageDialog | 3.이미지 보기 / Dark | 보고서 참조 · 최종 미승인 |
| [3223:6600](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3223-6600) | ImageDialog | 3.이미지 보기 / Dark | 보고서 참조 · 최종 미승인 |
| [3223:6912](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3223-6912) | ImageDialog | img_check/delete / Dark | 보고서 참조 · 최종 미승인 |
| [2987:23282](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2987-23282) | ImageDialog | 이미지 보기 / Light | 보고서 참조 · 최종 미승인 |
| [3223:4982](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3223-4982) | ImageDialog | 1.이미지-등록전 / Light | 보고서 참조 · 최종 미승인 |
| [3223:4647](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3223-4647) | ImageDialog | 2.이미지-등록후 / Light | 보고서 참조 · 최종 미승인 |
| [3223:4773](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=3223-4773) | ImageDialog | 3.이미지 보기 / Light | 보고서 참조 · 최종 미승인 |
| [2929:19326](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-19326) | ImageDialog | 3.이미지 보기 / Light | 보고서 참조 · 최종 미승인 |
| [2929:19488](https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=2929-19488) | ImageDialog | img_check/delete / Light | 보고서 참조 · 최종 미승인 |

ImageDialogContent는 ImageDialog에서 표시 부분만 분리했다. 사진 선택기 launcher와 업로드·삭제 callback은 실제 Dialog에 유지한다. 팝업 Preview의 dim은 0.6 근사치이며 시스템 창의 최종 크기·그림자를 증명하지 않는다.
