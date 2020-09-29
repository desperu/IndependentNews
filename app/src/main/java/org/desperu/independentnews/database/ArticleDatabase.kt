package org.desperu.independentnews.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.desperu.independentnews.database.dao.ArticleDao
import org.desperu.independentnews.database.dao.SourceDao
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.models.Source

/**
 * The database class of the application.
 */
@Database(entities = [Article::class, Source::class], version = 1)
abstract class ArticleDatabase: RoomDatabase() {

    /**
     * Returns the database access object for articles.
     *
     * @return the database access object for articles.
     */
    abstract fun articleDao(): ArticleDao

    /**
     * Returns the database access object for sources.
     *
     * @return the database access object for sources.
     */
    abstract fun sourceDao(): SourceDao
}