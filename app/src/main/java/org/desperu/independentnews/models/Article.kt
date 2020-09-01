package org.desperu.independentnews.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Class witch provides a model for article.
 *
 * @constructor Sets all properties of the article.
 *
 * @property id the unique identifier of the article.
 * @property source the source of the article.
 * @property title the title of the article.
 * @property author the author of the article.
 * @property date the published date of the article.
 * @property article the body of the article.
 * @property categories the categories of the article.
 * @property imageUrl the image url of the article.
 */
@Entity
data class Article(@PrimaryKey(autoGenerate = true)
                   val id: Long = 0L,
                   var source: String = "",
                   var title: String = "",
                   var author: String = "",
                   var date: String = "",
                   var article: String = "",
                   var categories: String = "",
                   var imageUrl: String = "")