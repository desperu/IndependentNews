package org.desperu.independentnews.ui.sources.fragment.sourceList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.bindView
import org.koin.core.KoinComponent
import org.koin.core.get

class RecyclerViewAdapter(@LayoutRes private val layoutId: Int): RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(), KoinComponent {

    // FOR DATA
    private lateinit var list: MutableList<Any>
    private val sourceListInterface = get<SourceListInterface>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), viewType, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
        sourceListInterface.updateTransitionName(position, holder.image)
    }

    override fun getItemViewType(position: Int) = layoutId

    override fun getItemCount(): Int = if (::list.isInitialized) list.size else 0

    /**
     * Update all item list.
     * @param newList the new list to set.
     */
    internal fun updateList(newList: MutableList<Any>) { list = newList }

    class ViewHolder(private val binding: ViewDataBinding): RecyclerView.ViewHolder(binding.root) {

        val image: View by bindView(R.id.item_source_image)

        internal fun bind(any: Any?) {
            binding.setVariable(org.desperu.independentnews.BR.viewModel, any)
            binding.executePendingBindings()
        }
    }
}