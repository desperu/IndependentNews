package org.desperu.independentnews.network.reporterre

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Interface containing methods to make requests to the Reporterre Web Site.
 */
interface ReporterreWebService {

    /**
     * Returns the article from the Reporterre Web Site.
     *
     * @param articleUrl the url of the article to get.
     *
     * @return the article from the Reporterre Web Site.
     */
    @GET("{articleUrl}")
    suspend fun getArticle(@Path("articleUrl") articleUrl: String): ResponseBody

    /**
     * Returns the category from the Reporterre Web Site.
     *
     * @param category the category to get articles list.
     * @param number the number of the first article on the page.
     *
     * @return the category from the Reporterre Web Site.
     */
    @GET("{category}" + "#pagination_autres_articles")
    suspend fun getCategory(
        @Path("category") category: String,
        @Query("debut_autres_articles") number: String
    ): ResponseBody

    /**
     * Returns the css style from the Reporterre Web Site.
     *
     * @param cssUrl the url of the article to get.
     *
     * @return the article from the Reporterre Web Site.
     */
    @GET
    suspend fun getCss(@Url cssUrl: String): ResponseBody
}