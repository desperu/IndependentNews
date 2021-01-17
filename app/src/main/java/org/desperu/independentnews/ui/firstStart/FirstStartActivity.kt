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
import kotlinx.android.synthetic.main.activity_first_start.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.desperu.independentnews.R
import org.desperu.independentnews.anim.AnimHelper.alphaViewAnimation
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
    private var resultOk = false

    /** Used to handle first apk start. */
    private val prefs: SharedPreferences
        get() = getSharedPreferences(INDEPENDENT_NEWS_PREFS, Context.MODE_PRIVATE)
    private var isFirstTime: Boolean
        get() = prefs.getBoolean(IS_FIRST_TIME, FIRST_TIME_DEFAULT)
        set(value) = prefs.edit { putBoolean(IS_FIRST_TIME, value) }

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_first_start

    override fun configureDesign() {
        configureKoinDependency()
        askForPermissions()
        handleFetchData()
        animateViews()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure koin dependency for main activity.
     */
    private fun configureKoinDependency() {
        get<FirstStartInterface> { parametersOf(this) }
        snackBarHelper = get { parametersOf(this) }
        dialogHelper = get { parametersOf(this) }
    }

    /**
     * Ask for permission at first application start.
     */
    @SuppressLint("InlinedApi")
    private fun askForPermissions() { // Seems to do nothing...
        val isOreoOrUpper = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
        val permissions = mutableListOf(ACCESS_NETWORK_STATE, INTERNET, RECEIVE_BOOT_COMPLETED, WAKE_LOCK)
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
        if (checkConnectivity()) {

            resultOk = false
            fetchData { ideNewsRepository.createSourcesForFirstStart() }.join() // Wait job finish to handle result
            if (resultOk) {
                //  save state of each download to know and retry after

                resultOk = false
                fetchData { ideNewsRepository.fetchRssArticles() }.join()
                if (resultOk) {
                    setAlarmAtFirstStart()
                    isFirstTime = false
                    firstStartFinish(RESULT_OK)
                }
            }
        }
    }

    /**
     * Fetch data wrapper in coroutine, to await complete from parent Job,
     * and store result to handle it.
     *
     * @param block the suspend block to execute.
     *
     * @return the job that is used to run the fetch functions.
     */
    private suspend inline fun fetchData(
        crossinline block: suspend () -> List<Long>
    ): Job = lifecycleScope.launch(Dispatchers.Main) {

        resultOk = block().isNotEmpty()
        if (!resultOk)
            dialogHelper.showDialog(FIRST_START_ERROR)
    }

    // --------------
    // ACTION
    // --------------

    @Suppress("Unused_parameter")
    fun onClickInfo(v: View) { startShowImagesActivity() }

    /**
     * Retry to fetch data.
     */
    override fun retryFetchData() { handleFetchData() }

    /**
     * Close the application.
     */
    override fun closeApplication() { firstStartFinish(RESULT_CANCELED) }

    // --------------
    // ACTIVITY
    // --------------

    /**
     * Start Show images activity.
     */
    @Suppress("Unchecked_cast")
    private fun startShowImagesActivity() =
        ShowImagesActivity.routeFromActivity(this, WHO_OWNS_WHAT as ArrayList<Any>, 0)

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
     *
     * @return true if a connexion is available, false otherwise.
     */
    private fun checkConnectivity(): Boolean =
        if (isInternetAvailable(baseContext)) {
            true
        } else {
            dialogHelper.showDialog(CONNEXION_START)
            false
        }

    /**
     * First Start finish, so send result to the parent activity, and finish this one.
     */
    private fun firstStartFinish(resultCode: Int) {
        setResult(resultCode, Intent(this, MainActivity::class.java))
        finish()
    }

    // -----------------
    // ANIMATION
    // -----------------

    /**
     * Animate views for better user experience.
     */
    private fun animateViews() {
        alphaViewAnimation(listOf(first_start_text), 1000L, true)
        alphaViewAnimation(listOf(first_start_who_owns_what), 3000L, true)
        alphaViewAnimation(listOf(first_start_text), 5000L, false)
        alphaViewAnimation(listOf(first_start_text_fetch_time), 10000L, true)
//        (first_start_app_logo.drawable as Animatable).start()
    }
}