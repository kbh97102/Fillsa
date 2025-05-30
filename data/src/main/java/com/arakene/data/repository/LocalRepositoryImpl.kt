package com.arakene.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.arakene.data.util.DataStoreKey
import com.arakene.domain.repository.LocalRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

class LocalRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : LocalRepository {

    override suspend fun setAccessToken(token: String) {
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
}