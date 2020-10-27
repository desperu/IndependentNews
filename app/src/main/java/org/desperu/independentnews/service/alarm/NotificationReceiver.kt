package org.desperu.independentnews.service.alarm

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.desperu.independentnews.R
import org.desperu.independentnews.repositories.IndependentNewsRepository
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.service.alarm.AppAlarmManager.getAlarmTime
import org.desperu.independentnews.service.alarm.AppAlarmManager.startAlarm
import org.desperu.independentnews.ui.main.MainActivity
import org.desperu.independentnews.ui.main.TODAY_ARTICLES
import org.desperu.independentnews.utils.*
import org.koin.core.KoinComponent
import org.koin.core.inject

// FOR NOTIFICATION
private const val CHANNEL_ID = "IndependentNewsNotification"
private const val NOTIFICATION_NAME = "NewArticles"
private const val NOTIFICATION_ID = 1
private const val CHANNEL_DESCRIPTION = "IndependentNews notifications channel"

/**
 * Broadcast Receiver to handle broadcast call for notification (alarm or system).
 *
 * @constructor Instantiate a new NotificationReceiver.
 */
class NotificationReceiver: BroadcastReceiver(), KoinComponent {

    // FOR DATA
    private lateinit var context: Context
    private val prefs = inject<SharedPrefService>()
    private val ideNewsRepository = inject<IndependentNewsRepository>()

    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let { this.context = it }

        val isBootComplete = intent?.action.equals("android.intent.action.BOOT_COMPLETED")
        val isNotifEnabled =
            prefs.value.getPrefs().getBoolean(NOTIFICATION_ENABLED, NOTIFICATION_DEFAULT)
        val notifTime =
            prefs.value.getPrefs().getInt(NOTIFICATION_TIME, NOTIFICATION_TIME_DEFAULT)
        val isFirstStart = prefs.value.getPrefs().getBoolean(IS_FIRST_TIME, true)

        if (isBootComplete && isNotifEnabled)
            context?.let { it1 -> startAlarm(it1, getAlarmTime(notifTime), NOTIFICATION) }
        else if (!isFirstStart)
            createNotification()
    }

    /**
     * Create notification, and set on click.
     */
    @Suppress("Deprecation")
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun createNotification() = GlobalScope.launch(Dispatchers.Unconfined) {
        val todayArticles = getTodayArticles()

        // Create notification.
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.app_logo_black)
            .setLargeIcon(context.resources.getDrawable(R.drawable.app_logo).toBitmap())
            .setContentTitle(context.getString(R.string.notification_title))
            .setContentText(
                if (todayArticles.isEmpty()) context.getString(R.string.notification_text_no_today_articles)
                else context.getString(R.string.notification_text, todayArticles.size.toString())
            )
            .setDefaults(Notification.DEFAULT_LIGHTS or Notification.DEFAULT_SOUND)
            .setAutoCancel(true)

        // Create intent for notification click.
        val clickIntent: Intent = Intent(context, MainActivity::class.java)
            .putParcelableArrayListExtra(TODAY_ARTICLES, ArrayList(todayArticles))

        // Adds the intent that starts the activity.
        val resultPendingIntent = PendingIntent.getActivities(
            context,
            0,
            arrayOf(clickIntent),
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(resultPendingIntent)

        // Notification Manager instance.
        val notificationManagerCompat = NotificationManagerCompat.from(context)

        // Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel =
                NotificationChannel(CHANNEL_ID, NOTIFICATION_NAME, importance)
            mChannel.description = CHANNEL_DESCRIPTION
            notificationManagerCompat.createNotificationChannel(mChannel)
        }

        // Show notification.
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build())
    }

    /**
     * Returns the today article list, articles published today.
     *
     * @return the today article list, articles published today.
     */
    private suspend fun getTodayArticles() = withContext(Dispatchers.Unconfined) {
        ideNewsRepository.value.getTodayArticles()
    }
}