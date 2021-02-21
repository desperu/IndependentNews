package org.desperu.independentnews.ui.showArticle

import android.graphics.Rect
import android.view.View
import android.view.ViewAnimationUtils.createCircularReveal
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.animation.doOnEnd
import androidx.core.view.*
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.leinardi.android.speeddial.FabWithLabelView
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.leinardi.android.speeddial.SpeedDialView.OnActionSelectedListener
import kotlinx.android.synthetic.main.activity_show_article.*
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindColor
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.utils.TEXT_SIZE
import org.desperu.independentnews.views.MySpeedDialView
import org.desperu.independentnews.views.MySpeedDialView.OnAnimationListener
import org.koin.core.KoinComponent
import org.koin.core.get
import kotlin.math.sqrt

class FabsMenu : KoinComponent{

    // FOR COMMUNICATION
    private val showArticleInterface: ShowArticleInterface = get()
    private val prefs: SharedPrefService = get()
    private val activity = showArticleInterface.activity

    // FOR UI
    private val speedDialView: MySpeedDialView by bindView(activity, R.id.fabs_menu)
    private val fabBackgroundColor by bindColor(activity, R.color.colorPrimaryDark)
    private val fabImageTintColor by bindColor(activity, R.color.subtitle_color)
    private val fabLabelColor by bindColor(activity, R.color.filter_pill_color)
    private val fabLabelBackgroundColor by bindColor(activity, R.color.toolbar_title_color)

    // FOR DATA
    private val subFabList = listOf(
        R.id.fab_action1,
        R.id.fab_action2,
        R.id.fab_action3,
        R.id.fab_action4,
        R.id.fab_action5
    )

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
    private fun configureSubFabs() {
        subFabList.forEach {
            addSubFab(it, null)
        }
    }

    /**
     * Add sub fab to the Speed Dial View.
     *
     * @param subFabKey the key of the sub fab to configure and add.
     */
    private fun addSubFab(@IdRes id: Int, subFabsMap: Map<Int, Pair<Int, Int>>?) {
        speedDialView.addActionItem(
            SpeedDialActionItem.Builder(id, R.drawable.ic_filter)
                .setFabBackgroundColor(fabBackgroundColor)
                .setFabImageTintColor(fabImageTintColor)
                .setLabel(activity.getString(R.string.fragment_today_articles))
                .setLabelColor(fabLabelColor)
                .setLabelBackgroundColor(fabLabelBackgroundColor)
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

                R.id.fab_action1 -> {
                    val vWSettings = showArticleInterface.activity.web_view.settings
                    vWSettings.textZoom -= 10
                    prefs.getPrefs().edit().putInt(TEXT_SIZE, vWSettings.textZoom).apply()
                    speedDialView.overlayLayout?.alpha = 0.6f
                }

                R.id.fab_action2 -> {
                    showArticleInterface.activity.web_view.settings.textZoom += 10
                }

                R.id.fab_action3, R.id.fab_action4, R.id.fab_action5 -> {
                    Toast.makeText(
                        activity,
                        "No label action clicked!\nClosing with animation",
                        Toast.LENGTH_SHORT
                    ).show()
                    speedDialView.close() // To close the Speed Dial with animation
                }
            }

            // true to keep the Speed Dial open, false otherwise.
            return@OnActionSelectedListener true // false will close it without animation
        })
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
                customizeOverlayAnim(isOpen)
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

    /**
     * Customize the speed dial overlay anim for API >= LOLLIPOP.
     *
     * @param isOpen true if the speed dial is open, false otherwise.
     */
    private fun customizeOverlayAnim(isOpen: Boolean) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            val overlay = speedDialView.overlayLayout
            val width = overlay?.width ?: speedDialView.width
            val height = overlay?.height ?: speedDialView.height
            val mainFabRect = speedDialView.mainFab.run { Rect().apply(::getGlobalVisibleRect) }

            //Simply use the diagonal of the view
            val finalRadius = sqrt((width * width + height * height).toDouble()).toFloat()
            val durationIdRes = if (!isOpen) R.integer.sd_open_animation_duration
                                else R.integer.sd_close_animation_duration

            // Clear original animation and prepare view for custom anim.
            if (isOpen) overlay?.let { ViewCompat.animate(it).cancel() }
            overlay?.alpha = 1f
            overlay?.visibility = View.VISIBLE

            val anim = createCircularReveal(
                overlay,
                mainFabRect.centerX(),
                mainFabRect.centerY(),
                if (!isOpen) 0f else finalRadius,
                if (!isOpen) finalRadius else 0f
            )

            anim.interpolator = FastOutSlowInInterpolator()
            anim.duration = activity.resources.getInteger(durationIdRes).toLong()
            if (isOpen) anim.duration += subFabList.size * 20
            anim.doOnEnd { if (isOpen) overlay?.visibility = View.GONE }
            anim.start()
        }
    }
}