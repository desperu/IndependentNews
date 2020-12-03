package org.desperu.independentnews.helpers

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.utils.ERROR
import org.desperu.independentnews.utils.SEARCH
import org.koin.core.KoinComponent

/**
 * FetchHelper witch provide functions for fetch data.
 */
object FetchHelper : KoinComponent {

    // FOR DATA
    private val snackBarHelper: SnackBarHelper? = getKoin().getOrNull()

    /**
     * Wrap coroutine block in secure call (try/catch), add message for ui and logs.
     * Used in network repository to fetch data.
     *
     * @param sourceName    the name of the source for which fetch data.
     * @param block         the coroutine block to execute into secure call.
     */
    internal suspend fun catchException(
        sourceName: String,
        block: suspend () -> List<Article>?
    ): List<Article>? =
        try {
            snackBarHelper?.showMessage(SEARCH, listOf(sourceName))

            withContext(Dispatchers.IO) { block() }
        } catch (e: Exception) {
            snackBarHelper?.showMessage(ERROR, listOf(sourceName))

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