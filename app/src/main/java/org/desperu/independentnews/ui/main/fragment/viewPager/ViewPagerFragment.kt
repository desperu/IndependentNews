package org.desperu.independentnews.ui.main.fragment.viewPager

import android.os.Bundle
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_view_pager.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseFragment
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListFragment
import org.desperu.independentnews.utils.FRAG_USER_ARTICLE
import org.desperu.independentnews.utils.NO_FRAG
import org.koin.core.KoinComponent

/**
 * The argument name for bundle to received the fragment key to this Fragment.
 */
const val VP_FRAG_KEY: String = "vpFragKey"

/**
 * Fragment to show article list fragment for each category, into a view pager.
 *
 * @constructor Instantiates a new CategoriesFragment.
 */
class ViewPagerFragment: BaseFragment(), KoinComponent {

    // FROM BUNDLE
    private val fragKey: Int? get() = arguments?.getInt(VP_FRAG_KEY, NO_FRAG)

    // FOR DATA
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var tabsHandler: TabsHandler? = null

    /**
     * Companion object, used to create a new instance of this fragment.
     */
    companion object {
        /**
         * Create a new instance of this fragment and set fragment key.
         * @param fragKey the fragment key to configure the data to show in this fragment.
         * @return the new instance of CategoriesFragment.
         */
        fun newInstance(fragKey: Int): ViewPagerFragment {
            val categoriesFragment = ViewPagerFragment()
            categoriesFragment.arguments = Bundle()
            categoriesFragment.arguments?.putInt(VP_FRAG_KEY, fragKey)
            return categoriesFragment
        }
    }

    // --------------
    // BASE METHODS
    // --------------

    override val fragmentLayout: Int = R.layout.fragment_view_pager

    override fun configureDesign() {}

    override fun updateDesign() {
        configureViewPager()
        setupTabLayout()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure View pager.
     */
    private fun configureViewPager() {
        viewPager = list_view_pager
        viewPager?.adapter = fragKey?.let {
            ViewPagerAdapter(
                it,
                childFragmentManager,
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            )
        }
    }

    /**
     * Setup tab layout with view pager, add Tabs Handler support, only for user article frag.
     */
    private fun setupTabLayout() {
        tabLayout = view?.rootView?.findViewById(R.id.app_bar_tab_layout)
        tabLayout?.setupWithViewPager(viewPager)
        tabLayout?.tabMode = TabLayout.MODE_FIXED

        if (fragKey == FRAG_USER_ARTICLE) {
            tabsHandler = TabsHandler(this, fragKey ?: NO_FRAG)
            tabsHandler?.pageChangeListener?.let { viewPager?.addOnPageChangeListener(it) }
        }
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onDestroyView() {
        if (tabLayout?.isShown == false) {
            tabLayout?.setupWithViewPager(null)
            tabsHandler?.pageChangeListener?.let { viewPager?.removeOnPageChangeListener(it) } // Useless, always removed with viewPager = null
        }
        tabsHandler = null
        viewPager = null
        super.onDestroyView()
    }

    // --- GETTERS ---

    /**
     * Return the current fragment instance from the view pager.
     * @return the current fragment instance from the view pager.
     */
    internal fun getCurrentFrag(): ArticleListFragment =
        viewPager?.adapter?.instantiateItem(viewPager!!, viewPager!!.currentItem) as ArticleListFragment
}