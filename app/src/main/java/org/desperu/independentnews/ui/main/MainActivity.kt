package org.desperu.independentnews.ui.main

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import icepick.State
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.layout_filter_motion.*
import kotlinx.android.synthetic.main.nav_drawer.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.di.module.ui.mainModule
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.repositories.IndependentNewsRepository
import org.desperu.independentnews.repositories.SourceRepository
import org.desperu.independentnews.service.alarm.AppAlarmManager.getAlarmTime
import org.desperu.independentnews.service.alarm.AppAlarmManager.startAlarm
import org.desperu.independentnews.ui.main.fragment.MainFragmentManager
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListFragment
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListInterface
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleRouter
import org.desperu.independentnews.ui.settings.SettingsActivity
import org.desperu.independentnews.utils.*
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

var animationPlaybackSpeed: Double = 0.8
/**
 * The name of the argument to received today article list in this Activity.
 */
const val TODAY_ARTICLES: String = "todayArticles"

/**
 * Main Activity root activity of the application.
 *
 * @property mainModule the koin of the activity to load at start.
 *
 * @constructor Instantiates a new MainActivity.
 */
class MainActivity: BaseActivity(mainModule), MainInterface, OnNavigationItemSelectedListener {

    // FROM INTENT
    private val todayArticles: List<Article>? get() = intent?.getParcelableArrayListExtra(TODAY_ARTICLES)

    // FOR UI
    @JvmField @State var fragmentKey: Int = NO_FRAG
    private val drawerIcon: View by bindView(R.id.drawer_icon)
    // layout/nav_drawer views
    private val drawerLayout: DrawerLayout by bindView(R.id.drawer_layout)

    // FOR DATA
    private val fm by lazy { MainFragmentManager(supportFragmentManager, this as MainInterface) }
    override val mainLifecycleScope: LifecycleCoroutineScope = lifecycleScope
    private val ideNewsRepository = get<IndependentNewsRepository>()

    /**
     * Used to detect first apk start.
     */
    private val prefs: SharedPreferences
        get() = getSharedPreferences(INDEPENDENT_NEWS_PREFS, Context.MODE_PRIVATE)
    private var isFirstTime: Boolean
        get() = prefs.getBoolean(IS_FIRST_TIME, true)
        set(value) = prefs.edit { putBoolean(IS_FIRST_TIME, value) }

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_main

