package org.desperu.independentnews.ui.sources.fragment.sourceDetail

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.widget.NestedScrollView
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.fragment_source_detail.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.databinding.FragmentSourceDetailBinding
import org.desperu.independentnews.models.SourceWithData
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * The name of the argument to received source with data for this Fragment.
 */
const val SOURCE_WITH_DATA: String = "sourceWithData"
/**
 * The name of the argument to received the position of this source item into the recycler view.
 */
const val SOURCE_POSITION = "sourcePosition"

/**
 * Fragment to show source details.
 *
 * @constructor Instantiates a new SourceDetailFragment.
 */
class SourceDetailFragment : BaseBindingFragment(), SourceDetailInterface {

    // FROM BUNDLE
    private val sourceWithData: SourceWithData get() = arguments?.getParcelable(SOURCE_WITH_DATA) ?: SourceWithData()
    private val sourcePosition: Int get() = arguments?.getInt(SOURCE_POSITION) ?: -1

    // FOR DATA
    private lateinit var binding: FragmentSourceDetailBinding
    private val viewModel: SourceDetailViewModel by viewModel { parametersOf(sourceWithData, this) }
    private var sourceDetailAdapter: SourceDetailAdapter? = null

    /**
     * Companion object, used to create a new instance of this fragment.
     */
    companion object {
        /**
         * Create a new instance of this fragment and set source.
         * @param sourceWithData the source with data to set and show in this fragment.
         * @param sourcePosition the position of the source item in the recycler view.
         * @return the new instance of SourceDetailFragment.
         */
        fun newInstance(sourceWithData: SourceWithData, sourcePosition: Int): SourceDetailFragment {
            val sourceDetailFragment = SourceDetailFragment()
            sourceDetailFragment.arguments = Bundle()
            sourceDetailFragment.arguments?.putParcelable(SOURCE_WITH_DATA, sourceWithData)
            sourceDetailFragment.arguments?.putInt(SOURCE_POSITION, sourcePosition)
            return sourceDetailFragment
        }
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {
        updateTransitionName()
        configureRecyclerView()
    }

    override fun updateDesign() {
        updateRecyclerData()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure data binding, recycler view and view model.
     */
    private fun configureViewModel(): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_source_detail, container, false)
        binding.viewModel = viewModel
        return binding.root
    }

    /**
     * Update transition name of the shared element (image).
     */
    private fun updateTransitionName() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            postponeEnterTransition()
            source_detail_image.transitionName =
                getString(R.string.animation_source_list_to_detail) + sourcePosition
            startPostponedEnterTransition()
        }
    }

    /**
     * Configure recycler view, support large screen size. Set from left animation
     * for recycler view items, to show animation each time the recycler appear on user screen.
     */
    private fun configureRecyclerView() {
        sourceDetailAdapter = SourceDetailAdapter(context!!, R.layout.item_source_link)
        source_detail_nested_scroll.setOnScrollChangeListener(
            NestedScrollView.OnScrollChangeListener { v, _, scrollY, _, _ ->
                val recyclerTop = source_detail_recycler.top - v.measuredHeight
                    source_detail_recycler.adapter =
                        if (recyclerTop < scrollY)
                            if (source_detail_recycler.adapter == null) {
                                animDisableButton(viewModel.getSourcePageList.size - 1)
                                sourceDetailAdapter // TODO remove listener before onDestroy
                            }
                            else return@OnScrollChangeListener
                        else
                            null
            }
        )
    }

    // --------------
    // UPDATE
    // --------------

    /**
     * Update the recycler adapter list.
     */
    private fun updateRecyclerData() = viewModel.updateRecyclerData()

    /**
     * Animate the disable source button.
     */
    private fun animDisableButton(position: Int) {
        val anim = AnimationUtils.makeInAnimation(context, true)
        anim.startOffset = position * 200L / 3
        anim.duration = 250L
        source_detail_disable_button.startAnimation(anim)
    }

    // --- GETTERS ---

    /**
     * Get the source detail recycler view adapter instance.
     */
    override fun getRecyclerAdapter(): SourceDetailAdapter? = sourceDetailAdapter
}