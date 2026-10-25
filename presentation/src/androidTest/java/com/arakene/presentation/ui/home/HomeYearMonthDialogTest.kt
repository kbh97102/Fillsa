package com.arakene.presentation.ui.home

import android.view.View
import android.widget.NumberPicker
import android.view.accessibility.AccessibilityNodeInfo
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.ViewAction
import androidx.test.espresso.UiController
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.arakene.presentation.ui.theme.FillsaTheme
import com.arakene.presentation.util.HomeAnswerUiState
import com.arakene.presentation.util.LocaleType
import org.hamcrest.Matcher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.YearMonth

@RunWith(AndroidJUnit4::class)
class HomeYearMonthDialogTest {
    @get:Rule val compose = createComposeRule()

    @Test fun yearAndMonthOpenPicker_cancelDoesNotChangeMonth_confirmChangesOnlyMonth() {
        val displayed = mutableStateOf(YearMonth.of(2026, 10))
        var dateSelections = 0
        compose.setContent {
            FillsaTheme {
                HomeInlineCalendar(displayed.value, LocalDate.of(2026, 10, 25),
                    LocalDate.of(2026, 10, 25), { displayed.value = it }, { dateSelections++ })
            }
        }
        compose.onNodeWithText("2026").performTouchInput { click() }
        onView(withContentDescription("연도")).inRoot(isDialog()).check(matches(isDisplayed()))
        onView(withContentDescription("월")).inRoot(isDialog()).perform(setPickerValue(8))
        onView(withId(android.R.id.button2)).inRoot(isDialog()).perform(click())
        compose.runOnIdle { assertEquals(YearMonth.of(2026, 10), displayed.value) }

        compose.onAllNodesWithText("10")[0].performTouchInput { click() }
        onView(withContentDescription("월")).inRoot(isDialog()).perform(setPickerValue(8))
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click())
        compose.runOnIdle {
            assertEquals(YearMonth.of(2026, 8), displayed.value)
            assertEquals(0, dateSelections)
        }
    }

    @Test fun changingBoundaryYears_clampsMonthToAvailableRange() {
        val displayed = mutableStateOf(YearMonth.of(2026, 1))
        compose.setContent {
            HomeInlineCalendar(displayed.value, LocalDate.of(2026, 10, 25),
                LocalDate.of(2026, 10, 25), { displayed.value = it }, {})
        }
        compose.onNodeWithText("2026").performTouchInput { click() }
        onView(withContentDescription("연도")).inRoot(isDialog()).perform(setPickerValue(2025))
        onView(withContentDescription("월")).inRoot(isDialog()).check { view, error ->
            if (error != null) throw error
            val picker = view as NumberPicker
            assertEquals(6, picker.minValue)
            assertEquals(12, picker.maxValue)
            assertEquals(6, picker.value)
        }
        onView(withContentDescription("월")).inRoot(isDialog()).perform(setPickerValue(12))
        onView(withContentDescription("연도")).inRoot(isDialog()).perform(setPickerValue(2026))
        onView(withContentDescription("월")).inRoot(isDialog()).check { view, error ->
            if (error != null) throw error
            val picker = view as NumberPicker
            assertEquals(1, picker.minValue)
            assertEquals(10, picker.maxValue)
            assertEquals(10, picker.value)
        }
        onView(withId(android.R.id.button1)).inRoot(isDialog()).perform(click())
        compose.runOnIdle { assertEquals(YearMonth.of(2026, 10), displayed.value) }
    }

    @Test fun sameYearRange_respectsBothBoundaries_andBackCancels() {
        var changed = false
        compose.setContent {
            HomeInlineCalendar(YearMonth.of(2025, 8), LocalDate.of(2025, 9, 10),
                LocalDate.of(2025, 9, 10), { changed = true }, {}, darkMode = true)
        }
        compose.onNodeWithText("2025").performTouchInput { click() }
        onView(withContentDescription("월")).inRoot(isDialog()).check { view, error ->
            if (error != null) throw error
            val picker = view as NumberPicker
            assertEquals(6, picker.minValue)
            assertEquals(9, picker.maxValue)
        }
        androidx.test.espresso.Espresso.pressBack()
        compose.runOnIdle { assertFalse(changed) }
        compose.onNodeWithText("2025").assertExists()
    }

    @Test fun homeCalendar_insideTapKeepsCalendar_andOutsideTapDismisses() {
        val open = mutableStateOf(true)
        compose.setContent {
            FillsaTheme {
                FigmaHomeContent(
                    date = LocalDate.of(2026, 10, 25), quote = "", author = "", isQuoteLoading = false,
                    selectedLocale = LocaleType.KOR, isLike = false, backgroundImageUrl = "", weekDays = emptyList(),
                    question = "", isCalendarOpen = open.value, displayedMonth = YearMonth.of(2026, 10),
                    calendarReferenceDate = LocalDate.of(2026, 10, 25), isStreakTooltipOpen = false,
                    answerUiState = HomeAnswerUiState(), canGoNext = false,
                    onLocaleChanged = {}, onHome = {}, onProfile = {}, onCalendar = {},
                    onDismissCalendar = { open.value = false }, onMonthChanged = {}, onDateSelected = {},
                    onWeekDaySelected = {}, onStreakStatus = {}, onDismissStreakTooltip = {}, onStreakCalendar = {},
                    onQuote = {}, onAnswerChanged = {}, onRecordAnswer = {}, onEditAnswer = {}, onAuthor = {},
                    onPreviousQuote = {}, onNextQuote = {}, onCopy = {}, onShare = {}, onLike = {}, onImage = {},
                )
            }
        }
        val density = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext.resources.displayMetrics.density
        compose.onRoot().performTouchInput { click(Offset(245 * density, 420 * density)) }
        compose.runOnIdle { assertTrue(open.value) }
        compose.onRoot().performTouchInput { click(Offset(305 * density, 420 * density)) }
        compose.runOnIdle { assertFalse(open.value) }
    }

    private fun setPickerValue(value: Int) = object : ViewAction {
        override fun getConstraints(): Matcher<View> = isAssignableFrom(NumberPicker::class.java)
        override fun getDescription() = "Select picker value $value"
        override fun perform(uiController: UiController, view: View) {
            val picker = view as NumberPicker
            require(value in picker.minValue..picker.maxValue)
            repeat(kotlin.math.abs(picker.value - value)) {
                val action = if (picker.value > value) AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD else AccessibilityNodeInfo.ACTION_SCROLL_FORWARD
                assertTrue(picker.accessibilityNodeProvider.performAction(View.NO_ID, action, null))
                uiController.loopMainThreadForAtLeast(350)
            }
            assertEquals(value, picker.value)
        }
    }
}
