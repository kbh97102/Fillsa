package com.arakene.data.util

import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKey {

    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val IMAGE_URI = stringPreferencesKey("image_uri")

}