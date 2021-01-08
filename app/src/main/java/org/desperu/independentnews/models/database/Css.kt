package org.desperu.independentnews.models.database

import android.content.ContentValues
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import org.desperu.independentnews.utils.CSS_ARTICLE_ID
import org.desperu.independentnews.utils.CSS_CONTENT
import org.desperu.independentnews.utils.CSS_ID
import org.desperu.independentnews.utils.CSS_URL

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
    onDelete = ForeignKey.CASCADE)],
    indices = [Index(name = "css_articleId_index", value = ["articleId"])])
data class Css(
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,
    var articleId: Long = 0L,
    var url: String = "",
    var content: String = ""
): Parcelable {

    /**
     * Get css data from content values.
     *
     * @param values the content value to get data from.
     *
     * @return the Css object created from content values.
     */
    fun fromContentValues(values: ContentValues?): Css {
        val css = Css()

        if (values != null) {
            if (values.containsKey(CSS_ID)) css.id = values.getAsLong(CSS_ID)
            if (values.containsKey(CSS_ARTICLE_ID)) css.articleId = values.getAsLong(CSS_ARTICLE_ID)
            if (values.containsKey(CSS_URL)) css.url = values.getAsString(CSS_URL)
            if (values.containsKey(CSS_CONTENT)) css.content = values.getAsString(CSS_CONTENT)
        }

        return css
    }

    /**
     * Convert css object to content values.
     *
     * @param css the css object to convert.
     *
     * @return the content values of the css object.
     */
    fun toContentValues(css: Css?): ContentValues {
        val contentValues = ContentValues()

        if (css != null) {
            contentValues.put(CSS_ID, css.id)
            contentValues.put(CSS_ARTICLE_ID, css.articleId)
            contentValues.put(CSS_URL, css.url)
            contentValues.put(CSS_CONTENT, css.content)
        }

        return contentValues
    }
}