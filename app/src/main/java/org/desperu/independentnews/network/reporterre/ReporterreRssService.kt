package org.desperu.independentnews.network.reporterre

import org.desperu.independentnews.models.rss.RssResponse
import retrofit2.http.GET

/**
 * Interface containing methods to make requests to the Reporterre RSS service.
 */
interface ReporterreRssService {

    /**
     * Returns the new published articles from the RSS Service.
     * @return the new published articles from the RSS Service.
     */
    @GET("spip.php?page=backend-simple")
    suspend fun getRssArticles(): RssResponse
}