package org.desperu.independentnews.ui.main.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import org.desperu.independentnews.R
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.ui.main.MainInterface
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListFragment
import org.desperu.independentnews.ui.main.fragment.articleList.TODAY_ARTICLES_FRAG
import org.desperu.independentnews.ui.main.fragment.viewPager.ViewPagerFragment
import org.desperu.independentnews.utils.FRAG_CATEGORY
import org.desperu.independentnews.utils.FRAG_TOP_STORY
import org.desperu.independentnews.utils.FRAG_USER_ARTICLE
import org.desperu.independentnews.utils.MainUtils.getFragFromKey
import org.desperu.independentnews.utils.MainUtils.retrievedKeyFromFrag
import org.koin.core.component.KoinComponent

/**
 * Main Fragment Manager class witch manage all fragments for main activity.
 *
 * @property fm                 the FragmentManager of the ui part to manage.
 * @property mainInterface      the main interface that allow communication with activity.
 *
 * @constructor Instantiates a new MainFragmentManager.
 *
 * @param fm                    the FragmentManager of the ui part to manage to set.
 * @param mainInterface         the main interface that allow communication with activity to set.
 */
class MainFragmentManager(
    private val fm: FragmentManager,
    private val mainInterface: MainInterface
): KoinComponent {

    // FOR DATA
    private val fragmentKey get() = mainInterface.getFragmentKey()
    private var backCount = 0

    // --------------
    // FRAGMENT MANAGEMENT
    // --------------

    /**
     * Configure and show fragments, with back stack management to restore instance.
     *
     * @param fragmentKey the fragment key to show corresponding fragment.
     * @param articleList the article list to show in the article list fragment.
     */
    internal fun configureAndShowFragment(fragmentKey: Int, articleList: List<Article>?) {
        if (this.fragmentKey != fragmentKey || articleList != null) {
            mainInterface.setFragmentKey(fragmentKey)

            // Get the fragment instance from the fragment key
            val fragment = getFragFromKey(fragmentKey)

            // Populate data to fragment with bundle.
            populateDataToFragment(fragment, articleList)

            // Clear all back stack when recall top story Fragment,
            // because it's the root fragment of this activity.
            if (fragmentKey == FRAG_TOP_STORY) clearAllBackStack()

            // Apply the fragment transaction in the corresponding frame.
            fragmentTransaction(fragment)
        }
    }

    /**
     * Populate data to fragment with bundle.
     * @param fragment      the fragment instance to send data.
     * @param articleList   the article list to send to the fragment, to show it.
     */
    private fun populateDataToFragment(fragment: Fragment, articleList: List<Article>?) {
        // Populate article lit to fragment with bundle if there's one.
        articleList?.let { populateArticleListToFragment(fragment, it) }
    }

    /**
     * Show fragment in corresponding container, add to back stack and set transition.
     * @param fragment the fragment to show in the frame layout.
     */
    private fun fragmentTransaction(fragment: Fragment) {
        if (!fm.isDestroyed) {
            fm.beginTransaction()
                .replace(R.id.main_frame_container, fragment, fragment.javaClass.simpleName)
                .addToBackStack(fragment.javaClass.simpleName)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit()
        }
    }

    /**
     * Remove all fragments from the back stack.
     */
    internal fun clearAllBackStack() {
        while (fm.backStackEntryCount > 0) fm.popBackStackImmediate()
    }

    // --------------
    // SHOW FRAGMENT
    // --------------

    /**
     * Show previous fragment in back stack, with onBackPressed support and set fragmentKey with restored fragment.
     * @param block the super onBackPressed() call.
     */
    internal fun fragmentBack(block: () -> Unit) {
        val tempFragmentKey = fragmentKey
        block()
        getCurrentFragment()?.let { mainInterface.setFragmentKey(retrievedKeyFromFrag(it)) }

        if (backCount == 3) {
            backCount = 0
            return
        } else if (tempFragmentKey == fragmentKey) {
            backCount += 1
            fragmentBack(block)
        }
    }

    // --------------
    // BUNDLE
    // --------------

    /**
     * Set bundle instance only if given is null.
     * @param bundle the bundle to set.
     */
    private fun setBundle(bundle: Bundle?): Bundle = bundle ?: Bundle()

    /**
     * Populate article list to fragment with bundle.
     * @param fragment the fragment instance to send article list.
     */
    private fun populateArticleListToFragment(fragment: Fragment, articleList: List<Article>) {
        fragment.arguments = setBundle(fragment.arguments)
        fragment.arguments?.putParcelableArrayList(TODAY_ARTICLES_FRAG, ArrayList(articleList))
    }

    // --------------
    // GETTERS
    // --------------

    // Try to get Fragment instance from current and back stack, if not found value was null.
    private val articleListFragment
        get() = (fm.findFragmentByTag(ArticleListFragment::class.java.simpleName) as ArticleListFragment?)

    private val viewPagerFragment
        get() = (fm.findFragmentByTag(ViewPagerFragment::class.java.simpleName) as ViewPagerFragment?)

    /**
    * Returns the current fragment instance attached to the frame layout main container.
    * @return the current fragment instance attached to the frame layout main container.
    */
    internal fun getCurrentFragment(): Fragment? = fm.findFragmentById(R.id.main_frame_container)

    /**
     * Returns the current article list fragment instance attached to the frame layout.
     * @return the current article list fragment instance attached to the frame layout.
     */
    internal fun getCurrentArticleListFrag(): ArticleListFragment? =
        if (fragmentKey in listOf(FRAG_CATEGORY, FRAG_USER_ARTICLE))
            viewPagerFragment?.getCurrentFrag()
        else
            articleListFragment

}