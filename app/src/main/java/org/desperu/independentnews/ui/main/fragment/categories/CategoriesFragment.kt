package org.desperu.independentnews.ui.main.fragment.categories

import android.os.Bundle
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_categories.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseFragment
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListFragment

/**
 * The argument name for bundle to received the category key to this Fragment.
 */
const val CAT_KEY: String = "catKey"

/**
 * Fragment to show article categories.
 *
 * @constructor Instantiates a new CategoriesFragment.
 */
class CategoriesFragment: BaseFragment() {

    // FOR DATA
    private lateinit var viewPager: ViewPager
    private val tabLayout: TabLayout? by lazy { view?.rootView?.findViewById<TabLayout>(R.id.app_bar_tab_layout) }

    // FOR INTENT
    private val catKey: Int? get() = arguments?.getInt(CAT_KEY, 0)
// TODO to remove
    /**
     * Companion object, used to create a new instance of this fragment.
     */
    companion object {
        /**
         * Create a new instance of this fragment and set article.
         * @param catKey the category key to configure the data to show in this fragment.
         * @return the new instance of CategoriesFragment.
         */
        fun newInstance(catKey: Int): CategoriesFragment {
            val categoriesFragment = CategoriesFragment()
            categoriesFragment.arguments = Bundle()
            categoriesFragment.arguments?.putInt(CAT_KEY, catKey)
            return categoriesFragment
        }
    }

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