package org.desperu.independentnews.ui.showArticle.fabsMenu

import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.os.postDelayed
import androidx.core.view.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.leinardi.android.speeddial.FabWithLabelView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView.OnActionSelectedListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_show_article.*
import kotlinx.android.synthetic.main.layout_fabs_menu.*
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.*
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.desperu.independentnews.ui.showArticle.design.ScrollHandlerInterface
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.FabsMenuUtils.getSubFabIcon
import org.desperu.independentnews.utils.FabsMenuUtils.getSubFabId
import org.desperu.independentnews.utils.FabsMenuUtils.getSubFabLabel
import org.desperu.independentnews.utils.SourcesUtils.getSourceTextZoom
import org.desperu.independentnews.views.MySpeedDialView
import org.desperu.independentnews.views.MySpeedDialView.OnAnimationEndListener
import org.desperu.independentnews.views.MySpeedDialView.OnAnimationStartListener
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * Fabs Menu class that is used to handle the Speed Dial View.
 * Customize animation, handle actions.
 *
 * @property showArticleInterface   the interface of the show article activity.
 * @property scrollHandler          the scroll handler interface access.
 * @property prefs                  the shared preferences interface access.
 * @property activity               the activity that owns this Fabs Menu.
 *
 * @constructor Instantiate a new FabsMenu.
 *
 * @author Desperu.
 */
class FabsMenu : KoinComponent{

    // FOR COMMUNICATION
    private val showArticleInterface: ShowArticleInterface = get()
    private val scrollHandler: ScrollHandlerInterface = get()
    private val prefs: SharedPrefService = get()
    private val activity = showArticleInterface.activity

    // FOR UI
    private val speedDialView: MySpeedDialView by bindView(activity, R.id.fabs_menu)
    private val customOverlay: View by bindView(activity, R.id.fabs_custom_overlay)
    private val fabBackgroundColor by bindColor(activity, R.color.colorPrimaryDark)
    private val fabImageTintColor by bindColor(activity, R.color.subtitle_color)
//    private val fabLabelColor by bindColor(activity, R.color.filter_pill_color)
//    private val fabLabelBackgroundColor by bindColor(activity, R.color.toolbar_title_color)
    private val starColor by bindColor(activity, R.color.colorStar)
    private val pauseColor by bindColor(activity, R.color.colorDark)

