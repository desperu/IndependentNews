package org.desperu.independentnews.ui.main.fragment.articleList

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.view.View
import android.view.ViewTreeObserver.OnPreDrawListener
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension
import org.desperu.independentnews.R
import org.desperu.independentnews.extension.design.getScreenRect
import org.desperu.independentnews.extension.toShow
import kotlin.math.max
import kotlin.properties.Delegates

/**
 * Swipe Item class that implement Item Touch Helper Extension to support swipe and drag.
 * We use here only swipe to start touch action, with a custom animation to display
 * swipe actions, they depends of article states.
 *
 * @property articleListInterface   the article list interface access.
 *
 * @constructor Instantiates a new SwipeItem.
 *
 * @param articleListInterface      the article list interface access to set.
 *
 * @author Desperu.
 */
class SwipeItem(
    private val articleListInterface: ArticleListInterface
) : ItemTouchHelperExtension.Callback() {

    // FOR DATA
    private lateinit var recycler: RecyclerView
    private lateinit var holder: ArticleListAdapter.ArticleViewHolder
    private val actionsState = mutableMapOf<View, Boolean>()
    private val enabledActions get() = actionsState.filter { it.value }.size + 1 // For the share action always shown.
    private var previousActions by Delegates.notNull<Int>()
    private var interceptTouch = false
    private var collapseListenerSet = false

    // FOR UI
    private var expandedWidth by Delegates.notNull<Float>()

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun getMovementFlags(p0: RecyclerView?, p1: RecyclerView.ViewHolder?): Int {
        p0?.let { recycler = it }
        return makeMovementFlags(ItemTouchHelper.ACTION_STATE_IDLE, ItemTouchHelper.START)
    }

    override fun onMove(
        p0: RecyclerView?,
        p1: RecyclerView.ViewHolder?,
        p2: RecyclerView.ViewHolder?
    ): Boolean {
        return true
    }

    override fun onSwiped(p0: RecyclerView.ViewHolder?, p1: Int) {
        val holder = p0 as ArticleListAdapter.ArticleViewHolder
        setActionListener(holder)
    }

    override fun getItemFrontView(viewHolder: RecyclerView.ViewHolder?): View? =
        viewHolder?.let { (it as ArticleListAdapter.ArticleViewHolder).cardContainer }


    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {

        viewHolder?.let {

            // Set the swipe action item width
            setExpandedWidth(viewHolder.itemView.context)

            // Cast to ArticleViewHolder
            viewHolder as ArticleListAdapter.ArticleViewHolder

            // If the stored holder is different, remove previous action view from map.
            // And it was collapsed automatically by the ItemTouchHelperExtension API.
            if (::holder.isInitialized && holder != viewHolder)
                removePreviousAction(holder)

            // Set the new holder for data
            setupNewHolder(viewHolder)

            getDefaultUIUtil().onSelected(viewHolder.cardContainer)
        }
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val holder = viewHolder as ArticleListAdapter.ArticleViewHolder
        val foregroundView: View = holder.cardContainer
        val limitedDX = max(-expandedWidth * enabledActions, dX)

        // Animate swipe action views
        animateSwipeActionViews(holder, limitedDX, enabledActions)

        getDefaultUIUtil().onDraw(
            c, recyclerView, foregroundView, limitedDX, dY,
            actionState, isCurrentlyActive
        )
    }

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {

        val foregroundView: View = (viewHolder as ArticleListAdapter.ArticleViewHolder).cardContainer
        getDefaultUIUtil().onDrawOver(
            c, recyclerView, foregroundView, dX, dY,
            actionState, isCurrentlyActive
        )
    }

    override fun clearView(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?) {
        val foregroundView: View = (viewHolder as ArticleListAdapter.ArticleViewHolder).cardContainer

        if (!collapseListenerSet)
            collapseAnim(viewHolder)

        getDefaultUIUtil().clearView(foregroundView)
    }

    // --------------
    // CONFIGURATION
    // --------------

    /**
     * Setup the new selected holder, to prepare before play anim.
     *
     * @param holder the new selected holder.
     */
    private fun setupNewHolder(holder: ArticleListAdapter.ArticleViewHolder) {
        // Store the selected view holder
        this.holder = holder
        showSwipeContainer(holder, true)

        // Handle current actions to display.
        handleCurrentActions(holder)
        showViews()
        updateRootContainerSize(holder)
    }

    /**
     * Handle the current action list, used to display useful actions.
     *
     * @param viewHolder the view holder for which handle actions.
     */
    private fun handleCurrentActions(viewHolder: RecyclerView.ViewHolder?) {
        val position = viewHolder?.layoutPosition ?: error("Swipe item, view holder is null !!")
        val viewModel = articleListInterface.getRecyclerAdapter()?.adapterList?.get(position) as ArticleItemViewModel
        val isFavorite = viewModel.isFavorite.get()
        val isPaused = viewModel.isPaused.get()

        actionsState[holder.favorite] = !isFavorite
        actionsState[holder.removeFavorite] = isFavorite

        actionsState[holder.removePaused] = isPaused
    }

    /**
     * Remove the previous action view from the actionsState list,
     * store previous value needed for the collapse anim.
     *
     * @param holder the previous holder.
     */
    private fun removePreviousAction(holder: ArticleListAdapter.ArticleViewHolder) {
        previousActions = enabledActions
        val toRemove = holder.run { listOf(favorite, removeFavorite, removePaused) }
        toRemove.forEach { actionsState.remove(it) }
    }

    // --------------
    // UI
    // --------------

    /**
     * Set the expanded width for the swipe action item.
     *
     * @param context the context of the holder.
     */
    private fun setExpandedWidth(context: Context) {
        expandedWidth = context.resources.getDimension(R.dimen.swipe_container_width)
    }

    /**
     * Show or hide swipe container, depends of to show value.
     *
     * @param holder    the holder fot which handle swipe container visibility.
     * @param toShow    true to show, false to hide.
     */
    private fun showSwipeContainer(holder: ArticleListAdapter.ArticleViewHolder, toShow: Boolean) {
        holder.swipeContainer.toShow(toShow)
    }

    /**
     * Update action views visibility, depends of the enabled value.
     */
    private fun showViews() { actionsState.forEach { it.key.toShow(it.value) } }

    /**
     * Update the root swipe container size, to adapt with the enabled actions and item size.
     *
     * @param holder the view holder for which match size.
     */
    private fun updateRootContainerSize(holder: ArticleListAdapter.ArticleViewHolder) {
        val height = holder.cardContainer.measuredHeight

        holder.swipeContainer.layoutParams.width = (expandedWidth * enabledActions).toInt()
        holder.swipeContainer.layoutParams.height = height

        holder.swipeContainer.requestLayout()
    }

    // --------------
    // ACTION
    // --------------

    /**
     * Set action listener to handle action click, for the opened article view holder.
     *
     * @param holder the article view holder
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun setActionListener(holder: ArticleListAdapter.ArticleViewHolder) {
        if (isOpened(holder)) {
            interceptTouch = true
            recycler.setOnTouchListener { _, event ->
                val x = event.rawX.toInt()
                val y = event.rawY.toInt()
                val swipeRect = holder.swipeContainer.getScreenRect()

                if (swipeRect.contains(x, y)) {
                    val shareRect = holder.share.getScreenRect()
                    val favoriteRect = holder.favorite.getScreenRect()
                    val removeFavoriteRect = holder.removeFavorite.getScreenRect()
                    val removePausedRect = holder.removePaused.getScreenRect()
                    val actionsRect = listOf(shareRect, favoriteRect, removeFavoriteRect, removePausedRect)

                    actionsRect.forEach {
                        if (it.contains(x, y)) {
                            recycler.setOnTouchListener(null)
                            return@setOnTouchListener true
                        }
                    }
                }

                false
            }
        }
    }

    // --------------
    // ANIMATION
    // --------------

    /**
     * Animate action containers expand and collapse, to show and hide them.
     * Use limited delta X value to swipe views only as needed to show them,
     * not the complete user motion event delta value.
     * Translate views from right to display them at the same place of the holder item view,
     * it depends of [limitedDX] value, negative to expand, positive to collapse.
     *
     * @param holder            the Article View Holder used for the animation.
     * @param limitedDX         the limited delta X value.
     * @param enabledActions    the enabled actions number.
     */
    private fun animateSwipeActionViews(
        holder: ArticleListAdapter.ArticleViewHolder,
        limitedDX: Float,
        enabledActions: Int
    ) {

        holder.share.translationX = expandedWidth + limitedDX / enabledActions
        holder.favorite.translationX = expandedWidth + limitedDX / enabledActions * 2
        holder.removeFavorite.translationX = expandedWidth + limitedDX / enabledActions * 2
        holder.removePaused.translationX = expandedWidth + limitedDX
    }

    /**
     * Collapse swipe action view animation, hide view on animation end.
     *
     * @param holder the given article view holder.
     */
    private fun collapseAnim(holder: ArticleListAdapter.ArticleViewHolder) {
        collapseListenerSet = true

        val foregroundView = holder.cardContainer
        foregroundView.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {

            override fun onPreDraw(): Boolean {
                val enabledActions = if (this@SwipeItem.holder == holder) enabledActions
                                     else previousActions
                animateSwipeActionViews(holder, foregroundView.translationX, enabledActions)

                // Set swipe container visibility to gone, on animation end, to prevent ui mistake.
                if (!isOpened(holder) && !interceptTouch) {
                    showSwipeContainer(holder, false)
                    foregroundView.viewTreeObserver.removeOnPreDrawListener(this)
                    collapseListenerSet = false
                }
                interceptTouch = !isOpened(holder)

                return true
            }
        })
    }

    // --------------
    // UTILS
    // --------------

    /**
     * Returns true of the given article view holder is opened, false otherwise
     *
     * @param holder the given holder.
     *
     * @return true if the given article view holder is opened, false otherwise.
     */
    private fun isOpened(holder: ArticleListAdapter.ArticleViewHolder) =
        holder.cardContainer.translationX != 0f
}