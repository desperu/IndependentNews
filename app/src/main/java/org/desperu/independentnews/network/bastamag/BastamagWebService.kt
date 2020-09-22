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

//    /**
//     * Request get for Top Stories.
//     * @param section Section name.
//     * @param apiKey Api key of this application.
//     * @return An Observable object of NyTimesAPI model.
//     */
//    @GET("topstories/v2/{section}.json")
//    fun getNyTimesTopStories(
//        @Path("section") section: String?,
//        @Query("api-key") apiKey: String?
//    ): Observable<NyTimesAPI?>?
//
//    /**
//     * Request get for Most Popular.
//     * @param apiKey Api key of this application.
//     * @return An Observable object of NyTimesAPI model.
//     */
//    @GET("mostpopular/v2/viewed/1.json")
//    fun getNyTimesMostPopular(@Query("api-key") apiKey: String?): Observable<NyTimesAPI?>?
//
//    /**
//     * Request get for New York Times Search.
//     * @param queryTerms Search query terms.
//     * @param beginDate Begin date to search.
//     * @param endDate End date to search.
//     * @param sections Sections into search.
//     * @param sort Order to sort.
//     * @param apiKey Api key of this application.
//     * @return An Observable object of NyTimesAPI model.
//     */
//    @GET("search/v2/articlesearch.json")
//    fun getNyTimesSearch(
//        @Query(value = "q", encoded = true) queryTerms: String?,
//        @Query("begin_date") beginDate: String?,
//        @Query("end_date") endDate: String?,
//        @Query("fq") sections: String?,
//        @Query("sort") sort: String?,
//        @Query("api-key") apiKey: String?
//    ): Observable<NyTimesAPI?>?
}