package org.desperu.independentnews.ui.showArticle.fabsMenu

import android.view.View
import android.widget.Toast
import androidx.core.view.*
import com.leinardi.android.speeddial.FabWithLabelView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView.OnActionSelectedListener
import kotlinx.android.synthetic.main.activity_show_article.*
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindColor
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.ui.showArticle.ShowArticleInterface
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.FabsMenuUtils.getSubFabIcon
import org.desperu.independentnews.utils.FabsMenuUtils.getSubFabId
import org.desperu.independentnews.utils.FabsMenuUtils.getSubFabLabel
import org.desperu.independentnews.views.MySpeedDialView
import org.desperu.independentnews.views.MySpeedDialView.OnAnimationListener
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * Fabs Menu class that is used to handle the Speed Dial View.
 * Customize animation, handle actions.
 *
 * @property showArticleInterface   the interface of the show article activity.
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
    private val prefs: SharedPrefService = get()
    private val activity = showArticleInterface.activity

    // FOR UI
    private val speedDialView: MySpeedDialView by bindView(activity, R.id.fabs_menu)
    private val customOverlay: View by bindView(activity, R.id.fabs_custom_overlay)
    private val fabBackgroundColor by bindColor(activity, R.color.colorPrimaryDark)
    private val fabImageTintColor by bindColor(activity, R.color.subtitle_color)
    private val fabLabelColor by bindColor(activity, R.color.filter_pill_color)
    private val fabLabelBackgroundColor by bindColor(activity, R.color.toolbar_title_color)

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
        customizeAnimation()
    }

    /**
     * Configure all sub fabs as needed.
     */
    private fun configureSubFabs() { subFabList.forEach { addSubFab(it) } }

    /**
     * Add sub fab to the Speed Dial View.
     *
     * @param subFabKey the key of the sub fab to configure and add.
     */
    private fun addSubFab(subFabKey: Int) {
        speedDialView.addActionItem(
            SpeedDialActionItem.Builder(getSubFabId(subFabKey), getSubFabIcon(subFabKey))
                .setFabBackgroundColor(fabBackgroundColor)
                .setFabImageTintColor(fabImageTintColor)
                .setLabel(getSubFabLabel(subFabKey))
//                .setLabelColor(fabLabelColor)
//                .setLabelBackgroundColor(fabLabelBackgroundColor)
                .setLabelClickable(true)
                .create()
        )
    }

    /**
     * Configure the Action Listener to handle user actions.
     */
    private fun configureActionListener() {
        speedDialView.setOnActionSelectedListener(OnActionSelectedListener { actionItem ->
            when (actionItem.id) {

                R.id.fab_minus_text -> updateTextSize(false)

                R.id.fab_up_text -> updateTextSize(true)

                R.id.fab_star -> {
                    Toast.makeText(
                        activity,
                        "No label action clicked!\nClosing with animation",
                        Toast.LENGTH_SHORT
                    ).show()
                    speedDialView.close() // To close the Speed Dial with animation
                }

                R.id.fab_pause -> {

                    speedDialView.close()
                }

                R.id.fab_home -> {

                    speedDialView.close()
                }
            }

            // true to keep the Speed Dial open, false otherwise.
            return@OnActionSelectedListener true // false will close it without animation
        })
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

        val wVSettings = showArticleInterface.activity.web_view.settings
        wVSettings.textZoom += if (toUp) 10 else -10

        // Store the new value in the preferences.
        prefs.getPrefs().edit().putInt(TEXT_SIZE, wVSettings.textZoom).apply()
    }

    // --------------
    // ANIMATIONS
    // --------------

    /**
     * Customize open and close animation of the speed dial view and the speed dial overlay.
     */
    private fun customizeAnimation() {
        speedDialView.setOnAnimationListener(object : OnAnimationListener {

            override fun onToggle(isOpen: Boolean) {
                customizeFabLabelAnim()
                OverlayAnim().customizeOverlayAnim(isOpen)
            }
        })
    }

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
}