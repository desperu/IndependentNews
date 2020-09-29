package org.desperu.independentnews.repositories

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.desperu.independentnews.database.dao.SourceDao
import org.desperu.independentnews.models.Source

/**
 * Source Repository interface to get data from Source database.
 *
 * @author Desperu
 */
interface SourceRepository {

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
     * Create sources in database, used for first apk start.
     *
     * @param sources the sources to create.
     */
    suspend fun createSources(vararg sources: Source)
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
class SourceNewsRepositoryImpl(private val sourceDao: SourceDao): SourceRepository {

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
     * Create sources in database, used for first apk start.
     *
     * @param sources the sources to create.
     */
    override suspend fun createSources(vararg sources: Source) = withContext(Dispatchers.IO) {
        sourceDao.insertSources(*sources)
    }
}