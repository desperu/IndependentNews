package org.desperu.independentnews.service.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.repositories.IndependentNewsRepository
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.service.alarm.AppAlarmManager.getAlarmTime
import org.desperu.independentnews.service.alarm.AppAlarmManager.startAlarm
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.isWifiAvailable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

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
        val isFirstStart = prefs.value.getPrefs().getBoolean(IS_FIRST_TIME, true)

        if (isBootComplete && isUpdateDataEnabled)
            context?.let { startAlarm(it, getAlarmTime(updateDataTime), UPDATE_DATA) }
        else if (!isFirstStart)
            updateData()
    }

    /**
     * Update data for all sources in database.
     */
    private fun updateData() = CoroutineScope(Dispatchers.IO).launch { // Was GlobalScope, on test
        // All data logged to find error, to remove and correct
        val tag = javaClass.simpleName

        val isWifiOnly = prefs.value.getPrefs().getBoolean(REFRESH_ONLY_WIFI, REFRESH_ONLY_WIFI_DEFAULT)
        Log.e(tag, "isWifiOnly : $isWifiOnly")

        Log.e(tag, "isWifiAvailable ${isWifiAvailable(context!!)}")

        if (!isWifiOnly || isWifiOnly && isWifiAvailable(context!!)) {
            ideNewsRepository.value.refreshData()
            Log.e(tag, "Start to refresh data !! Rocks !!")
        }
    }
}