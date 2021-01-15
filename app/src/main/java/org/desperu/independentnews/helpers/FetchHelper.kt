package org.desperu.independentnews.helpers

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Css
import org.desperu.independentnews.models.database.SourcePage
import org.desperu.independentnews.repositories.database.CssRepository
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.deConcatenateStringToMutableList
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * FetchHelper which provide functions to fetch data.
 */
object FetchHelper : KoinComponent {

    // FOR DATA
    private val snackBarHelper: SnackBarHelper? = getKoin().getOrNull()
    private val cssRepository: CssRepository = get()

    /**
     * Wrap coroutine block in secure call (try/catch), add message for ui and logs.
     * Used in network repository to fetch article data.
     *
     * @param sourceName    the name of the source for which fetch data.
     * @param block         the coroutine block to execute into secure call.
     */
    internal suspend fun <T: Any> catchFetchArticle(
        sourceName: String,
        block: suspend () -> List<T>?
    ): List<T>? = // Use T on test
        try {
            snackBarHelper?.showMessage(SEARCH, listOf(sourceName))

            withContext(Dispatchers.IO) { return@withContext block() }
        } catch (e: Exception) {
            snackBarHelper?.showMessage(ERROR, listOf(sourceName))

            // add store fetch result

            val tag = "${block.javaClass.enclosingClass?.simpleName}" +
                    "-${block.javaClass.enclosingMethod?.name}"
            Log.e(tag, e.message.toString())
            e.printStackTrace()

            null
        }

    /**
     * Wrap coroutine block in secure call (try/catch), add message for ui and logs.
     * Used in network repository to fetch source data.
     *
     * @param sourceName    the name of the source for which fetch data.
     * @param block         the coroutine block to execute into secure call.
     */
    internal suspend fun catchFetchSource(
        sourceName: String,
        block: suspend () -> List<SourcePage>?
    ): List<SourcePage>? =
        try {
            snackBarHelper?.showMessage(SOURCE_FETCH, listOf(sourceName))

            withContext(Dispatchers.IO) { return@withContext block() }
        } catch (e: Exception) {
            snackBarHelper?.showMessage(SOURCE_ERROR, listOf(sourceName))

            // add store fetch result

            val tag = "${block.javaClass.enclosingClass?.simpleName}" +
                    "-${block.javaClass.enclosingMethod?.name}"
            Log.e(tag, e.message.toString())
            e.printStackTrace()

            null
        }

    // Look at that to handle response, new coroutine
    //        callbackFlow<> {  }

    /**
     * Fetch css style, for the given css list, and persist into the database.
     * Fetch css style only if not already in the database
     * and prevent duplicate in the current list, use url to check.
     *
     * @param list      the list of Article or SourcePage for which fetch and set the css style.
     * @param block     the coroutine block to fetch the css style.
     */
    internal suspend fun <T: Any> fetchAndPersistCssList(
            list: List<T>,
            block: suspend (cssUrl: String) -> String
    ) = withContext(Dispatchers.IO) {

        // Determine and transform the given list to it's original object type.
        val cssList = list.map {
            if (it is Article)
                Css(url = it.cssUrl)
            else {
                it as SourcePage
                Css(url = it.cssUrl)
            }
        }
        val toFetchList = mutableListOf<Css>()

        cssList.forEach { css ->
            val cssUrls = deConcatenateStringToMutableList(css.url)

            cssUrls.forEach { cssUrl ->
                val cssId = cssRepository.getCssForUrl(cssUrl)?.id ?: 0L

                if (!toFetchList.map { it.url }.contains(cssUrl) && cssId == 0L) {
                    val fetchedCss = Css(url = cssUrl, style = block(cssUrl))
                    toFetchList.add(fetchedCss)
                }
            }
        }

        cssRepository.insertCss(*toFetchList.toTypedArray())
    }
}