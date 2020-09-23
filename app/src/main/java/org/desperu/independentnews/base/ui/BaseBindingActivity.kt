package org.desperu.independentnews.base.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import kotlinx.android.synthetic.main.app_bar.*
import org.desperu.independentnews.R
import org.desperu.independentnews.ui.main.MainActivity
import org.desperu.independentnews.ui.showArticle.ShowArticleActivity
import org.desperu.independentnews.views.ToolbarBehavior

//import icepick.Icepick
//import kotlinx.android.synthetic.main.toolbar.*

abstract class BaseBindingActivity: AppCompatActivity() {

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
        this.setContentView(getBindingView())
//        Icepick.restoreInstanceState(this, savedInstanceState)
        configureDesign()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        Icepick.saveInstanceState(this, outState)
    }

    // --------------------
    // UI
    // --------------------
// TODO to remove??

//    protected open fun configureToolBar() {
//        setSupportActionBar(toolbar)
//    }
//
//    protected open fun configureUpButton() {
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Respond to the action bar's Up/Home button
//        if (item.itemId == android.R.id.home) {
//            finish()
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }

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
    private fun showAppBarIcon(iconList: List<Int>) {
        iconList.forEach { findViewById<View>(it).visibility = View.VISIBLE }
    }
    /**
     * Show Main Activity icon in app bar (drawer and search).
     */
    protected fun showMainActivityIcon() = showAppBarIcon(listOf(R.id.drawer_icon, R.id.search_icon))

    /**
     * Show child activity icon in app bar (back arrow and share).
     */
    protected fun showChildActivityIcon() = showAppBarIcon(listOf(R.id.back_arrow_icon, R.id.share_icon))

    // --- MENU ACION ---

    /**
     * Onc click on back arrow finish the child activity.
     */
    protected fun onClickBackArrow() = supportFinishAfterTransition()
}