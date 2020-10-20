package org.desperu.independentnews.base.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import kotlinx.android.synthetic.main.app_bar.*
import org.desperu.independentnews.views.ToolbarBehavior
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module

/**
 * Abstract base binding activity class witch provide standard functions for activities.
 *
 * @param module the koin module to load for the corresponding activity.
 */
abstract class BaseBindingActivity(private vararg val module: Module): AppCompatActivity() {

    // --------------------
    // BASE METHODS
    // --------------------

    protected abstract fun getBindingView(): View
    protected abstract fun configureDesign()

    // --------------------
    // LIFE CYCLE
    // --------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadKoinModules(module.toList())
        setContentView(getBindingView())
        configureDesign()
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinModules(listOf(*module))
    }

    // --------------------
    // UI
    // --------------------

    /**
     * Configure App Bar.
     */
    protected fun configureAppBar() {
        (appbar.layoutParams as CoordinatorLayout.LayoutParams).behavior =
            ToolbarBehavior()
    }

    /**
     * Show App Bar Icon for the given list.
     */
    protected fun showAppBarIcon(iconList: List<Int>) {
        iconList.forEach { findViewById<View>(it).visibility = View.VISIBLE }
    }

    // --- MENU ACTION ---

    /**
     * Onc click on back arrow finish the child activity.
     */
    protected fun onClickBackArrow() = supportFinishAfterTransition()
}