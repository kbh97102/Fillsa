package com.arakene.data.repository

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.arakene.domain.model.PromptAnswerRecord
import java.nio.file.Files
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PromptAnswerStoreTest {

    @Test
    fun `saved prompt answer reloads only for the same date and question`() = runBlocking {
        val directory = Files.createTempDirectory("prompt-answer-store-test")
        val store = PromptAnswerStore(
            PreferenceDataStoreFactory.create { directory.resolve("answers.preferences_pb").toFile() },
        )
        val record = PromptAnswerRecord(
            date = "2026-08-29",
            question = "오늘 어떤 생각을 했나요?",
            answer = "기록한 답변",
        )

        store.save(record)

        assertEquals(record, store.load(record.date, record.question))
        assertEquals(null, store.load(record.date, "다른 질문"))
        assertEquals(null, store.load("2026-08-30", record.question))
    }
}
