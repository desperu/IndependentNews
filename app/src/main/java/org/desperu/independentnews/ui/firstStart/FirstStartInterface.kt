package org.desperu.independentnews.ui.firstStart

/**
 * Interface to allow communications with First Start Activity.
 */
interface FirstStartInterface {

    /**
     * Close the application.
     */
    fun closeApplication()

    /**
     * Retry to fetch data.
     */
    fun retryFetchData()
}