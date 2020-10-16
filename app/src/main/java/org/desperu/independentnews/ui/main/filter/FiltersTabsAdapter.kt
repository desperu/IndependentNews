package org.desperu.independentnews.ui.main.filter

import android.animation.TimeInterpolator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindView
import org.desperu.independentnews.extension.design.setScale

class FiltersTabsAdapter(context: Context, private val listener: (Int) -> Unit) : RecyclerView.Adapter<FiltersTabsAdapter.FiltersTabsViewHolder>() {

    private val payloadUpdateBadge = "UPDATE_BADGE"
    private val toggleAnimDuration = context.resources.getInteger(R.integer.toggleAnimDuration).toLong()

    private val tabTitle = context.resources.getStringArray(R.array.filter_tab_title)

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var hasFilters = MutableList(tabTitle.size) { false }

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun getItemCount(): Int = tabTitle.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FiltersTabsViewHolder =
            FiltersTabsViewHolder(inflater.inflate(R.layout.item_filter_tab, parent, false))

    override fun onBindViewHolder(holder: FiltersTabsViewHolder, position: Int) {
        holder.title.text = tabTitle[position]
        holder.itemView.setScale(defaultScale)
        holder.badge.setScale(if (hasFilters[position]) 1f else 0f)

        holder.itemView.setOnClickListener { listener(position) }
    }

    /**
     * This is called when badge state is updated so animate the badge
     */
    override fun onBindViewHolder(holder: FiltersTabsViewHolder, position: Int, payloads: MutableList<Any>) {
        payloads.firstOrNull { it is String && it == payloadUpdateBadge }
                ?: return super.onBindViewHolder(holder, position, payloads)

        val badgeShown = hasFilters[position]

        @Suppress("USELESS_CAST")
        val timeInterpolator =
                if (badgeShown) OvershootInterpolator(3f) as TimeInterpolator
                else AccelerateInterpolator()

        holder.badge.animate()
                .scaleX(if (badgeShown) 1f else 0f)
                .scaleY(if (badgeShown) 1f else 0f)
                .setDuration(toggleAnimDuration)
                .setInterpolator(timeInterpolator)
                .start()

    }

    fun updateBadge(position: Int, hasFilters: Boolean) {
        if (this.hasFilters[position] == hasFilters) return
        this.hasFilters[position] = hasFilters
        notifyItemChanged(position, payloadUpdateBadge)
    }

    class FiltersTabsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val badge: View by bindView(R.id.tab_badge)
        val title: TextView by bindView(R.id.tab_title)
    }

    companion object {
        const val defaultScale = 0.9f
        const val maxScale = 1.15f
    }
}
