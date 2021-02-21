package org.desperu.independentnews.di.module

import androidx.room.Room
import org.desperu.independentnews.database.ArticleDatabase
import org.desperu.independentnews.utils.DATABASE_NAME
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Koin module which provides dependency related to database.
 */
val dbModule = module{

    /**
     * Provides the database of the application.
     */
    single{
        Room.databaseBuilder(androidContext(), ArticleDatabase::class.java, DATABASE_NAME).build()
    }

    /**
     * Provides the database access object for article with data.
     */
    single{
        (get() as ArticleDatabase).articleWithDataDao()
    }

    /**
     * Provides the database access object for articles.
     */
    single{
        (get() as ArticleDatabase).articleDao()
    }

    /**
     * Provides the database access object for css.
     */
    single{
        (get() as ArticleDatabase).cssDao()
    }

    /**
     * Provides the database access object for favorite.
     */
    single{
        (get() as ArticleDatabase).favoriteDao()
    }

    /**
     * Provides the database access object for paused.
     */
    single{
        (get() as ArticleDatabase).pausedDao()
    }

    /**
     * Provides the database access object for source with data.
     */
    single{
        (get() as ArticleDatabase).sourceWithDataDao()
    }

    /**
     * Provides the database access object for sources.
     */
    single{
        (get() as ArticleDatabase).sourceDao()
    }

    /**
     * Provides the database access object for source pages.
     */
    single{
        (get() as ArticleDatabase).sourcePageDao()
    }
}