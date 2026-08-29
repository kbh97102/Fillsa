# Figma UI QA 기록

이 디렉터리는 Android UI 변경의 Figma 검증 증거를 보관한다. 상세 절차는 [Figma 기반 UI 개편 워크플로우](../ui-redesign-workflow.md)를 따른다.

## 파일 이름

`YYYY-MM-DD-<screen>-<topic>-qa.md`

예: `2026-08-29-home-header-qa.md`

## 필수 구성

각 QA 기록은 다음 섹션을 포함한다.

- `Reference`: Figma URL, 대상 프레임/노드, 기준 이미지, Android 런타임 대상
- `Component inventory`: 컴포넌트·Figma 노드·대상 상태·최종 결과
- `Validation rounds`: 라운드별 차이, 수정, 결과
- `Final assembled-screen result`: 최종 런타임 캡처, 통과 또는 5회 후 차단 결과, 남은 차이

기준 이미지와 런타임 캡처는 재검증 가능한 프로젝트 경로 또는 유지되는 외부 산출물 경로를 기록한다.
