package org.desperu.independentnews.ui.main.fragment.articleList

import android.util.Pair
import android.view.View
import android.view.View.OnClickListener
import androidx.annotation.IdRes
import androidx.core.view.doOnNextLayout
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.desperu.independentnews.R
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.repositories.IndependentNewsRepository
import org.desperu.independentnews.repositories.database.UserArticleRepository
import org.desperu.independentnews.service.ResourceService
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.ui.main.MainInterface
import org.desperu.independentnews.utils.ADDED_FAVORITE
import org.desperu.independentnews.utils.ADDED_FAVORITE_DEFAULT
import org.desperu.independentnews.utils.FRAG_FAVORITE
import org.desperu.independentnews.utils.FRAG_PAUSED
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * View Model which provide data for article item.
 *
 * @property article                the given article data for this view model.
 * @property articleListInterface   the article list interface which provide fragment access.
 * @property userArticleRepository  the repository which provide user article database access.
 * @property ideNewsRepository      the app repository which provide database and network access.
 * @property mainInterface          the interface that allow communication with main activity.
 * @property router                 the router that allows redirection of the user.
 * @property isRead                 true if is read, false otherwise.
 * @property resource               the resource interface access.
 * @property prefs                  the shared preferences service interface access.
 * @property isFavorite             true if is favorite, false otherwise.
 * @property isPaused               true if is paused, false otherwise.
 *
 * @constructor Instantiates a new ArticleItemViewModel.
 *
 * @param article                   the given article data for this view model to set.
 * @param articleListInterface      the article list interface which provide fragment access to set.
 */
class ArticleItemViewModel(
    val article: Article,
    private val articleListInterface: ArticleListInterface
): ViewModel(), KoinComponent {

    // FOR DATA
    private val ideNewsRepository: IndependentNewsRepository = get()
    private val userArticleRepository: UserArticleRepository = get()
    private val mainInterface: MainInterface = get()
    private val router: ArticleRouter get() = get()
    private val resource: ResourceService = get()
    private val prefs: SharedPrefService = get()
    val isRead = ObservableBoolean(article.read)
    val isFavorite = ObservableBoolean(false)
    val isPaused = ObservableBoolean(false)

    // --------------
    // CONFIGURATION
    // --------------

    init {
        setUserArticleState()
    }

    /**
     * Set user article state values, favorite and paused.
     */
    internal fun setUserArticleState() = viewModelScope.launch(Dispatchers.IO) {
        isFavorite.set(userArticleRepository.getFavoriteArticle(article.id) != null)
        isPaused.set(userArticleRepository.getPausedArticle(article.id) != null)
    }

    // ------------
    // LISTENERS
    // ------------

    /**
     * On click image, title and description container listener.
     */
    val onClickArticle = OnClickListener {
        val imageView = (it.parent as View).findViewById<View>(R.id.image)
        val imageName = resource.getString(R.string.animation_main_to_show_article)

        mainInterface.showFilterMotion(false)
        router.openShowArticle(article, Pair(imageView, imageName))
        markAsRead(imageView)
    }

    /**
     * On click swipe action, handle it.
     */
    val onClickSwipeAction = OnClickListener { handleSwipeAction(it.id) }

    // ------------
    // ACTION
    // ------------

    /**
     * Handle the swipe action, depends of the clicked view.
     * Update user article state after executed the action.
     *
     * @param id the unique identifier of the clicked view.
     *
     * @throws IllegalArgumentException if the [id] was not found.
     */
    private fun handleSwipeAction(@IdRes id: Int) = viewModelScope.launch(Dispatchers.IO) {
        when (id) {
            R.id.swipe_share_container -> mainInterface.shareArticle(article.title, article.url)

            R.id.swipe_star_container, R.id.swipe_remove_star_container -> {
                val isFavorite = userArticleRepository.handleFavorite(article.id)

                if (isFavorite) prefs.getPrefs().apply {
                    val previousVal = getInt(ADDED_FAVORITE, ADDED_FAVORITE_DEFAULT)
                    edit().putInt(ADDED_FAVORITE, previousVal + 1).apply()
                }

                val isCurrentFrag = articleListInterface.fragKey == FRAG_FAVORITE
                if (!isFavorite && isCurrentFrag) removeThisItem()
            }

            R.id.swipe_remove_pause_container -> {
                val isPaused = userArticleRepository.handlePaused(article.id, 0f)

                val isCurrentFrag = articleListInterface.fragKey == FRAG_PAUSED
                if (!isPaused && isCurrentFrag) removeThisItem()
            }

            else -> throw IllegalArgumentException("Unique identifier not found : $id")
        }
        closeSwipeContainer()

        // Update user article state for ui.
        setUserArticleState()
    }

    // ------------
    // UI
    // ------------

    /**
     * Close the root swipe container, not automatic..., if there is one opened.
     */
    private fun closeSwipeContainer() = viewModelScope.launch(Dispatchers.Main) {
        articleListInterface.closeSwipeContainer()
    }

    /**
     * Remove this item in the adapter list and update UI.
     */
    private suspend fun removeThisItem() = withContext(Dispatchers.Main) {
        articleListInterface.getRecyclerAdapter()?.apply {
            val index = adapterList.indexOf(this@ArticleItemViewModel)
            adapterList.removeAt(index)
            notifyItemRemoved(index)
        }
    }

    // ------------
    // UTILS
    // ------------

    /**
     * Mark the article as read in current list and database.
     *
     * @param imageView the animated image view, used to delay article color animation.
     */
    private fun markAsRead(imageView: View) = viewModelScope.launch(Dispatchers.IO) {
        ideNewsRepository.markArticleAsRead(article)
        imageView.doOnNextLayout { isRead.set(true) }
    }
}