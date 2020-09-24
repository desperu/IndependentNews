package org.desperu.independentnews.ui.main

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.di.module.mainModule
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.ui.main.filter.FiltersMotionLayout
import org.desperu.independentnews.ui.main.fragment.MainFragmentManager
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleRouter
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListViewModel
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListAdapter
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListInterface
import org.desperu.independentnews.utils.FRAG_ARTICLE_LIST
import org.desperu.independentnews.utils.NO_FRAG
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

var animationPlaybackSpeed: Double = 0.8

class MainActivity: BaseActivity(mainModule), MainInterface {

    // FOR UI
//    @JvmField @State
    private var fragmentKey: Int = NO_FRAG
    private val recyclerView: RecyclerView by bindView(R.id.recycler_view)
    private val drawerIcon: View by bindView(R.id.drawer_icon)
    private val filtersMotionLayout: FiltersMotionLayout by bindView(R.id.filters_motion_layout)

    // layout/nav_drawer views
    private val drawerLayout: DrawerLayout by bindView(R.id.drawer_layout)

    // FOR DATA
    private val fm by lazy { MainFragmentManager(this, this as MainInterface) }
    private val viewModel by viewModel<ArticleListViewModel>()
    private lateinit var mainListAdapter: ArticleListAdapter
    private val loadingDuration: Long
        get() = (resources.getInteger(R.integer.loadingAnimDuration) / animationPlaybackSpeed).toLong()

//    /**
//     * Used to open nav drawer when opening app for first time (to show options)
//     */
//    private val prefs: SharedPreferences
//        get() = getSharedPreferences("FabFilter", Context.MODE_PRIVATE)
//    private var isFirstTime: Boolean
//        get() = prefs.getBoolean("isFirstTime", true)
//        set(value) = prefs.edit { putBoolean("isFirstTime", value) }
    /**
     * Used by FiltersLayout since we don't want to expose mainListAdapter (why?)
     * (Option: Combine everything into one activity if & when necessary)
     */
    var isAdapterFiltered: Boolean
        get() = mainListAdapter.isFiltered
        set(value) {
            mainListAdapter.isFiltered = value
        }

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_main

    override fun configureDesign() {
        configureKoinDependency()
        configureAppBar()
        showMainActivityIcon()
        configureDrawerLayout()
        fm.configureAndShowFragment(FRAG_ARTICLE_LIST)
//        configureNavigationView()
//        configureViewModel()
//        configureRecyclerView()
//        testRequest()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure koin dependency for main interfaces.
     */
    private fun configureKoinDependency() {
        get<MainInterface> { parametersOf(this@MainActivity) }
        get<ArticleRouter> { parametersOf(this@MainActivity) }
    }

    /**
     * Configure Drawer layout.
     */
    @SuppressLint("SetTextI18n")
    private fun configureDrawerLayout() {
        val toggle = ActionBarDrawerToggle(this, drawerLayout, null,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
//        animationSpeedSeekbar.setOnSeekbarChangeListener { value ->
//            animationPlaybackSpeed = value as Double
//            animationSpeedText.text = "${"%.1f".format(animationPlaybackSpeed)}x"
//            filtersMotionLayout.updateDurations()
//            updateRecyclerViewAnimDuration()
//        }
        drawerIcon.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
    }

//    /**
//     * Configure Recycler view.
//     */
//    private fun configureRecyclerView() {
//        mainListAdapter = MainListAdapter(this, R.layout.item_article)
//        recyclerView.adapter = mainListAdapter
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.setHasFixedSize(true)
//        updateRecyclerViewAnimDuration()
//    }

    private fun testRequest() { // TODO for test
        viewModel.getArticle()
    }

    // --------------
    // FRAGMENT
    // --------------

    /**
     * Return the fragment key value.
     * @return the fragment key value.
     */
    override fun getFragmentKey(): Int = fragmentKey

    /**
     * Set fragment key with the given value.
     * @param fragmentKey the fragment key value to set.
     */
    override fun setFragmentKey(fragmentKey: Int) { this.fragmentKey = fragmentKey }

    // --------------
    // ACTIVITY
    // --------------

    /**
     * Update RecyclerView Item Animation Durations
     */
    private fun updateRecyclerViewAnimDuration() = recyclerView.itemAnimator?.run {
        removeDuration = loadingDuration * 60 / 100
        addDuration = loadingDuration
    }

//    /**
//     * Open browser for given string resId URL
//     */
//    private fun openBrowser(@StringRes resId: Int): Unit =
//            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(resId))))

    /**
     * Called from FiltersLayout to get adapter scale down animator
     */
    fun getAdapterScaleDownAnimator(isScaledDown: Boolean): ValueAnimator? =
        (fm.articleListFragment as ArticleListInterface).getRecyclerAdapter()?.getScaleDownAnimator(isScaledDown)

    /**
     * Return the main list adapter instance.
     * @return the main list adapter instance.
     */
//    override fun getRecyclerAdapter(): ArticleListAdapter? = if (::mainListAdapter.isInitialized) mainListAdapter else null
}