package org.desperu.independentnews.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.desperu.independentnews.database.dao.ArticleDao
import org.desperu.independentnews.database.dao.SourceDao
import org.desperu.independentnews.database.dao.SourcePageDao
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.Source
import org.desperu.independentnews.models.SourcePage
import org.desperu.independentnews.models.SourceWithData

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
    abstract fun sourceWithDataDao(): SourceWithData

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