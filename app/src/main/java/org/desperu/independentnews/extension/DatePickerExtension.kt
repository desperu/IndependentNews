package org.desperu.independentnews.extension

import android.app.DatePickerDialog
import android.content.Context
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import org.desperu.independentnews.R
import org.desperu.independentnews.ui.main.MainActivity
import org.desperu.independentnews.utils.Utils.intDateToString
import org.desperu.independentnews.utils.Utils.stringToDate
import java.util.*

/**
 * Create a DatePickerDialog, for associated picker text view, with given string date to set.
 *
 * @param context the context from this function is called.
 * @param pickerView the associated picker text view.
 * @param date the given string date wrapped in a MutableLieData, to set DatePickerDialog.
 */
internal fun createDatePickerDialog(context: Context, pickerView: TextView, date: MutableLiveData<String>) {
    val cal: Calendar = Calendar.getInstance()
    val dateValue = date.value
    if (!dateValue.isNullOrEmpty()) stringToDate(dateValue)?.let { cal.time = it }

    val year: Int = cal.get(Calendar.YEAR)
    val monthOfYear: Int = cal.get(Calendar.MONTH)
    val dayOfMonth: Int = cal.get(Calendar.DAY_OF_MONTH)

    DatePickerDialog(
        context,
        R.style.DatePickerDialogStyle,
        getOnDateSetListener(context, pickerView, date),
        year,
        monthOfYear,
        dayOfMonth
    ).show()
}

/**
 * Create and return OnDateSetListener, for the DatePickerDialog associated with the picker text view,
 * set picker text and date value when a date is selected.
 *
 * @param context the context from this function is called.
 * @param pickerView the picker text view associated with the DatePickerDialog.
 * @param date the given string date wrapped in a MutableLieData, to set DatePickerDialog.
 *
 * @return the listener created.
 */
private fun getOnDateSetListener(context: Context, pickerView: TextView, date: MutableLiveData<String>) =
    DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
        date.value = intDateToString(dayOfMonth, month, year)
        pickerView.text = date.value
        date.removeObservers(context as MainActivity)
    }