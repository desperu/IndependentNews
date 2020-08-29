package org.desperu.independentnews.base

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
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
}