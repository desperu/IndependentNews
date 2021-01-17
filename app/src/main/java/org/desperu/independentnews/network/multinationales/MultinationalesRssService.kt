package org.desperu.independentnews.network.multinationales

import org.desperu.independentnews.models.network.rss.RssResponse
import retrofit2.http.GET

/**
 * Interface containing methods to make requests to the Multinationales RSS service.
 */
interface MultinationalesRssService {

    /**
     * Returns the new published articles from the RSS Service.
     * @return the new published articles from the RSS Service.
     */
    @GET("spip.php?page=backend")
    suspend fun getRssArticles(): RssResponse // TODO serialize Rss, use @Path for that
}