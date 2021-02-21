package org.desperu.independentnews.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.desperu.independentnews.database.dao.*
import org.desperu.independentnews.models.database.*
import org.desperu.independentnews.utils.DATABASE_VERSION

/**
 * The database class of the application.
 */
@Database(
    entities = [
        Article::class,
        Css::class,
        Favorite::class,
        Paused::class,
        Source::class,
        SourcePage::class
    ],
    version = DATABASE_VERSION
)
abstract class ArticleDatabase: RoomDatabase() {

    /**
     * Returns the database access object for article with data.
     *
     * @return the database access object for article with data.
     */
    abstract fun articleWithDataDao(): ArticleWithDataDao

    /**
     * Returns the database access object for articles.
     *
     * @return the database access object for articles.
     */
    abstract fun articleDao(): ArticleDao

    /**
     * Returns the database access object for css.
     *
     * @return the database access object for css.
     */
    abstract fun cssDao(): CssDao

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

    /**
     * Returns the database access object for favorite.
     *
     * @return the database access object for favorite.
     */
    abstract fun favoriteDao(): FavoriteDao

    /**
     * Returns the database access object for paused.
     *
     * @return the database access object for paused.
     */
    abstract fun pausedDao(): PausedDao
}