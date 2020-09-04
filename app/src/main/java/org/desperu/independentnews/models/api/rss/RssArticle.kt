package org.desperu.independentnews.models.api.rss

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import com.tickaroo.tikxml.converter.htmlescape.HtmlEscapeStringConverter
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.utils.Utils.concatenateStringFromMutableList
import org.desperu.independentnews.utils.Utils.stringToDate

/**
 * Data class with provide an RssArticle.
 *
 * @param title the title of the article.
 * @param url the url of the article.
 * @param permUrl the permanent url of the article.
 * @param publishedDate the published date of the article.
 * @param author the author of the article.
 * @param categoryList the category list of the article.
 * @param description the description of the article.
 *
 * @constructor Instantiates a new Article.
 *
 * @property title the title of the article to set.
 * @property url the url of the article to set.
 * @property permUrl the permanent url of the article to set.
 * @property publishedDate the published date of the article to set.
 * @property author the author of the article to set.
 * @property categoryList the category list of the article to set.
 * @property description the description of the article to set.
 * @property imageUrl the image url of the article to set.
 */
@Xml
data class RssArticle(

    @PropertyElement(name = "title", converter = HtmlEscapeStringConverter::class)
    val title: String,

    @PropertyElement(name = "link", converter = HtmlEscapeStringConverter::class)
    val url: String,

    @PropertyElement(name = "guid", converter = HtmlEscapeStringConverter::class)
    val permUrl: String,

    @PropertyElement(name = "dc:date", converter = HtmlEscapeStringConverter::class)
    val publishedDate: String,

    @PropertyElement(name = "dc:creator", converter = HtmlEscapeStringConverter::class)
    val author: String,

    @Element(name = "dc:subject")
    val categoryList: List<Category>,

    @PropertyElement(name = "description", converter = HtmlEscapeStringConverter::class)
    val description: String
) {

    internal lateinit var imageUrl: String

    /**
     * Convert RssArticle to Article.
     */
    internal fun toArticle(): Article {
        val article = Article(
            url = url,
            title = title,
            author = author,
            categories = concatenateStringFromMutableList(categoryList.map { it.category }.toMutableList()),
            description = description)

        stringToDate(publishedDate)?.time?.let { article.publishedDate = it }

        return article
    }
}