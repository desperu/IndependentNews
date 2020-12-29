package org.desperu.independentnews.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.desperu.independentnews.database.dao.ArticleDao
import org.desperu.independentnews.database.dao.SourceDao
import org.desperu.independentnews.database.dao.SourcePageDao
import org.desperu.independentnews.database.dao.SourceWithDataDao
import org.desperu.independentnews.models.database.Article
import org.desperu.independentnews.models.database.Source
import org.desperu.independentnews.models.database.SourcePage

/**
 * The database class of the application.
 */
@Database(entities = [Article::class, Source::class, SourcePage::class], version = 1)
abstract class ArticleDatabase: RoomDatabase() {

    /**
     * Returns the database access object for articles.
     *
     * @return the database access object for articles.
     */
    abstract fun articleDao(): ArticleDao

    /**
     * Returns the database access object for sources with data.
     *
     * @return the database access object for sources with data.
     */
    abstract fun sourceWithDataDao(): SourceWithDataDao

    /**
     * Returns the database access object for sources.
     *
     * @return the database access object for sources.
     */
    abstract fun sourceDao(): SourceDao

    /**
     * Returns the database access object for source pages.
     *
     * @return the database access object for source pages.
     */
    abstract fun sourcePageDao(): SourcePageDao
}