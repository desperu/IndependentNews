package org.desperu.independentnews.ui.showArticle

import android.view.View.OnClickListener
import androidx.databinding.Observable
import androidx.databinding.Observable.OnPropertyChangedCallback
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.desperu.independentnews.models.database.*
import org.desperu.independentnews.repositories.IndependentNewsRepository
import org.desperu.independentnews.repositories.database.CssRepository
import org.desperu.independentnews.repositories.database.UserArticleRepository
import org.desperu.independentnews.service.SharedPrefService
import org.desperu.independentnews.ui.showArticle.design.ArticleDesignInterface
import org.desperu.independentnews.utils.ADDED_FAVORITE
import org.desperu.independentnews.utils.ADDED_FAVORITE_DEFAULT
import org.desperu.independentnews.utils.ADDED_PAUSED
import org.desperu.independentnews.utils.ADDED_PAUSED_DEFAULT
import org.desperu.independentnews.utils.SourcesUtils.getSourceNameFromUrl
import org.koin.core.KoinComponent
import org.koin.core.get
import org.koin.core.inject
import kotlin.properties.Delegates

/**
 * View Model witch provide data for Show Article Activity.
 *
 * @property article                the article object with contains data.
 * @property ideNewsRepository      the app repository interface witch provide database and network access.
 * @property userArticleRepository  the repository which provide user article database access.
 * @property router                 the image router which provide user redirection.
 * @property articleDesign          the article design interface access.
 * @property cssRepository          the repository that allow access for css from the database.
 * @property prefs                  the shared preferences service interface access.
 * @property navHistoryMap          the navigation history of the web view.
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
): ViewModel(), KoinComponent {

    // FOR DATA
    private val articleDesign: ArticleDesignInterface by inject()
    private val cssRepository: CssRepository = get()
    private val prefs: SharedPrefService = get()
    private val navHistoryMap = mutableMapOf<Int, Pair<Article?, Int>>()
    internal val updatedUserArticles = mutableListOf<Long>()
    val isFavorite = ObservableBoolean(false)
    val isPaused = ObservableBoolean(false)

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
        paused?.let { articleDesign.resumePausedArticle(it.scrollPosition) }
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
        return@withContext cssRepository.getCssStyle(article.get()?.cssUrl ?: "")
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
     * Fetch article and display it to the user.
     *
     * @param url the article url to fetch.
     */
    internal fun fetchArticle(url: String) = viewModelScope.launch(Dispatchers.Main) {
        val article = Article(url = url, source = Source(name = getSourceNameFromUrl(url)))
        var articleDb by Delegates.notNull<Article>()
        withContext(Dispatchers.IO) { ideNewsRepository.fetchArticle(article)?.let { articleDb = it } }
        this@ArticleViewModel.article.set(articleDb)
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
     * @param index         the index of the new page to add to the history.
     * @param articlePair   the pair of article and scroll view position.
     */
    internal fun addPage(index: Int, articlePair: Pair<Article?, Int>) {
        navHistoryMap[index] = articlePair
    }

    /**
     * Back to the previous page in the history.
     *
     * @param index the index of the page.
     *
     * @return the article pair for the index.
     */
    internal fun previousPage(index: Int): Pair<Article?, Int>? {
        navHistoryMap.remove(index)
        val previous = navHistoryMap[index - 1]
        previous?.let { article.set(it.first) }
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