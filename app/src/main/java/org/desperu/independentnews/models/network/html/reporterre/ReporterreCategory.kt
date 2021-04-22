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

        // Get all articles.
        getTagList(A).getMatchAttr(CLASS, LIEN_ARTICLE).forEach { element ->
            val article = Article(source = Source(name = REPORTERRE))

            // Set the url of the article
            article.url = element.attr(HREF).toFullUrl(REPORTERRE_BASE_URL)

            // Set the published date of the article
            element.select(P).getMatchAttr(CLASS, ARTICLE_DATE).forEach {
                literalDateToMillis(it.ownText())?.let { millis -> article.publishedDate = millis }
            }

            articleList.add(article)
        }

        return articleList
    }
}