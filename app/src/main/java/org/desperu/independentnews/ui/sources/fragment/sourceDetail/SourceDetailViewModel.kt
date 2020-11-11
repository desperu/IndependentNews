package org.desperu.independentnews.ui.sources.fragment.sourceDetail

import android.view.View.OnClickListener
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.models.SourceWithData
import org.desperu.independentnews.repositories.SourceRepository
import org.desperu.independentnews.service.ResourceService
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * View Model witch provide details data for source.
 *
 * @property sourceWithData             the given source with data for this view model.
 * @property sourceDetailInterface      the source detail interface which provide fragment interface.
 * @property resourceService            the resource service to access application resources.
 * @property sourceRepository           the source repository database access.
 * @property sourcePageAdapter          the source page adapter instance, (recycler view).
 *
 * @constructor Instantiates a new SourceDetailViewModel.
 *
 * @param sourceWithData                the given source with data for this view model.
 * @param sourceDetailInterface         the source detail interface which provide fragment interface to set.
 */
class SourceDetailViewModel(
    val sourceWithData: SourceWithData,
    private val sourceDetailInterface: SourceDetailInterface
) : ViewModel(), KoinComponent {

    // FOR DATA
    private val resourceService: ResourceService = get()
    private val sourceRepository: SourceRepository = get()
    private val sourcePageAdapter: SourceDetailAdapter?
        get() = sourceDetailInterface.getRecyclerAdapter() // TODO WeakReference or MutableLiveData but here to save adapter instance....gahfy
    val isEnabled = ObservableBoolean(sourceWithData.source.isEnabled)
    val primaryPage = sourceWithData.sourcePages.find { it.isPrimary }

    /**
     * Update recycler views data.
     */
    internal fun updateRecyclerData() {
        sourcePageAdapter?.updateList(
            sourceWithData.sourcePages
                .filter { !it.isPrimary } // TODO to perfect
                .mapIndexed { index, _ -> SourceLinkViewModel(sourceWithData.toSimplePage(index + 1)) }
                .toMutableList()
        )
    }

    /**
     * On click enable listener.
     */
    val onClickEnable = OnClickListener {
        val fab = it as FloatingActionButton
        fab.rotation = 3f
        inverseSourceState()
    }

    /**
     * Inverse the state of the source.
     */
    private fun inverseSourceState() = viewModelScope.launch(Dispatchers.IO) {
        val originalState = sourceRepository.getSource(sourceWithData.source.id).isEnabled
        sourceRepository.setEnabled(sourceWithData.source.id, !originalState)
        isEnabled.set(!originalState)
    }
}