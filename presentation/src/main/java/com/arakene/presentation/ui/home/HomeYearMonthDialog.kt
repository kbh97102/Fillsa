package com.arakene.presentation.ui.home

import android.app.AlertDialog
import android.content.Context
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.NumberPicker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.arakene.presentation.R
import com.arakene.presentation.util.DateCondition
import java.time.YearMonth

@Composable
internal fun HomeYearMonthDialog(
    displayedMonth: YearMonth,
    lastMonth: YearMonth,
    darkMode: Boolean,
    onConfirm: (YearMonth) -> Unit,
    onDismiss: () -> Unit,
) {
    val context = LocalContext.current
    var year by rememberSaveable { mutableIntStateOf(displayedMonth.year) }
    var month by rememberSaveable { mutableIntStateOf(displayedMonth.monthValue) }
    val confirm by rememberUpdatedState(onConfirm)
    val dismiss by rememberUpdatedState(onDismiss)
    DisposableEffect(context, lastMonth, darkMode) {
        val themedContext = yearMonthDialogContext(context, darkMode)
        val pickers = yearMonthPickerView(themedContext, YearMonth.of(year, month), lastMonth) {
            year = it.year
            month = it.monthValue
        }
        val dialog = AlertDialog.Builder(themedContext)
            .setTitle(R.string.select_year_month)
            .setView(pickers)
            .setPositiveButton(android.R.string.ok) { _, _ -> confirm(YearMonth.of(year, month)) }
            .setNegativeButton(android.R.string.cancel, null)
            .setOnDismissListener { dismiss() }
            .create()
        dialog.show()
        onDispose {
            dialog.setOnDismissListener(null)
            dialog.dismiss()
        }
    }
}

private fun yearMonthDialogContext(context: Context, darkMode: Boolean) = ContextThemeWrapper(
    context,
    if (darkMode) android.R.style.Theme_Material_Dialog_Alert else android.R.style.Theme_Material_Light_Dialog_Alert,
)

private fun yearMonthPickerView(
    context: Context,
    initialMonth: YearMonth,
    lastMonth: YearMonth,
    onChanged: (YearMonth) -> Unit,
): LinearLayout {
    val firstMonth = DateCondition.startMonth
    val initial = initialMonth.coerceIn(firstMonth, lastMonth)
    val yearPicker = NumberPicker(context).apply {
        minValue = firstMonth.year
        maxValue = lastMonth.year
        value = initial.year
        wrapSelectorWheel = false
        descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        contentDescription = context.getString(R.string.calendar_year)
        displayedValues = (minValue..maxValue).map { context.getString(R.string.calendar_year_value, it) }.toTypedArray()
    }
    val monthPicker = NumberPicker(context).apply {
        descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        contentDescription = context.getString(R.string.calendar_month)
    }
    fun updateMonths(year: Int, month: Int) {
        monthPicker.apply {
            // Reset the full range before narrowing it when switching boundary years.
            displayedValues = null
            minValue = 1
            maxValue = 12
            minValue = if (year == firstMonth.year) firstMonth.monthValue else 1
            maxValue = if (year == lastMonth.year) lastMonth.monthValue else 12
            value = month.coerceIn(minValue, maxValue)
            displayedValues = (minValue..maxValue).map { context.getString(R.string.calendar_month_value, it) }.toTypedArray()
            wrapSelectorWheel = false
        }
    }
    updateMonths(initial.year, initial.monthValue)
    onChanged(initial)
    yearPicker.setOnValueChangedListener { _, _, year ->
        updateMonths(year, monthPicker.value)
        onChanged(YearMonth.of(year, monthPicker.value))
    }
    monthPicker.setOnValueChangedListener { _, _, month -> onChanged(YearMonth.of(yearPicker.value, month)) }
    return LinearLayout(context).apply {
        val background = TypedValue()
        context.theme.resolveAttribute(android.R.attr.colorBackground, background, true)
        setBackgroundColor(background.data)
        orientation = LinearLayout.HORIZONTAL
        gravity = Gravity.CENTER
        addView(yearPicker, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
        addView(monthPicker, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
    }
}

@Preview(widthDp = 280, heightDp = 180, locale = "ko", fontScale = 1f, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun HomeYearMonthPickerLightPreview() {
    AndroidView(factory = { yearMonthPickerView(yearMonthDialogContext(it, false), YearMonth.of(2026, 8), YearMonth.of(2026, 10)) {} })
}

@Preview(widthDp = 280, heightDp = 180, locale = "ko", fontScale = 1f, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES, showBackground = true, backgroundColor = 0xFF303030)
@Composable
private fun HomeYearMonthPickerDarkPreview() {
    AndroidView(factory = { yearMonthPickerView(yearMonthDialogContext(it, true), YearMonth.of(2026, 8), YearMonth.of(2026, 10)) {} })
}
