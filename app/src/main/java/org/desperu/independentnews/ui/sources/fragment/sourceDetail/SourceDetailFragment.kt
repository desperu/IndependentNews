package org.desperu.independentnews.ui.sources.fragment.sourceDetail

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.view.doOnNextLayout
import androidx.core.widget.NestedScrollView.OnScrollChangeListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_source_detail.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.R
import org.desperu.independentnews.anim.AnimHelper.alphaViewAnimation
import org.desperu.independentnews.anim.AnimHelper.fromBottomAnimation
import org.desperu.independentnews.anim.AnimHelper.scaleViewAnimation
import org.desperu.independentnews.base.ui.BaseBindingFragment
import org.desperu.independentnews.extension.design.dp
import org.desperu.independentnews.models.database.SourceWithData
import org.desperu.independentnews.ui.sources.SourcesInterface
import org.koin.android.ext.android.get
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
    private val binding get() = viewBinding!!
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
        imageRequestLayout()
        configureWebView()
        configureRecyclerView()
    }

    override fun updateDesign() {
        updateRecyclerData()
        animateViews()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure data binding, recycler view and view model.
     */
    private fun configureViewModel(): View {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_source_detail, container, false)
        binding.setVariable(org.desperu.independentnews.BR.viewModel, viewModel)
        return binding.root
    }

    /**
     * Configure the web view, set the css style.
     */
    @SuppressLint("SetJavaScriptEnabled")
    private fun configureWebView() {
        source_detail_web_view.settings.javaScriptEnabled = true
        lifecycleScope.launch(Dispatchers.Main) {
            source_detail_web_view.updateWebViewDesign(
                sourceWithData.source.name,
                viewModel.getCss()
            )
        }
    }

    /**
     * Configure recycler view, support large screen size. Set from left animation
     * for recycler view items,.
     */
    private fun configureRecyclerView() {
        sourceDetailAdapter = SourceDetailAdapter(context!!, R.layout.item_source_link)
        source_detail_nested_scroll.setOnScrollChangeListener(scrollListener)
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onResume() {
        super.onResume()
        source_detail_nested_scroll.doOnNextLayout {
            get<SourcesInterface>().updateAppBarOnTouch()
        }
    }

    // --------------
    // LISTENER
    // --------------

    /**
     * Scroll listener, to show recycler animation each time it appear on user screen.
     */
    private val scrollListener = OnScrollChangeListener { v, _, scrollY, _, _ -> // to perfect, not remove the adapter, just re-play anim
        source_detail_nested_scroll?.let {
            val safeMarge = 50.dp // Safe marge to prevent ui mistake
            val recyclerTop = source_detail_recycler.top - v.measuredHeight - safeMarge
            source_detail_recycler.adapter =
                if (recyclerTop < scrollY)
                    if (source_detail_recycler.adapter == null)
                        sourceDetailAdapter
                    else return@OnScrollChangeListener
                else
                    null
        }
    }

    // --------------
    // UPDATE
    // --------------

    /**
     * Update the recycler adapter list.
     */
    private fun updateRecyclerData() = viewModel.updateRecyclerData()

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

    // --------------
    // ANIMATION
    // --------------

    /**
     * Request layout after transition animation for the image.
     */
    private fun imageRequestLayout() =
        source_detail_image.postOnAnimation { source_detail_image.requestLayout() }

    /**
     * Animate view when fragment appear.
     */
    private fun animateViews() {
        alphaViewAnimation(listOf(source_detail_container), 0, true)
        scaleViewAnimation(source_detail_disable_button, 250)
        alphaViewAnimation(listOf(source_detail_title, source_detail_web_view), 50, true)
        fromBottomAnimation(source_detail_web_view, 50)
    }

    // --- GETTERS ---

    /**
     * Get the source detail recycler view adapter instance.
     */
    override fun getRecyclerAdapter(): SourceDetailAdapter? = sourceDetailAdapter
}