    init {
        configureSpeedDial()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure the Speed Dial View, sub Fab, action and custom animation.
     */
    private fun configureSpeedDial() {
        configureSubFabs()
        configureActionListener()
        configureAnimationListener()
    }

    /**
     * Configure all sub fabs as needed, add action item for each sub fab key.
     */
    private fun configureSubFabs() {
        subFabList.forEach { speedDialView.addActionItem(createActionItem(it)) }
    }

    /**
     * Create an action item for the speed dial.
     * Configure the id, icon, colors, label and label clickable.
     *
     * @param subFabKey the key of the sub fab to create.
     *
     * @return the created action item.
     */
    private fun createActionItem(subFabKey: Int): SpeedDialActionItem =
        SpeedDialActionItem.Builder(getSubFabId(subFabKey), getSubFabIcon(subFabKey))
            .setFabBackgroundColor(fabBackgroundColor)
            .setFabImageTintColor(fabImageTintColor)
            .setLabel(getSubFabLabel(subFabKey))
//            .setLabelColor(fabLabelColor)
//            .setLabelBackgroundColor(fabLabelBackgroundColor)
            .setLabelClickable(true)
            .create()

    /**
     * Configure the Action Listener to handle user actions.
     */
    private fun configureActionListener() {
        speedDialView.setOnActionSelectedListener(OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.fab_minus_text -> updateTextSize(false)
                R.id.fab_up_text -> updateTextSize(true)
                R.id.fab_star -> onClickStar(actionItem.id)
                R.id.fab_pause -> onClickPause(actionItem.id)
                R.id.fab_home -> onClickHome()
            }

            // true to keep the Speed Dial open, false otherwise.
            return@OnActionSelectedListener true // false will close it without animation
        })
    }

    /**
     * Configure the animation start listener, to update sub fabs and customize animation.
     */
    private fun configureAnimationListener() {
        speedDialView.setOnAnimationStartListener(object : OnAnimationStartListener {

            override fun onPreStart(isOpen: Boolean) {
                handleSubFabs()
            }

            override fun onStart(isOpen: Boolean) {
                // Customize open, close and speed dial overlay animations.
                customizeFabLabelAnim()
                OverlayAnim().customizeOverlayAnim(isOpen)
            }
        })
    }

    /**
     * Handle sub fabs, needed for favorite and paused (user article),
     * show only if it's an article displayed.
     * Update their state too, switch the icon tint color.
     */
    private fun handleSubFabs() {
        val isArticle = (activity.viewModel.article.get()?.id ?: 0L) != 0L

        speedDialView.apply {
            if (isArticle) {
                addActionItem(createActionItem(SUB_FAB_STAR), subFabList.indexOf(SUB_FAB_STAR))
                addActionItem(createActionItem(SUB_FAB_PAUSE), subFabList.indexOf(SUB_FAB_PAUSE))
            } else {
                removeActionItemById(R.id.fab_star)
                removeActionItemById(R.id.fab_pause)
            }
        }

        updateSubFabState(R.id.fab_star, activity.viewModel.isFavorite.get())
        updateSubFabState(R.id.fab_pause, activity.viewModel.isPaused.get())
    }

    // --------------
    // ACTION
    // --------------

    /**
     * Update the article text size, immediately in the web view, and in the shared preferences.
     *
     * @param toUp true to up text size, false to minus.
     */
    private fun updateTextSize(toUp: Boolean) {
        // Start to low the overlay transparency
        customOverlay.alpha = 0.6f

        Handler(Looper.getMainLooper()).postDelayed(500L) {
            // Save the scroll Y percent
            val yPercent = scrollHandler.getScrollYPercent()

            // Calculus new text zoom and ratio
            val wVSettings = activity.webView.settings
            val origTextZoom = wVSettings.textZoom
            val newTextZoom = origTextZoom + if (toUp) 10 else -10
            val textRatio = newTextZoom.toFloat() / origTextZoom.toFloat()
            val correct = if (toUp) 0.9f else 1.1f

            // Correct the scroll position
            activity.webView.doOnNextLayout {
                scrollHandler.scrollTo(yPercent * textRatio * correct)
            }

            // Update text size
            wVSettings.textZoom = newTextZoom

            // Calculus the real text zoom, take care of specific source text zoom.
            val actualUrl = showArticleInterface.fragmentInterface.mWebViewClient?.actualUrl ?: ""
            val sourceName = activity.viewModel.article.get()?.source?.name ?: ""
            val realTextZoom = newTextZoom - getSourceTextZoom(actualUrl, sourceName)
            // Store the new value in the preferences.
            prefs.getPrefs().edit().putInt(TEXT_SIZE, realTextZoom).apply()
        }
    }

    /**
     * On click start, update article state in database, and show anim for switch state
     * of the article.
     *
     * @param id the unique identifier of the clicked sub fab.
     */
    private fun onClickStar(@IdRes id: Int) {
        val subFab = speedDialView.getFabWithLabelViewById(id)?.fab
        val isFavorite = isStateEnabled(subFab)
        val newColor = if (!isFavorite) starColor else fabImageTintColor

        // Switch state
        activity.viewModel.updateFavorite()
        subFab?.supportImageTintList = ColorStateList.valueOf(newColor)

        // Display action to user, animation or toast
        if (!isFavorite) IconAnim().getIconAnim(id, subFab).start()
        else Toast.makeText(activity, activity.getString(R.string.sub_fab_toast_remove_favorite), Toast.LENGTH_SHORT).show()

        // To show the sub fab icon color switch
        Handler(Looper.getMainLooper()).postDelayed(100L) { speedDialView.close() }
    }

    /**
     * On click pause, update article state in database, and show anim for switch state
     * of the article.
     *
     * @param id the unique identifier of the clicked sub fab.
     */
    private fun onClickPause(@IdRes id: Int) {
        val subFab = speedDialView.getFabWithLabelViewById(id)?.fab
        val isPaused = isStateEnabled(subFab)
        val newColor = if (!isPaused) pauseColor else fabImageTintColor
        val yPercent = scrollHandler.getScrollYPercent()
        val textRatio = activity.webView.getTextRatio()

        // Switch state
        activity.viewModel.updatePaused(yPercent / textRatio)
        subFab?.supportImageTintList = ColorStateList.valueOf(newColor)

        // Display action to user, animation or toast
        if (!isPaused) IconAnim().getIconAnim(id, subFab).start()
        else Toast.makeText(activity, activity.getString(R.string.sub_fab_toast_remove_paused), Toast.LENGTH_SHORT).show()

        // To show the sub fab icon color switch
        Handler(Looper.getMainLooper()).postDelayed(100L) { speedDialView.close() }
    }

    /**
     * On click home, back to parent activity with animation.
     */
    private fun onClickHome() {
        speedDialView.setOnAnimationEndListener(object : OnAnimationEndListener {

            override fun onEnd() {
                // Need to support image change transition in SowArticleActivity.
                activity.supportFinishAfterTransition()
            }
        })

        speedDialView.close() // To close the Speed Dial with animation
    }

    // --------------
    // ANIMATIONS
    // --------------

    /**
     * Customize the Fab With Label animation.
     * Work with the anim resource : sd_scale_fade_and_translate_in.
     */
    private fun customizeFabLabelAnim() {
        speedDialView.descendants.forEach {
            if (it is FabWithLabelView)
                it.run {
                    val startOffset = (fab.animation.startOffset * 1.5).toLong()
                    fab.animation.startOffset = startOffset
                    labelBackground.animation.startOffset = startOffset
                }
        }
    }

    // --------------
    // UI
    // --------------

    /**
     * Update the sub fab icon color and text label, for the given id and state, enabled/disabled.
     *
     * @param id            the unique identifier of the sub fab.
     * @param isEnabled     true if is enabled, false otherwise.
     */
    private fun updateSubFabState(@IdRes id: Int, isEnabled: Boolean) {
        val fabWithLabelView = speedDialView.getFabWithLabelViewById(id)
        val isFavoriteFab = id == R.id.fab_star
        val enabledColor = if (isFavoriteFab) starColor else pauseColor
        val enabledKey = if (isFavoriteFab) SUB_FAB_REMOVE_STAR else SUB_FAB_REMOVE_PAUSE
        val disabledKey = if (isFavoriteFab) SUB_FAB_STAR else SUB_FAB_PAUSE


        fabWithLabelView?.fab?.supportImageTintList =
            ColorStateList.valueOf(
                if (isEnabled) enabledColor
                else fabImageTintColor
            )

        fabWithLabelView?.labelBackground.findView<TextView>()?.text =
            getSubFabLabel(
                if (isEnabled) enabledKey
                else disabledKey
            )
    }

    // --------------
    // UTILS
    // --------------

    /**
     * Returns true if the sub fab icon color is not default color,
     * the sub fab is state enabled.
     *
     * @param subFab the sub fab to determinate state.
     *
     * @return true if is enabled, false otherwise.
     */
    private fun isStateEnabled(subFab: FloatingActionButton?): Boolean =
        subFab?.supportImageTintList?.defaultColor != fabImageTintColor
}