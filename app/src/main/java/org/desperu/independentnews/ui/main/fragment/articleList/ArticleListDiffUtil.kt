package org.desperu.independentnews.ui.main.fragment.articleList

import androidx.recyclerview.widget.DiffUtil

class ArticleListDiffUtil(
    private val oldList: List<ArticleItemViewModel>,
    private val newList: List<ArticleItemViewModel>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].article.id == newList[newItemPosition].article.id

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].article == newList[newItemPosition].article
}