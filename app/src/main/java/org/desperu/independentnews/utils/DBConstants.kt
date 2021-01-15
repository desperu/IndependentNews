package org.desperu.independentnews.utils

// FOR CONTENT PROVIDER

// For init DB
const val DATABASE_NAME = "article"
const val DATABASE_VERSION = 1

// For create DB
internal val CREATE_DB: List<String>
    get() = listOf(
        CREATE_SOURCE_TABLE,
        CREATE_SOURCE_PAGE_TABLE,
        CREATE_SOURCE_PAGE_SOURCE_INDEX,
        CREATE_SOURCE_PAGE_CSS_INDEX,
        CREATE_CSS_TABLE,
        CREATE_ARTICLE_TABLE,
        CREATE_ARTICLE_SOURCE_INDEX,
        CREATE_ARTICLE_CSS_INDEX,
        CREATE_ROOM_MASTER,
        INSERT_ROOM_MASTER
    )
const val CREATE_SOURCE_TABLE = "CREATE TABLE IF NOT EXISTS `Source`" +
        " (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
        " `name` TEXT NOT NULL," +
        " `url` TEXT NOT NULL," +
        " `isEnabled` INTEGER NOT NULL);"

const val CREATE_SOURCE_PAGE_TABLE = "CREATE TABLE IF NOT EXISTS `SourcePage`" +
        " (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
        " `sourceId` INTEGER NOT NULL," +
        " `cssId` INTEGER NOT NULL," +
        " `url` TEXT NOT NULL," +
        " `buttonName` TEXT NOT NULL," +
        " `title` TEXT NOT NULL," +
        " `body` TEXT NOT NULL," +
        " `position` INTEGER NOT NULL," +
        " `isPrimary` INTEGER NOT NULL," +
        " FOREIGN KEY(`sourceId`) REFERENCES `Source`(`id`) ON UPDATE CASCADE ON DELETE CASCADE," +
        " FOREIGN KEY(`cssId`) REFERENCES `Css`(`id`) ON UPDATE CASCADE ON DELETE CASCADE );"
const val CREATE_SOURCE_PAGE_SOURCE_INDEX = "CREATE INDEX IF NOT EXISTS `sourcePage_sourceId_index` ON `SourcePage` (`sourceId`);"
const val CREATE_SOURCE_PAGE_CSS_INDEX = "CREATE INDEX IF NOT EXISTS `sourcePage_cssId_index` ON `SourcePage` (`cssId`);"

const val CREATE_CSS_TABLE = "CREATE TABLE IF NOT EXISTS `Css`" +
        " (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
        " `url` TEXT NOT NULL," +
        " `style` TEXT NOT NULL);"

const val CREATE_ARTICLE_TABLE = "CREATE TABLE IF NOT EXISTS `Article`" +
        " (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
        " `sourceId` INTEGER NOT NULL," +
        " `cssId` INTEGER NOT NULL," +
        " `url` TEXT NOT NULL," +
        " `title` TEXT NOT NULL," +
        " `section` TEXT NOT NULL," +
        " `theme` TEXT NOT NULL," +
        " `author` TEXT NOT NULL," +
        " `publishedDate` INTEGER NOT NULL," +
        " `article` TEXT NOT NULL," +
        " `categories` TEXT NOT NULL," +
        " `description` TEXT NOT NULL," +
        " `imageUrl` TEXT NOT NULL," +
        " `cssUrl` TEXT NOT NULL," +
        " `isTopStory` INTEGER NOT NULL," +
        " `read` INTEGER NOT NULL," +
        " FOREIGN KEY(`sourceId`) REFERENCES `Source`(`id`) ON UPDATE CASCADE ON DELETE CASCADE," +
        " FOREIGN KEY(`cssId`) REFERENCES `Css`(`id`) ON UPDATE CASCADE ON DELETE CASCADE );"
const val CREATE_ARTICLE_SOURCE_INDEX = "CREATE INDEX IF NOT EXISTS `article_sourceId_index` ON `Article` (`sourceId`);"
const val CREATE_ARTICLE_CSS_INDEX = "CREATE INDEX IF NOT EXISTS `article_cssId_index` ON `Article` (`cssId`);"

const val CREATE_ROOM_MASTER = "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT);"
const val INSERT_ROOM_MASTER = "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '5b43469ec3a35080aada025348cbd405');"

// For Css Table
const val CSS_ID = "id"
const val CSS_URL = "url"
const val CSS_STYLE = "style"