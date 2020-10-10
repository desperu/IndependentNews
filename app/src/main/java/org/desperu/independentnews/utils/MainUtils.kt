package org.desperu.independentnews.utils

import androidx.fragment.app.Fragment
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
        else -> throw IllegalArgumentException("Fragment key not found : $fragmentKey")
    }

    /**
     * Retrieve the associated fragment key with the fragment instance.
     * @param fragment the given fragment from witch retrieved the key.
     * @return the corresponding fragment key.
     */
    internal fun retrievedKeyFromFrag(fragment: Fragment): Int = when(fragment) {
        is ArticleListFragment -> if (fragment.arguments?.getInt(FRAG_KEY) == FRAG_TOP_STORY) FRAG_TOP_STORY
                                  else FRAG_ALL_ARTICLES
        is CategoriesFragment -> FRAG_CATEGORY
        else -> throw IllegalArgumentException("Fragment class not found : ${fragment.tag}")
    }
}