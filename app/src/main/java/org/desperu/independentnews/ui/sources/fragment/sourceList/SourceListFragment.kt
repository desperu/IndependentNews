package org.desperu.independentnews.ui.sources.fragment.sourceList

import android.os.Build
import android.view.View
import androidx.core.view.doOnNextLayout
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_source_list.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.ui.sources.SourcesInterface
import org.desperu.independentnews.ui.sources.fragment.sourceDetail.SOURCE_POSITION
import org.desperu.independentnews.utils.SourcesUtils.getSourceTransitionName
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Fragment to show source list.
 *
 * @constructor Instantiates a new SourceListFragment.
 */
class SourceListFragment : BaseBindingFragment(), SourceListInterface {

    // FROM BUNDLE
    private val sourcePosition get() = arguments?.getInt(SOURCE_POSITION) ?: -1

    // FOR DATA
    private val binding get() = viewBinding!!
    private val viewModel: SourcesListViewModel by viewModel()
    private var sourceListAdapter: SourceListAdapter? = null

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {
        configureKoinDependency()
        postponeEnterTransition()
        configureRecyclerView()
    }

    override fun updateDesign() {
        updateRecyclerData()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure data binding and view model.
     */
    private fun configureViewModel(): View {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_source_list, container, false)
        binding.setVariable(org.desperu.independentnews.BR.viewModel, viewModel)
        return binding.root
    }

    /**
     * Configure koin dependency for source list fragment.
     */
    private fun configureKoinDependency() {
        get<SourceListInterface> { parametersOf(this) }
    }

    /**
     * Configure recycler view, support large screen size and use specific user interface.
     * Set fall down animation for recycler view items.
     */
    private fun configureRecyclerView() {
        sourceListAdapter = SourceListAdapter(R.layout.item_source)
        sources_recycler.layoutManager = GridLayoutManager(context, 2)
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onResume() {
        super.onResume()

        get<SourcesInterface>().handleShowcase()

        sources_recycler.doOnNextLayout {
            get<SourcesInterface>().updateAppBarOnTouch()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        sourceListAdapter = null
    }

    // --------------
    // UPDATE
    // --------------

    /**
     * Update the recycler adapter list.
     */
    private fun updateRecyclerData() = viewModel.getSourceList()

    // --------------
    // ANIMATION
    // --------------

    /**
     * Update shared element transition name, and start scheduled transition
     * when all transition names are updated.
     *
     * @param itemPosition      the position of the item that contains the shared element.
     * @param sharedElements    the shared elements for which update they're transition name.
     */
    override fun updateTransitionName(itemPosition: Int, vararg sharedElements: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            sharedElements.forEach {
                it.transitionName = getSourceTransitionName(it, itemPosition)

                val isSharedItem = sourcePosition == itemPosition || sourcePosition == -1

                if (isSharedItem)
                    scheduleStartPostponedTransition(it)
            }
        }
    }

    /**
     * Schedules the shared element transition to be started immediately
     * after the shared element has been measured and laid out within the
     * activity's view hierarchy.
     *
     * @param sharedElement the shared element to animate for the transition.
     */
    private fun scheduleStartPostponedTransition(sharedElement: View) {
        sharedElement.doOnPreDraw { startPostponedEnterTransition() }
    }

    // --- GETTERS ---

    /**
     * Return the source list adapter instance.
     * @return the source list adapter instance.
     */
    override fun getRecyclerAdapter(): SourceListAdapter? = sourceListAdapter

    /**
     * Returns if there's source state change (enabled/disabled).
     * @return if there's source state change (enabled/disabled).
     */
    internal fun hasChange(): Boolean = viewModel.hasChange()
}