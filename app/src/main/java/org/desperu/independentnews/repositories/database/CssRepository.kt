package org.desperu.independentnews.repositories.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.CssDao
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Css
import org.desperu.independentnews.utils.SOURCE
import org.desperu.independentnews.utils.SourcesUtils.getAdditionalCss
import org.desperu.independentnews.utils.Utils.deConcatenateStringToMutableList
import org.koin.core.component.KoinComponent

/**
 * Css Repository interface to get data from Css database.
 *
 * @author Desperu
 */
interface CssRepository {

    /**
     * Return the css with it's unique identifier.
     *
     * @param id the unique identifier of the css.
     *
     * @return the css with it's unique identifier.
     */
    suspend fun getCss(id: Long): Css

    /**
     * Return the css with it's url.
     *
     * @param url the url of the css.
     *
     * @return the css with it's url, null if not find.
     */
    suspend fun getCssForUrl(url: String): Css?

    /**
     * Returns the css for the given url, concatenate css style if there's multiples urls.
     * Add additional css style to customize article style.
     *
     * @param url           the css url for which get the style.
     * @param sourceName    the source name of the css.
     * @param isSourcePage  true if it is for source page, false otherwise.
     *
     * @return the css style, concatenated if needed.
     */
    suspend fun getCssStyle(url: String, sourceName: String?, isSourcePage: Boolean): Css

    /**
     * Returns the list of all css with data from the database.
     *
     * @return the list of all css with data from the database.
     */
    suspend fun getAll(): List<Css>

    /**
     * Insert the given css in the database.
     *
     * @param css the css to insert.
     *
     * @return the row id of inserted css.
     */
    suspend fun insertCss(vararg css: Css): List<Long>

    /**
     * Update the given css in database.
     *
     * @param css the css to update.
     *
     * @return the number of row affected.
     */
    suspend fun updateCss(css: Css): Int

    /**
     * Remove unused css from the database.
     *
     * @param articleList the article list actually in the database.
     *
     * @return the number of row affected.
     */
    suspend fun removeOldCss(articleList: List<Article>): Int
}

/**
 * Implementation of the Css Repository interface.
 *
 * @author Desperu
 *
 * @property cssDao              the database access for css.
 *
 * @constructor Instantiates a new CssRepositoryImpl.
 *
 * @param cssDao                 the database access for css to set.
 */
class CssRepositoryImpl(private val cssDao: CssDao): CssRepository, KoinComponent {

    /**
     * Returns the css with it's unique identifier.
     *
     * @return the css with it's unique identifier.
     */
    override suspend fun getCss(id: Long): Css = withContext(Dispatchers.IO) {
        return@withContext cssDao.getCss(id)
    }

    /**
     * Returns the css with it's url.
     *
     * @param url the url of the css.
     *
     * @return the css with it's url, null if not find.
     */
    override suspend fun getCssForUrl(url: String): Css? = withContext(Dispatchers.IO) {
        return@withContext cssDao.getCssForUrl(url)
    }

    /**
     * Returns the css for the given url, concatenate css style if there's multiples urls.
     * Add additional css style to customize article style.
     *
     * @param url           the css url for which get the style.
     * @param sourceName    the source name of the css.
     * @param isSourcePage  true if it is for source page, false otherwise.
     *
     * @return the css style, concatenated if needed.
     */
    override suspend fun getCssStyle(
        url: String,
        sourceName: String?,
        isSourcePage: Boolean
    ): Css = withContext(Dispatchers.IO) {
        var cssStyle = String()

        if (url.contains(",")) {
            val urls = deConcatenateStringToMutableList(url)

            urls.forEachIndexed { index, url ->
                if (index != 0) cssStyle += " "
                cssStyle += cssDao.getCssForUrl(url)?.style
            }
        } else
            cssStyle = cssDao.getCssForUrl(url)?.style.mToString()

        if (!sourceName.isNullOrBlank()) {
            // Add some css to correct/perfect article design
            val handledSourceName = sourceName + if (isSourcePage) SOURCE else ""
            cssStyle += " ${getAdditionalCss(handledSourceName)}"
        }

        return@withContext Css(url = url, style = cssStyle)
    }

    /**
     * Returns the list of all css with data from the database.
     *
     * @return the list of all css with data from the database.
     */
    override suspend fun getAll(): List<Css> = withContext(Dispatchers.IO) {
        return@withContext cssDao.getAll()
    }

    /**
     * Insert the given css style in the database.
     *
     * @param css the css to insert.
     *
     * @return the row id of the inserted css.
     */
    override suspend fun insertCss(vararg css: Css): List<Long> = withContext(Dispatchers.IO) {
        return@withContext cssDao.insertCss(*css)
    }

    /**
     * Insert the given css in database.
     *
     * @param css the css to insert.
     *
     * @return the number of row affected.
     */
    override suspend fun updateCss(css: Css): Int = withContext(Dispatchers.IO) {
        cssDao.updateCss(css)
    }

    /**
     * Remove unused css from the database.
     *
     * @param articleList the article list actually in the database.
     *
     * @return the number of row affected.
     */
    override suspend fun removeOldCss(articleList: List<Article>): Int = withContext(Dispatchers.IO) {
        val cssList = cssDao.getAll()
        val articleCssUrls = mutableListOf<String>()

        articleList.forEach { article ->
            val urls = deConcatenateStringToMutableList(article.cssUrl)

            urls.forEach {
                if (!articleCssUrls.contains(it))
                    articleCssUrls.add(it)
            }
        }

        val cssToRemove = cssList.filter { !articleCssUrls.contains(it.url) }

        cssDao.deleteCss(*cssToRemove.toTypedArray())
    }
}