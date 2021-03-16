package org.desperu.independentnews.ui.main.fragment.viewPager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import org.desperu.independentnews.R
import org.desperu.independentnews.service.ResourceService
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListFragment
import org.desperu.independentnews.utils.FRAG_CATEGORY
import org.desperu.independentnews.utils.FRAG_ECOLOGY
import org.desperu.independentnews.utils.FRAG_FAVORITE
import org.desperu.independentnews.utils.USER_ARTICLE_ICONS
import org.koin.java.KoinJavaComponent.getKoin

/**
 * View Pager Adapter that handle categories or user articles fragments,
 * depends of fragment key value.
 *
 * @property fragKey    the fragment key used to determine the adapter data.
 *
 * @constructor Instantiate a new [ViewPagerAdapter].
 *
 * @param fragKey
 * @param fm            fragment manager that will interact with this adapter.
 * @param behavior      determines if only current fragments are in a resumed state.
 *
 * @author Desperu
 */
class ViewPagerAdapter(
    private val fragKey: Int,
    fm: FragmentManager,
    behavior: Int
) : FragmentPagerAdapter(fm, behavior) {

    // FOR DATA
    private val isCategories = fragKey == FRAG_CATEGORY
    private val tabTitles =
        getKoin().get<ResourceService>().getStringArray(R.array.categories_tab_titles)

    override fun getCount(): Int = if (isCategories) tabTitles.size else USER_ARTICLE_ICONS.size

    override fun getItem(position: Int): Fragment =
        ArticleListFragment.newInstance(
            position +
                    // Needed decal and switch value in ArticleListFragment for all case.
                    if (isCategories) FRAG_ECOLOGY
                    else FRAG_FAVORITE
        )

    override fun getPageTitle(position: Int): CharSequence? =
        if (isCategories) tabTitles[position]
        else null // We use icons
}