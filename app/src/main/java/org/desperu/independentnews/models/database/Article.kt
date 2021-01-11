package org.desperu.independentnews.models.database

import android.os.Parcelable
import androidx.room.*
import kotlinx.android.parcel.Parcelize

/**
 * Class witch provides a model for an article.
 *
 * @property id                 the unique identifier of the article.
 * @property sourceName         the source name of the article.
 * @property url                the url of the article.
 * @property title              the title of the article.
 * @property section            the section of the article.
 * @property theme              the theme of the article.
 * @property author             the author of the article.
 * @property publishedDate      the published date of the article.
 * @property article            the body of the article.
 * @property categories         the categories of the article.
 * @property description        the description of the article.
 * @property imageUrl           the image url of the article.
 * @property cssUrl             the css url of the article.
 * @property isTopStory         whether the article is top story.
 * @property read               whether the article has been read or not.
 * @property source             the source of the article.
 * @property cssStyle           the css style of the article.
 *
 * @constructor Sets all properties of the article.
 *
 * @param id                    the unique identifier of the article to set.
 * @param sourceName            the source name of the article to set.
 * @param url                   the url of the article to set.
 * @param title                 the title of the article to set.
 * @param section               the section of the article to set.
 * @param theme                 the theme of the article to set.
 * @param author                the author of the article to set.
 * @param publishedDate         the published date of the article to set.
 * @param article               the body of the article to set.
 * @param categories            the categories of the article to set.
 * @param description           the description of the article to set.
 * @param imageUrl              the image url of the article to set.
 * @param cssUrl                the css url of the article to set.
 * @param isTopStory            whether the article is top story to set.
 * @param read                  whether the article has been read or not to set.
 * @param source                the source of the article to set.
 * @param cssStyle              the css style of the article to set.
 */
@Parcelize
@Entity(foreignKeys = [ForeignKey(entity = Source::class,
    parentColumns = ["id"],
    childColumns = ["sourceId"],
    onUpdate = ForeignKey.CASCADE,
    onDelete = ForeignKey.CASCADE)],
    indices = [Index(name = "article_sourceId_index", value = ["sourceId"])])
data class Article(@PrimaryKey(autoGenerate = true)
                   var id: Long = 0L,
                   var sourceId: Long = 0L,
                   var sourceName: String = "", // TODO to remove, use source.name
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
                   var read: Boolean = false,
//                   var scrollPosition: Int = 0,

                   // Should use ArticleWithData instead
                   @Ignore
                   var source: Source = Source(),
                   @Ignore
                   var cssStyle: String = ""
): Parcelable

// TODO export body and remove unused param