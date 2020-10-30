package org.desperu.independentnews.ui.sources

import android.view.View
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_sources.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseBindingActivity
import org.desperu.independentnews.di.module.ui.sourcesModule
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Activity to manages and present the medias, informations, sources of the application.
 *
 * @constructor Instantiates a new SourcesActivity.
 */
class SourcesActivity : BaseBindingActivity(sourcesModule), SourcesInterface {

    // FOR DATA
    private lateinit var binding: ViewDataBinding
    private val viewModel: SourcesListViewModel by viewModel { parametersOf(this) }
    private lateinit var sourcesAdapter: RecyclerViewAdapter

    // --------------
    // BASE METHODS
    // --------------

    override fun getBindingView(): View = configureDataBinding()

    override fun configureDesign() {
        configureAppBar()
        showAppBarIcon(listOf(R.id.back_arrow_icon))
        configureRecyclerView()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure data binding and return the root view.
     * @return the binding root view.
     */
    private fun configureDataBinding(): View {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sources)
        binding.setVariable(org.desperu.independentnews.BR.viewModel, viewModel)
        return binding.root
    }

    /**
     * Configure recycler view, support large screen size and use specific user interface.
     * Set fall down animation for recycler view items.
     */
    private fun configureRecyclerView() {
        sourcesAdapter = RecyclerViewAdapter(R.layout.item_source)
        sources_recycler.layoutManager = LinearLayoutManager(this)

        val controller = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_anim_fall_down)
        sources_recycler.layoutAnimation = controller
        sources_recycler.layoutAnimation.animation.startOffset = 100
        sources_recycler.scheduleLayoutAnimation()
    }

    // --------------
    // ACTION
    // --------------

    /**
     * On click back arrow icon menu.
     */
    @Suppress("unused_parameter")
    fun onClickBackArrow(v: View) = onClickBackArrow()

    // --- GETTERS ---

    /**
     * Return the source list adapter instance.
     * @return the source list adapter instance.
     */
    override fun getRecyclerAdapter(): RecyclerViewAdapter? = sourcesAdapter
}