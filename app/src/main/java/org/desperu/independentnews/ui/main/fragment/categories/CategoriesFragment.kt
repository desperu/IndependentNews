package org.desperu.independentnews.ui.main.fragment.categories

import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_categories.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseFragment
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListFragment

/**
 * Fragment to show article list fragment for each category, into a view pager.
 *
 * @constructor Instantiates a new CategoriesFragment.
 */
class CategoriesFragment: BaseFragment() {

    // FOR DATA
    private lateinit var viewPager: ViewPager
    private val tabLayout: TabLayout? by lazy { view?.rootView?.findViewById<TabLayout>(R.id.app_bar_tab_layout) }

    // --------------
    // BASE METHODS
    // --------------

    override val fragmentLayout: Int = R.layout.fragment_categories

    override fun configureDesign() {}

    override fun updateDesign() {
        configureViewPagerAndTabs()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure Tab layout and View pager.
     */
    private fun configureViewPagerAndTabs() {
        viewPager = categories_view_pager
        viewPager.adapter = CategoriesAdapter(requireContext(), childFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)
        tabLayout?.setupWithViewPager(viewPager)
        tabLayout?.tabMode = TabLayout.MODE_FIXED
    }

    // --- GETTERS ---

    /**
     * Return the current fragment instance from the view pager.
     * @return the current fragment instance from the view pager.
     */
    internal fun getCurrentFrag(): ArticleListFragment =
        viewPager.adapter?.instantiateItem(viewPager, viewPager.currentItem) as ArticleListFragment
}