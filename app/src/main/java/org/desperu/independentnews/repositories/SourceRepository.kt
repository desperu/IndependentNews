package org.desperu.independentnews.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.SourceDao
import org.desperu.independentnews.models.Source
import org.desperu.independentnews.utils.BASTAMAG_SOURCE
import org.desperu.independentnews.utils.REPORTERRE_SOURCE

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
    suspend fun getEnabledSources(): List<Source>?

    /**
     * Returns the list of all sources from the database.
     *
     * @return the list of all sources from the database.
     */
    suspend fun getAll(): List<Source>?

    /**
     * Set the enabled state of the source in the database.
     *
     * @param name the name of the source.
     * @param isEnabled the value to update.
     */
    suspend fun setEnabled(name: String, isEnabled: Boolean)

    /**
     * Insert the given sources in database.
     *
     * @param sources the sources to insert.
     */
    suspend fun insertSources(vararg sources: Source)

    /**
     * Create all sources in database for first apk start.
     */
    suspend fun createSourcesForFirstStart()
}

/**
 * Implementation of the Source Repository interface.
 *
 * @author Desperu
 *
 * @property sourceDao      the database access for source.
 *
 * @constructor Instantiates a new SourceRepositoryImpl.
 *
 * @param sourceDao         the database access for source to set.
 */
class SourceRepositoryImpl(private val sourceDao: SourceDao): SourceRepository {

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
     * Returns the list of all sources from the database.
     *
     * @return the list of all sources from the database.
     */
    override suspend fun getAll(): List<Source>? = withContext(Dispatchers.IO) {
        return@withContext sourceDao.getAll()
    }

    /**
     * Set the enabled state of the source in the database.
     *
     * @param name the name of the source.
     * @param isEnabled the value to update.
     */
    override suspend fun setEnabled(name: String, isEnabled: Boolean) = withContext(Dispatchers.IO) {
        sourceDao.setIsEnabled(name, isEnabled)
    }

    /**
     * Insert the given sources in database.
     *
     * @param sources the sources to insert.
     */
    override suspend fun insertSources(vararg sources: Source) = withContext(Dispatchers.IO) {
        sourceDao.insertSources(*sources)
    }

    /**
     * Create all sources in database for first apk start.
     */
    override suspend fun createSourcesForFirstStart() = withContext(Dispatchers.IO) {
        insertSources(BASTAMAG_SOURCE, REPORTERRE_SOURCE)
    }
}