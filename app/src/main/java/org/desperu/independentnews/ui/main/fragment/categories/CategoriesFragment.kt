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
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null

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
        viewPager?.adapter = CategoriesAdapter(requireContext(), childFragmentManager,
            FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT)

        tabLayout = view?.rootView?.findViewById(R.id.app_bar_tab_layout)
        tabLayout?.setupWithViewPager(viewPager)
        tabLayout?.tabMode = TabLayout.MODE_FIXED
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onDestroyView() {
        super.onDestroyView()
        tabLayout = null
        viewPager = null
    }

    // --- GETTERS ---

    /**
     * Return the current fragment instance from the view pager.
     * @return the current fragment instance from the view pager.
     */
    internal fun getCurrentFrag(): ArticleListFragment =
        viewPager?.adapter?.instantiateItem(viewPager!!, viewPager!!.currentItem) as ArticleListFragment
}