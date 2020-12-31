package org.desperu.independentnews.models.database

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import kotlinx.android.parcel.Parcelize

/**
 * Class witch provides a model for an article with data.
 *
 * @property article    the article.
 * @property css        the css style of the article.
 * @property source     the source of the article.
 *
 * @constructor Sets all properties of the article with data.
 *
 * @param article       the article to set.
 * @param css           the css style of the article to set.
 * @param source        the source of the article to set.
 */
@Parcelize
data class ArticleWithData(
    @Embedded var article: Article = Article(),
    @Relation(
        entity = Css::class,
        parentColumn = "id",
        entityColumn = "articleId"
    )
    val css: Css = Css(),
    @Relation(
        entity = Source::class,
        parentColumn = "sourceId",
        entityColumn = "id"
    )
    val source: Source = Source()
): Parcelable