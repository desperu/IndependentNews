package org.desperu.independentnews.models.rss

import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.converter.htmlescape.HtmlEscapeStringConverter

/**
 * Model class which provides data for a category.
 *
 * @property category the category.
 *
 * @constructor Instantiates a new Category.
 *
 * @param category the category to set.
 */
@Xml
data class Category(

    @TextContent(writeAsCData = true)
    var category: String?
) {

    init {
        setCategory()
    }

    /**
     * Set category with the html escape string converter.
     */
    private fun setCategory() {
        category = HtmlEscapeStringConverter()
            .read(category)
            .removePrefix(" ")
            .removeSuffix(" ")
    }
}