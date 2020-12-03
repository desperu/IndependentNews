package org.desperu.independentnews.helpers

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.postOnAnimationDelayed
import com.google.android.material.behavior.SwipeDismissBehavior
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.desperu.independentnews.R
import org.desperu.independentnews.service.ResourceService
import org.desperu.independentnews.ui.main.MainInterface
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
}

/**
 * Implementation of the SnackBarHelper which use an Activity instance
 * to display message into the snack bar.
 *
 * @property activity the Activity instance used to display the message.
 * @property mainInterface the
 *
 * @constructor Instantiate a new SnackBarHelperImpl.
 *
 * @param activity the Activity instance used to display the message, to set.
 */
class SnackBarHelperImpl(private val activity: AppCompatActivity) : SnackBarHelper, KoinComponent {

    // FOR DATA
    private val mainInterface: MainInterface = get()
    private val resource: ResourceService = get()
    private var snackBar: Snackbar? = null
    private var hasError = false
    private var errorMessage = mutableListOf<String>()

    /**
     * Show the message into the snack bar for the given key and data.
     *
     * @param snackKey the snack bar key to display the corresponding message.
     * @param data the data list to display to the user into the message.
     */
    override suspend fun showMessage(snackKey: Int, data: List<String>) = withContext(Dispatchers.Main) {
        if (snackBar == null)
            initSnackBar(snackKey, data)
        else
            updateSnackBar(snackKey, data)

        handleButton(snackKey)
        handleError(snackKey, data)
        snackBar?.show()
    }

    /**
     * Init the snack bar for the given key and data.
     *
     * @param snackKey the snack bar key to display the corresponding message.
     * @param data the data list to display to the user into the message.
     */
    private fun initSnackBar(snackKey: Int, data: List<String>) {
        snackBar = Snackbar.make(
            activity.coordinator_layout, // TODO work only for main activity, use when switch !!!
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

    /**
     * Show error snackbar message if there's one.
     * 
     * @param snackKey the snack bar key to display the corresponding message.
     */
    private fun showError(snackKey: Int) {
        if (hasError && (snackKey == END_NOT_FIND || snackKey == END_FIND)) {
            snackBar?.view?.postOnAnimationDelayed(getDuration(snackKey) - 500L) {
                mainInterface.mainLifecycleScope.launch {
                    showMessage(END_ERROR, listOf(concatenateStringFromMutableList(errorMessage)))
                    errorMessage.clear()
                }
            }
        }
    }

    // --------------
    // UTILS
    // --------------

    /**
     * Returns the corresponding message for the given snack key and data.
     *
     * @param snackKey the snack bar key to display the corresponding message.
     * @param data the data list to display to the user into the message.
     *
     * @return the corresponding message for the given snack key and data.
     */
    private fun getMessage(snackKey: Int, data: List<String>) = when(snackKey) {
        SEARCH -> resource.getString(R.string.snack_bar_message_search_articles, data[0])
        FIND -> resource.getString(R.string.snack_bar_message_find_articles, data[0], data[1])
        FETCH -> resource.getString(
            R.string.snack_bar_message_fetch_list,
            data[0],
            data[1],
            data[2]
        )
        ERROR -> resource.getString(R.string.snack_bar_message_error, data[0])
        END_FIND -> resource.getString(R.string.snack_bar_message_end, data[0])
        END_NOT_FIND -> resource.getString(R.string.snack_bar_message_not_find)
        END_ERROR -> resource.getString(R.string.snack_bar_message_error, data[0])
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
     * Handle snack bar button, show, configure and dismiss, depends of the snack key value.
     *
     * @param snackKey the snack bar key to display the corresponding message.
     */
    private fun handleButton(snackKey: Int) {
        when(snackKey) {
            END_FIND -> snackBar
                ?.setAction(R.string.snack_bar_button_show) { mainInterface.showNewArticles() } // TODO for perfect anim use smooth scroll and DiffUtils
                ?.setActionTextColor(ResourcesCompat.getColor(activity.resources, android.R.color.holo_green_dark, null))

            END_NOT_FIND -> snackBar
                ?.setAction(R.string.snack_bar_button_close) { snackBar?.dismiss() }
                ?.setActionTextColor(ResourcesCompat.getColor(activity.resources, R.color.list_item_bg_collapsed, null))

            END_ERROR -> snackBar
                ?.setAction(R.string.snack_bar_button_retry) { mainInterface.refreshData(); snackBar = null }
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
}