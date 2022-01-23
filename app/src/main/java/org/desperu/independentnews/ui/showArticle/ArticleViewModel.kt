package org.desperu.independentnews.ui.showArticle

import android.view.View.OnClickListener
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.desperu.independentnews.models.database.*
import org.desperu.independentnews.repositories.IndependentNewsRepository
import org.desperu.independentnews.repositories.database.CssRepository
import org.desperu.independentnews.repositories.database.UserArticleRepository
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.ui.showArticle.design.ArticleAnimations
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.SourcesUtils.getSourceNameFromUrl
import org.desperu.independentnews.utils.Utils.getRandomString
import org.desperu.independentnews.views.webview.skeleton.SkeletonWebViewBinding
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import kotlin.properties.Delegates

/**
 * View Model witch provide data for Show Article Activity.
 *
 * @property article                the article object with contains data.
 * @property ideNewsRepository      the app repository interface witch provide database and network access.
 * @property userArticleRepository  the repository which provide user article database access.
 * @property router                 the image router which provide user redirection.
 * @property articleAnimations      the article animations interface access.
 * @property cssRepository          the repository that allow access for css from the database.
 * @property prefs                  the shared preferences service interface access.
 * @property navHistory             the navigation history of the web view.
 * @property updatedUserArticles    the id list of updated user articles state.
 * @property isFavorite             true if the article is favorite, false otherwise.
 * @property isPaused               true if the article is paused, false otherwise.
 *
 * @constructor Instantiates a new ArticleViewModel.
 *
 * @param article                   the article object with contains data to set.
 * @param ideNewsRepository         the app repository interface witch provide database and network
 *                                  access to set.
 * @param userArticleRepository     the repository which provide user article database access to set.
 * @param router                    the image router which provide user redirection to set.
 */
