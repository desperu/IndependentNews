package org.desperu.independentnews.ui.showArticle

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.ui.showArticle.fragment.ArticleFragment

/**
 * Adapter class for Show Articles View Pager.
 *
 * @param fm the fragment manager to use for this view pager adapter.
 * @param behavior to determine if only current fragments are in resumed state.
 *
 * @constructor Instantiates a new ShowArticleAdapter.
 *
 * @property articleList the article list to show in the view pager.
 */
class ShowArticleAdapter(fm: FragmentManager?,
                         behavior: Int
) : FragmentPagerAdapter(fm!!, behavior) {

    private lateinit var articleList: List<Article>

    override fun getCount(): Int = if (::articleList.isInitialized) articleList.size else 0

    override fun getItem(position: Int): Fragment = ArticleFragment.newInstance(articleList[position], position)

    /**
     * Update all item list.
     * @param newArticleList the new article list to set.
     */
    internal fun updateImageList(newArticleList: List<Article>) { articleList = newArticleList }
}