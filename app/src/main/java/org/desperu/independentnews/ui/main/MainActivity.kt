package org.desperu.independentnews.ui.main

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import icepick.State
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.content_loading_bar.*
import kotlinx.android.synthetic.main.layout_filter_motion.*
import kotlinx.android.synthetic.main.nav_drawer.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseActivity
import org.desperu.independentnews.di.module.ui.mainModule
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.extension.shareArticle
import org.desperu.independentnews.extension.showActivity
import org.desperu.independentnews.extension.showActivityForResult
import org.desperu.independentnews.extension.showInBrowser
import org.desperu.independentnews.helpers.DialogHelper
import org.desperu.independentnews.helpers.SnackBarHelper
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.repositories.IndependentNewsRepository
import org.desperu.independentnews.ui.firstStart.FirstStartActivity
import org.desperu.independentnews.ui.main.fragment.MainFragmentManager
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleItemViewModel
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleRouter
import org.desperu.independentnews.ui.settings.SettingsActivity
import org.desperu.independentnews.ui.sources.SourcesActivity
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.MainUtils.getDrawerItemIdFromFragKey
import org.desperu.independentnews.utils.MainUtils.setTitle
import org.desperu.independentnews.utils.Utils.isInternetAvailable
import org.koin.android.ext.android.get
import org.koin.core.parameter.parametersOf

var animationPlaybackSpeed: Double = 1.2 // Was 0.8
/**
 * The names of the arguments to received data to this Activity.
 */
