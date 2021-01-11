package org.desperu.independentnews.service

import android.content.ContentValues
import android.content.Context
import android.net.Uri

/**
 * Service for being able to access the database of the application with the Cursor support.
 */
interface ContentService {

    /**
     * Insert the given data into the database, with the uri table, the data to insert
     * and the support of the ContentProvider / ContentResolver.
     *
     * @param uri               the uri of table for which insert data.
     * @param contentValues     the content values to insert into the database.
     *
     * @return the uri of the inserted data.
     */
    fun insertData(uri: Uri, contentValues: ContentValues): Uri?
}

/**
 * Implementation of the ContentService which uses a Context instance to access the
 * database of the application with the Cursor support.
 *
 * @property context The Context instance used to access the database of the application.
 *
 * @constructor Instantiates a new ContentServiceImpl.
 *
 * @param context The Context instance used to access the database of the application to set.
 */
class ContentServiceImpl(private val context: Context) : ContentService {

    /**
     * Insert the given data into the database, with the uri table, the data to insert
     * and the support of the ContentProvider / ContentResolver.
     *
     * @param uri               the uri of table for which insert data.
     * @param contentValues     the content values to insert into the database.
     *
     * @return the uri of the inserted data.
     */
    override fun insertData(uri: Uri, contentValues: ContentValues): Uri? =
        // TODO try to use persistable uri
//        context.contentResolver.query(uri)
//        val test = CursorLoader(context, uri, null, null, null, null) as Cursor
//        test.getString(3)

//        ContentUris.withAppendedId(IdeNewsProvider.URI_CSS, id)

        try {
            context.contentResolver.insert(uri, contentValues)
        } catch (e: Exception) {
            null
        }
}