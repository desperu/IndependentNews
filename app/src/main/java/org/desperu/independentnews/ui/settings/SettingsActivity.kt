package org.desperu.independentnews.ui.settings

import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.alert_dialog.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingActivity
import org.desperu.independentnews.di.module.ui.settingsModule
import org.desperu.independentnews.service.alarm.AppAlarmManager.getAlarmTime
import org.desperu.independentnews.service.alarm.AppAlarmManager.startAlarm
import org.desperu.independentnews.service.alarm.AppAlarmManager.stopAlarm
import org.desperu.independentnews.utils.*
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
        configureAppBar()
        showAppBarIcon(listOf(R.id.back_arrow_icon))
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

    // --------------
    // ACTION
    // --------------

    /**
     * On click back arrow icon menu.
     */
    @Suppress("unused_parameter")
    fun onClickBackArrow(v: View) = onClickBackArrow()

    // --------------
    // UI
    // --------------

    /**
     * Create alert dialog to set zoom value or confirm reset settings.
     *
     * @param dialogKey Key to show corresponding dialog.
     */
    override fun alertDialog(dialogKey: Int) {
        val dialog: AlertDialog.Builder = AlertDialog.Builder(this, R.style.AlertDialogTheme)
        // Create dialog for zoom level
        dialog.setTitle(getDialogTitle(dialogKey))
        dialog.setMessage(getDialogMessage(dialogKey))

        if (dialogKey in listOf(NOTIF_TIME_DIALOG, TEXT_SIZE_DIALOG, REFRESH_TIME_DIALOG, STORE_DELAY_DIALOG)) {

            // Add edit text to dialog
            val editView: View = LayoutInflater.from(this).inflate(R.layout.alert_dialog, alert_dialog_linear_root)
            val editText: EditText = editView.findViewById(R.id.alert_dialog_edit_text)
            editText.setText(getDialogValue(dialogKey))
            editText.setSelection(editText.text.length)
            dialog.setView(editView)

            // Set positive button
            dialog.setPositiveButton(R.string.activity_settings_dialog_positive_button) { _, _ ->
                val value = editText.text.toString()
                val rangeValue = getMinAndMaxValues(dialogKey)
                if (value.isNotEmpty() && value.isDigitsOnly()
                    && value.toInt() >= rangeValue[0] && value.toInt() <= rangeValue[1])
                    activity_settings_text_size_value.text = value
                else
                    showToast(getToastMessage(dialogKey), Toast.LENGTH_LONG)
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

    // TODO put in settings utils

    /**
     * Returns the corresponding title for given dialog key.
     *
     * @param dialogKey the key to show the corresponding title.
     *
     * @return the corresponding title for the given dialog key.
     */
    private fun getDialogTitle(dialogKey: Int) = when(dialogKey) {
        NOTIF_TIME_DIALOG -> getString(R.string.activity_settings_text_notifications_time)
        TEXT_SIZE_DIALOG -> getString(R.string.activity_settings_text_article_text_size)
        REFRESH_TIME_DIALOG -> getString(R.string.activity_settings_text_refresh_article_list_time)
        STORE_DELAY_DIALOG -> getString(R.string.activity_settings_text_article_store_delay)
        RESET_DIALOG -> getString(R.string.activity_settings_text_category_reset_settings)
        else -> error("Error while retrieving the Dialog Key : $dialogKey")
    }

    /**
     * Returns the corresponding message for given dialog key.
     *
     * @param dialogKey the key to show the corresponding message.
     *
     * @return the corresponding message for the given dialog key.
     */
    private fun getDialogMessage(dialogKey: Int) = when(dialogKey) {
        NOTIF_TIME_DIALOG -> getString(R.string.activity_settings_text_notifications_time_description)
        TEXT_SIZE_DIALOG -> getString(R.string.activity_settings_text_article_text_size_description)
        REFRESH_TIME_DIALOG -> getString(R.string.activity_settings_text_refresh_article_list_time_description)
        STORE_DELAY_DIALOG -> getString(R.string.activity_settings_text_article_store_delay_description)
        RESET_DIALOG -> getString(R.string.activity_settings_dialog_reset_settings_message)
        else -> error("Error while retrieving the Dialog Key : $dialogKey")
    }

    /**
     * Returns the corresponding value for given dialog key.
     *
     * @param dialogKey the key to show the corresponding value.
     *
     * @return the corresponding value for the given dialog key.
     */
    private fun getDialogValue(dialogKey: Int) = when(dialogKey) {
        NOTIF_TIME_DIALOG -> viewModel.notificationTime.get().toString()
        TEXT_SIZE_DIALOG -> viewModel.textSize.get().toString()
        REFRESH_TIME_DIALOG -> viewModel.refreshTime.get().toString()
        STORE_DELAY_DIALOG -> viewModel.storeDelay.get().toString()
        else -> error("Error while retrieving the Dialog Key : $dialogKey")
    }

    /**
     * Returns the corresponding dialog message for given dialog key.
     *
     * @param dialogKey the key to show the corresponding message.
     *
     * @return the corresponding dialog message for the given dialog key.
     */
    private fun getMinAndMaxValues(dialogKey: Int) = when(dialogKey) {
        NOTIF_TIME_DIALOG -> listOf(0, 24)
        TEXT_SIZE_DIALOG -> listOf(1, 200)
        REFRESH_TIME_DIALOG -> listOf(0, 24)
        STORE_DELAY_DIALOG -> listOf(1, 24)
        else -> error("Error while retrieving the Dialog Key : $dialogKey")
    }

    /**
     * Returns the corresponding toast message for given dialog key.
     *
     * @param dialogKey the key to show the corresponding message.
     *
     * @return the corresponding toast message for the given dialog key.
     */
    private fun getToastMessage(dialogKey: Int) = when(dialogKey) {
        NOTIF_TIME_DIALOG -> getString(R.string.activity_settings_toast_time_wrong_value, getString(R.string.activity_settings_text_notifications_time))
        TEXT_SIZE_DIALOG -> getString(R.string.activity_settings_toast_text_size_wrong_value)
        REFRESH_TIME_DIALOG -> getString(R.string.activity_settings_toast_time_wrong_value, getString(R.string.activity_settings_text_refresh_article_list_time))
        STORE_DELAY_DIALOG -> getString(R.string.activity_settings_toast_store_delay_wrong_value)
        else -> error("Error while retrieving the Dialog Key : $dialogKey")
    }
}