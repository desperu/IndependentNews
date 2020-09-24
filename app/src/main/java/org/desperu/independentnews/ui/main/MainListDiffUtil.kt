package org.desperu.independentnews.ui.main

import androidx.recyclerview.widget.DiffUtil
import org.desperu.independentnews.ui.main.fragment.articleList.ItemListViewModel

class MainListDiffUtil(
    private val oldList: List<ItemListViewModel>,
    private val newList: List<ItemListViewModel>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].article.id == newList[newItemPosition].article.id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] == newList[newItemPosition]
}