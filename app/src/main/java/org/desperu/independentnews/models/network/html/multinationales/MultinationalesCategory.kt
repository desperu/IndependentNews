package org.desperu.independentnews.models.network.html.multinationales

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlCategory
import org.desperu.independentnews.extension.parseHtml.getMatchAttr
import org.desperu.independentnews.extension.parseHtml.toFullUrl
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Source
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.stringToDate

/**
 * Class which provides a model to parse multinationales category html page.
 *
 * @property htmlPage the multinationales article html page.
 *
 * @constructor Instantiate a new MultinationalesArticle.
 *
 * @param htmlPage the multinationales article html page to set.
 */
data class MultinationalesCategory(private val htmlPage: ResponseBody): BaseHtmlCategory(htmlPage) {

    override fun getArticleList(): List<Article> {
        val articleList = mutableListOf<Article>()

        getTagList(LI).getMatchAttr(CLASS, ITEM_LONG).forEach { element ->
            val article = Article(source = Source(name = MULTINATIONALES))

            // Set the published date of the article
            element.select(TIME).getMatchAttr(PUBDATE, PUBDATE).forEach {
                stringToDate(it.attr(DATETIME))?.time?.let { millis ->
                    article.publishedDate = millis
                }
            }

            // Set the url of the article
            element.select(H3).forEach {
                if (it.attr(CLASS) == H3)
                    article.url = it.select(a).attr(HREF).toFullUrl(MULTINATIONALES_BASE_URL)
            }

            articleList.add(article)
        }

        return articleList
    }
}