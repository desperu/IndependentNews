package org.desperu.independentnews.models.network.html.reporterre

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlCategory
import org.desperu.independentnews.extension.parseHtml.getMatchAttr
import org.desperu.independentnews.extension.parseHtml.toFullUrl
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Source
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.literalDateToMillis

/**
 * Class which provides a model to parse reporterre category html page.
 *
 * @property htmlPage the reporterre article html page.
 *
 * @constructor Instantiate a new ReporterreArticle.
 *
 * @param htmlPage the reporterre article html page to set.
 */
data class ReporterreCategory(private val htmlPage: ResponseBody): BaseHtmlCategory(htmlPage) {

    override fun getArticleList(): List<Article> {
        val articleList = mutableListOf<Article>()

        // Get top story article.
        val topStory = Article(source = Source(name = REPORTERRE))
        topStory.url =
            getTagList(A)
                .getMatchAttr(CLASS, BLOC_ARTICLE)
                .attr(HREF).
                toFullUrl(REPORTERRE_BASE_URL)
        articleList.add(topStory)

        // Get all articles.
        getTagList(A).getMatchAttr(CLASS, LIEN_ARTICLE).forEach { element ->
            val article = Article(source = Source(name = REPORTERRE))

            article.url = element.attr(HREF).toFullUrl(REPORTERRE_BASE_URL)

            element.select(SPAN).getMatchAttr(CLASS, PETIT_VERT).forEach {
                literalDateToMillis(it.ownText())?.let { millis -> article.publishedDate = millis }
            }

            if (article.url != topStory.url)
                articleList.add(article)

            if (articleList.size == 51) // Fifty-one for the top story from category page
                return articleList
        }

        return articleList
    }
}