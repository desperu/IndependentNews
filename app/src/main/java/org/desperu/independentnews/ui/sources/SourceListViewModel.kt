package org.desperu.independentnews.ui.sources

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.models.Source
import org.desperu.independentnews.repositories.SourceRepository

/**
 * View Model witch provide data for sources list.
 *
 * @property sourceRepository       the source repository interface witch provide database and
 *                                  network access.
 * @property sourcesInterface       the sources interface witch provide activity interface.
 * @property router                 the source router interface witch provide user redirection.
 *
 * @constructor Instantiates a new SourcesViewModel.
 *
 * @param sourceRepository          the source repository interface witch provide database and
 *                                  network access to set.
 * @param sourcesInterface          the sources interface witch provide activity interface to set.
 * @param router                    the source router interface witch provide user redirection to set.
 */
class SourcesListViewModel(private val sourceRepository: SourceRepository,
                           private val sourcesInterface: SourcesInterface,
                           private val router: SourceRouter
) : ViewModel() {

    // FOR DATA
    val recyclerAdapter get() = sourcesInterface.getRecyclerAdapter()
    private var sourceList: List<Source>? = null

    init {
        getSourceList()
    }

    // -----------------
    // DATABASE
    // -----------------

    /**
     * Get top source list from database, and dispatch to recycler adapter.
     */
    private fun getSourceList() = viewModelScope.launch(Dispatchers.IO) {
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
                updateList(it.map { source -> SourceViewModel(source, router) }.toMutableList())
                notifyDataSetChanged()
            }
        }
    }
}