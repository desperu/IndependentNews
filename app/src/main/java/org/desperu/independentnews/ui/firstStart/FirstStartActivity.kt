package org.desperu.independentnews.ui.firstStart

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.di.module.ui.firstStartModule
import org.desperu.independentnews.di.module.ui.mainModule
import org.desperu.independentnews.helpers.DialogHelper
import org.desperu.independentnews.helpers.SnackBarHelper
import org.desperu.independentnews.repositories.IndependentNewsRepository
import org.desperu.independentnews.service.alarm.AppAlarmManager.getAlarmTime
import org.desperu.independentnews.service.alarm.AppAlarmManager.startAlarm
import org.desperu.independentnews.ui.main.MainActivity
import org.desperu.independentnews.ui.showImages.ShowImagesActivity
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.isInternetAvailable
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.hasPermissions
import pub.devrel.easypermissions.EasyPermissions.requestPermissions

/**
 * First start activity of the application.
 *
 * @property mainModule the koin of the activity to load at start.
 *
 * @constructor Instantiates a new FirstStartActivity.
 */
class FirstStartActivity : BaseActivity(firstStartModule), FirstStartInterface {

    // FOR DATA
    private val ideNewsRepository = get<IndependentNewsRepository>()
    private lateinit var snackBarHelper: SnackBarHelper
    private lateinit var dialogHelper: DialogHelper

    /** Used to handle first apk start. */
    private val prefs: SharedPreferences
        get() = getSharedPreferences(INDEPENDENT_NEWS_PREFS, Context.MODE_PRIVATE)
    private var isFirstTime: Boolean
        get() = prefs.getBoolean(IS_FIRST_TIME, true)
        set(value) = prefs.edit { putBoolean(IS_FIRST_TIME, value) }

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_first_start

    override fun configureDesign() {
        configureKoinDependency()
        askForPermissions()
        handleFetchData()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure koin dependency for main activity.
     */
    private fun configureKoinDependency() {
        snackBarHelper = get { parametersOf(this) }
        dialogHelper = get { parametersOf(this) }
    }

    /**
     * Ask for permission at first application start.
     */
    @SuppressLint("InlinedApi")
    private fun askForPermissions() {// Seems to do nothing...
        val isOreoOrUpper = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        val permissions = mutableListOf(ACCESS_NETWORK_STATE, INTERNET, RECEIVE_BOOT_COMPLETED)
        if (isOreoOrUpper) permissions.add(REQUEST_COMPANION_USE_DATA_IN_BACKGROUND)

        val message = getString(
            if (isOreoOrUpper) R.string.activity_first_start_ask_permissions_oreo
            else R.string.activity_first_start_ask_permissions
        )

        if (!hasPermissions(this, *permissions.toTypedArray())) {
            requestPermissions(
                this,
                message,
                RC_PERMS,
                *permissions.toTypedArray()
            )
        }
    }

    /**
     * Set refresh data and notification alarms at first apk start.
     */
    private fun setAlarmAtFirstStart() {
        startAlarm(this, getAlarmTime(prefs.getInt(REFRESH_TIME, REFRESH_TIME_DEFAULT)), UPDATE_DATA)
        startAlarm(this, getAlarmTime(prefs.getInt(NOTIFICATION_TIME, NOTIFICATION_TIME_DEFAULT)), NOTIFICATION)
    }

    // --------------------
    // METHODS OVERRIDE
    // --------------------

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
        // Calling the appropriate method after permissions result
        handlePermissionsResult(requestCode, grantResults)
    }

    // --------------
    // DATA
    // --------------

    /**
     * Handle fetch data for application start, set needed sources data,
     * fetch articles and set alarms, data for automatic refresh, and for notifications.
     */
    private fun handleFetchData() = lifecycleScope.launch(Dispatchers.Main) {
        checkConnectivity()
        fetchSources()
        //  save state of each download to know and retry after
        ideNewsRepository.createSourcesForFirstStart()
        ideNewsRepository.fetchRssArticles()
        setAlarmAtFirstStart()
        isFirstTime = false
//        showFirstStart(false)
//        snackBarHelper.closeSnackBar()
        ideNewsRepository.fetchCategories() // Do in main on result ok
        fetchFinish()
    }

    private suspend fun fetchSources() = ideNewsRepository.createSourcesForFirstStart()

    /**
     * Retry to fetch data.
     */
    override fun retryFetchData() { handleFetchData() }

    // --------------
    // ACTION
    // --------------

    @Suppress("Unused_parameter")
    fun onClickInfo(v: View) {
        startShowImagesActivity()
    }

    /**
     * Close the application.
     */
    override fun closeApplication() {
        setResult(RESULT_CANCELED, Intent(this, MainActivity::class.java))
        finishAffinity()
    }

    // --------------
    // ACTIVITY
    // --------------

    /**
     * Start Show images activity.
     */
    @Suppress("Unchecked_cast")
    private fun startShowImagesActivity() =
        ShowImagesActivity.routeFromActivity(this, WHO_OWNS_WHAT as ArrayList<Any>, 0)
        // TODO use main to open show image and clean error so

    // -----------------
    // UTILS
    // -----------------

    /**
     * Handle permissions result, after user allow permissions or not.
     *
     * @param requestCode Code of the request.
     * @param grantResults the permission result.
     */
    private fun handlePermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == RC_PERMS &&
            (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED))

            Toast.makeText(
                this,
                getString(R.string.activity_first_start_toast_perm_refused),
                Toast.LENGTH_SHORT
            ).show()
    }

    /**
     * Check the connectivity before try to download. If no connection display message
     * and prompt user.
     */
    private fun checkConnectivity() {
        if (!isInternetAvailable(baseContext)) dialogHelper.showDialog(CONNEXION)
    }

    /**
     * Fetch properly finish, so send result to the parent activity, and finish this one.
     */
    private fun fetchFinish() {
        setResult(RESULT_OK, Intent(this, MainActivity::class.java))
        finishAffinity()
    }
}