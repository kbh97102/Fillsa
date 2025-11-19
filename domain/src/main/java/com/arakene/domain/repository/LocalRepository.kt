package com.arakene.domain.repository

import androidx.paging.PagingData
import com.arakene.domain.model.StreakInfo
import com.arakene.domain.requests.LocalQuoteInfo
import com.arakene.domain.responses.DailyQuotaNoToken
import com.arakene.domain.responses.DailyQuoteDto
import com.arakene.domain.util.DarkModeType
import com.arakene.domain.util.YN
import kotlinx.coroutines.flow.Flow

interface LocalRepository {

    fun getLocalQuoteForWidget(): Flow<DailyQuoteDto?>
    suspend fun setLocalQuoteForWidget(data: DailyQuotaNoToken)

    suspend fun setAccessToken(token: String)
    suspend fun getAccessToken(): String
    suspend fun setRefreshToken(token: String)
    suspend fun getRefreshToken(): String
    suspend fun setImageUri(uri: String)
    suspend fun setShareDescriptionVisible(boolean: Boolean)
    suspend fun getShareDescriptionVisible(): Boolean
    fun getImageUri(): Flow<String>
    fun getLoginStatus(): Flow<Boolean>
    suspend fun isFirstOpen(): Flow<Boolean>
    suspend fun setFirstOpen(value: Boolean)
    suspend fun setAlarm(value: Boolean)
    suspend fun setName(value: String)
    fun getAlarm(): Flow<Boolean>
    fun getName(): Flow<String>
    fun isAlarmPermissionRequestedBefore(): Flow<Boolean>
    suspend fun setAlarmPermissionRequestedBefore(requested: Boolean)

    suspend fun getLocalQuotes(): List<LocalQuoteInfo>
    suspend fun addLocalQuote(quote: LocalQuoteInfo)
    suspend fun deleteQuote(quote: LocalQuoteInfo)
    suspend fun updateQuote(quote: LocalQuoteInfo)
    fun getLocalQuotesPaging(
        likeYN: YN,
        startDate: String,
        endDate: String
    ): Flow<PagingData<LocalQuoteInfo>>

    suspend fun updateLocalQuoteMemo(memo: String, seq: Int)
    suspend fun updateLocalQuoteLike(likeYN: YN, seq: Int): Int
    suspend fun getQuoteLocal(seq: Int): LocalQuoteInfo?

    suspend fun emitTokenExpired(errorCode: String)
    fun getTokenExpired(): Flow<String>

    suspend fun findLocalQuoteById(seq: Int): LocalQuoteInfo?

    suspend fun clear()

    suspend fun deleteQuote(seq: Int)

    suspend fun setDarkModeType(darkMode: DarkModeType)
    fun getDarkModeType(): Flow<DarkModeType>

    suspend fun setTodayStreakInfo()
    suspend fun getYesterdayStreakInfo(): StreakInfo?
    suspend fun getAllStreakInfos(): List<StreakInfo>
    suspend fun getStreakDateCount(): Int
    suspend fun checkYesterdayStreak()
    suspend fun getTodayLocalStreakInfo(): StreakInfo?

    suspend fun checkPopupIsHidden(seq: Int): Boolean
    suspend fun addHiddenPopup(seq: Int)
    suspend fun clearAllHiddenPopUp()
}