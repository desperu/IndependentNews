package org.desperu.independentnews.helpers

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.models.SourcePage
import org.desperu.independentnews.utils.ERROR
import org.desperu.independentnews.utils.SEARCH
import org.desperu.independentnews.utils.SOURCE_ERROR
import org.desperu.independentnews.utils.SOURCE_FETCH
import org.koin.core.KoinComponent

/**
 * FetchHelper which provide functions to fetch data.
 */
object FetchHelper : KoinComponent {

    // FOR DATA
    private val snackBarHelper: SnackBarHelper? = getKoin().getOrNull()

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
}