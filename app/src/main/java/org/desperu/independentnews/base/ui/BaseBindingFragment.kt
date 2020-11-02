package org.desperu.independentnews.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module

//import icepick.Icepick

/**
 * Abstract base activity class witch provide standard functions for binding fragment.
 *
 * @param module the koin module to load for the corresponding fragment.
 */
abstract class BaseBindingFragment(private vararg val module: Module): Fragment() {

    // FOR DATA
    protected lateinit var inflater: LayoutInflater
    protected var container: ViewGroup? = null

    // --------------
    // BASE METHODS
    // --------------

    protected abstract fun getBindingView(): View
    protected abstract fun configureDesign()
    protected abstract fun updateDesign()

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        Icepick.restoreInstanceState(this, savedInstanceState)
//        configureDesign()
        super.onCreateView(inflater, container, savedInstanceState)
        this.inflater = inflater
        this.container = container
        loadKoinModules(module.toList())
        return getBindingView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureDesign()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        updateDesign()
        // keep the fragment and all its data across screen rotation
        retainInstance = true
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        Icepick.saveInstanceState(this, outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        unloadKoinModules(module.toList())
    }
}