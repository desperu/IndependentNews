package org.desperu.independentnews.service.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.desperu.independentnews.repositories.IndependentNewsRepository
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.service.alarm.AppAlarmManager.getAlarmTime
import org.desperu.independentnews.service.alarm.AppAlarmManager.startAlarm
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.isWifiAvailable
import org.koin.core.KoinComponent
import org.koin.core.inject

/**
 * The argument name for intent extra to received the action to this receiver.
 */
const val ACTION = "action"

/**
 * Broadcast Receiver to handle broadcast call (alarm or system).
 *
 * @constructor Instantiate a new AlarmReceiver.
 */
class AlarmReceiver: BroadcastReceiver(), KoinComponent {

    // FOR DATA
    private var context: Context? = null
    private val ideNewsRepository = inject<IndependentNewsRepository>()
    private val prefs = inject<SharedPrefService>()

    override fun onReceive(context: Context?, intent: Intent?) {
        this.context = context
        intent?.let<Intent, Unit> {
            val isBootComplete = it.action.equals("android.intent.action.BOOT_COMPLETED")
            val isNotifEnabled = prefs.value.getPrefs().getBoolean(NOTIFICATION_ENABLED, NOTIFICATION_DEFAULT)
            val notifTime = prefs.value.getPrefs().getInt(NOTIFICATION_TIME, NOTIFICATION_TIME_DEFAULT)
            val action = it.extras?.getInt(ACTION)


            when {
                isBootComplete && isNotifEnabled -> context?.let { it1 ->
                    startAlarm(
                        it1, getAlarmTime(notifTime),
                        UPDATE_DATA)
                }
                action == UPDATE_DATA -> updateData()
                action == NOTIFICATION -> createNotification()
                else -> {}
            }
        }
    }

    /**
     * Update data for all sources in database.
     */
    private fun updateData() = GlobalScope.launch(Dispatchers.IO) {
        val isWifiOnly = prefs.value.getPrefs().getBoolean(REFRESH_ONLY_WIFI, REFRESH_ONLY_WIFI_DEFAULT)
        val isWifiAvailable = isWifiAvailable(context!!)
        if (!isWifiOnly || isWifiOnly && isWifiAvailable)
            ideNewsRepository.value.refreshData()
    }

    /**
     * Create notification, and set on click.
     * @param bookedUserNameList Booked user name list.
     */
    private fun createNotification(){//bookedUserNameList: List<String>) {
        // Create notification.
//        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_base_logo_black)
//            .setContentTitle(
//                context.getString(
//                    R.string.notification_title,
//                    Go4LunchUtils.getSimpleRestaurantName(bookedRestaurant.getName()),
//                    Go4LunchUtils.getRestaurantStreetAddress(bookedRestaurant.getAddress())
//                )
//            )
//            .setContentText(
//                if (bookedUserNameList.size == 0) context.getString(R.string.notification_text_no_joining_user) else context.getString(
//                    R.string.notification_text
//                ) + Go4LunchUtils.getJoiningUsersName(context, bookedUserNameList)
//            )
//            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
//            .setAutoCancel(true)
//
//        // Create intent for notification click.
//        val resultIntent: Intent = Intent(context, RestaurantDetailActivity::class.java)
//            .putExtra(RestaurantDetailActivity.RESTAURANT_ID, bookedRestaurant.getRestaurantId())
//
//        // Add parent and activity to the top of stack.
//        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(context)
//        stackBuilder.addParentStack(MainActivity::class.java)
//        stackBuilder.addNextIntent(resultIntent)
//
//        // Adds the intent that starts the activity.
//        val resultPendingIntent: PendingIntent =
//            stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
//        builder.setContentIntent(resultPendingIntent)
//
//        // Notification Manager instance.
//        val notificationManagerCompat =
//            NotificationManagerCompat.from(context)
//
//        // Support Version >= Android 8
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val importance = NotificationManager.IMPORTANCE_HIGH
//            val mChannel =
//                NotificationChannel(CHANNEL_ID, NOTIFICATION_NAME, importance)
//            mChannel.description = CHANNEL_DESCRIPTION
//            notificationManagerCompat.createNotificationChannel(mChannel)
//        }
//
//        // Save that notification is sent
//        Go4LunchPrefs.savePref(context, IS_BOOKED_NOTIFICATION_SENT, true)
//
//        // Show notification.
//        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())
    }
}