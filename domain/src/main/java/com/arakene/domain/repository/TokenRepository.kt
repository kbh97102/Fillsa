package com.arakene.domain.repository

import com.arakene.domain.requests.TokenRefreshRequest
import com.arakene.domain.responses.TokenInfo

interface TokenRepository {

    suspend fun updateAccessToken(request: TokenRefreshRequest): TokenInfo?

}