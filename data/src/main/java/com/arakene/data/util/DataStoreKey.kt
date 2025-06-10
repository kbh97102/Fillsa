package com.arakene.data.util

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object DataStoreKey {

    val ACCESS_TOKEN = stringPreferencesKey("access_token")
    val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    val IMAGE_URI = stringPreferencesKey("image_uri")
    val USER_NAME = stringPreferencesKey("user_name")
    val FIRST_OPEN_KEY = booleanPreferencesKey("is_first_open")
    val ALARM_KEY  = booleanPreferencesKey("alarm_key")
}