package org.desperu.independentnews.repositories.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.SourceDao
import org.desperu.independentnews.database.dao.SourcePageDao
import org.desperu.independentnews.database.dao.SourceWithDataDao
import org.desperu.independentnews.models.Source
import org.desperu.independentnews.models.SourcePage
import org.desperu.independentnews.models.SourceWithData

/**
 * Source Repository interface to get data from Source database.
 *
 * @author Desperu
 */
interface SourceRepository {

    /**
     * Return the source with it's unique identifier.
     *
     * @param id the unique identifier of the source.
     *
     * @return the source with it's unique identifier.
     */
    suspend fun getSource(id: Long): Source

    /**
     * Returns the list of enabled sources from the database.
     *
     * @return the list of enabled sources from the database.
     */
    suspend fun getEnabledSources(): List<Source>

    /**
     * Returns the list of all sources with data from the database.
     *
     * @return the list of all sources with data from the database.
     */
    suspend fun getAll(): List<SourceWithData>

    /**
     * Set the enabled state of the source in the database.
     *
     * @param id the unique identifier of the source.
     * @param isEnabled the value to update.
     */
    suspend fun setEnabled(id: Long, isEnabled: Boolean)

    /**
     * Insert the given sources in database.
     *
     * @param sources the sources to insert.
     *
     * @return the id list of inserted sources.
     */
    suspend fun insertSources(vararg sources: Source): List<Long>

    /**
     * Insert the given source pages in database.
     *
     * @param sourcePages the source pages to insert.
     *
     * @return the id list of inserted source pages.
     */
    suspend fun insertSourcePages(vararg sourcePages: SourcePage): List<Long>

    /**
     * Delete all source pages in database, for the given source ids.
     */
    suspend fun deleteAllSourcePages()
}

/**
 * Implementation of the Source Repository interface.
 *
 * @author Desperu
 *
 * @property sourceDao              the database access for source.
 * @property sourcePageDao          the database access for source page.
 * @property sourceWithDataDao      the database access for source with data to set.
 *
 * @constructor Instantiates a new SourceRepositoryImpl.
 *
 * @param sourceDao                 the database access for source to set.
 * @param sourcePageDao             the database access for source page to set.
 * @param sourceWithDataDao         the database access for source with data to set.
 */
class SourceRepositoryImpl(
    private val sourceDao: SourceDao,
    private val sourcePageDao: SourcePageDao,
    private val sourceWithDataDao: SourceWithDataDao
): SourceRepository {

    /**
     * Return the source with it's unique identifier.
     *
     * @return the source with it's unique identifier.
     */
    override suspend fun getSource(id: Long): Source = sourceDao.getSource(id)

    /**
     * Returns the list of enabled sources.
     *
     * @return the list of enabled sources.
     */
    override suspend fun getEnabledSources(): List<Source> = sourceDao.getEnabled()

    /**
     * Returns the list of all sources with data from the database.
     *
     * @return the list of all sources with data from the database.
     */
    override suspend fun getAll(): List<SourceWithData> = withContext(Dispatchers.IO) {
        return@withContext sourceWithDataDao.getAll()
    }

    /**
     * Set the enabled state of the source in the database.
     *
     * @param id the unique identifier of the source.
     * @param isEnabled the value to update.
     */
    override suspend fun setEnabled(id: Long, isEnabled: Boolean) = withContext(Dispatchers.IO) {
        sourceDao.setIsEnabled(id, isEnabled)
    }

    /**
     * Insert the given sources in database.
     *
     * @param sources the sources to insert.
     *
     * @return the id list of inserted sources.
     */
    override suspend fun insertSources(vararg sources: Source): List<Long> = withContext(Dispatchers.IO) {
        sourceDao.insertSources(*sources)
    }

    /**
     * Insert the given source pages in database.
     *
     * @param sourcePages the source pages to insert.
     *
     * @return the id list of inserted source pages.
     */
    override suspend fun insertSourcePages(vararg sourcePages: SourcePage): List<Long> = withContext(Dispatchers.IO) {
        sourcePageDao.insertSourcePages(*sourcePages)
    }

    /**
     * Delete all source pages in database, for the given source ids.
     */
    override suspend fun deleteAllSourcePages() = withContext(Dispatchers.IO) {
        val ids = sourcePageDao.getAll().map { it.id }
        ids.forEach { sourcePageDao.deleteSourcePage(it) }
    }
}