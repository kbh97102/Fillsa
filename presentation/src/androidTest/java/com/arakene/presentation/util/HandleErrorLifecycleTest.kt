package com.arakene.presentation.util

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arakene.domain.util.CommonError
import kotlinx.coroutines.flow.MutableSharedFlow
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.atomic.AtomicInteger

@RunWith(AndroidJUnit4::class)
class HandleErrorLifecycleTest {
    @get:Rule val composeRule = createComposeRule()

    @Test fun resumedScreenHandlesOneErrorOnce() {
        val errors = MutableSharedFlow<CommonError>(extraBufferCapacity = 1)
        val handledCount = AtomicInteger()
        val owner = object : LifecycleOwner {
            val registry = LifecycleRegistry(this)
            override val lifecycle: Lifecycle = registry
        }

        composeRule.setContent {
            HandleError(errors, owner) { handledCount.incrementAndGet() }
        }
        composeRule.runOnIdle { owner.registry.currentState = Lifecycle.State.STARTED }
        composeRule.waitForIdle()
        composeRule.runOnIdle { owner.registry.currentState = Lifecycle.State.CREATED }
        composeRule.waitForIdle()
        composeRule.runOnIdle { owner.registry.currentState = Lifecycle.State.STARTED }
        composeRule.waitForIdle()

        errors.tryEmit(CommonError.NetworkError)
        composeRule.waitUntil(5_000) { handledCount.get() >= 1 }
        composeRule.waitForIdle()
        assertEquals(1, handledCount.get())
    }
}
