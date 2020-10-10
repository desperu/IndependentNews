package org.desperu.independentnews.service.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.*

/**
 * Class to manage alarm for app.
 *
 * @constructor Instantiate a new AppAlarmManager.
 */
class AppAlarmManager {

    /**
     * Get pending intent for alarm manager, to call broadcast receiver at alarm time.
     * @param context the context from this method is called.
     * @param action the action for the receiver.
     * @return Created pending intent.
     */
    private fun getPendingIntent(context: Context, action: Int): PendingIntent? {
        val alarmIntent = Intent(context, AlarmReceiver::class.java).putExtra(ACTION, action)
        return PendingIntent.getBroadcast(context, action, alarmIntent, 0)
    }

    /**
     * Get alarm time in millis.
     * @param hour the hour of the alarm.
     * @return Alarm time in millis.
     */
    internal fun getAlarmTime(hour: Int): Long {
        val cal: Calendar = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        return cal.timeInMillis
    }

    /**
     * Enable alarm for app services, notification and update data.
     * @param context the context from this method is called.
     * @param alarmTime the time to set the alarm.
     * @param action the action for the receiver.
     */
    internal fun startAlarm(context: Context, alarmTime: Long, action: Int) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmTime, AlarmManager.INTERVAL_DAY, getPendingIntent(context, action)
        )
    }

    /**
     * Disable alarm for app services, notification and update data.
     * @param context the context from this method is called.
     * @param action the action for the receiver.
     */
    internal fun stopAlarm(context: Context, action: Int) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(getPendingIntent(context, action))
    }
}