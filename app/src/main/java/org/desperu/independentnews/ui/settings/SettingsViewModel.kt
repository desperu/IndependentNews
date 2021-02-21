package org.desperu.independentnews.ui.settings

import android.view.View
import androidx.core.content.edit
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import androidx.lifecycle.ViewModel
import org.desperu.independentnews.R
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.utils.*

/**
 * View Model witch provide data for Settings Activity.
 *
 * @property prefs                  the shared preferences service interface witch provide access
 *                                  to the app shared preferences.
 * @property settingsInterface      the settings interface witch provide activity interface.
 *
 * @constructor Instantiates a new SettingsViewModel.
 *
 * @param prefs                     the shared preferences service interface witch provide access
 *                                  to the app shared preferences to set.
 * @param settingsInterface         the settings interface witch provide activity interface to set.
 */
class SettingsViewModel(
    private val prefs: SharedPrefService,
    private val settingsInterface: SettingsInterface
): ViewModel() {

    // FOR DATA
    val isNotificationsEnabled = ObservableBoolean()
    val notificationTime = ObservableInt()
    val textSize = ObservableInt()
    val isRefreshArticleList = ObservableBoolean()
    val refreshTime = ObservableInt()
    val isRefreshOnlyWifi = ObservableBoolean()
    val storeDelay = ObservableInt()

    init {
        setValues()
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onCleared() {
        super.onCleared()
        savePrefs()
        manageAlarms()
    }

    // --------------
    // LISTENERS
    // --------------

    /**
     * On click listener to handle clicks on settings properties.
     */
    val onClick = View.OnClickListener {
        when(it.id) {
            R.id.activity_settings_container_notification_state ->
                inverseBoolean(isNotificationsEnabled)

            R.id.activity_settings_container_notification_time ->
                settingsInterface.alertDialog(NOTIF_TIME_DIALOG, notificationTime)

            R.id.activity_settings_container_text_size ->
                settingsInterface.alertDialog(TEXT_SIZE_DIALOG, textSize)

            R.id.activity_settings_container_refresh_article_list ->
                inverseBoolean(isRefreshArticleList)

            R.id.activity_settings_container_refresh_list_time ->
                settingsInterface.alertDialog(REFRESH_TIME_DIALOG, refreshTime)

            R.id.activity_settings_container_only_wifi ->
                inverseBoolean(isRefreshOnlyWifi)

            R.id.activity_settings_container_article_store_delay ->
                settingsInterface.alertDialog(STORE_DELAY_DIALOG, storeDelay)

            R.id.activity_settings_container_reset_settings ->
                settingsInterface.alertDialog(RESET_DIALOG, null)
        }
    }

    // --------------
    // UTILS
    // --------------

    /**
     * Inverse the observable boolean value.
     *
     * @param observable the observable boolean to inverse value.
     */
    private fun inverseBoolean(observable: ObservableBoolean) {
        observable.set(observable.get().not())
    }

    /**
     * Set all needed values
     */
    private fun setValues() = prefs.getPrefs().run {
        isNotificationsEnabled.set(this.getBoolean(NOTIFICATION_ENABLED, NOTIFICATION_DEFAULT))
        notificationTime.set(this.getInt(NOTIFICATION_TIME, NOTIFICATION_TIME_DEFAULT))
        textSize.set(this.getInt(TEXT_SIZE, TEXT_SIZE_DEFAULT))
        isRefreshArticleList.set(this.getBoolean(REFRESH_ARTICLE_LIST, REFRESH_ARTICLE_LIST_DEFAULT))
        refreshTime.set(this.getInt(REFRESH_TIME, REFRESH_TIME_DEFAULT))
        isRefreshOnlyWifi.set(this.getBoolean(REFRESH_ONLY_WIFI, REFRESH_ONLY_WIFI_DEFAULT))
        storeDelay.set(this.getInt(STORE_DELAY, STORE_DELAY_DEFAULT))
    }

    /**
     * Save current prefs.
     */
    private fun savePrefs() = prefs.getPrefs().edit {
        putBoolean(NOTIFICATION_ENABLED, isNotificationsEnabled.get())
        putInt(NOTIFICATION_TIME, notificationTime.get())
        putInt(TEXT_SIZE, textSize.get())
        putBoolean(REFRESH_ARTICLE_LIST,isRefreshArticleList.get())
        putInt(REFRESH_TIME, refreshTime.get())
        putBoolean(REFRESH_ONLY_WIFI, isRefreshOnlyWifi.get())
        putInt(STORE_DELAY, storeDelay.get())
    }

    /**
     * Reset settings to default value.
     */
    internal fun resetSettings() {
        isNotificationsEnabled.set(NOTIFICATION_DEFAULT)
        notificationTime.set(NOTIFICATION_TIME_DEFAULT)
        textSize.set(TEXT_SIZE_DEFAULT)
        isRefreshArticleList.set(REFRESH_ARTICLE_LIST_DEFAULT)
        refreshTime.set(REFRESH_TIME_DEFAULT)
        isRefreshOnlyWifi.set(REFRESH_ONLY_WIFI_DEFAULT)
        storeDelay.set(STORE_DELAY_DEFAULT)
    }

    /**
     * Set or remove alarm when view model is cleared,
     * if there is already an alarm scheduled, it will first be canceled.
     */
    private fun manageAlarms() {
        // Manage notification alarm, if time has changed, it was updated.
        settingsInterface.manageAppAlarm(
            isNotificationsEnabled.get(),
            notificationTime.get(),
            NOTIFICATION
        )

        // Manage update data alarm, if time has changed, it was updated.
        settingsInterface.manageAppAlarm(
            isRefreshArticleList.get(),
            refreshTime.get(),
            UPDATE_DATA
        )
    }
}