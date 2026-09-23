# Android Session-Expired Logout Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Clear the persisted Android session and replace the current back stack with the login screen when an authenticated API request still returns the app's `TokenExpiredError` after refresh handling.

**Architecture:** Keep token refresh behavior unchanged. Convert only `CommonError.TokenExpiredError` into a login destination with a small pure presentation helper, then let the existing `WithBaseErrorHandling` boundary call the existing logout callback and navigate to login. This keeps the change inside presentation error handling and avoids new state holders, retry loops, or Home/Calendar-specific logic.

**Tech Stack:** Kotlin, Jetpack Compose, Navigation Compose, JUnit 4, Gradle

**Spec:** User-approved session-expiration behavior recorded in this task on 2026-09-19.

## Global Constraints

- Work directly on local `develop`; do not push as part of this plan.
- Treat only `CommonError.TokenExpiredError` as an automatic session logout.
- Do not change refresh `deviceId`, refresh synchronization, or Home/Calendar API behavior.
- Clear the navigation history so Back cannot reopen an authenticated screen.
- Follow RED → GREEN and verify the focused test before the complete presentation suite.

---

### Task 1: Express the session-expiration navigation policy

**Files:**
- Create: `presentation/src/main/java/com/arakene/presentation/util/SessionExpiration.kt`
- Create: `presentation/src/test/java/com/arakene/presentation/util/SessionExpirationTest.kt`

**Interfaces:**
- Consumes: `CommonError` and existing `Screens.Login`.
- Produces: `internal fun sessionExpirationDestination(error: CommonError): Screens.Login?`.

- [x] **Step 1: Write the failing policy tests**

```kotlin
class SessionExpirationTest {
    @Test
    fun tokenExpiredError_routesToNonOnboardingLogin() {
        assertEquals(
            Screens.Login(isOnBoarding = false),
            sessionExpirationDestination(
                CommonError.TokenExpiredError(ErrorResponse.getTokenExpired())
            )
        )
    }

    @Test
    fun nonSessionError_doesNotRouteToLogin() {
        assertNull(sessionExpirationDestination(CommonError.NetworkError))
    }
}
```

- [x] **Step 2: Run the focused test and confirm RED**

Run: `./gradlew :presentation:testDebugUnitTest --tests com.arakene.presentation.util.SessionExpirationTest --console=plain`

Expected: compilation failure because `sessionExpirationDestination` does not exist.

- [x] **Step 3: Add the minimal pure policy**

```kotlin
internal fun sessionExpirationDestination(error: CommonError): Screens.Login? =
    if (error is CommonError.TokenExpiredError) {
        Screens.Login(isOnBoarding = false)
    } else {
        null
    }
```

- [x] **Step 4: Re-run the focused test and confirm GREEN**

Run: `./gradlew :presentation:testDebugUnitTest --tests com.arakene.presentation.util.SessionExpirationTest --console=plain`

Expected: both tests pass.

### Task 2: Apply the policy at the common error boundary

**Files:**
- Modify: `presentation/src/main/java/com/arakene/presentation/ui/common/WithBaseErrorHandling.kt`
- Test: `presentation/src/test/java/com/arakene/presentation/util/SessionExpirationTest.kt`

**Interfaces:**
- Consumes: `sessionExpirationDestination(error)` and the existing `logoutEvent` callback.
- Produces: immediate token clearing plus single-top login navigation with a cleared back stack.

- [x] **Step 1: Use the policy before the existing error switch**

```kotlin
HandleError(viewModel.error, lifecycleOwner) errorHandler@{ error ->
    sessionExpirationDestination(error)?.let { destination ->
        logoutEvent()
        moveScreen?.navigate(destination) {
            popUpTo(0) { inclusive = true }
            launchSingleTop = true
        }
        return@errorHandler
    }

    when (error) {
        // Preserve existing non-session handling unchanged.
    }
}
```

- [x] **Step 2: Run the focused policy tests**

Run: `./gradlew :presentation:testDebugUnitTest --tests com.arakene.presentation.util.SessionExpirationTest --console=plain`

Expected: pass.

- [x] **Step 3: Run the complete presentation unit suite**

Run: `./gradlew :presentation:testDebugUnitTest --console=plain`

Expected: all presentation tests pass with no failures or errors.

- [x] **Step 4: Build the Debug APK**

Run: `./gradlew :app:assembleDebug --console=plain`

Expected: `BUILD SUCCESSFUL`.

- [x] **Step 5: Check the final diff**

Run: `git diff --check`

Expected: no whitespace errors. Confirm that only the plan, policy, policy test, and common error boundary changed.
