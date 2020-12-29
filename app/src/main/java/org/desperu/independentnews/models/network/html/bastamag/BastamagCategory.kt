package org.desperu.independentnews.models.network.html.bastamag

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlCategory
import org.desperu.independentnews.extension.parseHtml.getMatchAttr
import org.desperu.independentnews.extension.parseHtml.toFullUrl
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.stringToDate

/**
 * Class which provides a model to parse bastamag category html page.
 *
 * @property htmlPage the bastamag article html page.
 *
 * @constructor Instantiate a new BastamagArticle.
 *
 * @param htmlPage the bastamag article html page to set.
 */
data class BastamagCategory(private val htmlPage: ResponseBody): BaseHtmlCategory(htmlPage) {

    override fun getArticleList(): List<Article> {
        val articleList = mutableListOf<Article>()

        getTagList(ARTICLE).getMatchAttr(CLASS, ARTICLE_ENTRY).forEach { element ->
            val article = Article(sourceName = BASTAMAG)

            // Set the published date of the article
            element.select(TIME).getMatchAttr(PUBDATE, PUBDATE).forEach {
                stringToDate(it.attr(DATETIME))?.time?.let { millis ->
                    article.publishedDate = millis
                }
            }

            // Set the url of the article
            element.select(H3).forEach {
                if (it.attr(CLASS) == ENTRY_TITLE)
                    article.url = it.select(a).attr(HREF).toFullUrl(BASTAMAG_BASE_URL)
            }

            articleList.add(article)
        }

        return articleList
    }
}