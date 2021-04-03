package org.desperu.independentnews.ui.main.fragment.viewPager

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Build
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindColor
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.extension.design.blendColors
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.USER_ARTICLE_ICONS
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * Tabs Handler used to mange tab layout for icon color animation and badge.
 *
 * @property fragment   the fragment that's owns this tabs handler.
 * @property fragKey    the key of the fragment.
 * @property prefs      the shared preferences service interface access.
 * @property viewPager  the view pager to associate with tab layout.
 * @property tabLayout  the tab layout to handle.
 *
 * @constructor Instantiate a new TabsHandler.
 *
 * @param fragment      the fragment that's owns this tabs handler to set.
 * @param fragKey       the key of the fragment to set.
 *
 * @author Desperu
 */
class TabsHandler (
    private val fragment: Fragment,
    private val fragKey: Int
) : KoinComponent {

    // FOR DATA
    private val prefs: SharedPrefService = get()

    // FOR UI
    private val viewPager: ViewPager by bindView(fragment, R.id.list_view_pager)
    private var tabLayout: TabLayout? = fragment.view?.rootView?.findViewById(R.id.app_bar_tab_layout)

    // FOR COLORS
    private val tabIconColor by bindColor(viewPager, R.color.title_color)
    private val tabSelectedIconColor by bindColor(viewPager, R.color.list_item_bg_collapsed)
    private val badgeBg by bindColor(viewPager, R.color.filter_pill_color)
    private val badgeTextColor by bindColor(viewPager, R.color.toolbar_title_color)

    init {
        setupViewPagerListener()
        updateTabs()
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Setup page changed listener, only for user articles frag.
     */
    private fun setupViewPagerListener() {
        if (fragKey == FRAG_USER_ARTICLE)
            viewPager.addOnPageChangeListener(pageChangeListener)
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
                tab?.icon = ResourcesCompat.getDrawable(fragment.resources, iconId, null)

                val added = prefs.getPrefs().getInt(getKey(iconId), getDefVal(iconId))

                if (added > 0) {
                    val badge = tab?.orCreateBadge
                    badge?.number = added
                    badge?.backgroundColor = badgeBg
                    badge?.badgeTextColor = badgeTextColor
                }
            }
        }
    }

    // --------------
    // LISTENER
    // --------------

    /**
     * On Page change listener, for view pager, used to animate
     * changed tab icon color into user articles tabs.
     * Support API 19 and >= LOLLIPOP.
     */
    @Suppress("Deprecation")
    internal val pageChangeListener = object : ViewPager.OnPageChangeListener {

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
            // Remove badge and clear shared preferences value.
            tabLayout?.getTabAt(position)?.removeBadge()
            val iconId = USER_ARTICLE_ICONS[position]
            prefs.getPrefs().edit().putInt(getKey(iconId), getDefVal(iconId)).apply()
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }

    // --------------
    // UTILS
    // --------------

    /**
     * Returns the shared preferences kay for the given icon id.
     *
     * @param iconId the unique identifier of the icon.
     *
     * @return the shared preferences key for the given icon id.
     *
     * @throws IllegalArgumentException when the icon id is not found.
     */
    private fun getKey(iconId: Int) = when(iconId) {
        R.drawable.ic_baseline_star_black_24 -> ADDED_FAVORITE
        R.drawable.ic_baseline_pause_black_24 -> ADDED_PAUSED
        else -> throw IllegalArgumentException("Icon id not found : $iconId")
    }

    /**
     * Returns the default value for the given icon id.
     *
     * @param iconId the unique identifier of the icon.
     *
     * @return the default value for the given icon id.
     *
     * @throws IllegalArgumentException when the icon id is not found.
     */
    private fun getDefVal(iconId: Int) = when(iconId) {
        R.drawable.ic_baseline_star_black_24 -> ADDED_FAVORITE_DEFAULT
        R.drawable.ic_baseline_pause_black_24 -> ADDED_PAUSED_DEFAULT
        else -> throw IllegalArgumentException("Icon id not found : $iconId")
    }
}