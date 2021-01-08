package org.desperu.independentnews.provider

import android.content.*
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import org.desperu.independentnews.models.database.Css
import org.desperu.independentnews.utils.CREATE_DB
import org.desperu.independentnews.utils.CSS_ID
import org.desperu.independentnews.utils.DATABASE_NAME
import org.desperu.independentnews.utils.DATABASE_VERSION

/**
 * Content provider class that allow database access for css table from the whole application.
 *
 * @constructor Instantiate a new IdeNewsProvider.
 */
class IdeNewsProvider : ContentProvider() {

    // FOR DATA
    private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
        addURI(AUTHORITY, CSS_TABLE_NAME, CSS)
        addURI(AUTHORITY, "$CSS_TABLE_NAME/#", CSS)
    }

    private lateinit var db: SQLiteDatabase

    override fun onCreate(): Boolean {
        db = DatabaseHelper(context).writableDatabase
        return ::db.isInitialized
    }

    private val values: HashMap<String, String>? = null

    override fun query(
        uri: Uri, projection: Array<String?>?, selection: String?,
        selectionArgs: Array<String?>?, sortOrder: String?
    ): Cursor? {
        context?.let {
            val qb = SQLiteQueryBuilder()
            qb.tables = CSS_TABLE_NAME

            when (uriMatcher.match(uri)) {
                CSS -> qb.projectionMap = values
            }

            val cursor = qb.query(db, projection, selection, selectionArgs, null, null, CSS_ID)
            cursor?.setNotificationUri(it.contentResolver, uri)
            return cursor
        }
        throw IllegalArgumentException("Failed to query row for uri $uri")
    }

    override fun getType(uri: Uri): String = when (uriMatcher.match(uri)) {
        CSS -> "vnd.android.cursor.item/$AUTHORITY.$CSS_TABLE_NAME"
        else -> throw IllegalArgumentException("Unsupported URI: $uri")
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri {
        context?.let {
            val id: Long = when (uriMatcher.match(uri)) {
                CSS -> db.insert(CSS_TABLE_NAME, "", contentValues)
                else -> 0L
            }

            if (id > 0) {
                it.contentResolver.notifyChange(uri, null)
                return ContentUris.withAppendedId(uri, id)
            }
        }
        throw IllegalArgumentException("Failed to insert row into $uri")
    }

    override fun update(
        uri: Uri,
        contentValues: ContentValues?,
        s: String?,
        strings: Array<String?>?
    ): Int {
        context?.let {
            val count: Int = when (uriMatcher.match(uri)) {
                CSS -> db.update(CSS_TABLE_NAME, contentValues, s, strings)
                else -> 0
            }

            it.contentResolver.notifyChange(uri, null)
            return count
        }
        throw IllegalArgumentException("Failed to update row into $uri")
    }

    override fun delete(uri: Uri, s: String?, strings: Array<String?>?): Int {
        context?.let {
            val id = ContentUris.parseId(uri)
            val count: Int = when (uriMatcher.match(uri)) {
                CSS -> db.delete(CSS_TABLE_NAME, "$CSS_ID = $id", strings)
                else -> 0
            }

            it.contentResolver.notifyChange(uri, null)
            return count
        }
        throw IllegalArgumentException("Failed to delete row into $uri")
    }

    companion object {
        // FOR DATA
        const val AUTHORITY = "org.desperu.independentnews.provider"

        private const val CSS = 0

        private val CSS_TABLE_NAME = Css::class.java.simpleName

        private const val BASE_URI = "content://$AUTHORITY/"
        val URI_CSS: Uri = Uri.parse(BASE_URI + CSS_TABLE_NAME)
    }


    /**
     * Database helper which provide support to create and upgrade database.
     */
    private class DatabaseHelper constructor(context: Context?)
        : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        // creating all table in the database
        override fun onCreate(db: SQLiteDatabase) {
            CREATE_DB.forEach { db.execSQL(it) }
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            // sql query to drop a table
            // having similar name
            db.execSQL("DROP TABLE IF EXISTS $CSS_TABLE_NAME")
            onCreate(db)
        }
    }
}