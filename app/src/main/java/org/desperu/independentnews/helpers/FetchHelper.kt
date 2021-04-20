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
    private val snackBarHelper: SnackBarHelper? get() = getKoin().getOrNull()
    private val cssRepository: CssRepository = get()

    /**
     * Wrap coroutine block in secure call (try/catch), add message for logs.
     * Used in network repository to fetch data.
     *
     * @param block the coroutine block to execute into secure call.
     *
     * @return the fetched data list.
     */
    internal suspend fun <T: Any> catchFetch(
        block: suspend () -> List<T>?
    ): List<T>? = // Use T on test
        try {
            withContext(Dispatchers.IO) { return@withContext block() }
        } catch (e: Exception) {

            // add store fetch result

            val tag = "${block.javaClass.enclosingClass?.simpleName}" +
                    "-${block.javaClass.enclosingMethod?.name}"
            Log.e(tag, e.message.toString())
            e.printStackTrace()

            null
        }

    /**
     * Wrap coroutine block in secure call (catchFetch), add message for ui.
     * Used in network repository to fetch data and display message to the user.
     *
     * @param sourceName    the name of the source for which fetch data.
     * @param type          the type of the data to fetch.
     * @param block         the coroutine block to execute into secure call.
     *
     * @return the fetched data list.
     */
    internal suspend fun <T: Any> fetchWithMessage(
        sourceName: String,
        type: Int,
        block: suspend () -> List<T>?
    ): List<T>? {

        // Display start search message to the user
        snackBarHelper?.showMessage(
            if (type == FETCH) SEARCH else SOURCE_FETCH,
            listOf(sourceName)
        )

        // Execute fetch block with secure call
        val result = catchFetch(block)

        // Display error message to the user
        if (result == null)
            snackBarHelper?.showMessage(
                if (type == FETCH) ERROR else SOURCE_ERROR,
                listOf(sourceName)
            )

        return result
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
    ) = catchFetch {

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
                val isInDb = cssRepository.getCssForUrl(cssUrl)?.id != null
                val isInList = toFetchList.map { it.url }.contains(cssUrl)

                if (!isInDb && !isInList) {
                    val fetchedCss = Css(url = cssUrl, style = block(cssUrl))
                    toFetchList.add(fetchedCss)
                }
            }
        }

        cssRepository.insertCss(*toFetchList.toTypedArray())
    }
}