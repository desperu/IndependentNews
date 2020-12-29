package org.desperu.independentnews.models.network.rss

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

/**
 * Data class with provide an rss flux response.
 *
 * @param version the rss flux version.
 * @param channel the rss channel.
 *
 * @constructor Instantiates a new RssResponse.
 *
 * @property version the rss flux version to set.
 * @property channel the rss channel to set.
 */
@Xml
data class RssResponse(

    @Attribute(name = "version")
    val version: String?,

    @Element(name = "channel")
    val channel: Channel?
)