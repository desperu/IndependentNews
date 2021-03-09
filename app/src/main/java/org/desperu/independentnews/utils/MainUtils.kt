package org.desperu.independentnews.utils

import android.widget.TextView
import androidx.fragment.app.Fragment
import org.desperu.independentnews.R
import org.desperu.independentnews.service.ResourceService
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListFragment
import org.desperu.independentnews.ui.main.fragment.articleList.FRAG_KEY
import org.desperu.independentnews.ui.main.fragment.categories.CategoriesFragment
import org.desperu.independentnews.ui.main.fragment.categories.VP_FRAG_KEY
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * MainUtils object witch provide utils functions for main activity.
 */
object MainUtils : KoinComponent{

    // FOR DATA
    private val resources: ResourceService get() = get()

    // -----------------
    // FRAGMENT
    // -----------------

    /**
     * Get the associated fragment with the given fragment key.
     *
     * @param fragmentKey the given fragment key from witch get the key.
     *
     * @return the corresponding fragment instance.
     *
     * @throws IllegalArgumentException if the [fragmentKey] was not found.
     */
    internal fun getFragFromKey(fragmentKey: Int): Fragment = when(fragmentKey) {
        FRAG_TOP_STORY -> ArticleListFragment.newInstance(fragmentKey)
        FRAG_CATEGORY -> CategoriesFragment.newInstance(fragmentKey)
        FRAG_ALL_ARTICLES -> ArticleListFragment.newInstance(fragmentKey)
        FRAG_USER_ARTICLE -> CategoriesFragment.newInstance(fragmentKey)
        FRAG_TODAY_ARTICLES -> ArticleListFragment.newInstance(fragmentKey)
        else -> throw IllegalArgumentException("Fragment key not found : $fragmentKey")
    }

    /**
     * Retrieve the associated fragment key with the fragment instance.
     *
     * @param fragment the given fragment from witch retrieved the key.
     *
     * @return the corresponding fragment key.
     *
     * @throws IllegalArgumentException if the [fragment] class was not found.
     */
    internal fun retrievedKeyFromFrag(fragment: Fragment): Int =
        fragment.arguments?.getInt(FRAG_KEY)
            ?: fragment.arguments?.getInt(VP_FRAG_KEY)
            ?: throw IllegalArgumentException(
                "Fragment class not found : ${fragment.javaClass.simpleName}"
            )

    // -----------------
    // UI
    // -----------------

    /**
     * Set the title activity name for the asked fragment.
     *
     * @param titleView     the text view to set title.
     * @param fragmentKey   the key of the asked fragment.
     */
    internal fun setTitle(titleView: TextView, fragmentKey: Int) {
        titleView.text =
            resources.getString(
                when (fragmentKey) {
                    FRAG_TOP_STORY -> R.string.navigation_drawer_top_story
                    FRAG_ALL_ARTICLES -> R.string.navigation_drawer_all_articles
                    FRAG_USER_ARTICLE -> R.string.navigation_drawer_user_articles
                    FRAG_TODAY_ARTICLES -> R.string.fragment_today_articles
                    else -> R.string.app_name
                }
            )
    }

    // -----------------
    // MENU DRAWER
    // -----------------

    /**
     * Get the menu drawer item id from fragment key.
     *
     * @param fragmentKey the given fragment key from which get the id.
     *
     * @return the corresponding item id.
     *
     * @throws IllegalArgumentException if the [fragmentKey] was not found.
     */
    internal fun getDrawerItemIdFromFragKey(fragmentKey: Int) = when(fragmentKey) {
        NO_FRAG -> 0
        FRAG_TOP_STORY -> R.id.activity_main_menu_drawer_top_story
        FRAG_CATEGORY -> R.id.activity_main_menu_drawer_categories
        FRAG_ALL_ARTICLES -> R.id.activity_main_menu_drawer_all_articles
        FRAG_USER_ARTICLE -> R.id.activity_main_menu_drawer_user_article
        FRAG_TODAY_ARTICLES -> 0
        else -> throw IllegalArgumentException("Fragment key not found : $fragmentKey")
    }
}