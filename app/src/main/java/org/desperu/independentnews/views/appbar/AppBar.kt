package org.desperu.independentnews.views.appbar

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.appbar.AppBarLayout
import org.desperu.independentnews.extension.design.findSuitableScrollable

/**
 * A custom [AppBarLayout] that support collapse and expand animations implementation.
 * Works with [ToolbarBehavior] to perform animations.
 */
class AppBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppBarLayout(context, attrs, defStyleAttr) {

    // FOR DATA
    private lateinit var toolbarBehavior: ToolbarBehavior
    private var suitableScroll: View? = null

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        configureBehavior()
        configureOnClick()
        configureOnTouch()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure App Bar behavior.
     */
    private fun configureBehavior() {
        toolbarBehavior = ToolbarBehavior()
        (layoutParams as CoordinatorLayout.LayoutParams).behavior = toolbarBehavior
    }

    /**
     * Configure on click listener to expand app bar.
     */
    private fun configureOnClick() {
        setOnClickListener { toolbarBehavior.expandAppBar(true) }
    }

    /**
     * Configure on touch event listener to finish app bar anim.
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun configureOnTouch() {
        suitableScroll = findSuitableScrollable()
        suitableScroll?.setOnTouchListener { _, ev ->
            if (ev.action == MotionEvent.ACTION_UP)
                toolbarBehavior.finishAnimation()
            false
        }
    }

    // -----------------
    // UPDATE
    // -----------------

    /**
     * Update on touch listener when the fragment change.
     */
    internal fun updateOnTouch() {
        if (suitableScroll?.isShown == false) configureOnTouch()
    }

    /**
     * Show App Bar Icon for the given list.
     */
    internal fun showAppBarIcon(iconList: List<Int>) {
        iconList.forEach { findViewById<View>(it).visibility = View.VISIBLE }
    }

    /**
     * Synchronize the app bar size with the size of the previous activity.
     *
     * @param toExpand  true to expand, false to collapse.
     * @param appBar    the app bar layout to synchronize.
     */
    internal fun syncAppBarSize(appBar: AppBarLayout, toExpand: Boolean) {
        toolbarBehavior.syncAppBarSize(appBar, toExpand)
    }

    // --- GETTERS ---

    /**
     * Returns true if the app bar is expanded, false if is collapsed.
     */
    internal val isExpanded: Boolean get() = toolbarBehavior.isExpanded
}