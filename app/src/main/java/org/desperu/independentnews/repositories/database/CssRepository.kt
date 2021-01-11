package org.desperu.independentnews.repositories.database

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.CssDao
import org.desperu.independentnews.models.database.Css
import org.desperu.independentnews.provider.IdeNewsProvider
import org.desperu.independentnews.service.ContentService
import org.koin.core.KoinComponent
import org.koin.core.get

/**
 * Css Repository interface to get data from Css database.
 *
 * @author Desperu
 */
interface CssRepository {

    /**
     * Return the cs with it's unique identifier.
     *
     * @param id the unique identifier of the cs.
     *
     * @return the cs with it's unique identifier.
     */
    suspend fun getCss(id: Long): Css

    /**
     * Returns the list of enabled css from the database.
     *
     * @param articleId the article id to get the corresponding css from database.
     *
     * @return the list of enabled css from the database.
     */
    suspend fun getArticleCss(articleId: Long): Css

    /**
     * Returns the list of all css with data from the database.
     *
     * @return the list of all css with data from the database.
     */
    suspend fun getAll(): List<Css>

    /**
     * Insert the given css pages in database.
     *
     * @param css the css pages to insert.
     *
     * @return the id list of inserted css pages.
     */
    suspend fun insertCss(vararg css: Css): List<Long>

    /**
     * Insert the given css in the database, with the content provider and cursor support.
     *
     * @param css the css to insert in the database.
     *
     * @return the uri of the inserted css.
     */
    fun insertCssWithProvider(css: Css): Uri?

    /**
     * Update the given css in database.
     *
     * @param css the css to update.
     *
     * @return the number of row affected.
     */
    suspend fun updateCss(css: Css): Int

    /**
     * Delete article css in database, for the given article id.
     *
     * @param articleId the article id to delete the corresponding css from database.
     *
     * @return the number of row affected.
     */
    suspend fun deleteArticleCss(articleId: Long): Int
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
     * Return the css with it's unique identifier.
     *
     * @return the css with it's unique identifier.
     */
    override suspend fun getCss(id: Long): Css = withContext(Dispatchers.IO) {
        return@withContext cssDao.getCss(id)
    }

    /**
     * Returns the list of enabled css.
     *
     * @return the list of enabled css.
     */
    override suspend fun getArticleCss(articleId: Long): Css = withContext(Dispatchers.IO) {
        return@withContext cssDao.getArticleCss(articleId)
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
     * Insert the given css in the database.
     *
     * @param css the css to insert.
     *
     * @return the id list of inserted css.
     */
    override suspend fun insertCss(vararg css: Css): List<Long> = withContext(Dispatchers.IO) {
        cssDao.insertCss(*css)
    }

    /**
     * Insert the given css in the database, with the content provider and cursor support.
     *
     * @param css the css to insert in the database.
     *
     * @return the uri of the inserted css.
     */
    override fun insertCssWithProvider(css: Css): Uri? = // TODO to remove
        get<ContentService>().insertData(IdeNewsProvider.URI_CSS, css.toContentValues(css))

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
     * Delete article css in database, for the given article id.
     *
     * @param articleId the article id to delete the corresponding css from database.
     *
     * @return the number of row affected.
     */
    override suspend fun deleteArticleCss(articleId: Long): Int = withContext(Dispatchers.IO) {
        cssDao.deleteArticleCss(articleId)
    }
}