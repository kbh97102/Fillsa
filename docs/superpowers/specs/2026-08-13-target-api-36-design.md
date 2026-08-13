# Android 16 API 36 target SDK update

## Goal

Meet Google Play's target API requirement by targeting Android 16 (API 36).

## Scope

- Set `compileSdk = 36` in `app`, `data`, `domain`, `presentation`, and `compose-util`.
- Set `targetSdk = 36` in `app`, `data`, and `presentation`.
- Leave application versioning, dependencies, and source code unchanged.

## Compatibility and verification

- Keep the existing Gradle and Android Gradle Plugin versions unless the API 36 build proves them incompatible.
- Run the project build after the configuration change. A successful build confirms that all modules resolve against API 36.

## Out of scope

- Behavioral changes required by Android 16.
- Dependency upgrades unrelated to compiling and targeting API 36.
