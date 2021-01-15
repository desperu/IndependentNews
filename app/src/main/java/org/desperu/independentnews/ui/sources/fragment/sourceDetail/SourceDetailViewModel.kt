package org.desperu.independentnews.ui.sources.fragment.sourceDetail

import android.view.View.OnClickListener
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.desperu.independentnews.models.database.Css
import org.desperu.independentnews.models.database.SourceWithData
import org.desperu.independentnews.repositories.database.CssRepository
import org.desperu.independentnews.repositories.database.SourceRepository
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
    private val cssRepository: CssRepository = get()
    private val sourcePageAdapter: SourceDetailAdapter?
        get() = sourceDetailInterface.getRecyclerAdapter() // TODO WeakReference or MutableLiveData but here to save adapter instance....gahfy
    val isEnabled = ObservableBoolean(sourceWithData.source.isEnabled)
    val primaryPage = sourceWithData.sourcePages.find { it.isPrimary }

    /**
     * Update recycler views data.
     */
    internal fun updateRecyclerData() {
        val sourceWithData = SourceWithData(
            sourceWithData.source,
            sourceWithData.sourcePages.filter { !it.isPrimary }
        )
        sourcePageAdapter?.updateList(
            sourceWithData.sourcePages
                .mapIndexed { index, _ -> SourceLinkViewModel(sourceWithData.toSimplePage(index)) }
                .toMutableList()
        )
    }

    /**
     * On click enable listener.
     */
    val onClickEnable = OnClickListener { inverseSourceState() }

    /**
     * Inverse the state of the source.
     */
    private fun inverseSourceState() = viewModelScope.launch(Dispatchers.IO) {
        val originalState = sourceRepository.getSource(sourceWithData.source.id).isEnabled
        sourceRepository.setEnabled(sourceWithData.source.id, !originalState)
        isEnabled.set(!originalState)
    }

    /**
     * Returns the css of the current article.
     *
     * @return the css of the current article.
     */
    internal suspend fun getCss(): Css? = withContext(Dispatchers.IO) {
        return@withContext primaryPage?.cssUrl?.let { cssRepository.getCssStyle(it) }
    }
}