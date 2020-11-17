package org.desperu.independentnews.ui.sources.fragment.sourceList

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.view.animation.LayoutAnimationController
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.fragment_source_list.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.databinding.FragmentSourceListBinding
import org.desperu.independentnews.di.module.ui.sourceListModule
import org.desperu.independentnews.ui.sources.fragment.sourceDetail.SOURCE_POSITION
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Fragment to show source list.
 *
 * @constructor Instantiates a new SourceListFragment.
 */
class SourceListFragment : BaseBindingFragment(sourceListModule), SourceListInterface {

    // FROM BUNDLE
    private val sourcePosition get() = arguments?.getInt(SOURCE_POSITION) ?: -1

    // FOR DATA
    private lateinit var binding: FragmentSourceListBinding
    private val viewModel: SourcesListViewModel by viewModel { parametersOf(this) }
    private var sourceListAdapter: SourceListAdapter? = null
    private lateinit var controller: LayoutAnimationController
    private var fromDetail: Boolean? = null

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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_source_list, container, false)
        binding.viewModel = viewModel
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

        controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_anim_fall_down)
        updateRecyclerAnim()
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onResume() {
        super.onResume()
        updateRecyclerAnim()
        fromDetail = false
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        fromDetail?.let { fromDetail = true }
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
     * Update shared element transition name.
     *
     * @param itemPosition the position of the item that contains the shared element.
     * @param image the image for which update it's transition name.
     */
    override fun updateTransitionName(itemPosition: Int, image: View) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            image.transitionName = getString(R.string.animation_source_list_to_detail) + itemPosition
            if (this.sourcePosition == itemPosition || this.sourcePosition == -1)
                scheduleStartPostponedTransition(image)
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
        sharedElement.doOnPreDraw {
            updateRecyclerAnim()
            startPostponedEnterTransition()
        }
    }

    /**
     * Update recycler view layout animation, depends of fromDetail value
     * to show or not the animation.
     */
    private fun updateRecyclerAnim() {
        controller.animation.duration =
            if (fromDetail == null || fromDetail == false) // Show animation
                resources.getInteger(R.integer.item_anim_duration).toLong()
            else
                0L

        sources_recycler.layoutAnimation = controller
        sources_recycler.scheduleLayoutAnimation()
    }

    // --- GETTERS ---

    /**
     * Return the source list adapter instance.
     * @return the source list adapter instance.
     */
    override fun getRecyclerAdapter(): SourceListAdapter? = sourceListAdapter

}