class ArticleViewModel(
    val article: ObservableField<Article>,
    private val ideNewsRepository: IndependentNewsRepository,
    private val userArticleRepository: UserArticleRepository,
    private val router: ImageRouter
): SkeletonWebViewBinding(), KoinComponent {

    // FOR COMMUNICATION
    private val articleAnimations: ArticleAnimations by inject()
    private val cssRepository: CssRepository = get()
    private val prefs: SharedPrefService = get()

    // FOR DATA
    internal val navHistory = mutableListOf<Pair<Article, Int>>()
    internal val updatedUserArticles = mutableListOf<Long>()
    val isFavorite = ObservableBoolean(false)
    val isPaused = ObservableBoolean(false)

    // FOR SkeletonWebViewBinding implementation
    override val title = article.get()?.title ?: getRandomString(SKELETON_TITLE_LENGTH)
    override val isFetching = ObservableBoolean(false)
    override val isLoading = ObservableBoolean(true)

    // --------------
    // LISTENERS
    // --------------

    /**
     * On click image listener.
     */
    val onClickImage = OnClickListener {
        router.openShowImages(arrayListOf(article.get()?.imageUrl ?: ""))
    }

    /**
     * On article property changed callback.
     * Need to be declared before init to be instantiate before first call.
     */
    private val onArticleChanged = object : OnPropertyChangedCallback() {

        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            isLoading.set(true) // Show loading anim on each reloadE

            if (article.get()?.id == -1L)
                fetchAndSetArticle(article.get()!!.url)
            else
                setUserArticleState()
        }
    }

    // --------------
    // CONFIGURATION
    // --------------

    init {
        setArticleListener()
    }

    /**
     * Set user article state values, favorite and paused.
     * Restore the paused position if there's one.
     */
    internal fun setUserArticleState() = viewModelScope.launch(Dispatchers.IO) {
        val articleId = article.get()?.id ?: 0L
        isFavorite.set(userArticleRepository.getFavoriteArticle(articleId) != null)
        
        val paused = userArticleRepository.getPausedArticle(articleId)
        isPaused.set(paused != null)
        paused?.let { articleAnimations.resumePausedArticle(it.scrollPosition) }
    }

    /**
     * Set article property changed callback listener.
     */
    private fun setArticleListener() { article.addOnPropertyChangedCallback(onArticleChanged) }

    /**
     * Remove article property changed callback listener.
     */
    private fun removeArticleListener() { article.removeOnPropertyChangedCallback(onArticleChanged) }

    // --------------
    // DB
    // --------------

    /**
     * Returns the css of the current article.
     *
     * @return the css of the current article.
     */
    internal suspend fun getCss(): Css = withContext(Dispatchers.IO) {
        val url = article.get()?.cssUrl ?: ""
        val sourceName = article.get()?.source?.name
        val isSourcePage = article.get()?.sourceId == 0L
        return@withContext cssRepository.getCssStyle(url, sourceName, isSourcePage)
    }

    /**
     * Update favorite state for the current article, here in view model and in the database.
     */
    internal fun updateFavorite() = viewModelScope.launch(Dispatchers.IO) {
        val articleId = article.get()?.id ?: return@launch
        val isFavorite = userArticleRepository.handleFavorite(articleId)

        if (isFavorite) updateAddedUserArticles(ADDED_FAVORITE, ADDED_FAVORITE_DEFAULT)
        updatedUserArticles.add(articleId)
        this@ArticleViewModel.isFavorite.set(isFavorite)
    }

    /**
     * Update paused state for the current article, here in view model and in the database.
     *
     * @param scrollYPercent the scroll y percent value, bind with text ratio.
     */
    internal fun updatePaused(scrollYPercent: Float) = viewModelScope.launch(Dispatchers.IO) {
        val articleId = article.get()?.id ?: 0L
        val isPaused = userArticleRepository.handlePaused(articleId, scrollYPercent)

        if (isPaused) updateAddedUserArticles(ADDED_PAUSED, ADDED_PAUSED_DEFAULT)
        updatedUserArticles.add(articleId)
        this@ArticleViewModel.isPaused.set(isPaused)
    }

    // --------------
    // WEB
    // --------------

    /**
     * Fetch the article for the given url.
     *
     * @param url the article url to fetch.
     *
     * @return the fetched article.
     */
    internal suspend fun fetchArticle(url: String): Article = withContext(Dispatchers.Main) {
        val article = Article(url = url, source = Source(name = getSourceNameFromUrl(url)))
        var articleDb by Delegates.notNull<Article>()
        ideNewsRepository.fetchArticle(article)?.let { articleDb = it }
        return@withContext articleDb
    }

    /**
     * Fetch and set article for the given url to display it to the user.
     * Switch fetching state to handle skeleton loading anim.
     *
     * @param url the article url to fetch.
     */
    internal fun fetchAndSetArticle(url: String) = viewModelScope.launch(Dispatchers.IO) {
        isFetching.set(true)
        article.set(fetchArticle(url))
        isFetching.set(false)
    }

    // --------------
    // PREFS
    // --------------

    /**
     * Update added user article in shared preferences, for favorite and paused.
     *
     * @param key       the shared preferences key.
     * @param defVal    the default value.
     */
    private fun updateAddedUserArticles(key: String, defVal: Int) {
        prefs.getPrefs().apply {
            val previousVal = getInt(key, defVal)
            edit().putInt(key, previousVal + 1).apply()
        }
    }

    // --------------
    // NAVIGATION
    // --------------

    /**
     * Add a page to the history navigation.
     *
     * @param articlePair   the pair of article and scroll view position.
     */
    internal fun addPage(articlePair: Pair<Article, Int>) {
        navHistory.add(articlePair)
    }

    /**
     * Back to the previous page in the history.
     *
     * @return the article pair for the index.
     */
    internal fun previousPage(): Pair<Article, Int> {
        val previous = navHistory[navHistory.lastIndex]
        navHistory.removeAt(navHistory.lastIndex)
        article.set(previous.first)
        article.notifyChange()
        return previous
    }

    // --------------
    // METHODS OVERRIDE
    // --------------

    override fun onCleared() {
        super.onCleared()
        removeArticleListener()
    }
}