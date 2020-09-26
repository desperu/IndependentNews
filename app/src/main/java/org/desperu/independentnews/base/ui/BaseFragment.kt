package org.desperu.independentnews.base.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Abstract base fragment class witch provide standard functions for fragment.
 */
abstract class BaseFragment : Fragment() {

    // --------------
    // BASE METHODS
    // --------------

    protected abstract val fragmentLayout: Int
    protected abstract fun configureDesign()
    protected abstract fun updateDesign()

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(fragmentLayout, container, false)
//        ButterKnife.bind(this, view)
//        Icepick.restoreInstanceState(this, savedInstanceState)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configureDesign()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        updateDesign()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
//        Icepick.saveInstanceState(this, outState)
    }
}