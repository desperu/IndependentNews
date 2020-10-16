package org.desperu.independentnews.ui.main.filter

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.TextView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.createDatePickerDialog
import org.desperu.independentnews.extension.design.bindColor
import org.desperu.independentnews.extension.design.bindOptionalViews
import org.desperu.independentnews.extension.design.blendColors
import org.desperu.independentnews.extension.design.getValueAnimator
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.ui.main.MainActivity
import org.desperu.independentnews.utils.DATES
import org.desperu.independentnews.utils.FilterUtils.filterViewsId
import org.desperu.independentnews.utils.FilterUtils.getFilterValue
import org.desperu.independentnews.utils.SECTIONS
import org.desperu.independentnews.utils.SOURCES
import org.desperu.independentnews.utils.THEMES
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
    private var selectedMap = mutableMapOf<Int, MutableList<String>>().withDefault { mutableListOf() }

    private val beginDate = MutableLiveData<String>()
    private val endDate = MutableLiveData<String>()

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun getItemCount(): Int = context.resources.getStringArray(R.array.filter_tab_title).size

    override fun getItemViewType(position: Int): Int = when(position) {
        SOURCES -> R.layout.filter_layout_sources
        THEMES -> R.layout.filter_layout_themes
        SECTIONS -> R.layout.filter_layout_sections
        DATES -> R.layout.filter_layout_dates
        else -> R.layout.filter_layout_sources
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiltersPagerViewHolder =
            FiltersPagerViewHolder(inflater.inflate(viewType, parent, false))

    override fun onBindViewHolder(holder: FiltersPagerViewHolder, position: Int) {
        val selectedList = selectedMap.getOrPut(position) { mutableListOf() }

        /**
         * Bind all the filter buttons (if any). Clicking the filter button toggles state
         * which is shown by a short toggle animation
         */
        holder.filterViews.forEach { filterView: View ->
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

        // TODO to remove ???
        /**
         * Bind the Seekbars (if any). Sliding the seekbar between 1f..99f toggles it on.
         * 1f and 99f are chosen just to make the toggling seem more smooth
         */
        holder.seekBars.forEachIndexed { _: Int, seekBar: FilterSeekbar ->
            seekBar.setOnRangeSeekbarChangeListener { minValue, maxValue ->
                if (!selectedList.contains("date") && !(minValue.toFloat() < 1f && maxValue.toFloat() > 29f)) {
                    selectedList += "$minValue, $maxValue"
                    listener(position, selectedMap)
                    seekBar.setLeftThumbHighlightColor(selectedColor)
                    seekBar.setRightThumbHighlightColor(selectedColor)
                    seekBar.setLeftThumbColor(selectedColor)
                    seekBar.setRightThumbColor(selectedColor)
                    seekBar.setBarHighlightColor(selectedBarColor)
                } else if (selectedList.contains("date") && minValue.toFloat() < 1f && maxValue.toFloat() > 29f) {
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


        holder.pickerViews.forEachIndexed { index, pickerView ->
            val observer = Observer<String> {
                selectedList.clear()
                selectedList += beginDate.value.mToString()
                selectedList += endDate.value.mToString()
                listener(position, selectedMap)
            }

            pickerView.setOnClickListener {
                createDatePickerDialog(context, pickerView, if (index == 0) beginDate else endDate)
            }
            beginDate.observe((context as MainActivity), observer)
            endDate.observe(context, observer)
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        beginDate.removeObservers(context as MainActivity)
        endDate.removeObservers(context)
    }

    class FiltersPagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val filterViews: List<View> by bindOptionalViews(*filterViewsId.toIntArray())

        val pickerViews: List<TextView> by bindOptionalViews(R.id.filter_date_picker_begin, R.id.filter_date_picker_end)

        val seekBars: List<FilterSeekbar> by bindOptionalViews(R.id.rangeSeekbar2)
    }

    // --- GETTERS ---

    /**
     * Returns the selected map, that contains all selected filters.
     *
     * @return the selected map, that contains all selected filters.
     */
    internal fun getSelectedMap() = selectedMap
}
