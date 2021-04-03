package org.desperu.independentnews.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

/**
 * Abstract base activity class witch provide standard functions for binding fragment.
 */
abstract class BaseBindingFragment : Fragment() {

    // FOR DATA
    protected lateinit var inflater: LayoutInflater
    protected var container: ViewGroup? = null
    protected var viewBinding: ViewDataBinding? = null

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

//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        Icepick.saveInstanceState(this, outState)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewBinding = null
        container = null
    }
}