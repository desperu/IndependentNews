package org.desperu.independentnews.ui.sources.fragment.sourceList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.models.SourceWithData
import org.desperu.independentnews.repositories.database.SourceRepository
import org.desperu.independentnews.ui.sources.fragment.SourceRouter
import org.desperu.independentnews.utils.EQUALS
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * View Model witch provide data for sources list.
 *
 * @property sourceRepository       the source repository interface witch provide database and
 *                                  network access.
 * @property sourceListInterface    the sources interface witch provide activity interface.
 * @property router                 the source router interface witch provide user redirection.
 *
 * @constructor Instantiates a new SourcesViewModel.
 *
 * @param sourceRepository          the source repository interface witch provide database and
 *                                  network access to set.
 * @param router                    the source router interface witch provide user redirection to set.
 */
class SourcesListViewModel(
    private val sourceRepository: SourceRepository,
    private val router: SourceRouter
) : ViewModel(), KoinComponent {

    // FOR DATA
    private val sourceListInterface: SourceListInterface get() = get()
    val recyclerAdapter get() = sourceListInterface.getRecyclerAdapter()
    private var sourceList: List<SourceWithData>? = null
        set(value) {
            field = value
            if (originalSourceList == null)
                originalSourceList = value
        }
    private var originalSourceList: List<SourceWithData>? = null

    // -----------------
    // DATABASE
    // -----------------

    /**
     * Get all source list from database, and dispatch to recycler adapter.
     */
    internal fun getSourceList() = viewModelScope.launch(Dispatchers.IO) {
        sourceList = sourceRepository.getAll()
        updateRecyclerData()
    }

    // -----------------
    // UPDATE
    // -----------------

    /**
     * Update source adapter list with this list.
     */
    private fun updateRecyclerData() = viewModelScope.launch(Dispatchers.Main) {
        sourceList?.let {
            recyclerAdapter?.apply {
                updateList(it.mapIndexed { index, source ->
                    SourceViewModel(source, index, router)
                }.toMutableList())

                notifyDataSetChanged()
            }
        }
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * Returns if there's source state change (enabled/disabled).
     * @return if there's source state change (enabled/disabled).
     */
    internal fun hasChange(): Boolean =
        originalSourceList?.withIndex()?.find {
            sourceList?.get(it.index)?.let { sourceWithData ->
                it.value.compareTo(sourceWithData)
            } != EQUALS
        } != null
}