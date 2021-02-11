package org.desperu.independentnews.extension.design

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.children
import androidx.core.view.descendants
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.RecyclerView

/**
 * Find the suitable scrollable that owns the app bar animation.
 * Try to directly get the root Coordinator of the layout, to search into it's descendants.
 */
internal fun View?.findSuitableScrollable(): View? {
    var view = this

    do {
        val parent = view?.parent

        if (parent is CoordinatorLayout) {
            // We've found the parent coordinator layout, it should be the root layout,
            // so start to search in it's descendants
            view = parent
        }

        if (view is ViewGroup) {
            // If the view is a view group, search a scrollable in it's descendants
            view.descendants.forEach {

                if (it is NestedScrollView) {
                    // We've found a NestedScrollView, use it
                    return it

                } else if (it is RecyclerView) {
                    // We've found a RecyclerView, use it
                    return it
                }
            }
        }

        if (view != null) {
            // Else, we will loop and crawl up the view hierarchy and try to find a parent
            view = if (parent is View) parent else null
        }

    } while (view != null)

    // If we reach here then we didn't find a Scrollable
    return null
}

internal fun View?.findSuitableFrame(): View? {
    val parent = this?.parent

    if (parent is CoordinatorLayout) {
        // We've found the parent coordinator layout, so search in child
        parent.children.forEach {
            if (it is FrameLayout && it.id != android.R.id.content) {
                // If we've not hit the decor content view, then use it
                return it
            }
        }
    }

    // If we reach here then we didn't find a suitable frame
    return null
}

internal fun ViewGroup?.findScrollableChild(): View? {
    this?.descendants?.forEach {

        if (it is NestedScrollView) {
            // We've found a NestedScrollView, use it
            return it

        } else if (it is RecyclerView) {
            // We've found a RecyclerView, use it
            return it
        }
    }

    // If we reach here then we didn't find a Scrollable Child
    return null
}

/**
 * Find the asked view type in the view hierarchy.
 * Try to directly get the root Coordinator of the layout, to search into it's descendants.
 * Return null if the asked view type is not found.
 */
internal inline fun <reified T: View> View?.findView(): T? {
    var view = this

    do {
        val parent = view?.parent

        if (parent is CoordinatorLayout) {
            // We've found the parent coordinator layout, it should be the root layout,
            // so start to search in it's descendants
            view = parent
        }

        if (view is ViewGroup) {
            // If the view is a view group, search the view type in it's descendants
            view.descendants.forEach {

                if (it is T) {
                    // We've found the searched view type, use it
                    return it
                }
            }
        }

        if (view != null) {
            // Else, we will loop and crawl up the view hierarchy and try to find a parent
            view = if (parent is View) parent else null
        }

    } while (view != null)

    // If we reach here then we didn't find a view type
    return null
}