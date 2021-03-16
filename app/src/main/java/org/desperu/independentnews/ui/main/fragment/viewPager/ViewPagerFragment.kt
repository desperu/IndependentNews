package org.desperu.independentnews.ui.main.fragment.viewPager

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_categories.*
import org.desperu.independentnews.R
import org.desperu.independentnews.base.ui.BaseFragment
import org.desperu.independentnews.extension.design.bindColor
import org.desperu.independentnews.extension.design.blendColors
import org.desperu.independentnews.ui.main.fragment.articleList.ArticleListFragment
import org.desperu.independentnews.utils.FRAG_USER_ARTICLE
import org.desperu.independentnews.utils.NO_FRAG
import org.desperu.independentnews.utils.USER_ARTICLE_ICONS

/**
 * The argument name for bundle to received the fragment key to this Fragment.
 */
const val VP_FRAG_KEY: String = "vpFragKey"

/**
 * Fragment to show article list fragment for each category, into a view pager.
 *
 * @constructor Instantiates a new CategoriesFragment.
 */
class ViewPagerFragment: BaseFragment() {

    // FOR DATA
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null

    // FOR BUNDLE
    private val fragKey: Int? get() = arguments?.getInt(VP_FRAG_KEY, NO_FRAG)

    // FOR COLORS
    private val tabIconColor by bindColor(R.color.title_color)
    private val tabSelectedIconColor by bindColor(R.color.list_item_bg_collapsed)
    private val badgeBg by bindColor(R.color.filter_pill_color)
    private val badgeTextColor by bindColor(R.color.toolbar_title_color)

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

    override val fragmentLayout: Int = R.layout.fragment_categories

    override fun configureDesign() {}

    override fun updateDesign() {
        configureViewPagerAndTabs()
        setupViewPagerListener()
        updateTabs()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Configure Tab layout and View pager.
     */
    private fun configureViewPagerAndTabs() {
        viewPager = categories_view_pager
        viewPager?.adapter = fragKey?.let {
            ViewPagerAdapter(
                it,
                childFragmentManager,
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
            )
        }

        tabLayout = view?.rootView?.findViewById(R.id.app_bar_tab_layout)
        tabLayout?.setupWithViewPager(viewPager)
        tabLayout?.tabMode = TabLayout.MODE_FIXED
    }

    /**
     * Setup page changed listener, only for user articles frag.
     */
    private fun setupViewPagerListener() {
        if (fragKey == FRAG_USER_ARTICLE)
            viewPager?.addOnPageChangeListener(pageChangedListener)
    }

    // -----------------
    // METHODS OVERRIDE
    // -----------------

    override fun onDestroyView() {
        if (tabLayout?.isShown == false) {
            tabLayout?.setupWithViewPager(null)
            viewPager?.removeOnPageChangeListener(pageChangedListener) // Useless, always removed with viewPager = null
        }
        viewPager = null
        super.onDestroyView()
    }

    // --------------
    // UI
    // --------------

    /**
     * Update tabs if needed, only for User Article, to set icons.
     */
    private fun updateTabs() {
        if (fragKey == FRAG_USER_ARTICLE) {
            USER_ARTICLE_ICONS.forEachIndexed { index, iconId ->

                val tab = tabLayout?.getTabAt(index)
                tab?.icon = ResourcesCompat.getDrawable(resources, iconId, null)

                val badge = tab?.orCreateBadge
                badge?.number = +index
                badge?.backgroundColor = badgeBg
                badge?.badgeTextColor = badgeTextColor
            }
        }
    }

    // --------------
    // LISTENER
    // --------------

    /**
     * On Page changed listener, for view pager, used to animate
     * changed tab icon color into user articles tabs.
     * Support API 19 and >= LOLLIPOP.
     */
    @Suppress("Deprecation")
    private val pageChangedListener = object : OnPageChangeListener {

        override fun onPageScrolled(
            position: Int,
            positionOffset: Float,
            positionOffsetPixels: Int
        ) {

            // Be careful, position always start at 0, and that works set of that...
            val previousTab = tabLayout?.getTabAt(position - 1)
            val currentTab = tabLayout?.getTabAt(position)
            val nextTab = tabLayout?.getTabAt(position + 1)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                currentTab?.icon?.setTintList(
                    ColorStateList.valueOf(
                        blendColors(tabIconColor, tabSelectedIconColor, 1 - positionOffset)
                    )
                )

                nextTab?.icon?.setTintList(
                    ColorStateList.valueOf(
                        blendColors(tabIconColor, tabSelectedIconColor, positionOffset)
                    )
                )
            } else {
                previousTab?.icon?.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
                currentTab?.icon?.setColorFilter(tabSelectedIconColor, PorterDuff.Mode.SRC_IN)
                nextTab?.icon?.setColorFilter(tabIconColor, PorterDuff.Mode.SRC_IN)
            }
        }

        override fun onPageSelected(position: Int) {
            tabLayout?.getTabAt(position)?.removeBadge()
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    // --- GETTERS ---

    /**
     * Return the current fragment instance from the view pager.
     * @return the current fragment instance from the view pager.
     */
    internal fun getCurrentFrag(): ArticleListFragment =
        viewPager?.adapter?.instantiateItem(viewPager!!, viewPager!!.currentItem) as ArticleListFragment
}