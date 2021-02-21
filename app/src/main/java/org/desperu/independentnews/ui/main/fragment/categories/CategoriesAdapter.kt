package org.desperu.independentnews.ui.main.fragment.categories

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.desperu.independentnews.R
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListFragment

class CategoriesAdapter(mContext: Context,
                        fm: FragmentManager?,
                        behavior: Int) : FragmentPagerAdapter(fm!!, behavior) {

    private val tabTitles = mContext.resources.getStringArray(R.array.categories_tab_titles)

    override fun getCount(): Int = tabTitles.size

    override fun getItem(position: Int): Fragment = ArticleListFragment.newInstance(position + 3)

    override fun getPageTitle(position: Int): CharSequence? = tabTitles[position]
}