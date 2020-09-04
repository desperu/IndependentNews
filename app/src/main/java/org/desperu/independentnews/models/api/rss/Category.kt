package org.desperu.independentnews.models.api.rss

import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.converter.htmlescape.HtmlEscapeStringConverter

/**
 * Data class with provide an category.
 *
 * @param category the category.
 *
 * @constructor Instantiates a new Subject.
 *
 * @property category the category to set.
 */
@Xml
data class Category(

    @TextContent(writeAsCData = true)
    var category: String
) {

    init {
        setCategory()
    }

    /**
     * Set category with the html escape string converter.
     */
    private fun setCategory() {
        category = HtmlEscapeStringConverter().write(category)
    }
}