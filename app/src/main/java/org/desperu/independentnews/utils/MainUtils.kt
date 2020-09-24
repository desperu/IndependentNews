package org.desperu.independentnews.utils

import androidx.fragment.app.Fragment
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListFragment

object MainUtils {

    // -----------------
    // FRAGMENT
    // -----------------

    /**
     * Get the associated fragment class with the given fragment key.
     * @param fragmentKey the given fragment key from witch get the key.
     * @return the corresponding fragment class.
     */
    @Suppress("unchecked_cast")
    internal fun <T: Fragment> getFragClassFromKey(fragmentKey: Int): Class<T> = when (fragmentKey) {
        FRAG_ARTICLE_LIST -> ArticleListFragment::class.java as Class<T>
        else -> throw IllegalArgumentException("Fragment key not found : $fragmentKey")
    }

    /**
     * Retrieve the associated fragment key with the fragment class.
     * @param fragClass the given fragment class from witch retrieved the key.
     * @return the corresponding fragment key.
     */
    internal fun <T: Fragment> retrievedFragKeyFromClass(fragClass: Class<T>) = when (fragClass) {
        ArticleListFragment::class.java -> FRAG_ARTICLE_LIST
        else -> throw IllegalArgumentException("Fragment class not found : ${fragClass.simpleName}")
    }
}