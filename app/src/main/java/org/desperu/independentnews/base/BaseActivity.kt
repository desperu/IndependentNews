package org.desperu.independentnews.base

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
//import icepick.Icepick
//import kotlinx.android.synthetic.main.toolbar.*
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module

/**
 * Abstract base activity class witch provide standard functions for activities.
 *
 * @param module the koin module to load for the corresponding activity.
 */
abstract class BaseActivity(private vararg val module: Module): AppCompatActivity() {

    // --------------------
    // BASE METHODS
    // --------------------

    protected abstract fun getActivityLayout(): Int
    protected abstract fun configureDesign()

    // --------------------
    // LIFE CYCLE
    // --------------------

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.setContentView(getActivityLayout())
//        Icepick.restoreInstanceState(this, savedInstanceState)
        loadKoinModules(module.toList())
        configureDesign()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        Icepick.saveInstanceState(this, outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinModules(listOf(*module))
    }

    // --------------------
    // UI
    // --------------------
// TODO to remove ???

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
}