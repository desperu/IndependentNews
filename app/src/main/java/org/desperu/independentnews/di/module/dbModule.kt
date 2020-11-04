package org.desperu.independentnews.di.module

import androidx.room.Room
import org.desperu.independentnews.database.ArticleDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * The database name of the application.
 */
private const val DATABASE_NAME = "article"

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
     * Provides the database access object for articles.
     */
    single{
        (get() as ArticleDatabase).articleDao()
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