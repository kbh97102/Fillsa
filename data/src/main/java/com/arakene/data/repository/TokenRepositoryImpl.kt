package com.arakene.data.repository

import com.arakene.data.network.TokenApi
import com.arakene.domain.repository.TokenRepository
import com.arakene.domain.requests.TokenRefreshRequest
import com.arakene.domain.responses.TokenInfo
import javax.inject.Inject

class TokenRepositoryImpl @Inject constructor(
    private val tokenApi: TokenApi
) : TokenRepository {

    override suspend fun updateAccessToken(request: TokenRefreshRequest): TokenInfo? {
        return tokenApi.updateToken(request).body()
    }
}