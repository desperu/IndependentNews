package org.desperu.independentnews.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Class witch provides a model for an article.
 *
 * @param id the unique identifier of the article.
 * @param source the source of the article.
 * @param url the url of the article.
 * @param title the title of the article.
 * @param author the author of the article.
 * @param publishedDate the published date of the article.
 * @param article the body of the article.
 * @param categories the categories of the article.
 * @param description the description of the article.
 * @param imageUrl the image url of the article.
 *
 * @constructor Sets all properties of the article.
 *
 * @property id the unique identifier of the article to set.
 * @property source the source of the article to set.
 * @property url the url of the article to set.
 * @property title the title of the article to set.
 * @property author the author of the article to set.
 * @property publishedDate the published date of the article to set.
 * @property article the body of the article to set.
 * @property categories the categories of the article to set.
 * @property description the description of the article to set.
 * @property imageUrl the image url of the article to set.
 */
@Entity
data class Article(@PrimaryKey(autoGenerate = true)
                   val id: Long = 0L,
                   var source: String = "",
                   var url: String = "",
                   var title: String = "",
                   var author: String = "",
                   var publishedDate: Long = 0L,
                   var article: String = "",
                   var categories: String = "",
                   var description: String = "",
                   var imageUrl: String = "",
                   var read: Boolean = false // TODO check gahfy project NytMvvM viewedArticle
)