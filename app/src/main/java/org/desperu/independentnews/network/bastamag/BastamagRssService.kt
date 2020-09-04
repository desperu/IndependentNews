package org.desperu.independentnews.network.bastamag

import org.desperu.independentnews.models.web.rss.RssResponse
import retrofit2.http.GET

/**
 * Interface containing methods to make requests to the Bastamag RSS service.
 */
interface BastamagRssService {

    /**
     * Returns the new published articles from the RSS Service.
     * @return the new published articles from the RSS Service.
     */
    @GET("spip.php?page=backend")
    suspend fun getRssArticles(): RssResponse
}