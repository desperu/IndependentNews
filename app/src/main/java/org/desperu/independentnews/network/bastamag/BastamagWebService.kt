package org.desperu.independentnews.network.bastamag

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interface containing methods to make requests to the Bastamag Web Site.
 */
interface BastamagWebService {

    /**
     * Returns the article from the Bastamag Web Site.
     * @return the article from the Bastamag Web Site.
     */
    @GET("{articleUrl}")
    suspend fun getArticle(@Path("articleUrl") articleUrl: String): ResponseBody

    /**
     * Returns the article for the asked category from the Bastamag Web Site.
     * @return the article for the asked category from the Bastamag Web Site.
     */
    @GET("{category}")
    suspend fun getCategory(@Path("category") category: String): ResponseBody
}