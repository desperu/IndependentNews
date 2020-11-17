package org.desperu.independentnews.ui.sources.fragment.sourceDetail

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import org.desperu.independentnews.anim.AnimHelper.fromSideAnimation

/**
 * Recycler view adapter for Source Detail.
 *
 * @property context    the context from this adapter is instantiate to set.
 * @property layoutId   the unique identifier of the item layout to set.
 *
 * @constructor Instantiate a new SourceDetailAdapter.
 *
 * @param context       the context from this adapter is instantiate to set.
 * @param layoutId      the unique identifier of the item layout to set.
 */
class SourceDetailAdapter(
    private val context: Context,
    @LayoutRes private val layoutId: Int
): RecyclerView.Adapter<SourceDetailAdapter.ViewHolder>() {

    // FOR DATA
    private lateinit var list: MutableList<Any>

    override fun getItemCount(): Int = if (::list.isInitialized) list.size else 0

    override fun getItemViewType(position: Int) = layoutId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), viewType, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        setAnimation(holder.itemView, position)
    }

    /**
     * Set item animation, (from left animation).
     * @param itemView the item view to animate.
     * @param position the position of the item in the recycler view.
     */
    private fun setAnimation(itemView: View, position: Int) {
        val startDelay = position * 200L / 3
        fromSideAnimation(context, itemView, startDelay, true)
    }

    /**
     * Update all item list.
     * @param newList the new list to set.
     */
    internal fun updateList(newList: MutableList<Any>) { list = newList }

    class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {

        internal fun bind(any: Any?) {
            binding.setVariable(org.desperu.independentnews.BR.viewModel, any)
            binding.executePendingBindings()
        }
    }
}