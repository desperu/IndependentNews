package org.desperu.independentnews.models

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

/**
 * Class witch provides a model for an article.
 *
 * @param id                the unique identifier of the article.
 * @param sourceName        the source name of the article.
 * @param url               the url of the article.
 * @param title             the title of the article.
 * @param section           the section of the article.
 * @param theme             the theme of the article.
 * @param author            the author of the article.
 * @param publishedDate     the published date of the article.
 * @param article           the body of the article.
 * @param categories        the categories of the article.
 * @param description       the description of the article.
 * @param imageUrl          the image url of the article.
 * @param cssUrl            the css url of the article.
 * @param isTopStory        whether the article is top story.
 * @param read              whether the article has been read or not.
 * @param source            the source of the article.
 *
 * @constructor Sets all properties of the article.
 *
 * @property id             the unique identifier of the article to set.
 * @property sourceName     the source name of the article to set.
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
 * @property cssUrl         the css url of the article to set.
 * @property isTopStory     whether the article is top story to set.
 * @property read           whether the article has been read or not to set.
 * @property source         the source of the article to set.
 */
@Parcelize
@Entity(foreignKeys = [ForeignKey(entity = Source::class,
    parentColumns = ["id"],
    childColumns = ["sourceId"])],
    indices = [Index(name = "sourceId_index", value = ["sourceId"])])
data class Article(@PrimaryKey(autoGenerate = true)
                   var id: Long = 0L,
                   var sourceId: Long = 0L,
                   var sourceName: String = "",
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
                   var cssUrl: String = "",
                   var isTopStory: Boolean = false,
                   var read: Boolean = false, // TODO check gahfy project NytMvvM viewedArticle
//                   var scrollPosition: Int = 0,
                   @Ignore
                   var source: Source = Source()
): Parcelable