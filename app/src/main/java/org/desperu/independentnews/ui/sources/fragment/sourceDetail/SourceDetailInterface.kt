package org.desperu.independentnews.ui.sources.fragment.sourceDetail

/**
 * Interface that's allow communication with it's fragment.
 */
interface SourceDetailInterface {

    /**
     * Return the source page list adapter instance.
     * @return the source page list adapter instance.
     */
    fun getRecyclerAdapter(): SourceDetailAdapter?
}