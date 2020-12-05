package org.desperu.independentnews.views.pageTransformer

/**
 * Page Transformer interface, used to return position value.
 */
interface PageTransformerInterface {

    /**
     * Returns the current value of the page position.
     */
    fun getPosition(): Float
}