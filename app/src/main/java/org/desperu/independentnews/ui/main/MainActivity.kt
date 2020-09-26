package org.desperu.independentnews.ui.main

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import icepick.State
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.nav_drawer.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.di.module.mainModule
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.ui.main.filter.FiltersMotionLayout
import org.desperu.independentnews.ui.main.fragment.MainFragmentManager
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListAdapter
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListInterface
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleRouter
import org.desperu.independentnews.utils.FRAG_ALL_ARTICLES
import org.desperu.independentnews.utils.FRAG_CATEGORY
import org.desperu.independentnews.utils.FRAG_TOP_STORY
import org.desperu.independentnews.utils.NO_FRAG
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf


var animationPlaybackSpeed: Double = 0.8

class MainActivity: BaseActivity(mainModule), MainInterface, OnNavigationItemSelectedListener {

    // FOR UI
    @JvmField @State var fragmentKey: Int = NO_FRAG
    private val recyclerView: RecyclerView by bindView(R.id.recycler_view)
    private val drawerIcon: View by bindView(R.id.drawer_icon)
    private val filtersMotionLayout: FiltersMotionLayout by bindView(R.id.filters_motion_layout)

    // layout/nav_drawer views
    private val drawerLayout: DrawerLayout by bindView(R.id.drawer_layout)

    // FOR DATA
    private val fm by lazy { MainFragmentManager(this, this as MainInterface) }
    private lateinit var mainListAdapter: ArticleListAdapter

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
        nav_view.setNavigationItemSelectedListener(this)
//        animationSpeedSeekbar.setOnSeekbarChangeListener { value ->
//            animationPlaybackSpeed = value as Double
//            animationSpeedText.text = "${"%.1f".format(animationPlaybackSpeed)}x"
//            filtersMotionLayout.updateDurations()
//            updateRecyclerViewAnimDuration()
//        }
        drawerIcon.setOnClickListener { drawerLayout.openDrawer(GravityCompat.START) }
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

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onResume() {
        super.onResume()
        fm.configureAndShowFragment(if (fragmentKey != NO_FRAG) fragmentKey else FRAG_TOP_STORY)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.activity_main_menu_drawer_top_story -> fm.configureAndShowFragment(FRAG_TOP_STORY)
            R.id.activity_main_menu_drawer_categories -> fm.configureAndShowFragment(FRAG_CATEGORY)
            R.id.activity_main_menu_drawer_all_articles -> fm.configureAndShowFragment(FRAG_ALL_ARTICLES)
//            R.id.activity_main_drawer_search -> this.showSearchArticlesActivity()
//            R.id.activity_main_drawer_notifications -> this.showNotificationsActivity()
//            R.id.activity_main_drawer_about -> this.showAboutDialog()
//            R.id.activity_main_drawer_help -> this.showHelpDocumentation()
            else -> {}
        }
        showTabLayout()
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() = when {
        // If drawer is open, close it.
        drawerLayout.isDrawerOpen(GravityCompat.START) ->
            drawerLayout.closeDrawer(GravityCompat.START)

//        // If bottom sheet is state expanded , hide it.
//        fm.isBottomSheetInitialized && fm.isExpanded ->
//            closeFilterFragment(false)
//
//        // If search view is shown, hide it.
//        toolbar_search_view != null && toolbar_search_view.isShown ->
//            switchSearchView(false, isReload = false)
//
//        // If map is expended in estate detail fragment, collapse it.
//        fm.mapsFragmentChildDetail?.view?.findViewById<ImageButton>(R.id.fragment_maps_fullscreen_button)?.tag == FULL_SIZE ->
//            MapMotionLayout(this, fm.mapsFragmentChildDetail?.view).switchMapSize()

        // If current fragment is Top Story, remove it and call super to finish activity.
        fragmentKey == FRAG_TOP_STORY -> {
            fm.clearAllBackStack()
            super.onBackPressed()
        }
//        // If device is tablet and frag is not map frag, remove the two frags and call super to finish activity.
//        isFrame2Visible && fragmentKey != FRAG_ESTATE_MAP -> {
//            fm.clearAllBackStack()
//            super.onBackPressed()
//        }
        // Else show previous fragment in back stack.
        else -> {
            fm.fragmentBack { super.onBackPressed() }
            showTabLayout()
        }
    }

    // --------------
    // ACTIVITY
    // --------------

//    /**
//     * Open browser for given string resId URL
//     */
//    private fun openBrowser(@StringRes resId: Int): Unit =
//            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(resId))))

    // --------------
    // UI
    // --------------

    /**
     * Show or hide tab layout, depends of fragment key value.
     */
    private fun showTabLayout() {
        val toShow = fragmentKey == FRAG_CATEGORY
        app_bar_tab_layout.visibility = if (toShow) View.VISIBLE else View.GONE
    }

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