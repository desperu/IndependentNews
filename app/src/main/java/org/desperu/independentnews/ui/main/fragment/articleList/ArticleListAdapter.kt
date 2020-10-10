package org.desperu.independentnews.ui.main.fragment.articleList

import android.animation.ValueAnimator
import android.content.Context
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.view.doOnLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.*
import org.desperu.independentnews.ui.main.MainListDiffUtil
import org.desperu.independentnews.ui.main.animationPlaybackSpeed

class ArticleListAdapter(context: Context, @LayoutRes private val layoutId: Int) : RecyclerView.Adapter<ArticleListAdapter.ArticleViewHolder>() {

    private val originalBg: Int by bindColor(context, R.color.list_item_bg_collapsed)
    private val expandedBg: Int by bindColor(context, R.color.list_item_bg_expanded)

    private val listItemHorizontalPadding: Float by bindDimen(context, R.dimen.list_item_horizontal_padding)
    private val listItemVerticalPadding: Float by bindDimen(context, R.dimen.list_item_vertical_padding)
    private val originalWidth = context.screenWidth - 48.dp
    private val expandedWidth = context.screenWidth - 24.dp
    private var originalHeight = -1 // will be calculated dynamically
    private var expandedHeight = -1 // will be calculated dynamically

    private var list: MutableList<Any> = mutableListOf()
    internal var filteredList: MutableList<Any> = mutableListOf()
        set(value) {
            value.filter { list.contains(it) }
            field = value
        }
    private val adapterList: MutableList<Any> get() = if (!isFiltered) list else filteredList

    /** Variable used to filter adapter items. 'true' if filtered and 'false' if not */
    @Suppress("unchecked_cast")
    var isFiltered = false
        set(value) {
            // TODO handle value is empty ...
            field = value
            val diff = MainListDiffUtil(
                (if (field) list else filteredList) as List<ArticleItemViewModel>,
                (if (field) filteredList else list) as List<ArticleItemViewModel>
            )
            DiffUtil.calculateDiff(diff).dispatchUpdatesTo(this)
            if (!field)
                filteredList = mutableListOf()
        }

    private val listItemExpandDuration: Long get() = (300L / animationPlaybackSpeed).toLong()
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    private lateinit var recyclerView: RecyclerView
    private var expandedModel: Any? = null
    private var isScaledDown = false

    ///////////////////////////////////////////////////////////////////////////
    // Methods
    ///////////////////////////////////////////////////////////////////////////

    override fun getItemCount(): Int = adapterList.size

    override fun getItemViewType(position: Int): Int = layoutId

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder =
        ArticleViewHolder(
            DataBindingUtil.inflate(inflater, viewType, parent, false)
        )

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val model = adapterList[position]

        expandItem(holder, model == expandedModel, animate = false)
        scaleDownItem(holder, position, isScaledDown)

        holder.cardContainer.setOnClickListener {
            if (expandedModel == null) {

                // expand clicked view
                expandItem(holder, expand = true, animate = true)
                expandedModel = model
            } else if (expandedModel == model) {

                // collapse clicked view
                expandItem(holder, expand = false, animate = true)
                expandedModel = null
            } else {

                // collapse previously expanded view
                val expandedModelPosition = adapterList.indexOf(expandedModel!!)
                val oldViewHolder =
                        recyclerView.findViewHolderForAdapterPosition(expandedModelPosition) as? ArticleViewHolder
                if (oldViewHolder != null) expandItem(oldViewHolder, expand = false, animate = true)

                // expand clicked view
                expandItem(holder, expand = true, animate = true)
                expandedModel = model
            }
        }

