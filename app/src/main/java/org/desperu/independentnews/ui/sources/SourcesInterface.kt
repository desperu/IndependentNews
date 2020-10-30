package org.desperu.independentnews.ui.sources

/**
 * Interface that's allow communication with it's activity.
 */
interface SourcesInterface {

    /**
     * Return the source list adapter instance.
     * @return the source list adapter instance.
     */
    fun getRecyclerAdapter(): RecyclerViewAdapter?
}