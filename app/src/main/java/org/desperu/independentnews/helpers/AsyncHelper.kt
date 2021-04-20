package org.desperu.independentnews.helpers

import android.util.Log
import kotlinx.coroutines.*

/**
 * Async helper which provide async coroutine function.
 */
object AsyncHelper {

    /**
     * Wait that the condition is true before invoke the given block.
     * Use coroutine support to perform an asynchronous action.
     * If we reach the time out before the condition is true,
     * execute the given block.
     *
     * @param coroutineScope    the coroutine scope to use.
     * @param timeOut           the time out limit to wait the condition.
     * @param condition         the condition to wait for.
     * @param block             the given block to invoke after condition is true.
     */
    internal fun waitCondition(
        coroutineScope: CoroutineScope,
        timeOut: Long = 5000L,
        condition: () -> Boolean,
        block: () -> Unit
    ) {

        val waitCondition = coroutineScope.async(Dispatchers.IO) {
            var duration = 0L

            do {
                delay(50)
                duration += 50

                if (duration >= timeOut) {
                    val tag = "${block.javaClass.enclosingClass?.simpleName}" +
                            "-${block.javaClass.enclosingMethod?.name}"
                    Log.w(tag, "Wait Condition reach time out !!")
                    break
                }
            } while (!condition())

            withContext(Dispatchers.Main) { block() }
        }

        waitCondition[waitCondition.key]
    }
}