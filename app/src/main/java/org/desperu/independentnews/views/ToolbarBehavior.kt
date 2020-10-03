package org.desperu.independentnews.views

import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import com.google.android.material.appbar.AppBarLayout
import org.desperu.independentnews.R

/**
 * This behavior animates the toolbar elements (toolbarTitle and drawerIcon) as
 * the recyclerView in MainActivity scrolls
 */
class ToolbarBehavior : CoordinatorLayout.Behavior<AppBarLayout>() {// TODO look to AppBarLayout javadoc's source code

    private lateinit var toolbar: View
    private lateinit var toolbarTitle: View
    private lateinit var drawerIcon: View
    private lateinit var backArrowIcon: View
    private lateinit var searchIcon: View
    private lateinit var shareIcon: View

    private var toolbarOriginalHeight: Float = -1f
    private var toolbarCollapsedHeight: Float = -1f
    private var viewsSet = false
    private var minScale = 0.6f

    /**
     * Set the required view variables. Only accessed once because of the viewsSet variable.
     */
    private fun getViews(child: AppBarLayout) {
        if (viewsSet) return
        viewsSet = true

        toolbar = child.findViewById(R.id.appbar_container)
        toolbarTitle = toolbar.findViewById(R.id.toolbar_title)
        drawerIcon = toolbar.findViewById(R.id.drawer_icon)
        backArrowIcon = toolbar.findViewById(R.id.back_arrow_icon)
        searchIcon = toolbar.findViewById(R.id.search_icon)
        shareIcon = toolbar.findViewById(R.id.share_icon)

        toolbarOriginalHeight = toolbar.layoutParams.height.toFloat()
        toolbarCollapsedHeight = toolbarOriginalHeight * minScale
    }

    /**
     * Consume if vertical scroll because we don't care about other scrolls
     */
    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, directTargetChild: View,
                                     target: View, axes: Int, type: Int): Boolean {
        getViews(child)
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL ||
                super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, axes, type)
    }


    /**
     * Perform actual animation by determining the dY amount
     */
    override fun onNestedScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View,
                                dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int, dyUnconsumed: Int,
                                type: Int, consumed: IntArray) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed)
        getViews(child)

        if (dyConsumed > 0) {

            // scroll up:
            if (toolbar.layoutParams.height > toolbarCollapsedHeight) {

                //--- shrink toolbar
                val height = toolbar.layoutParams.height - dyConsumed
                toolbar.layoutParams.height = if (height < toolbarCollapsedHeight) toolbarCollapsedHeight.toInt() else height
                toolbar.requestLayout()

                //--- translate up drawer icon
                var translate: Float = (toolbarOriginalHeight - toolbar.layoutParams.height) / (toolbarOriginalHeight - toolbarCollapsedHeight)
                translate *= toolbarOriginalHeight
                drawerIcon.translationY = -translate
                backArrowIcon.translationY = -translate
                searchIcon.translationY = -translate
                shareIcon.translationY = -translate

                //--- title
                val scale = toolbar.layoutParams.height / toolbarOriginalHeight
                toolbarTitle.scaleX = if (scale < minScale) minScale else scale
                toolbarTitle.scaleY = toolbarTitle.scaleX
            }
        } else if (dyConsumed < -20) {// TODO not good, jump to 20 directly

            // scroll down
            if (toolbar.layoutParams.height < toolbarOriginalHeight) {

                //--- expand toolbar
                // subtract because dyConsumed is < 0
                val height = toolbar.layoutParams.height - dyConsumed
                toolbar.layoutParams.height = if (height > toolbarOriginalHeight) toolbarOriginalHeight.toInt() else height
                toolbar.requestLayout()

                //--- translate down  drawer icon
                var translate: Float = (toolbarOriginalHeight - toolbar.layoutParams.height) / (toolbarOriginalHeight - toolbarCollapsedHeight)
                translate *= toolbarOriginalHeight
                drawerIcon.translationY = -translate
                backArrowIcon.translationY = -translate
                searchIcon.translationY = -translate
                shareIcon.translationY = -translate

                //--- title
                val scale = toolbar.layoutParams.height / toolbarOriginalHeight
                toolbarTitle.scaleX = if (scale < minScale) minScale else scale
                toolbarTitle.scaleY = toolbarTitle.scaleX
            }
        }
    }
}