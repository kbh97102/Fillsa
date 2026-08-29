package com.arakene.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.arakene.domain.model.PromptAnswerRecord
import java.security.MessageDigest
import kotlinx.coroutines.flow.first

internal class PromptAnswerStore(
    private val dataStore: DataStore<Preferences>,
) {
    suspend fun save(record: PromptAnswerRecord) {
        dataStore.edit { preferences ->
            preferences[keyFor(record.date, record.question)] = record.answer
        }
    }

    suspend fun load(date: String, question: String): PromptAnswerRecord? =
        dataStore.data.first()[keyFor(date, question)]?.let { answer ->
            PromptAnswerRecord(date = date, question = question, answer = answer)
        }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.asMap().keys
                .filter { it.name.startsWith(PromptAnswerPreferencePrefix) }
                .forEach { preferences.remove(it) }
        }
    }

    private fun keyFor(date: String, question: String) =
        stringPreferencesKey(PromptAnswerPreferencePrefix + keyDigest("$date\u0000$question"))

    private fun keyDigest(value: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(value.toByteArray())
            .joinToString("") { byte -> "%02x".format(byte) }

    private companion object {
        const val PromptAnswerPreferencePrefix = "prompt_answer_"
    }
}
