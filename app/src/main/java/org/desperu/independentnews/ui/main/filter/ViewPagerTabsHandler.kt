package org.desperu.independentnews.ui.main.filter

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Build
import android.view.View
import androidx.cardview.widget.CardView
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.*
import org.desperu.independentnews.views.NoScrollHorizontalLayoutManager
import org.desperu.independentnews.views.NoScrollRecyclerView

/**
 * [FiltersMotionLayout] both use the same ViewPager2 and tab setup, hence
 * we abstracted away those functionalities here.
 *
 * This class is responsible for setting up the viewpager and tabs, syncing them and keeping track
 * of active filters
 */
@SuppressLint("WrongConstant")
class ViewPagerTabsHandler(
        private val viewPager: ViewPager2,
        private val tabsRecyclerView: NoScrollRecyclerView,
        private val bottomBarCardView: CardView
) {

    private val context = viewPager.context
    private val bottomBarColor: Int by bindColor(context, R.color.bottom_bar_color)
    private val bottomBarPinkColor: Int by bindColor(context, R.color.colorAccent)
    private val tabColor: Int by bindColor(context, R.color.tab_unselected_color)
    private val tabSelectedColor: Int by bindColor(context, R.color.tab_selected_color)

    private val tabItemWidth: Float by bindDimen(context, R.dimen.tab_item_width)
    private val filterLayoutPadding: Float by bindDimen(context, R.dimen.filter_layout_padding)

    private val toggleAnimDuration = context.resources.getInteger(R.integer.toggleAnimDuration).toLong()

    private lateinit var tabsAdapter: FiltersTabsAdapter
    private var totalTabsScroll = 0
    private var bottomBarAnimator: ValueAnimator? = null
    var hasActiveFilters = false
        private set

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    fun init() {
        // ViewPager & Tabs
        viewPager.offscreenPageLimit = context.resources.getStringArray(R.array.filter_tab_title).size
        tabsRecyclerView.updatePadding(right = (context.screenWidth - tabItemWidth - filterLayoutPadding).toInt())
        tabsRecyclerView.layoutManager = NoScrollHorizontalLayoutManager(context)

        // Sync Tabs And Pager
        tabsRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                totalTabsScroll += dx
            }
        })

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                // Scroll tabs as viewpager is scrolled
                val dx = (position + positionOffset) * tabItemWidth - totalTabsScroll
                tabsRecyclerView.scrollBy(dx.toInt(), 0)

                // This acts like a page transformer for tabsRecyclerView. Ideally we should do this in the
                // onScrollListener for the RecyclerView but that requires extra math. positionOffset
                // is all we need so let's use that to apply transformation to the tabs

                val previousTabView = tabsRecyclerView.layoutManager?.findViewByPosition(position - 1)
                val currentTabView = tabsRecyclerView.layoutManager?.findViewByPosition(position) ?: return
                val nextTabView = tabsRecyclerView.layoutManager?.findViewByPosition(position + 1)

                val defaultScale: Float = FiltersTabsAdapter.defaultScale
                val maxScale: Float = FiltersTabsAdapter.maxScale

                currentTabView.setScale(defaultScale + (1 - positionOffset) * (maxScale - defaultScale))
                nextTabView?.setScale(defaultScale + positionOffset * (maxScale - defaultScale))

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    currentTabView.findViewById<View>(R.id.tab_title).backgroundTintList =
                            ColorStateList.valueOf(
                                blendColors(
                                    tabColor,
                                    tabSelectedColor,
                                    1 - positionOffset
                                )
                            )
                    nextTabView?.findViewById<View>(R.id.tab_title)?.backgroundTintList =
                        ColorStateList.valueOf(
                            blendColors(
                                tabColor,
                                tabSelectedColor,
                                positionOffset
                            )
                        )
                } else {
                    previousTabView?.findViewById<View>(R.id.tab_title)?.setBackgroundResource(R.drawable.ic_tab_pill_unselected)
                    currentTabView.findViewById<View>(R.id.tab_title).setBackgroundResource(R.drawable.ic_tab_pill_selected)
                    nextTabView?.findViewById<View>(R.id.tab_title)?.setBackgroundResource(R.drawable.ic_tab_pill_unselected)
                }
            }
        })
    }

    /**
     * Used to set tab and view pager adapters and remove them when unnecessary.
     * This is done because keeping the adapters around when fab is never clicked
     * or when fab is collapsed is wasteful.
     */
    fun setAdapters(set: Boolean) {
        if (set) {
            viewPager.adapter = FiltersPagerAdapter(context!!, ::onFilterSelected)

            // Tabs
            tabsAdapter = FiltersTabsAdapter(context) { clickedPosition ->
                // smoothScroll = true will call the onPageScrolled callback which will smoothly
                // animate (transform) the tabs accordingly
                viewPager.setCurrentItem(clickedPosition, true)
            }
            tabsRecyclerView.adapter = tabsAdapter
        } else {
            viewPager.adapter = null
            tabsRecyclerView.adapter = null
            hasActiveFilters = false
            totalTabsScroll = 0
        }
    }

    /**
     * Callback method for [FiltersPagerAdapter]. When ever a filter is selected, adapter will call this function.
     * Animates the bottom bar to pink if there are any active filters and vice versa
     */
    private fun onFilterSelected(updatedPosition: Int, selectedMap: Map<Int, List<String>>) {
        val hasActiveFilters = selectedMap.filterValues { it.isNotEmpty() }.isNotEmpty()
        val bottomBarAnimator =
            if (hasActiveFilters && !this.hasActiveFilters) ValueAnimator.ofFloat(0f, 1f)
            else if (!hasActiveFilters && this.hasActiveFilters) ValueAnimator.ofFloat(1f, 0f)
            else null

        tabsAdapter.updateBadge(updatedPosition, !selectedMap[updatedPosition].isNullOrEmpty())

        bottomBarAnimator?.let {
            this.bottomBarAnimator = bottomBarAnimator.clone()
            this.hasActiveFilters = !this.hasActiveFilters
            this.bottomBarAnimator?.addUpdateListener { animation ->
                val color =
                    blendColors(
                        bottomBarColor,
                        bottomBarPinkColor,
                        animation.animatedValue as Float
                    )
                bottomBarCardView.setCardBackgroundColor(color)
            }
            this.bottomBarAnimator?.duration = toggleAnimDuration
            this.bottomBarAnimator?.start()
        }

        // To correct Motion Layout reset color when change tab and select filter
        if (bottomBarAnimator == null) {
            this.bottomBarAnimator?.duration = 1
            this.bottomBarAnimator?.start()
        }
    }
}