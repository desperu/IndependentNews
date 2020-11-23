package org.desperu.independentnews.utils

import androidx.fragment.app.Fragment
import org.desperu.independentnews.R
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListFragment
import org.desperu.independentnews.ui.main.fragment.articleList.FRAG_KEY
import org.desperu.independentnews.ui.main.fragment.categories.CategoriesFragment

/**
 * MainUtils object witch provide utils functions for main activity.
 */
object MainUtils {

    // -----------------
    // FRAGMENT
    // -----------------

    /**
     * Get the associated fragment with the given fragment key.
     * @param fragmentKey the given fragment key from witch get the key.
     * @return the corresponding fragment instance.
     */
    internal fun getFragFromKey(fragmentKey: Int): Fragment = when(fragmentKey) {
        FRAG_TOP_STORY -> ArticleListFragment()
        FRAG_CATEGORY -> CategoriesFragment()
        FRAG_ALL_ARTICLES -> ArticleListFragment()
        FRAG_TODAY_ARTICLES -> ArticleListFragment()
        else -> throw IllegalArgumentException("Fragment key not found : $fragmentKey")
    }

    /**
     * Retrieve the associated fragment key with the fragment instance.
     * @param fragment the given fragment from witch retrieved the key.
     * @return the corresponding fragment key.
     */
    internal fun retrievedKeyFromFrag(fragment: Fragment): Int = when(fragment) {
        is CategoriesFragment -> FRAG_CATEGORY
        else -> fragment.arguments?.getInt(FRAG_KEY)
            ?: throw IllegalArgumentException("Fragment class not found : ${fragment.javaClass.simpleName}")
    }

    // -----------------
    // MENU DRAWER
    // -----------------

    /**
     * Get the menu drawer item id from fragment key.
     * @param fragmentKey the given fragment key from which get the id.
     * @return the corresponding item id.
     */
    internal fun getDrawerItemIdFromFragKey(fragmentKey: Int) = when(fragmentKey) {
        FRAG_TOP_STORY -> R.id.activity_main_menu_drawer_top_story
        FRAG_CATEGORY -> R.id.activity_main_menu_drawer_categories
        FRAG_ALL_ARTICLES -> R.id.activity_main_menu_drawer_all_articles
        FRAG_TODAY_ARTICLES -> 0 // TODO to check when click on notification
        else -> throw IllegalArgumentException("Fragment key not found : $fragmentKey")
    }
}