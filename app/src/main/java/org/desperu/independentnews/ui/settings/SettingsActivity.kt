package org.desperu.independentnews.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableInt
import androidx.databinding.ViewDataBinding
import kotlinx.android.synthetic.main.alert_dialog.*
import kotlinx.android.synthetic.main.app_bar.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingActivity
import org.desperu.independentnews.di.module.ui.settingsModule
import org.desperu.independentnews.service.alarm.AppAlarmManager.getAlarmTime
import org.desperu.independentnews.service.alarm.AppAlarmManager.startAlarm
import org.desperu.independentnews.service.alarm.AppAlarmManager.stopAlarm
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.SettingsUtils.getDialogMessage
import org.desperu.independentnews.utils.SettingsUtils.getDialogTitle
import org.desperu.independentnews.utils.SettingsUtils.getMinAndMaxValues
import org.desperu.independentnews.utils.SettingsUtils.getToastMessage
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Activity to manage settings of the application.
 *
 * @constructor Instantiates a new SettingsActivity.
 */
class SettingsActivity : BaseBindingActivity(settingsModule), SettingsInterface {

    // FOR DATA
    private lateinit var binding: ViewDataBinding
    private val viewModel by viewModel<SettingsViewModel> { parametersOf(this) }

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureDataBinding()

    override fun configureDesign() {
        configAppBar()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure data binding and return the root view.
     * @return the binding root view.
     */
    private fun configureDataBinding(): View {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.setVariable(org.desperu.independentnews.BR.viewModel, viewModel)
        return binding.root
    }

    /**
     * Configure app bar, show icons, and set title.
     */
    private fun configAppBar() {
        appbar.showAppBarIcon(listOf(R.id.back_arrow_icon))
        toolbar_title.text = getString(R.string.navigation_drawer_settings)
    }

    // --------------
    // ACTION
    // --------------

    /**
     * On click back arrow icon menu.
     */
    @Suppress("unused_parameter")
    fun onClickBackArrow(v: View) = onBackPressed()

    // --------------
    // UI
    // --------------

    /**
     * Create alert dialog to set zoom value or confirm reset settings.
     *
     * @param dialogKey Key to show corresponding dialog.
     * @param observable the observable value to get and set.
     */
    override fun alertDialog(dialogKey: Int, observable: ObservableInt?) {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this, R.style.AlertDialogStyle)
        // Create dialog for zoom level
        dialog.setTitle(getDialogTitle(baseContext, dialogKey))
        dialog.setMessage(getDialogMessage(baseContext, dialogKey))

        if (dialogKey in listOf(NOTIF_TIME_DIALOG, TEXT_SIZE_DIALOG, REFRESH_TIME_DIALOG, STORE_DELAY_DIALOG)) {

            // Add edit text to dialog
            val editView: View = LayoutInflater.from(this).inflate(R.layout.alert_dialog, alert_dialog_linear_root)
            val editText: EditText = editView.findViewById(R.id.alert_dialog_edit_text)
            editText.setText(observable?.get().toString())
            editText.setSelection(editText.text.length)
            dialog.setView(editView)

            // Set positive button
            dialog.setPositiveButton(R.string.activity_settings_dialog_positive_button) { _, _ ->
                val value = editText.text.toString()
                val rangeValue = getMinAndMaxValues(dialogKey)
                if (value.isNotEmpty() && value.isDigitsOnly()
                    && value.toInt() >= rangeValue[0] && value.toInt() <= rangeValue[1])
                    observable?.set(value.toInt())
                else
                    showToast(getToastMessage(baseContext, dialogKey), Toast.LENGTH_LONG)
            }
        } else if (dialogKey == RESET_DIALOG) {

            // Set positive button
            dialog.setPositiveButton(R.string.activity_settings_dialog_positive_button) { _, _ -> viewModel.resetSettings() }
        }

        // Set negative button, and show dialog
        dialog.setNegativeButton(R.string.activity_settings_dialog_negative_button) { dialog3, _ -> dialog3.cancel() }
        dialog.show()
    }

    /**
     * Show toast message.
     *
     * @param message       the message to display to the user.
     * @param duration      the duration to display the message.
     */
    override fun showToast(message: String, duration: Int) {
        Toast.makeText(baseContext, message, duration).show()
    }

    // --------------
    // UTILS
    // --------------

    /**
     * Manage application alarm, start or stop, depends of isAlarmEnabled value.
     *
     * @param isAlarmEnabled    true if alarm is enabled, false otherwise.
     * @param alarmTime         the hour to set the alarm.
     * @param action            the action for the receiver.
     */
    override fun manageAppAlarm(isAlarmEnabled: Boolean, alarmTime: Int, action: Int) {
        if (isAlarmEnabled) startAlarm(baseContext, getAlarmTime(alarmTime), action)
        else stopAlarm(baseContext, action)
    }
}