package org.desperu.independentnews.helpers

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.isDigitsOnly
import androidx.core.view.postOnAnimationDelayed
import androidx.core.view.updateLayoutParams
import androidx.core.widget.ContentLoadingProgressBar
import com.google.android.material.behavior.SwipeDismissBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_first_start.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.desperu.independentnews.R
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.ui.firstStart.FirstStartActivity
import org.desperu.independentnews.ui.main.MainActivity
import org.desperu.independentnews.ui.main.MainInterface
import org.desperu.independentnews.ui.main.NEW_ARTICLES
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.concatenateStringFromMutableList
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * SnackBarHelper witch provide functions to display messages into the snack bar.
 */
interface SnackBarHelper {

    /**
     * Show the message into the snack bar for the given key and data.
     *
     * @param snackKey the snack bar key to display the corresponding message.
     * @param data the data list to display to the user into the message.
     */
    suspend fun showMessage(snackKey: Int, data: List<String>): Unit?

    /**
     * Used to hide snack bar during loading.
     */
    var userDismiss: Boolean
}

/**
 * Implementation of the SnackBarHelper which use an Activity instance
 * to display message into the snack bar.
 *
 * @property activity the Activity instance used to display the message.
 * @property mainInterface the interface of main activity.
 *
 * @constructor Instantiate a new SnackBarHelperImpl.
 *
 * @param activity the Activity instance used to display the message, to set.
 */
class SnackBarHelperImpl(private val activity: AppCompatActivity) : SnackBarHelper, KoinComponent {

    // FOR DATA
    private val mainInterface: MainInterface = get()
    private val prefs: SharedPrefService = get()
    private val resources = activity.resources
    private var snackBar: Snackbar? = null
    private var snackKey = 0
    override var userDismiss = false
    private var hasError = false
    private var errorMessage = mutableListOf<String>()
    private val isFirstStart get() = prefs.getPrefs().getBoolean(IS_FIRST_TIME, true)
    private var loadingBar: ContentLoadingProgressBar? = null


    // --------------
    // CALL FUNCTION
    // --------------

