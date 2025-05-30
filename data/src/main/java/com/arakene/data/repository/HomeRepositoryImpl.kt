package com.arakene.data.repository

import com.arakene.data.network.FillsaApi
import com.arakene.data.network.FillsaNoTokenApi
import com.arakene.data.util.safeApi
import com.arakene.domain.repository.HomeRepository
import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.util.ApiResult
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val nonTokenApi: FillsaNoTokenApi,
    private val api: FillsaApi
): HomeRepository{

    override suspend fun getDailyQuotaNoToken(quoteDate: String): ApiResult<DailyQuotaNoToken> {
        return safeApi {
            nonTokenApi.getWiseSayingNonMember(quoteDate)
        }
    }
}