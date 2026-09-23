# Android notification-preference final-review fix

## Result

- Added `DailyNotificationScheduler`, with its only Android implementation in
  `DailyNotificationWorkScheduler`. It schedules the existing unique
  `DailyNotificationWorker` using `KEEP` when enabled and cancels that exact
  unique work when disabled.
- Added a scheduler-aware preference update use case and a startup restore use
  case. Both `MyPageViewModel` and `SplashViewModel` now persist and reconcile
  the preference through the former; `FillsaApplication` runs the latter after
  startup to read the persisted value before doing any daily-notification work.
- Left `midnight_work` and `clear_hidden_popup` scheduling unchanged.
- Added domain unit coverage for enable, disable, and a persisted disabled
  startup preference.

## Verification

| Command | Result |
|---|---|
| `./gradlew :domain:testDebugUnitTest --tests 'com.arakene.domain.usecase.common.DailyNotificationPreferenceUseCaseTest' --console=plain` | Passed before the later instruction to skip test tasks. |
| `./gradlew :app:compileDebugKotlin :app:compileReleaseKotlin --console=plain` | Passed. |
| `./gradlew test :app:assembleDebug :app:assembleRelease --console=plain` | Started, then superseded by the instruction to skip test tasks; its two Gradle daemons were stopped before completion. |
| `./gradlew :app:assembleDebug :app:assembleRelease --console=plain` | Passed; Debug and Release assembled successfully. |
| `git diff --cached --check` | Passed before implementation commit. |

The first assemble attempt failed only because this isolated worktree lacked
ignored local signing/configuration files. I copied the already-existing
ignored `app/google-services.json` and `fillsa_keystore.jks` from the main
checkout into this worktree, then reran the same assemble command successfully.
Neither local artifact is tracked.

## Runtime evidence

Emulator/runtime validation and the in-flight full test suite were skipped at
the user's later direction. The two in-flight Gradle daemons were stopped with
`./gradlew --stop` before the final assemble-only verification. No emulator
state was changed.

## Commit

- Implementation: `8934f02 fix(android): respect daily notification preference`
