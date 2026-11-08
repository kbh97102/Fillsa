# Android 런처 앱 이름

## Android 구성

- 표시 위치: Android 런처·앱 정보의 애플리케이션 라벨. Compose 화면은 아니다.
- 코드 위치: `app/src/main/AndroidManifest.xml`의 `android:label="@string/app_name"`, `app/src/main/res/values/strings.xml`.
- 관련 QA: [앱 이름 이슈 계획 v1](2026-11-06-app-name-410e639f-plan.md) · [HTML](2026-11-06-app-name-410e639f-plan.html).

## Figma UI 기준

- Figma URL: https://www.figma.com/design/VdFocqyqTgevMVCQxwAQ2X?node-id=103-1907
- 대상 프레임/노드: `v. 1.0`의 브랜드 표기 규칙 `103:1907`(“필사 = Fillsa 로 표기”), Play Store 앱 아이콘 `446:776`(512×512, 내부 `Fillsa` 로고).
- 대상 상태: Android 런처에 설치된 앱의 표시 이름 `Fillsa`, 라이트 아이콘. 런처 기기 크기·밀도·OS별 레이아웃은 Figma에 정의되지 않았다.
- 기준 이미지: [브랜드 표기 규칙](../design-qa/assets/2026-11-06-qa-app-name-410e639f/figma-name-rule-103-1907.png) · [앱 아이콘](../design-qa/assets/2026-11-06-qa-app-name-410e639f/figma-playstore-icon-446-776.png).
- QA 기록: 계획 승인 및 수정 후 이슈별 QA 파일로 연결할 예정.
- 검증 상태: 계획 검토 대기. Figma에 전체 Android 런처 프레임이 없어 최종 전체 화면 시각 판정은 아직 불가.

### 컴포넌트 분해

| 컴포넌트 | Figma 노드 | 책임 | 조립 위치 | 검증 상태 |
|---|---|---|---|---|
| 앱 이름 라벨 | `103:1907` | 브랜드 영문 표기 `Fillsa` | Android OS 런처 | 미검증 |
| 앱 아이콘 | `446:776` | 기존 로고 자산 | Android OS 런처 | 수정 대상 아님 |
