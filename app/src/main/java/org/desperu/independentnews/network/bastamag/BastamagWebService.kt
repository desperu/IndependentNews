package org.desperu.independentnews.network.bastamag

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Interface containing methods to make requests to the Bastamag Web Site.
 */
interface BastamagWebService {

    /**
     * Returns the article from the Bastamag Web Site.
     * @param articleUrl the url of the article to get.
     * @return the article from the Bastamag Web Site.
     */
    @GET("{articleUrl}")
    suspend fun getArticle(@Path("articleUrl") articleUrl: String): ResponseBody

    /**
     * Returns the category from the Bastamag Web Site.
     * @param category the category to get articles list.
     * @param number the number of the first article on the page.
     * @return the category from the Bastamag Web Site.
     */
    @GET("{category}" + "#pagination_articles")
    suspend fun getCategory(@Path("category") category: String,
                            @Query("debut_articles") number: String
    ): ResponseBody
}