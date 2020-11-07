package org.desperu.independentnews.ui.sources.fragment.sourceDetail

import android.view.View.OnClickListener
import androidx.lifecycle.ViewModel
import org.desperu.independentnews.models.SourceWithData
import org.desperu.independentnews.ui.sources.fragment.sourceList.SourceRouter

/**
 * View Model witch provide details data for source.
 *
 * @property sourceWithData             the given source with data for this view model.
 * @property router                     the source router interface witch provide user redirection.
 * @property sourceDetailInterface      the source detail interface which provide fragment interface.
 *
 * @constructor Instantiates a new SourceDetailViewModel.
 *
 * @param sourceWithData                the given source with data for this view model.
 * @param router                        the source router interface witch provide user redirection to set.
 * @param sourceDetailInterface         the source detail interface which provide fragment interface to set.
 */
class SourceDetailViewModel(
    val sourceWithData: SourceWithData,
    private val router: SourceRouter,
    private val sourceDetailInterface: SourceDetailInterface
) : ViewModel() {

    // FOR DATA
    private val sourcePageAdapter get() = sourceDetailInterface.getRecyclerAdapter() // TODO WeakReference or MutableLiveData but here to save adapter instance....gahfy

    /**
     * Update recycler views data.
     */
    internal fun updateRecyclerData() {
        sourcePageAdapter?.updateList(
            sourceWithData.sourcePages
                .filter { !it.isPrimary }
                .map { SourceLinkViewModel(it, router) }
                .toMutableList()
        )
    }

    /**
     * On click enable listener.
     */
    val onClickEnable = OnClickListener {

    }
}