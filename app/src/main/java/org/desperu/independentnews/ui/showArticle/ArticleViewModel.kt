package org.desperu.independentnews.ui.showArticle

import android.view.View.OnClickListener
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Css
import org.desperu.independentnews.models.database.Source
import org.desperu.independentnews.repositories.IndependentNewsRepository
import org.desperu.independentnews.repositories.database.CssRepository
import org.desperu.independentnews.utils.SourcesUtils.getSourceNameFromUrl
import org.koin.core.KoinComponent
import org.koin.core.get
import kotlin.properties.Delegates

/**
 * View Model witch provide data for Show Article Activity.
 *
 * @property article                the article object with contains data.
 * @property ideNewsRepository      the app repository interface witch provide database and network access.
 * @property router                 the image router which provide user redirection.
 * @property cssRepository          the repository that allow access for css from the database.
 * @property navHistoryMap          the navigation history of the web view.
 *
 * @constructor Instantiates a new ArticleViewModel.
 *
 * @param article                   the article object with contains data to set.
 * @param ideNewsRepository         the app repository interface witch provide database and network
 *                                  access to set.
 * @param router                    the image router which provide user redirection to set.
 */
class ArticleViewModel(
    val article: ObservableField<Article>,
    private val ideNewsRepository: IndependentNewsRepository,
    private val router: ImageRouter
): ViewModel(), KoinComponent {

    // FOR DATA
    private val cssRepository: CssRepository = get()
    private val navHistoryMap = mutableMapOf<Int, Pair<Article?, Int>>()

    // --------------
    // LISTENERS
    // --------------

    /**
     * On click image listener.
     */
    val onClickImage = OnClickListener {
        router.openShowImages(arrayListOf(article.get()?.imageUrl ?: ""))
    }

    // --------------
    // DB AND WEB
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
}