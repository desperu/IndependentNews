package org.desperu.independentnews.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Class witch provides a model for an article.
 *
 * @param id            the unique identifier of the article.
 * @param source        the source of the article.
 * @param url           the url of the article.
 * @param title         the title of the article.
 * @param section       the section of the article.
 * @param theme         the theme of the article.
 * @param author        the author of the article.
 * @param publishedDate the published date of the article.
 * @param article       the body of the article.
 * @param categories    the categories of the article.
 * @param description   the description of the article.
 * @param imageUrl      the image url of the article.
 * @param read          whether the article has been read or not.
 *
 * @constructor Sets all properties of the article.
 *
 * @property id             the unique identifier of the article to set.
 * @property source         the source of the article to set.
 * @property url            the url of the article to set.
 * @property title          the title of the article to set.
 * @property section        the section of the article to set.
 * @property theme          the theme of the article to set.
 * @property author         the author of the article to set.
 * @property publishedDate  the published date of the article to set.
 * @property article        the body of the article to set.
 * @property categories     the categories of the article to set.
 * @property description    the description of the article to set.
 * @property imageUrl       the image url of the article to set.
 * @property read           whether the article has been read or not to set.
 */
@Parcelize
@Entity
data class Article(@PrimaryKey(autoGenerate = true)
                   val id: Long = 0L,
                   var source: String = "",
                   var url: String = "",
                   var title: String = "",
                   var section: String = "",
                   var theme: String = "",
                   var author: String = "",
                   var publishedDate: Long = 0L,
                   var article: String = "",
                   var categories: String = "",
                   var description: String = "",
                   var imageUrl: String = "",
                   var read: Boolean = false, // TODO check gahfy project NytMvvM viewedArticle
                   var css: String = "" // TODO on test
): Parcelable