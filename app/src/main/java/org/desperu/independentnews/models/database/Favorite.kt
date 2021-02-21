package org.desperu.independentnews.models.database

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Class which provide a model for a Favorite Article.
 *
 * @property id             the unique identifier of the favorite.
 * @property articleId      the unique identifier of the article of this favorite.
 *
 * @constructor Instantiate a new Favorite.
 *
 * @param id                the unique identifier of the favorite to set.
 * @param articleId         the unique identifier of the article of this favorite to set.
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
    indices = [Index(name = "favorite_articleId_index", value = ["articleId"])]
)
data class Favorite(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var articleId: Long = 0L
) : Parcelable