    override fun configureDesign() {
        firstStart()
        configureKoinDependency()
        configureAppBar()
        showAppBarIcon(listOf(R.id.drawer_icon))
        configureDrawerLayout()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure koin dependency for main activity.
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

    /**
     * Show the current fragment if set, else fragment top story.
     * @param fragmentKey the asked fragment key.
     * @param articleList the article list to show in fragment.
     */
    private fun showFragment(fragmentKey: Int, articleList: List<Article>?) =
        fm.configureAndShowFragment(
            when {
                articleList != null -> FRAG_TODAY_ARTICLES
                fragmentKey != NO_FRAG -> fragmentKey
                else -> FRAG_TOP_STORY
            },
            articleList
        )

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onResume() {
        super.onResume()
        showFragment(fragmentKey, todayArticles)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.activity_main_menu_drawer_top_story -> showFragment(FRAG_TOP_STORY, null)
            R.id.activity_main_menu_drawer_categories -> showFragment(FRAG_CATEGORY, null)
            R.id.activity_main_menu_drawer_all_articles -> showFragment(FRAG_ALL_ARTICLES, null)
            R.id.activity_main_menu_drawer_refresh_data -> refreshData()
            R.id.activity_main_menu_drawer_settings -> showSettingsActivity()
            R.id.activity_main_drawer_about -> this.showAboutDialog()
            R.id.activity_main_drawer_help -> this.showHelpDocumentation()
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

        // If filter motion is expended, collapse it.
        view_pager.isVisible ->
        { close_icon.performClick(); Unit }

        // If current fragment is Top Story, remove it and call super to finish activity.
        fragmentKey == FRAG_TOP_STORY -> {
            fm.clearAllBackStack()
            super.onBackPressed()
        }
        // Else show previous fragment in back stack.
        else -> {
            fm.fragmentBack { super.onBackPressed() }
            showTabLayout()
        }
    }

    // --------------
    // ACTION
    // --------------

    /**
     * Show about dialog.
     */
    private fun showAboutDialog() {
        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setTitle("${getString(R.string.activity_main_dialog_about_title)} ${getString(R.string.app_name)}")
            .setMessage(R.string.activity_main_dialog_about_message)
            .setPositiveButton(R.string.activity_main_dialog_about_positive_button, null)
            .show()
        dialog.findViewById<TextView>(android.R.id.message)?.movementMethod = LinkMovementMethod.getInstance()
    }

    /**
     * Show help documentation.
     */
    private fun showHelpDocumentation() {
        val browserIntent = Intent(Intent.ACTION_VIEW)
        browserIntent.setDataAndType(Uri.parse(DOCUMENTATION_URL), "text/html")
        startActivity(browserIntent)
    }

    // --------------
    // ACTIVITY
    // --------------

    /**
     * Start Settings activity.
     */
    private fun showSettingsActivity() =
        startActivity(Intent(this, SettingsActivity::class.java))

    // -----------------
    // DATA
    // -----------------

    /**
     * Set needed sources data, fetch data and alarm data at first start.
     */
    private fun firstStart() = lifecycleScope.launch(Dispatchers.Main) {
        if (isFirstTime) {
            showFirstStart(true)
            get<SourceRepository>().createSourcesForFirstStart()
            ideNewsRepository.fetchRssArticles()
            setAlarmAtFirstStart()
            isFirstTime = false
            showFirstStart(false)
            ideNewsRepository.fetchCategories()
        }
    }

    /**
     * Set alarm data and notification at first apk start.
     */
    private fun setAlarmAtFirstStart() {
        startAlarm(this, getAlarmTime(prefs.getInt(REFRESH_TIME, REFRESH_TIME_DEFAULT)), UPDATE_DATA)
        startAlarm(this, getAlarmTime(prefs.getInt(NOTIFICATION_TIME, NOTIFICATION_TIME_DEFAULT)), NOTIFICATION)
    }

    /**
     * Refresh data for the application, fetch data from Rss and Web, and persist them
     * into the database.
     */
    private fun refreshData() {
        GlobalScope.launch(Dispatchers.IO) {
            ideNewsRepository.refreshData()
        }
    }

    /**
     * Apply selected filters to the current article list.
     * @param selectedMap the map of selected filters to apply.
     * @param isFiltered true if apply filters to the list, false otherwise.
     */
    override fun filterList(selectedMap: Map<Int, MutableList<String>>, isFiltered: Boolean) =
        (getCurrentFrag() as ArticleListInterface).filterList(selectedMap, isFiltered)

    // --------------
    // UI
    // --------------

    /**
     * Show first start layout and hide coordinator container.
     * Show article list fragment when hide.
     * @param toShow if true show first start, else hide.
     */
    private fun showFirstStart(toShow: Boolean) {
        if (toShow) {
            first_start_container.visibility = View.VISIBLE
            coordinator_layout.visibility = View.INVISIBLE
        } else {
            coordinator_layout.visibility = View.VISIBLE
            first_start_container.visibility = View.INVISIBLE
            fragmentKey = NO_FRAG
            if (coordinator_layout.isShown) showFragment(fragmentKey, todayArticles)
        }
    }

    /**
     * Show or hide tab layout, depends of fragment key value.
     */
    private fun showTabLayout() {
        val toShow = fragmentKey == FRAG_CATEGORY
        app_bar_tab_layout.visibility = if (toShow) View.VISIBLE else View.GONE
    }

    /**
     * Show or hide filter motion, depends of toShow value.
     * @param toShow true to show filter motion, false to hide.
     */
    override fun showFilterMotion(toShow: Boolean) {
        filters_motion_layout.setOnShow(1f)
        filters_motion_layout.setOnHide(0f)
        filters_motion_layout.visibility =
            if (toShow) View.VISIBLE
            else View.INVISIBLE
    }

    /**
     * Return the adapter scale down animator for the recycler view of article list.
     * @return the adapter scale down animator for the recycler view of article list.
     */
    override fun getAdapterScaleDownAnimator(isScaledDown: Boolean): ValueAnimator? =
        (getCurrentFrag() as? ArticleListInterface)?.getRecyclerAdapter()?.getScaleDownAnimator(isScaledDown)

    /**
     * Update filters motion state adapter state, when switch fragment.
     * @param isFiltered true if the adapter is filtered, false otherwise.
     */
    override fun updateFiltersMotionState(isFiltered: Boolean) =
        filters_motion_layout.updateFiltersMotionState(isFiltered)

    // --------------
    // UTILS
    // --------------

    /**
     * Return the current fragment instance.
     * @return the current fragment instance.
     */
    private fun getCurrentFrag(): ArticleListFragment? =
        if (fragmentKey == FRAG_CATEGORY)
            fm.categoryFragment?.getCurrentFrag()
        else
            fm.articleListFragment
}