package org.desperu.independentnews.ui.showArticle

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_show_article.*
import kotlinx.android.synthetic.main.app_bar.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.BaseActivity
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.ui.main.ToolbarBehavior
import org.desperu.independentnews.views.pageTransformer.ZoomOutPageTransformer

/**
 * The name of the argument to received article list for this Activity.
 */
const val ARTICLE_LIST: String = "articleList"

/**
 * The name of the argument to received the position of the clicked article in the list.
 */
const val POSITION: String = "position"

/**
 * Activity to show articles list.
 *
 * @constructor Instantiates a new ShowArticleActivity.
 */
class ShowArticleActivity: BaseActivity(), ShowArticleInterface {

    // FROM BUNDLE
    private val articleList: List<Article>? get() = intent.getParcelableArrayListExtra(ARTICLE_LIST)
    private val position: Int? get() = intent.getIntExtra(POSITION, 0)

    // FOR DATA
    private lateinit var viewPager: ViewPager
    private lateinit var mAdapter: ShowArticleAdapter

    /**
     * Companion object, used to redirect to this Activity.
     */
    companion object {
        /**
         * Redirects from an Activity to this Activity with transition animation.
         * @param activity the activity use to perform redirection.
         * @param articleList the article list to show in this activity.
         * @param position the position of the clicked article in the list.
         * @param clickedView the clicked article's image view to animate.
         */
        fun routeFromActivity(activity: AppCompatActivity,
                              articleList: ArrayList<Article>,
                              position: Int,
                              clickedView: View) {
            val intent = Intent(activity, ShowArticleActivity::class.java)
                .putParcelableArrayListExtra(ARTICLE_LIST, articleList)
                .putExtra(POSITION, position)

            // Start Animation
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val options = ActivityOptions.makeSceneTransitionAnimation(
                    activity,
                    clickedView,
                    activity.getString(R.string.animation_main_to_show_article) + position
                )
                activity.startActivity(intent, options.toBundle())
            } else {
                activity.startActivity(intent)
            }
        }
    }

    // --------------
    // BASE METHODS
    // --------------

    override fun getActivityLayout(): Int = R.layout.activity_show_article

    override fun configureDesign() {
        postponeSceneTransition()
        configureAppBar()
        configureViewPager()
        updateViewPager()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure App Bar.
     */
    private fun configureAppBar() {
        (appbar.layoutParams as CoordinatorLayout.LayoutParams).behavior = ToolbarBehavior()
        // TODO wrap toolbar in appBar to allow menu item usage
    }

    /**
     * Configure View pager with zoom out page transformer.
     */
    private fun configureViewPager() {
        viewPager = show_article_view_pager
        mAdapter = ShowArticleAdapter(supportFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        viewPager.adapter = mAdapter
        viewPager.setPageTransformer(true, ZoomOutPageTransformer())
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onStop() {// TODO not good
        removeTransitionAnimation()
        super.onStop()
    }

    // --------------
    // UI
    // --------------

    /**
     * Update view pager data, and set current item.
     */
    private fun updateViewPager() {
        articleList?.let { mAdapter.updateImageList(it) }
        mAdapter.notifyDataSetChanged()
        position?.let { viewPager.currentItem = it }
    }

    /**
     * Postpone the shared elements enter transition, because the shared elements
     * are in the fragment of the view pager.
     */
    private fun postponeSceneTransition() = supportPostponeEnterTransition()

    /**
     * Schedules the shared element transition to be started immediately
     * after the shared element has been measured and laid out within the
     * activity's view hierarchy.
     *
     * @param sharedElement the shared element to animate for the transition.
     */
    override fun scheduleStartPostponedTransition(sharedElement: View) {
        sharedElement.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    sharedElement.viewTreeObserver.removeOnPreDrawListener(this)
                    supportStartPostponedEnterTransition()
                    return true
                }
            }
        )
    }

    /**
     * Remove the transition scene animation from intent extra if the article shown
     * is different from the clicked article.
     */
    private fun removeTransitionAnimation() {
        if (position != viewPager.currentItem)
            intent.removeExtra("android.app.ActivityOptions")
    }

    // TODO update revert transition position of recycler view and views to animate !!! ActivityOptions.update

    // --------------
    // UTILS
    // --------------

    /**
     * Return the given fragment position into the view pager.
     * @param fragment the fragment to find position into the view pager view.
     */ // TODO remove unused...
    override fun getFragmentPosition(fragment: Fragment): Int? = viewPager.currentItem

    /**
     * Return the position of the clicked item into the recycler view.
     * @return the position of the clicked item into the recycler view.
     */
    override fun getClickedItemPosition(): Int? = position
}