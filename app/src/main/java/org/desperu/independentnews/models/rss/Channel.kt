package org.desperu.independentnews.models.rss

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

/**
 * Data class with provide a Channel.
 *
 * @param language the language of the channel.
 * @param rssArticleList the item list of the channel.
 *
 * @constructor Instantiates a new RssResponse.
 *
 * @property language the language of the channel to set.
 * @property rssArticleList the item list of the channel to set.
 */
@Xml
data class Channel(

    @Attribute(name = "xml:lang")
    val language: String?,

    @Element(name = "item")
    val rssArticleList: List<RssArticle>?
)