        // set data in item
        holder.bind(adapterList[position])
    }

    /**
     * Update all item list.
     * @param newList the new list to set.
     */
    internal fun updateList(newList: MutableList<Any>) { list = newList }

    private fun expandItem(holder: ArticleViewHolder, expand: Boolean, animate: Boolean) {
        if (animate) {
            val animator =
                getValueAnimator(
                    expand, listItemExpandDuration, AccelerateDecelerateInterpolator()
                ) { progress -> setExpandProgress(holder, progress) }

            if (expand) animator.doOnStart { holder.expandView.isVisible = true }
            else animator.doOnEnd { holder.expandView.isVisible = false }

            animator.start()
        } else {

            // show expandView only if we have expandedHeight (onViewAttached)
            holder.expandView.isVisible = expand && expandedHeight >= 0
            setExpandProgress(holder, if (expand) 1f else 0f)
        }
    }

    override fun onViewAttachedToWindow(holder: ArticleViewHolder) {
        super.onViewAttachedToWindow(holder)

        // get originalHeight & expandedHeight if not gotten before
        if (expandedHeight < 0) {
            expandedHeight = 0 // so that this block is only called once

            holder.cardContainer.doOnLayout { view ->
                originalHeight = view.height

                // show expandView and record expandedHeight in next layout pass
                // (doOnPreDraw) and hide it immediately. We use onPreDraw because
                // it's called after layout is done. doOnNextLayout is called during
                // layout phase which causes issues with hiding expandView.
                holder.expandView.isVisible = true
                holder.title.maxLines = 5
                view.doOnPreDraw {// TODO use dimens
                    expandedHeight = view.height + 20.dp// + if (length > 200) 100.dp else 50.dp
                    holder.title.maxLines = 2
                    holder.expandView.isVisible = false
                }
            }
        }
    }

    private fun setExpandProgress(holder: ArticleViewHolder, progress: Float) {
        if (expandedHeight > 0 && originalHeight > 0) {
            holder.cardContainer.layoutParams.height =
                    (originalHeight + (expandedHeight - originalHeight) * progress).toInt()
        }
        holder.cardContainer.layoutParams.width =
                (originalWidth + (expandedWidth - originalWidth) * progress).toInt()

        holder.cardContainer.setBackgroundColor(
            blendColors(
                originalBg,
                expandedBg,
                progress
            )
        )
        holder.cardContainer.requestLayout()

        holder.chevron.rotation = 90 * progress

        // TODO animate text length with input filter ??
        holder.title.maxLines = if (progress > 0.4f) 5 else 2
        val originalLength = 56
        val title = (adapterList[holder.adapterPosition] as ArticleItemViewModel).article.title
        val expandedLength = title.length
        holder.title.filters = arrayOf(InputFilter.LengthFilter((originalLength + (expandedLength - originalLength) * progress).toInt()))
        holder.title.text = title

        // TODO use dimens ...
        holder.image.layoutParams.apply {
            height = (75.dp + (90.dp - 75.dp) * progress).toInt()
            width = (75.dp + (90.dp - 75.dp) * progress).toInt()
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Scale Down Animation
    ///////////////////////////////////////////////////////////////////////////

    private inline val LinearLayoutManager.visibleItemsRange: IntRange
        get() = findFirstVisibleItemPosition()..findLastVisibleItemPosition()

    fun getScaleDownAnimator(isScaledDown: Boolean): ValueAnimator {
        val lm = recyclerView.layoutManager as LinearLayoutManager

        val animator =
            getValueAnimator(
                isScaledDown,
                duration = 300L, interpolator = AccelerateDecelerateInterpolator()
            ) { progress ->

                // Get viewHolder for all visible items and animate attributes
                for (i in lm.visibleItemsRange) {
                    val holder = recyclerView.findViewHolderForLayoutPosition(i) as ArticleViewHolder
                    setScaleDownProgress(holder, i, progress)
                }
            }

        // Set adapter variable when animation starts so that newly binded views in
        // onBindViewHolder will respect the new size when they come into the screen
        animator.doOnStart { this.isScaledDown = isScaledDown }

        // For all the non visible items in the layout manager, notify them to adjust the
        // view to the new size
        animator.doOnEnd {
            repeat(lm.itemCount) { if (it !in lm.visibleItemsRange) notifyItemChanged(it) }
        }
        return animator
    }

    private fun setScaleDownProgress(holder: ArticleViewHolder, position: Int, progress: Float) {
        val itemExpanded = position >= 0 && position < adapterList.size && adapterList[position] == expandedModel
        holder.cardContainer.layoutParams.apply {
            width = ((if (itemExpanded) expandedWidth else originalWidth) * (1 - 0.1f * progress)).toInt()
            height = ((if (itemExpanded) expandedHeight else originalHeight) * (1 - 0.1f * progress)).toInt()
//            log("width=$width, height=$height [${"%.2f".format(progress)}]")
        }
        holder.cardContainer.requestLayout()

        holder.scaleContainer.scaleX = 1 - 0.05f * progress
        holder.scaleContainer.scaleY = 1 - 0.05f * progress

        holder.scaleContainer.setPadding(
                (listItemHorizontalPadding * (1 - 0.2f * progress)).toInt(),
                (listItemVerticalPadding * (1 - 0.2f * progress)).toInt(), // TODO to change for 10dp to allow show source
                (listItemHorizontalPadding * (1 - 0.2f * progress)).toInt(),
                (listItemVerticalPadding * (1 - 0.2f * progress)).toInt()
        )

        // TODO use dimens
//        holder.image.layoutParams.apply {
//            height = (75.dp + (70.dp - 75.dp) * progress).toInt()
//            width = (75.dp + (70.dp - 75.dp) * progress).toInt()
//
//        }
//        (holder.image.layoutParams  as RelativeLayout.LayoutParams).apply {
//            marginStart = (20.dp * progress).toInt()
//            alignWithParent = true
//
//        }
        holder.image.scaleX = 1 - 0.05f * progress
        holder.image.scaleY = 1 - 0.05f * progress

        holder.listItemFg.alpha = progress
    }

    /** Convenience method for calling from onBindViewHolder */
    private fun scaleDownItem(holder: ArticleViewHolder, position: Int, isScaleDown: Boolean) {
        setScaleDownProgress(holder, position, if (isScaleDown) 1f else 0f)
    }

    ///////////////////////////////////////////////////////////////////////////
    // ViewHolder
    ///////////////////////////////////////////////////////////////////////////

    class ArticleViewHolder(private val binding: ViewDataBinding) : RecyclerView.ViewHolder(binding.root) {
        val expandView: View by bindView(R.id.expand_view)
        val chevron: View by bindView(R.id.chevron)
        val cardContainer: View by bindView(R.id.card_container)
        val scaleContainer: View by bindView(R.id.scale_container)
        val listItemFg: View by bindView(R.id.list_item_fg)
        val title: TextView by bindView(R.id.title)
        val image: View by bindView(R.id.image)
        val description: TextView by bindView(R.id.description)

        internal fun bind(any: Any?) {
            binding.setVariable(org.desperu.independentnews.BR.viewModel, any)
            binding.executePendingBindings()
        }
    }
}