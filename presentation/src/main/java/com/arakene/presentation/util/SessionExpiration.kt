package com.arakene.presentation.util

import com.arakene.domain.util.CommonError

@PublishedApi
internal fun sessionExpirationDestination(error: CommonError): Screens.Login? =
    if (error is CommonError.TokenExpiredError) {
        Screens.Login(isOnBoarding = false)
    } else {
        null
    }
