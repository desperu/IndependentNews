package org.desperu.independentnews.ui.sources.fragment.sourceDetail

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import kotlinx.android.synthetic.main.fragment_source_detail.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.databinding.FragmentSourceDetailBinding
import org.desperu.independentnews.models.Source
import org.desperu.independentnews.ui.sources.fragment.sourceList.SourceViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * The name of the argument to received source for this Fragment.
 */
const val SOURCE: String = "source"
/**
 * The name of the argument to received the position of this source item into the recycler view.
 */
const val ITEM_POSITION = "itemPosition"

/**
 * Fragment to show source details.
 *
 * @constructor Instantiates a new SourceDetailFragment.
 */
class SourceDetailFragment : BaseBindingFragment() {

    // FROM BUNDLE
    private val source: Source get() = arguments?.getParcelable(SOURCE) ?: Source()
    private val itemPosition: Int get() = arguments?.getInt(ITEM_POSITION) ?: -1

    // FOR DATA
    private lateinit var binding: FragmentSourceDetailBinding
    private val viewModel: SourceViewModel by viewModel { parametersOf(source, itemPosition, this) }

    /**
     * Companion object, used to create a new instance of this fragment.
     */
    companion object {
        /**
         * Create a new instance of this fragment and set source.
         * @param source the source to set and show in this fragment.
         * @param itemPosition the position of the source item in the recycler view.
         * @return the new instance of SourceDetailFragment.
         */
        fun newInstance(source: Source, itemPosition: Int): SourceDetailFragment {
            val sourceDetailFragment = SourceDetailFragment()
            sourceDetailFragment.arguments = Bundle()
            sourceDetailFragment.arguments?.putParcelable(SOURCE, source)
            sourceDetailFragment.arguments?.putInt(ITEM_POSITION, itemPosition)
            return sourceDetailFragment
        }
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureViewModel()

    override fun configureDesign() {
        updateTransitionName()
    }

    override fun updateDesign() {}

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
                getString(R.string.animation_source_list_to_detail) + itemPosition
            startPostponedEnterTransition()
        }
    }
}