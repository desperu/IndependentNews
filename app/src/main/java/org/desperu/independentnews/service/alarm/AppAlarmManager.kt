package org.desperu.independentnews.service.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.desperu.independentnews.utils.UPDATE_DATA
import java.util.*

/**
 * Object to manage alarm for app.
 *
 * @constructor Instantiate a new AppAlarmManager.
 */
object AppAlarmManager {

    /**
     * Get pending intent for alarm manager, to call broadcast receiver at alarm time.
     *
     * @param context the context from this method is called.
     * @param action the action for the receiver.
     *
     * @return Created pending intent.
     */
    private fun getPendingIntent(context: Context, action: Int): PendingIntent? {
        val receiver =
            if (action == UPDATE_DATA) UpdateDataReceiver::class.java
            else NotificationReceiver::class.java
        val alarmIntent = Intent(context, receiver)
        return PendingIntent.getBroadcast(context, 0, alarmIntent, 0)
    }

    /**
     * Get alarm time in millis.
     *
     * @param hour the hour of the alarm.
     *
     * @return Alarm time in millis.
     */
    internal fun getAlarmTime(hour: Int): Long {
        val cal: Calendar = Calendar.getInstance()
        val isInPast = cal.get(Calendar.HOUR_OF_DAY) >= hour
        val dayMillis = 86400000 // 24 * 60 * 60 * 1000

        cal.set(Calendar.HOUR_OF_DAY, hour)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)

        return cal.timeInMillis + if (isInPast) dayMillis else 0
    }

    /**
     * Enable alarm for app services, notification and update data.
     *
     * @param context the context from this method is called.
     * @param alarmTime the time to set the alarm.
     * @param action the action for the receiver.
     */
    internal fun startAlarm(context: Context, alarmTime: Long, action: Int) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            alarmTime,
            AlarmManager.INTERVAL_DAY,
            getPendingIntent(context, action)
        )
    }

    /**
     * Disable alarm for app services, notification and update data.
     *
     * @param context the context from this method is called.
     * @param action the action for the receiver.
     */
    internal fun stopAlarm(context: Context, action: Int) {
        val manager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        manager.cancel(getPendingIntent(context, action))
    }
}