# 오전 9시 알림 미수신 QA 계획 v1

## 1. 선택·승인 기록

- Notion: [오전9시 알람 오지 않음](https://app.notion.com/p/9-40ee639fc7a480f8b78cc68b8b689b62), `40ee639f-c7a4-80f8-b78c-c68b8b689b62`.
- Notion 생성 2026-11-04 05:22 UTC, 최종 수정 2026-11-04 05:23 UTC. Status `Not started`, Priority `높음`, Device `iOS, Android`, Category `기능`, 화면 미기재.
- 계획 v1 / 단계: 분석·설계 완료, **계획 검토 대기**. 사용자의 “안드로이드 전부”는 이슈 선택이며 구현 승인은 아직 없다. 승인 범위·발언: 없음. 다음 행동: 이 계획의 해결 방식과 권한 거절 시 동작을 검토받은 뒤 Android 범위만 수정한다.
- 기준 리비전: `8aee2aafc7b76d705ffb1e3c6b5296aa872a1e03`. 기존 미커밋 변경 `app/.DS_Store`, `fastlane/report.xml`은 보존한다.

## 2. 문제와 원본 근거

- Notion 페이지 본문·하위 블록은 비어 있고 첨부는 없다. SooJi의 댓글 1건(2026-11-04 05:23 UTC): “오늘 오전 9시 알림이 오지 않아서 확인 부탁드립니다”. [원본 이슈](https://app.notion.com/p/9-40ee639fc7a480f8b78cc68b8b689b62)에서 확인했다.
- Android 설정 화면 문구는 “매일 오전 9시에 새로운 문장 알림을 받을 수 있습니다.”이다. 기대 결과는 알림을 켜고 알림 권한이 있는 기기에서 현지 시각 오전 9시경 오늘의 문장 알림을 받는 것이다.
- 제보 기종, Android 버전, 알림·정확한 알람 권한, 설정 스위치 상태, 네트워크 및 API 응답, 실제 알림 시각은 원문에 없다. 한 번의 미수신이 예약·권한·API 중 어디서 발생했는지는 미확정이다.

## 3. 수정 전 AS-IS와 재현 상태

- `DailyNotificationWorkScheduler.newDailyNotificationWork()`는 현재 시각과 무관하게 **다음 날 오전 6시**를 첫 목표로 정하고, 24시간 주기 `PeriodicWorkRequest`를 만든다. 설정 문구의 오전 9시와 코드 목표가 세 시간 다르다는 점은 코드에서 확인된다.
- `ExistingPeriodicWorkPolicy.KEEP` 때문에 이미 등록된 고유 작업은 새 예약 요청으로 교체되지 않는다. `SetDailyNotificationEnabledUseCase`는 선호도 저장 후 예약하고, `RestoreDailyNotificationScheduleUseCase`는 앱 시작과 알림 화면 진입 시 선호도를 읽어 예약을 복원한다.
- `DailyNotificationWorker`는 해당 날짜 문장 API 성공 후 알림을 게시하며 API 실패 또는 예외에서는 `Result.failure()`를 반환한다. 실제 신고에서 API 실패가 있었는지는 확인되지 않았다.
- 2026-11-06 조사에서 `emulator-5556`의 설치 패키지와 권한 설정을 확인했다. 알림 권한은 허용 상태이고 정확한 알람 권한은 기본 상태다. 조사 시 기기 시각은 21:59 KST여서 제보 당시 오전 9시 미수신을 직접 재현하지 못했다. 실행 중 작업과 API 응답의 동일 조건 로그도 확보하지 못했다. 기존 설치 앱의 대량 JobScheduler 출력은 이 이슈의 특정 작업을 식별하지 못해 성공 증거로 사용하지 않는다.

## 4. 원인 분석과 대안

- **확인된 결함:** 첫 예약이 오전 6시다. 또한 WorkManager의 주기 작업은 배터리 최적화 때문에 정확한 시각 실행을 보장하지 않는다. [Android PeriodicWorkRequest 문서](https://developer.android.com/reference/androidx/work/PeriodicWorkRequest).
- **가능한 추가 원인:** 알림 권한 거절, 정확한 알람 권한 거절, 문장 API 실패, 기기 재부팅·시간대 변경, 기존 `KEEP` 작업 유지. 제보 기기의 상태와 로그가 없어 각각은 가설이다.
- 대안 A: WorkManager 목표만 9시로 바꾸고 기존 작업을 갱신한다. 변경량이 작지만 오전 9시 도착을 보장하지 못한다.
- **권장안 B:** 이미 선언된 `SCHEDULE_EXACT_ALARM` 권한을 활용해 다음 현지 오전 9시에 일회성 정확한 알람을 예약하고, 수신 시 기존 문장 작업을 실행하고 다음 날 예약을 만든다. 권한이 없으면 오전 9시를 목표로 하는 WorkManager 작업으로 낮춰 제공한다. 정확한 알람 권한은 Android 14 새 설치에서 기본 거절될 수 있으므로 허용 상태를 매번 검사한다. [Android 정확한 알람 문서](https://developer.android.com/develop/background-work/services/alarms).

## 5. 해결 설계 / TO-BE

1. **상태·책임:** `ALARM_KEY`가 사용자의 켜짐 상태다. domain의 scheduler 인터페이스와 use case는 유지한다. app scheduler가 켜기·끄기·재예약을 담당하고, BroadcastReceiver가 오전 9시 트리거를 받아 다음 날 일정을 예약한 후 기존 `DailyNotificationWorker`를 한 번 실행한다. Worker는 날짜별 문장 조회와 알림 게시만 담당한다.
2. **예약 흐름:** 켜면 기존 `DailyNotificationWorker` 주기 작업을 취소하고 중복 PendingIntent를 정리한다. 현지 시간의 다음 오전 9시를 계산해 정확한 알람 권한이 있으면 `AlarmManager`로 예약한다. 9시가 지났으면 다음 날로 넘어간다. 권한이 없으면 WorkManager의 9시 목표 예약을 사용하고, 기존 작업이 남지 않도록 갱신한다. 끄면 두 경로를 모두 취소한다.
3. **복원 흐름:** 앱 시작, 기기 재부팅, 시간·시간대 변경, 정확한 알람 권한 부여 후 저장된 켜짐 상태에 따라 하나의 경로만 복원한다. 권한 철회 시 정확한 알람이 시스템에서 취소될 수 있으므로 다음 앱 실행에서 낮춘 경로를 복원한다.
4. **실행·실패:** 알림 게시 전 `POST_NOTIFICATIONS` 권한을 확인한다. 문장 API 일시 실패는 제한된 재시도 대상으로 취급하고, 영구 실패는 로그와 검증 결과에 남긴다. 동일 날짜에 중복 알림을 보내지 않도록 수신/작업의 고유성을 검증한다. 정확한 알람은 작업을 시작하는 시각을 제어하지만 네트워크 응답과 OS 처리 이후의 알림 표시를 초 단위로 보장하지는 않는다.
5. **UI·데이터 계약:** Compose 레이아웃·문구와 API 요청/응답 형식은 바꾸지 않는다. `MyPageAction.ClickAlarmUsage` → ViewModel → use case → scheduler 흐름을 유지한다. Figma UI 수정은 이 기능 이슈의 승인 범위에 넣지 않는다.

## 6. 변경 순서와 영향 범위

1. `app/.../DailyNotificationWorkScheduler.kt`: 다음 현지 9시 계산, 정확한 알람/대체 작업 예약, 기존 주기 작업 제거 및 중복 방지. 스위치 호출자, 앱 시작 복원 호출자를 함께 확인한다.
2. `app/.../DailyNotificationAlarmReceiver.kt`(신규)와 `app/src/main/AndroidManifest.xml`: 알람 수신·다음 날 예약, 재부팅/시간대/권한 부여 시 복원 경로와 필요한 receiver/permission 선언.
3. `app/.../DailyNotificationWorker.kt`: 권한과 조회 실패 처리, 하루 중복 게시 방지. 기존 API 계약과 알림 클릭 동작은 유지한다.
4. `presentation/.../SplashView.kt`, `AlertView.kt` 및 ViewModel 호출: 현재 권한 복귀와 설정 변경이 scheduler에 전달되는지 확인하고 누락된 호출만 보완한다. 화면 디자인은 변경하지 않는다.
5. 기존 domain 선호도/use case 테스트와 app 예약·수신 테스트를 보완한다. `docs/screens/5_2_inform.md`, 해당 이슈 QA 기록과 결과 HTML을 실제 증거로 갱신한다. iOS와 다른 Notion 이슈의 고유 수정은 포함하지 않는다.

## 7. 검증·완료 조건

- 수정 전에 6시 계산과 `KEEP` 유지 경로를 고정 시각 테스트로 재현하고, 수정 뒤 08:59/09:00/09:01, 날짜 경계, 시간대 변경, 권한 허용·거절·철회, 스위치 끄기·재시작, 재부팅 복원을 검증한다.
- 동일 날짜에 알림 1건만 게시하고 API 성공·일시 실패·영구 실패가 의도한 결과인지 확인한다. WorkManager 테스트만으로 실제 정확한 알람 전달을 주장하지 않는다.
- 에뮬레이터에서 권한별 예약 상태와 강제 트리거의 알림 표시를 확인한다. 실제 오전 9시 현지 시각에서 앱 종료 상태·Doze를 포함한 최종 도착 여부를 별도로 확인한다. 그 증거가 없으면 기능 검증은 부분 통과 또는 `Blocked`로 기록한다.
- `:app:testDebugUnitTest`, 관련 `:domain:test`와 필요한 계측 테스트를 실행한다. 수정 전후 동일 조건의 예약 시각·권한·알림 결과를 QA 기록과 이슈별 AS-IS/TO-BE HTML에 남긴다.

## 8. 위험·복구와 검토할 결정

- 정확한 알람 권한을 허용하지 않은 기기에서는 오전 9시 정각을 보장할 수 없다. 대체 경로를 유지하면 알림은 늦게 도착할 수 있다. 정확한 알람 권한을 요구하지 않고 대안 A만 적용하면 코드가 작지만 같은 시각 보장 문제가 남는다.
- 기존 주기 작업 마이그레이션 중 중복 예약 위험이 있다. 이전 고유 작업과 새 PendingIntent를 함께 취소·검사하고, 끄기/켜기/업데이트 회귀 검증으로 막는다. 롤백 시 앱 코드와 manifest 변경만 되돌리고 사용자 DataStore 선호도는 유지한다.
- **승인 시 확인할 제품 결정:** 정확한 알람 권한을 거절한 경우 오전 9시 목표의 지연 가능 알림을 계속 받을지(권장), 해당 권한을 줄 때까지 알림을 보내지 않을지 선택이 필요하다. 계획 v1은 전자를 권장한다.
