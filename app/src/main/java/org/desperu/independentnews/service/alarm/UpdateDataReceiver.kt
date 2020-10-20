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
 * Broadcast Receiver to handle broadcast call for update data (alarm or system).
 *
 * @constructor Instantiate a new UpdateDataReceiver.
 */
class UpdateDataReceiver: BroadcastReceiver(), KoinComponent {

    // FOR DATA
    private var context: Context? = null
    private val ideNewsRepository = inject<IndependentNewsRepository>()
    private val prefs = inject<SharedPrefService>()

    override fun onReceive(context: Context?, intent: Intent?) {
        this.context = context
        val isBootComplete = intent?.action.equals("android.intent.action.BOOT_COMPLETED")
        val isUpdateDataEnabled =
            prefs.value.getPrefs().getBoolean(REFRESH_ARTICLE_LIST, REFRESH_ARTICLE_LIST_DEFAULT)
        val updateDataTime = prefs.value.getPrefs().getInt(REFRESH_TIME, REFRESH_TIME_DEFAULT)

        if (isBootComplete && isUpdateDataEnabled)
            context?.let { startAlarm(it, getAlarmTime(updateDataTime), UPDATE_DATA) }
        else
            updateData()
    }

    /**
     * Update data for all sources in database.
     */
    private fun updateData() = GlobalScope.launch(Dispatchers.IO) {
        val isWifiOnly = prefs.value.getPrefs().getBoolean(REFRESH_ONLY_WIFI, REFRESH_ONLY_WIFI_DEFAULT)
        if (!isWifiOnly || isWifiOnly && isWifiAvailable(context!!))
            ideNewsRepository.value.refreshData()
    }
}