const val TODAY_ARTICLES: String = "todayArticles"                  // For today article list
const val HAS_CHANGE: String = "hasChange"                          // For source state change
const val NEW_ARTICLES: String = "newArticles"                      // For new article find
const val UPDATED_USER_ARTICLES: String = "updatedUserArticles"     // For user articles state

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
    private val newArticles: Int get() = intent.getIntExtra(NEW_ARTICLES, 0)

    // FOR UI
    @JvmField @State var fragmentKey: Int = NO_FRAG
    private val drawerIcon: View by bindView(R.id.drawer_icon)
    // layout/nav_drawer views
    private val drawerLayout: DrawerLayout by bindView(R.id.drawer_layout)

    // FOR DATA
    private val fm by lazy { MainFragmentManager(supportFragmentManager, this as MainInterface) }
    override val mainLifecycleScope: LifecycleCoroutineScope = lifecycleScope
    private val ideNewsRepository = get<IndependentNewsRepository>()
    private lateinit var snackBarHelper: SnackBarHelper
    private lateinit var dialogHelper: DialogHelper

    /** Used to detect first apk start. */
    private val isFirstTime: Boolean
        get() = getSharedPreferences(INDEPENDENT_NEWS_PREFS, Context.MODE_PRIVATE)
            .getBoolean(IS_FIRST_TIME, FIRST_TIME_DEFAULT)

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_main

    override fun configureDesign() {
        firstStart()
        configureKoinDependency()
        appbar.showAppBarIcon(listOf(R.id.drawer_icon))
        configureDrawerLayout()
    }

    // -----------------
    // CONFIGURATION
    // -----------------

    /**
     * Configure koin dependency for main activity.
     */
    private fun configureKoinDependency() {
        get<MainInterface> { parametersOf(this) }
        snackBarHelper = get { parametersOf(this) }
        dialogHelper = get { parametersOf(this) }
        get<ArticleRouter> { parametersOf(this) }
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
    private fun showFragment(fragmentKey: Int, articleList: List<Article>?) {
        fm.configureAndShowFragment(
            when {
                articleList != null -> FRAG_TODAY_ARTICLES
                fragmentKey != NO_FRAG -> fragmentKey
                else -> FRAG_TOP_STORY
            },
            articleList
        )
        showTabLayout()
        setTitle(toolbar_title, this.fragmentKey)
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onStart() {
        super.onStart()
        content_loading_bar.hide() // Needed when back from show article
    }

    override fun onResume() {
        super.onResume()
        handleOnResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Dispatch Activity response on activity result.
        when (requestCode) {
            RC_FIRST_START -> handleFirstStartResponse(resultCode)
            RC_SHOW_ARTICLE -> handleShowArticleResponse(resultCode, data)
            RC_SOURCE -> handleSourceResponse(resultCode, data)
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.activity_main_menu_drawer_top_story -> showFragment(FRAG_TOP_STORY, null)
            R.id.activity_main_menu_drawer_categories -> showFragment(FRAG_CATEGORY, null)
            R.id.activity_main_menu_drawer_all_articles -> showFragment(FRAG_ALL_ARTICLES, null)
            R.id.activity_main_menu_drawer_sources -> showActivityForResult(SourcesActivity::class.java, RC_SOURCE)
            R.id.activity_main_menu_drawer_user_article -> showFragment(FRAG_USER_ARTICLE, null)
            R.id.activity_main_menu_drawer_refresh_data -> refreshData()
            R.id.activity_main_menu_drawer_settings -> showActivity(SettingsActivity::class.java)
            R.id.activity_main_drawer_about -> dialogHelper.showDialog(ABOUT)
            R.id.activity_main_drawer_help -> showInBrowser(DOCUMENTATION_URL)
            else -> {}
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() = when {
        // If drawer is open, close it.
        drawerLayout.isDrawerOpen(GravityCompat.START) ->
            drawerLayout.closeDrawer(GravityCompat.START)

        // If filter motion is expended, collapse it.
        view_pager.isVisible -> { close_icon.performClick(); Unit }

        // If current fragment is Top Story, remove it and call super to finish activity.
        fragmentKey == FRAG_TOP_STORY -> {
            fm.clearAllBackStack()
            super.onBackPressed()
        }
        // Else show previous fragment in back stack.
        else -> {
            fm.fragmentBack { super.onBackPressed() }
            showTabLayout()
            setTitle(toolbar_title, fragmentKey)
        }
    }

    override fun onUserInteraction() { super.onUserInteraction(); syncDrawerWithFrag() }

    // -----------------
    // DATA
    // -----------------

    /**
     * Show First Start activity if it's the first apk start.
     */
    private fun firstStart() {
        if (isFirstTime)
            showActivityForResult(FirstStartActivity::class.java, RC_FIRST_START)
    }

    /**
     * Share article with title and url, to other applications.
     *
     * @param title the title of the article.
     * @param url   the url of the article.
     */
    override fun shareArticle(title: String, url: String) =
        (this as Activity).shareArticle(title, url)

    /**
     * Refresh data for the application, fetch data from Rss and Web, and persist them
     * into the database.
     */
    override fun refreshData() {
        if (isInternetAvailable(this))
            lifecycleScope.launch(Dispatchers.IO) { ideNewsRepository.refreshData() }
        else
            dialogHelper.showDialog(CONNEXION) // Mistake on retry, no shown
    }

    /**
     * Apply selected filters to the current article list.
     * @param selectedMap the map of selected filters to apply.
     * @param isFiltered true if apply filters to the list, false otherwise.
     */
    override fun filterList(selectedMap: Map<Int, MutableList<String>>, isFiltered: Boolean) {
        fm.getCurrentArticleListFrag()?.filterList(selectedMap, isFiltered)
    }

    // --------------
    // UI
    // --------------

    /**
     * Show new downloaded articles.
     */
    override fun showNewArticles() {
        intent.removeExtra(NEW_ARTICLES)

        if (fragmentKey == FRAG_TOP_STORY)
            fm.getCurrentArticleListFrag()?.showNewArticles()
        else {
            fragmentKey = NO_FRAG
            showFragment(FRAG_TOP_STORY, null)
        }
    }

    /**
     * Update app bar on touch listener, used to finish app bar anim.
     */
    override fun updateAppBarOnTouch() = appbar.updateOnTouch()

    /**
     * Show or hide tab layout, depends of fragment key value.
     */
    private fun showTabLayout() {
        val toShow = fragmentKey in listOf(FRAG_CATEGORY, FRAG_USER_ARTICLE)
        app_bar_tab_layout.visibility = if (toShow) View.VISIBLE else View.GONE
    }

    /***
     * Synchronize the checked drawer item with the current fragment.
     */
    private fun syncDrawerWithFrag() =
        nav_view.setCheckedItem(getDrawerItemIdFromFragKey(fragmentKey))

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
        fm.getCurrentArticleListFrag()?.getRecyclerAdapter()?.getScaleDownAnimator(isScaledDown)

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
     * Handle response when retrieve first start result, if a fetching properly finish
     * show frag and do nothing here, else finish application.
     * @param resultCode the result code of request.
     */
    private fun handleFirstStartResponse(resultCode: Int) {
        when (resultCode) {
            RESULT_OK -> lifecycleScope.launch {
                fragmentKey = NO_FRAG // Force to reload
                snackBarHelper.userDismiss = true // Used to no display fetch result to user
                ideNewsRepository.fetchCategories()
                snackBarHelper.userDismiss = false
            }
            RESULT_CANCELED -> finishAffinity()
        }
    }

    /**
     * Handle response when retrieve show article result, if a user article state was updated,
     * refresh state data in it's view model.
     * @param resultCode    the result code of request.
     * @param data          the intent request result data.
     */
    private fun handleShowArticleResponse(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val updatedUserArticles = data?.getLongArrayExtra(UPDATED_USER_ARTICLES)

            val adapterList = fm.getCurrentArticleListFrag()?.getRecyclerAdapter()
                ?.adapterList?.map { it as ArticleItemViewModel }

            updatedUserArticles?.forEach { articleId: Long ->
                val articleItemVM = adapterList?.find { it.article.id == articleId }
                articleItemVM?.setUserArticleState()
            }
        }
    }

    /**
     * Handle response when retrieve source result, if a source state has changed,
     * update the article list.
     * @param resultCode    the result code of request.
     * @param data          the intent request result data.
     */
    private fun handleSourceResponse(resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val hasChange: Boolean? = data?.getBooleanExtra(HAS_CHANGE, false)

            // If has change, refresh the article list
            if (hasChange == true) fm.getCurrentArticleListFrag()?.refreshList()
        }
    }

    /**
     * Handle on resume activity, prompt user to show new articles if there's
     * and resume previous fragment previous fragment.
     */
    private fun handleOnResume() {
        if (newArticles != 0)
            lifecycleScope.launch {
                snackBarHelper.showMessage(END_FIND, listOf(newArticles.toString()))
            }

        if (!isFirstTime){
            showFragment(fragmentKey, todayArticles)
            intent?.removeExtra(TODAY_ARTICLES)
        }
    }
}