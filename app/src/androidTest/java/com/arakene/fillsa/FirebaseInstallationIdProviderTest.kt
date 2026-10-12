package com.arakene.fillsa

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FirebaseInstallationIdProviderTest {
    @Test fun installedAppHasNonemptyInstallationId() = runBlocking {
        val id = withTimeout(15_000) { FirebaseInstallationIdProvider().getId() }
        assertTrue(id.isNotBlank())
    }
}
