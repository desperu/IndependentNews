package org.desperu.independentnews.views.appbar

import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.descendants
import com.google.android.material.appbar.AppBarLayout
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.findSuitableScrollable
import org.desperu.independentnews.extension.design.getValueAnimator

/**
 * This behavior animates the toolbar (frame appbar_container) and it's elements
 * (toolbarTitle and icons) as the child scrollable owner scrolls.
 */
class ToolbarBehavior : CoordinatorLayout.Behavior<AppBarLayout>() {

    // FOR DATA
    private lateinit var toolbar: FrameLayout
    private lateinit var toolbarTitle: View
    private val iconList = mutableListOf<View>()
    private var toolbarOriginalHeight: Float = -1f
    private var toolbarCollapsedHeight: Float = -1f
    private var viewsSet = false
    private var minScale = 0.6f
    private var isShrinking = false
    private var isExpanding = false

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Set the required view variables. Only accessed once because of the viewsSet variable.
     */
    private fun getViews(child: AppBarLayout) {
        if (viewsSet) return
        viewsSet = true

        toolbar = child.findViewById(R.id.appbar_container)
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title)
        toolbar.descendants.forEach {
            if (it is ImageView)
                iconList.add(it)
        }

        toolbarOriginalHeight = toolbar.layoutParams.height.toFloat()
        toolbarCollapsedHeight = toolbarOriginalHeight * minScale
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    /**
     * Consume if vertical scroll because we don't care about other scrolls
     */
    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout, child: AppBarLayout, directTargetChild: View,
        target: View, axes: Int, type: Int
    ): Boolean {

        getViews(child)
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
    }

    /**
     * Perform actual animation by determining the dY amount
     */
    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View,
        dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int,
        type: Int, consumed: IntArray
    ) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed,
            dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)

        if (dyConsumed > 0) {
            // scroll up:
            if (toolbar.layoutParams.height > toolbarCollapsedHeight) {

                //--- shrink toolbar
                handleAppBarAnim(dyConsumed, false)
            }
        } else if (dyConsumed < -10) { // Use -10 here to allow little scroll without expand
            // scroll down
            if (toolbar.layoutParams.height < toolbarOriginalHeight) {

                //--- expand toolbar
                handleAppBarAnim(dyConsumed, true)
            }
        }
    }

    override fun layoutDependsOn(
        parent: CoordinatorLayout,
        child: AppBarLayout,
        dependency: View
    ): Boolean {

        (child as AppBar).updateOnTouch()

        return super.layoutDependsOn(parent, child, dependency)
    }

    // -----------------
    // ANIMATION
    // -----------------

    /**
     * Handle app bar animation, needed to prevent reverse event at end of user event.
     *
     * @param dyConsumed    the Y value to consume.
     * @param toExpand      true to expand, false to collapse.
     */
    private fun handleAppBarAnim(dyConsumed: Int, toExpand: Boolean) {
        // prevent reverse event at end of user event
        // set animating to false
        if (toExpand && isShrinking) {
            isShrinking = false
            return
        } else if (!toExpand && isExpanding){
            isExpanding = false
            return
        }

        isShrinking = !toExpand
        isExpanding = toExpand

        animAppBar(dyConsumed, toExpand)
    }

    /**
     * Animate the app bar, expand or collapse the app bar, scale title and translate icons.
     *
     * @param dyConsumed    the Y value to consume.
     * @param toExpand      true to expand, false to collapse.
     */
    private fun animAppBar(dyConsumed: Int, toExpand: Boolean) {
        expandAppBar(dyConsumed, toExpand)
        translateIcons()
        scaleTitle()
    }

    /**
     * Expand or collapse the app bar, depends of toExpand value.
     *
     * @param dyConsumed    the Y value to consume.
     * @param toExpand      true to expand, false to collapse.
     */
    private fun expandAppBar(dyConsumed: Int, toExpand: Boolean) {
        //--- expand or shrink toolbar
        // always subtract because dyConsumed is < 0 when expand
        val height = toolbar.layoutParams.height - dyConsumed

        toolbar.layoutParams.height =
            if (toExpand)
                if (height > toolbarOriginalHeight) toolbarOriginalHeight.toInt()
                else height
            else
                if (height < toolbarCollapsedHeight) toolbarCollapsedHeight.toInt()
                else height

        toolbar.requestLayout()
    }

    /**
     * Translate icons, based on the app bar height.
     */
    private fun translateIcons() {
        //--- translate up/down icons
        var translate: Float = (toolbarOriginalHeight - toolbar.layoutParams.height) /
                (toolbarOriginalHeight - toolbarCollapsedHeight)
        translate *= toolbarOriginalHeight

        iconList.forEach { it.translationY = -translate }
    }

    /**
     * Scale the title, based on the app bar height.
     */
    private fun scaleTitle() {
        val scale = toolbar.layoutParams.height / toolbarOriginalHeight
        toolbarTitle.scaleX = if (scale < minScale) minScale else scale
        toolbarTitle.scaleY = toolbarTitle.scaleX
    }

    // -----------------
    // CONVENIENCE CALLS
    // -----------------

    /**
     * Synchronize the app bar size with the size of the previous activity.
     *
     * @param toExpand  true to expand, false to collapse.
     * @param appBar    the app bar layout to synchronize.
     */
    internal fun syncAppBarSize(appBar: AppBarLayout, toExpand: Boolean) {
        val dyConsumed = 100 * if (toExpand) -1 else 1

        getViews(appBar)
        animAppBar(dyConsumed, toExpand)
    }

    /**
     * Expand App Bar with value animator support, and synchronously correct scrollable position
     * when the app bar size change.
     *
     * @param toExpand true to expand, false to collapse.
     */
    internal fun expandAppBar(toExpand: Boolean) {
        val anim =
            getValueAnimator(
                true,
                300L,
                LinearInterpolator(),
                { progress ->

                    val amount = if (toExpand) toolbar.layoutParams.height - toolbarOriginalHeight
                                 else toolbar.layoutParams.height - toolbarCollapsedHeight
                    val dyConsumed = (progress * amount).toInt()

                    // Correct scrollable position
                    toolbar.findSuitableScrollable()?.scrollBy(0, -dyConsumed)
                    // Animate the app bar
                    animAppBar(dyConsumed, toExpand)
                }
            )

        if (::toolbar.isInitialized) anim.start()
    }

    /**
     * Finish the animation, shrink or expand, if the user event not finished it.
     */
    internal fun finishAnimation() {
        val currentHeight = toolbar.layoutParams.height
        val isCollapsed = currentHeight == toolbarCollapsedHeight.toInt()
        val isExpanded = currentHeight == toolbarOriginalHeight.toInt()

        // If current height is not in end state (collapsed or expanded),
        // anim app bar to the nearly state
        if (!isCollapsed && ! isExpanded) {
            val middleSize = (toolbarOriginalHeight - toolbarCollapsedHeight) / 2 + toolbarCollapsedHeight
            val isMoreMiddle = currentHeight > middleSize
            expandAppBar(isMoreMiddle)
        }
    }

    // --- GETTERS ---

    /**
     * Returns true if the app bar is expanded, false if is collapsed.
     */
    internal val isExpanded: Boolean get() =
        toolbar.layoutParams.height == toolbarOriginalHeight.toInt()
}