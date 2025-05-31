package com.arakene.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.arakene.data.util.DataStoreKey
import com.arakene.data.util.TokenProvider
import com.arakene.domain.repository.LocalRepository
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val tokenProvider: TokenProvider
) : LocalRepository {

    override suspend fun setAccessToken(token: String) {
        tokenProvider.setToken(token)
        dataStore.edit {
            it[DataStoreKey.ACCESS_TOKEN] = token
        }
    }

    override suspend fun getAccessToken(): String {
        return dataStore.data.firstOrNull()?.get(DataStoreKey.ACCESS_TOKEN) ?: ""
    }

    override suspend fun setRefreshToken(token: String) {
        dataStore.edit {
            it[DataStoreKey.REFRESH_TOKEN] = token
        }
    }

    override suspend fun getRefreshToken(): String {
        return dataStore.data.firstOrNull()?.get(DataStoreKey.REFRESH_TOKEN) ?: ""
    }

    override fun getLoginStatus() = flow {
        val access = getAccessToken()
        val refresh = getRefreshToken()

        emit(access.isNotEmpty() && refresh.isNotEmpty())
    }
}