    /**
     * Show the message into the snack bar for the given key and data.
     *
     * @param snackKey the snack bar key to display the corresponding message.
     * @param data the data list to display to the user into the message.
     */
    override suspend fun showMessage(snackKey: Int, data: List<String>) = withContext(Dispatchers.Main) {
        handleUserDismiss(snackKey)
        if (userDismiss && snackKey < END_FIND) return@withContext

        if (snackBar == null)
            initSnackBar(snackKey, data)
        else
            updateSnackBar(snackKey, data)

        handleUi(snackKey)
        handleButton(snackKey)
        handleError(snackKey, data)
        sendResult(snackKey, data)
        snackBar?.show()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Init the snack bar for the given key and data.
     *
     * @param snackKey the snack bar key to display the corresponding message.
     * @param data the data list to display to the user into the message.
     */
    private fun initSnackBar(snackKey: Int, data: List<String>) {
        snackBar = Snackbar.make(
            getRootView(),
            getMessage(snackKey, data),
            getDuration(snackKey)
        )
        snackBar?.apply {
            behavior = BaseTransientBottomBar.Behavior()
            behavior.setSwipeDirection(SwipeDismissBehavior.SWIPE_DIRECTION_ANY)
            view.addOnAttachStateChangeListener(listener)
        }
    }

    /**
     * Listener to handle dismiss user action, use this because
     * [SwipeDismissBehavior.OnDismissListener] not received call.
     * It's the listener of the behavior for the [Snackbar].
     */
    private val listener = object : View.OnAttachStateChangeListener {
        override fun onViewAttachedToWindow(v: View?) {}

        override fun onViewDetachedFromWindow(v: View?) {
            snackBar = null
            if (snackKey < END_FIND) userDismiss = true
        }
    }

    /**
     * Update the snack bar with the new message, the duration, and add button if needed.
     *
     * @param snackKey the snack bar key to display the corresponding message.
     * @param data the data list to display to the user into the message.
     */
    private fun updateSnackBar(snackKey: Int, data: List<String>) {
        snackBar?.setText(getMessage(snackKey, data))
        snackBar?.duration = getDuration(snackKey)
    }

    // --------------
    // RESULT
    // --------------

    /**
     * Show error snackbar message if there's one.
     * 
     * @param snackKey the snack bar key to display the corresponding message.
     */
    private fun showError(snackKey: Int) {
        if (hasError && (snackKey == END_FIND || snackKey == END_NOT_FIND)) {
            snackBar?.view?.postOnAnimationDelayed(getDuration(snackKey) - PRE_DELAY) {
                mainInterface.mainLifecycleScope.launch {
                    showMessage(END_ERROR, listOf(concatenateStringFromMutableList(errorMessage)))
                    errorMessage.clear()
                }
            }
        }
    }

    /**
     * Send result to the parent activity.
     *
     * @param snackKey the snack bar key to handle send result.
     */
    private fun sendResult(snackKey: Int, data: List<String>) {
        if (snackKey > ERROR) {
            val newArticles =
                if (data.isNotEmpty() && data[0].isDigitsOnly()) data[0].toInt()
                else 0

            if (newArticles > 0)
                activity.intent.putExtra(NEW_ARTICLES, newArticles)
        }
    }

    // --------------
    // UTILS
    // --------------

    /**
     * Returns the view used as parent to display the snack bar.
     *
     * @return the view used as parent to display the snack bar.
     */
    private fun getRootView() = when (activity) {
        is FirstStartActivity -> activity.first_start_root
        is MainActivity -> activity.coordinator_layout
        else -> activity.coordinator_layout
    }

    /**
     * Returns the corresponding message for the given snack key and data.
     *
     * @param snackKey the snack bar key to display the corresponding message.
     * @param data the data list to display to the user into the message.
     *
     * @return the corresponding message for the given snack key and data.
     */
    private fun getMessage(snackKey: Int, data: List<String>): String = when(snackKey) {
        SOURCE_FETCH -> resources.getString(R.string.snack_bar_message_source_fetch, data[0])
        SOURCE_ERROR -> resources.getString(R.string.snack_bar_message_source_error, data[0])
        SEARCH -> resources.getString(R.string.snack_bar_message_search_articles, data[0])
        FIND -> resources.getString(R.string.snack_bar_message_find_articles, data[0], data[1])
        FETCH -> resources.getString(
            R.string.snack_bar_message_fetch_list,
            data[0],
            data[1],
            data[2]
        )
        ERROR -> resources.getString(R.string.snack_bar_message_error, data[0])
        END_FIND -> resources.getString(R.string.snack_bar_message_end, data[0])
        END_NOT_FIND -> resources.getString(R.string.snack_bar_message_not_find)
        END_ERROR -> resources.getString(R.string.snack_bar_message_error, data[0])
        else -> throw IllegalArgumentException("Snack key not found : $snackKey")
    }

    /**
     * Returns the snackbar duration, depends of the snack key value.
     *
     * @param snackKey the snack bar key to display the corresponding message.
     */
    private fun getDuration(snackKey: Int) = when(snackKey) {
        in arrayOf(END_FIND, END_ERROR) -> 4000
        END_NOT_FIND -> 3000
        else -> Snackbar.LENGTH_INDEFINITE
    }

    /**
     * Handle user dismiss state, hide snackbar while fetching data not finish.
     *
     * @param snackKey the snack bar key to display the corresponding message.
     */
    private fun handleUserDismiss(snackKey: Int) {
        this.snackKey = snackKey
        if (userDismiss && snackKey >= END_FIND) userDismiss = false
    }

    /**
     * Handle snack bar button, show, configure and dismiss, depends of the snack key value.
     *
     * @param snackKey the snack bar key to display the corresponding message.
     */
    private fun handleButton(snackKey: Int) {
        if (!isFirstStart)
            when(snackKey) {
                END_FIND -> snackBar
                    ?.setAction(R.string.snack_bar_button_show) { mainInterface.showNewArticles() }
                    ?.setActionTextColor(ResourcesCompat.getColor(resources, android.R.color.holo_green_dark, null))

                END_NOT_FIND -> snackBar
                    ?.setAction(R.string.snack_bar_button_close) { snackBar?.dismiss() }
                    ?.setActionTextColor(ResourcesCompat.getColor(activity.resources, R.color.list_item_bg_collapsed, null))

                END_ERROR -> snackBar
                    ?.setAction(R.string.snack_bar_button_retry) { retry() }
                    ?.setActionTextColor(ResourcesCompat.getColor(activity.resources, android.R.color.holo_orange_dark, null))

                else -> snackBar?.setAction(null, null)
            }
    }

    /**
     * Handle refresh data error, to properly print if append.
     *
     * @param snackKey the snack bar key to display the corresponding message.
     * @param data the data list to display to the user into the message.
     */
    private fun handleError(snackKey: Int, data: List<String>) {
        showError(snackKey)
        when(snackKey) {
            ERROR -> {
                hasError = true
                errorMessage.addAll(data)
            }
            in arrayOf(END_FIND, END_NOT_FIND, END_ERROR) -> hasError = false
        }
    }

    /**
     * Retry to fetch data.
     */
    private fun retry() {
        mainInterface.refreshData()
//        loadingBar = null
        snackBar = null
    }

    // --------------
    // UI
    // --------------

    /**
     * Handle the ui of the snack bar, between first start and standard use.
     *
     * @param snackKey the snack bar key used to handle ui.
     */
    private fun handleUi(snackKey: Int) {
        if (isFirstStart) setBackgroundColor()

        showLoadingBar(snackKey) // Call before init to hide at good time
        if (loadingBar == null && snackKey < ERROR) initLoadingBar()
        if (snackKey > ERROR) loadingBar = null
    }

    /**
     * Set the background color for the view of the snack bar.
     */
    private fun setBackgroundColor() {
        snackBar?.view?.setBackgroundColor(
            ResourcesCompat.getColor(activity.resources, android.R.color.transparent, null)
        )
    }

    /**
     * Initialize the loading progress bar.
     */
    private fun initLoadingBar() {
        val snackText = snackBar?.view?.findViewById<View>(com.google.android.material.R.id.snackbar_text)
        val snackLayout = snackText?.parent as ViewGroup
        val snackView = LayoutInflater.from(activity).inflate(R.layout.loading_bar, snackLayout)
        loadingBar = snackView.findViewById(R.id.loading_bar)

        loadingBar?.updateLayoutParams<LinearLayout.LayoutParams> {
            gravity = Gravity.CENTER
        }
    }

    /**
     * Show or hide loading progress bar, depends of toShow value.
     *
     * @param snackKey the snack bar key to display loading bar.
     */
    private fun showLoadingBar(snackKey: Int) {
        if (snackKey > ERROR) loadingBar?.hide() else loadingBar?.show()
    }
}