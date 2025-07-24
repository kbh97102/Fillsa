package com.arakene.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.arakene.data.db.LocalQuoteInfoDao
import com.arakene.data.network.GetLocalQuoteDataSource
import com.arakene.data.util.DataStoreKey
import com.arakene.data.util.DataStoreKey.ACCESS_TOKEN
import com.arakene.data.util.DataStoreKey.ALARM_KEY
import com.arakene.data.util.DataStoreKey.FIRST_OPEN_KEY
import com.arakene.data.util.DataStoreKey.PERMISSION_REQUESTED
import com.arakene.data.util.DataStoreKey.REFRESH_TOKEN
import com.arakene.data.util.TokenProvider
import com.arakene.data.util.toDomain
import com.arakene.data.util.toEntity
import com.arakene.domain.repository.LocalRepository
import com.arakene.domain.requests.LocalQuoteInfo
import com.arakene.domain.util.YN
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val tokenProvider: TokenProvider,
    private val dao: LocalQuoteInfoDao
) : LocalRepository {

    override fun isAlarmPermissionRequestedBefore(): Flow<Boolean> {
        return dataStore.data.map {
            it[PERMISSION_REQUESTED] ?: false
        }
    }

    override suspend fun setAlarmPermissionRequestedBefore(requested: Boolean) {
        dataStore.edit {
            it[PERMISSION_REQUESTED] = requested
        }
    }

    override suspend fun deleteQuote(seq: Int) {
        dao.deleteQuoteById(seq)
    }

    override suspend fun clear() {
        dao.clear()
    }

    override suspend fun findLocalQuoteById(seq: Int): LocalQuoteInfo? {
        return dao.findQuoteById(seq)?.toDomain()
    }

    override suspend fun emitTokenExpired(errorCode: String) {
        dataStore.edit {
            it[DataStoreKey.TOKEN_EXPIRED] = errorCode
        }
    }

    override fun getTokenExpired(): Flow<String> = dataStore.data.map {
        it[DataStoreKey.TOKEN_EXPIRED] ?: ""
    }

    override suspend fun getQuoteLocal(seq: Int): LocalQuoteInfo? {
        return dao.getQuote(seq)?.toDomain()
    }

    override suspend fun updateLocalQuoteLike(likeYN: YN, seq: Int): Int {

        if (likeYN == YN.N) {
            dao.findQuoteById(seq)?.let { quote ->
                if (quote.korTyping.isEmpty() && quote.engTyping.isEmpty()) {
                    dao.deleteQuoteById(seq)
                    return 0
                }
            }
        }

        return dao.updateLike(likeYN.type, seq)
    }

    override suspend fun updateLocalQuoteMemo(memo: String, seq: Int) {
        dao.updateMemo(memo, seq)
    }

    override fun getLocalQuotesPaging(likeYN: YN): Flow<PagingData<LocalQuoteInfo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { GetLocalQuoteDataSource(dao, likeYn = likeYN.type) }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }

    override suspend fun getLocalQuotes(): List<LocalQuoteInfo> {
        return dao.getAllQuotes().map {
            it.toDomain()
        }
    }

    override suspend fun addLocalQuote(quote: LocalQuoteInfo) {
        dao.insertQuote(quote.toEntity())
    }

    override suspend fun deleteQuote(quote: LocalQuoteInfo) {
        dao.deleteQuote(quote.toEntity())
    }

    override suspend fun updateQuote(quote: LocalQuoteInfo) {
        dao.updateQuote(quote.toEntity())
    }

    override suspend fun setAlarm(value: Boolean) {
        dataStore.edit {
            it[ALARM_KEY] = value
        }
    }

    override fun getAlarm(): Flow<Boolean> = dataStore.data.map {
        it[ALARM_KEY] ?: false
    }

    override suspend fun isFirstOpen(): Flow<Boolean> {
        return dataStore.data.map {
            it[FIRST_OPEN_KEY] ?: true
        }
    }

    override suspend fun setFirstOpen(value: Boolean) {
        dataStore.edit { it[FIRST_OPEN_KEY] = value }
    }

    override suspend fun setAccessToken(token: String) {
        tokenProvider.setToken(token)
        dataStore.edit {
            it[ACCESS_TOKEN] = token
        }
    }

    override suspend fun getAccessToken(): String {
        return dataStore.data.firstOrNull()?.get(ACCESS_TOKEN) ?: ""
    }

    override suspend fun setRefreshToken(token: String) {
        dataStore.edit {
            it[REFRESH_TOKEN] = token
        }
    }

    override suspend fun getRefreshToken(): String {
        return dataStore.data.firstOrNull()?.get(REFRESH_TOKEN) ?: ""
    }

    override fun getLoginStatus() = dataStore.data.map { preferences ->
        val accessToken = preferences[ACCESS_TOKEN]
        val refreshToken = preferences[REFRESH_TOKEN]
        !accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank()
    }

    override suspend fun setImageUri(uri: String) {
        dataStore.edit {
            it[DataStoreKey.IMAGE_URI] = uri
        }
    }

    override fun getImageUri(): Flow<String> {
        return dataStore.data.map {
            it[DataStoreKey.IMAGE_URI] ?: ""
        }
    }

    override suspend fun setName(value: String) {
        dataStore.edit {
            it[DataStoreKey.USER_NAME] = value
        }
    }

    override fun getName(): Flow<String> = dataStore.data.map {
        it[DataStoreKey.USER_NAME] ?: ""
    }
}