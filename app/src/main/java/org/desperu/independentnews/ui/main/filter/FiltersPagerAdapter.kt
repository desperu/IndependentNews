package org.desperu.independentnews.ui.main.filter

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import androidx.recyclerview.widget.RecyclerView
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindColor
import org.desperu.independentnews.extension.design.bindOptionalViews
import org.desperu.independentnews.extension.design.blendColors
import org.desperu.independentnews.extension.design.getValueAnimator
import org.desperu.independentnews.utils.FilterUtils.filterViewsId
import org.desperu.independentnews.utils.FilterUtils.getFilterValue
import org.desperu.independentnews.views.FilterSeekbar

/**
 * ViewPager adapter to display all the filters
 */
class FiltersPagerAdapter(private val context: Context, private val listener: (updatedPosition: Int, selectedMap: Map<Int, List<String>>) -> Unit)
    : RecyclerView.Adapter<FiltersPagerAdapter.FiltersPagerViewHolder>() {

    private val unselectedColor: Int by bindColor(context, R.color.filter_pill_color)
    private val selectedColor: Int by bindColor(context, R.color.filter_pill_selected_color)
    private val unselectedBarColor: Int by bindColor(context, R.color.filter_seek_bar_color)
    private val selectedBarColor: Int by bindColor(context, R.color.filter_seek_bar_selected_color)

    private val toggleAnimDuration = context.resources.getInteger(R.integer.toggleAnimDuration).toLong()
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var selectedMap = mutableMapOf<Int, MutableList<String>>()

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun getItemCount(): Int = FiltersMotionLayout.numTabs

    override fun getItemViewType(position: Int): Int = when {
        position == 2 -> R.layout.filter_layout_dates
        position % 2 == 0 -> R.layout.filter_layout_sources
        else -> R.layout.filter_layout_themes
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiltersPagerViewHolder =
            FiltersPagerViewHolder(inflater.inflate(viewType, parent, false))

    override fun onBindViewHolder(holder: FiltersPagerViewHolder, position: Int) {
        val selectedList = selectedMap.getOrPut(position) { mutableListOf() }

        /**
         * Bind all the filter buttons (if any). Clicking the filter button toggles state
         * which is shown by a short toggle animation
         */
        holder.filterViews.forEachIndexed { _: Int, filterView: View ->
            filterView.setOnClickListener {

                val filterValue = getFilterValue(context, filterView.id)
                val isToggled = selectedList.contains(filterValue)

                if (isToggled) selectedList -= filterValue
                else selectedList += filterValue

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    val toggleAnimator =
                        getValueAnimator(
                            !isToggled,
                            toggleAnimDuration, DecelerateInterpolator()
                        ) { progress ->

                            filterView.backgroundTintList =
                                ColorStateList.valueOf(
                                    blendColors(
                                        unselectedColor,
                                        selectedColor,
                                        progress
                                    )
                                )
                        }
                    toggleAnimator.start()
                } else
                    filterView.setBackgroundResource(
                        if (isToggled) R.drawable.ic_filter_pill_unselected
                        else R.drawable.ic_filter_pill_selected
                    )

                listener(position, selectedMap)
            }
        }

        /**
         * Bind the Seekbars (if any). Sliding the seekbar between 1f..99f toggles it on.
         * 1f and 99f are chosen just to make the toggling seem more smooth
         */
        holder.seekBars.forEachIndexed { _: Int, seekBar: FilterSeekbar ->
            seekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
                if (!selectedList.contains("date") && !(minValue.toFloat() < 1f && maxValue.toFloat() > 99f)) {
                    selectedList += "$minValue, $maxValue"
                    listener(position, selectedMap)
                    seekBar.setLeftThumbHighlightColor(selectedColor)
                    seekBar.setRightThumbHighlightColor(selectedColor)
                    seekBar.setLeftThumbColor(selectedColor)
                    seekBar.setRightThumbColor(selectedColor)
                    seekBar.setBarHighlightColor(selectedBarColor)
                } else if (selectedList.contains("date2") && minValue.toFloat() < 1f && maxValue.toFloat() > 99f) {
                    selectedList -= "$minValue, $maxValue"
                    listener(position, selectedMap)
                    seekBar.setLeftThumbHighlightColor(unselectedColor)
                    seekBar.setRightThumbHighlightColor(unselectedColor)
                    seekBar.setLeftThumbColor(unselectedColor)
                    seekBar.setRightThumbColor(unselectedColor)
                    seekBar.setBarHighlightColor(unselectedBarColor)
                }
            }
        }
    }

    class FiltersPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filterViews: List<View> by bindOptionalViews(*filterViewsId.toIntArray())

        val seekBars: List<FilterSeekbar> by bindOptionalViews(R.id.rangeSeekbar1, R.id.rangeSeekbar2)
    }

    // --- GETTERS ---

    /**
     * Returns the selected map, that contains all selected filters.
     *
     * @return the selected map, that contains all selected filters.
     */
    internal fun getSelectedMap() = selectedMap
}
