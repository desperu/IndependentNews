package org.desperu.independentnews.models.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Class witch provides a model for a css style.
 *
 * @property id             the unique identifier of the css style.
 * @property articleId      the unique identifier of the article.
 * @property url            the url of the css style.
 * @property content        the content of the css style.
 *
 * @constructor Sets all properties of the css style.
 *
 * @param id                the unique identifier of the css style to set.
 * @param articleId         the unique identifier of the article to set.
 * @param url               the url of the css style to set.
 * @param content           the content of the css style to set.
 */
@Parcelize
@Entity(foreignKeys = [ForeignKey(entity = Article::class,
    parentColumns = ["id"],
    childColumns = ["articleId"],
    onUpdate = ForeignKey.CASCADE,
    onDelete = ForeignKey.CASCADE)], indices = [Index(name = "css_articleId_index", value = ["articleId"])])
data class Css(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    var articleId: Long = 0L,
    val url: String = "",
    var content: String = ""
): Parcelable