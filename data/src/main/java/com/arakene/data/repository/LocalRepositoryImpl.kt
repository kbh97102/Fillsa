package com.arakene.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.arakene.data.util.DataStoreKey
import com.arakene.data.util.DataStoreKey.ACCESS_TOKEN
import com.arakene.data.util.DataStoreKey.FIRST_OPEN_KEY
import com.arakene.data.util.DataStoreKey.REFRESH_TOKEN
import com.arakene.data.util.TokenProvider
import com.arakene.domain.repository.LocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>,
    private val tokenProvider: TokenProvider
) : LocalRepository {

    override suspend fun isFirstOpen(): Flow<Boolean> {
        return dataStore.data.map {
            it[FIRST_OPEN_KEY] ?: false
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
}