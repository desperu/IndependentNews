package org.desperu.independentnews.models.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Class which provide a model for a Paused Article.
 *
 * @property id                 the unique identifier of the paused.
 * @property articleId          the unique identifier of the article of this paused.
 * @property scrollPosition     the scroll position of the article.
 * @property creationDate       the creation date of the paused.
 *
 * @constructor Instantiate a new Paused.
 *
 * @param id                    the unique identifier of the paused to set.
 * @param articleId             the unique identifier of the article of this paused to set.
 * @param scrollPosition        the scroll position of the article to set.
 * @param creationDate          the creation date of the paused to set.
 */
@Parcelize
@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Article::class,
            parentColumns = ["id"],
            childColumns = ["articleId"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(name = "paused_articleId_index", value = ["articleId"])]
)
data class Paused(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var articleId: Long = 0L,
    var scrollPosition: Float = 0f,
    var creationDate: Long = 0L
) : Parcelable