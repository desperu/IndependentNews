package org.desperu.independentnews.ui.main

/**
 * Interface to allow communications with Main Activity.
 */
interface MainInterface {

    /**
     * Return the main list adapter instance.
     * @return the main list adapter instance.
     */
    fun getRecyclerAdapter(): MainListAdapter?
}