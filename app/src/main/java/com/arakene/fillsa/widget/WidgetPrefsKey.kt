package com.arakene.fillsa.widget

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

object WidgetPrefsKey {

    val TEST_STRING = stringPreferencesKey("test_string_key")
    val LANGUAGE_KEY = stringPreferencesKey("LANGUAGE_KEY")
    val FONT_SIZE_KEY = stringPreferencesKey("FONT_SIZE_KEY")

}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "widget_test")