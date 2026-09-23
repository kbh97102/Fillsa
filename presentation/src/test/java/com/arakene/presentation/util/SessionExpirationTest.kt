package com.arakene.presentation.util

import com.arakene.domain.responses.ErrorResponse
import com.arakene.domain.util.CommonError
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

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
