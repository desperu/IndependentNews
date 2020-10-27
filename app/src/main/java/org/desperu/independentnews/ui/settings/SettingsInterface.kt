package org.desperu.independentnews.ui.settings

import androidx.databinding.ObservableInt

/**
 * Interface to allow communications with Settings Activity.
 */
interface SettingsInterface {

    /**
     * Show toast message.
     *
     * @param message       the message to display to the user.
     * @param duration      the duration to display the message.
     */
    fun showToast(message: String, duration: Int)

    /**
     * Create alert dialog to set zoom value or confirm reset settings.
     * @param dialogKey Key to show corresponding dialog.
     * @param observable the observable value to get and set.
     */
    fun alertDialog(dialogKey: Int, observable: ObservableInt?)

    /**
     * Manage application alarm, start or stop, depends of isAlarmEnabled value.
     *
     * @param isAlarmEnabled    true if alarm is enabled, false otherwise.
     * @param alarmTime         the hour to set the alarm.
     * @param action            the action for the receiver.
     */
    fun manageAppAlarm(isAlarmEnabled: Boolean, alarmTime: Int, action: Int)
}