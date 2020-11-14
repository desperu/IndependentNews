package org.desperu.independentnews.models.web.reporterre

import okhttp3.ResponseBody
import org.desperu.independentnews.base.html.BaseHtmlArticle
import org.desperu.independentnews.extension.parseHtml.*
import org.desperu.independentnews.extension.parseHtml.sources.correctRepoMediaUrl
import org.desperu.independentnews.extension.parseHtml.correctUrlLink
import org.desperu.independentnews.extension.parseHtml.getAuthor
import org.desperu.independentnews.extension.parseHtml.mToString
import org.desperu.independentnews.extension.parseHtml.toFullUrl
import org.desperu.independentnews.models.Article
import org.desperu.independentnews.utils.*
import org.desperu.independentnews.utils.Utils.literalDateToMillis
import org.jsoup.nodes.Document

/**
 * Class which provides a model to parse reporterre article html page.
 *
 * @property htmlPage the reporterre article html page.
 *
 * @constructor Instantiate a new ReporterreArticle.
 *
 * @param htmlPage the reporterre article html page to set.
 */
data class ReporterreArticle(private val htmlPage: ResponseBody): BaseHtmlArticle(htmlPage) {

    // FOR DATA
    override val sourceName = REPORTERRE

    // --- GETTERS ---

    override fun getTitle(): String? = findData(H1, null, null, null)?.text()

    override fun getSection(): String? =
        findData(A, CLASS, ARIANNE, 1)?.text()?.removeSuffix(" >")

    override fun getTheme(): String? = findData(A, CLASS, LIEN_THEME, null)?.text()

    override fun getAuthor() : String? {
        val article = findData(DIV, STYLE, TEXT_ALIGN, null)

        var author = findData(A, CLASS, LIEN_AUTEUR, null)?.text()

        if (author.isNullOrBlank())
            author = findData(HR, SIZE, ONE, null)?.ownText()

        if (author.isNullOrBlank())
            author = article?.select(UL).getAuthor()

        if (author.isNullOrBlank())
            author = article?.select(P).getAuthor()

        return author?.removePrefix(" ")
    }

    override fun getPublishedDate(): String? =
        findData(SPAN, CLASS, DATE_PUBLICATION, null)?.text()

    override fun getArticle(): String? =
        findData(DIV, CLASS, TEXTE, null)?.outerHtml().updateArticleBody()

    override fun getDescription(): String? = findData(DIV, CLASS, CHAPO, null)?.text()

    override fun getImage(): List<String?> {
        val element = findData(IMG, CLASS, REPO_IMAGE_CLASS, null)
        return listOf(
            element?.attr(SRC).toFullUrl(REPORTERRE_BASE_URL),
            element?.attr(WIDTH),
            element?.attr(HEIGHT)
        )
    }

    override fun getCssUrl(): String? =
        findData(LINK, REL, STYLE_SHEET, null)?.attr(HREF).toFullUrl(REPORTERRE_BASE_URL)

    // -----------------
    // CONVERT
    // -----------------

    /**
     * Convert ReporterreArticle to Article.
     * @param article the article to set data.
     * @return article with all data set.
     */
    internal fun toArticle(article: Article): Article {
        val author = getAuthor()
        val publishedDate = getPublishedDate()?.let { literalDateToMillis(it) }
        val description = getDescription()
        article.apply {
            sourceName = this@ReporterreArticle.sourceName
            title = getTitle().mToString()
            section = getSection().mToString()
            theme = getTheme().mToString()
            if (!author.isNullOrBlank()) this.author = author
            if (this.publishedDate == 0L && publishedDate != null) this.publishedDate = publishedDate
            this.article = getArticle().mToString()
            if (!description.isNullOrBlank()) this.description = description
            imageUrl = getImage()[0].mToString()
            cssUrl = getCssUrl().mToString()
        }

        return article
    }

    // -----------------
    // UTILS
    // -----------------

    /**
     * Update article body to add, correct and remove needed data.
     *
     * @return the article body updated.
     */
    private fun String?.updateArticleBody(): String? =
        this?.let {
            it.toDocument()
                .addDescription()
                .addDonateCall()
                .correctUrlLink(REPORTERRE_BASE_URL)
                .correctRepoMediaUrl()
                .removeBottomLogo()
                .mToString()
                .escapeHashtag()
                .forceHttps()
        }

    /**
     * Add description with the article body.
     *
     * @return the full article, description and article body.
     */
    private fun Document?.addDescription(): Document? =
        this?.let {
            val description = if (!getDescription().isNullOrBlank())
                findData(DIV, CLASS, CHAPO, null)?.outerHtml()
            else
                ""

            select(BODY)?.before(description)
            this
        }

    /**
     * Add donation call to the article body.
     *
     * @return the article with donation call.
     */
    private fun Document?.addDonateCall(): Document? =
        this?.let {
            val appelDon = "<div  id=\"appel-dons\"><p><strong>Puisque vous êtes ici…</strong></p>\n" +
                    "<p>... nous avons une faveur à vous demander. Le désastre environnemental s&#8217;accélère et s&#8217;aggrave, les citoyens sont de plus en plus concernés, et pourtant, le sujet reste secondaire dans le paysage médiatique. Ce bouleversement étant le problème fondamental de ce siècle, nous estimons qu&#8217;il doit occuper une place centrale dans le traitement de l&#8217;actualité.<br class='autobr' />\n" +
                    "Contrairement à de nombreux autres médias, nous avons fait des choix drastiques :</p>\n" +
                    "<ul class=\"spip\"><li> celui de l&#8217;indépendance éditoriale, ne laissant aucune prise aux influences de pouvoirs. Le journal n&#8217;appartient à aucun milliardaire ou entreprise<small class=\"fine d-inline\"> </small>; <i>Reporterre</i> est géré par une association à but non lucratif. Nous pensons que l&#8217;information ne doit pas être un levier d&#8217;influence de l&#8217;opinion au profit d&#8217;intérêts particuliers.</li><li> celui de l&#8217;ouverture : tous nos articles sont en libre consultation, sans aucune restriction. Nous considérons que l&#8217;accès à information est essentiel à la compréhension du monde et de ses enjeux, et ne doit pas être dépendant des ressources financières de chacun.</li><li> celui de la cohérence : <i>Reporterre</i> traite des bouleversements environnementaux, causés entre autres par la surconsommation. C&#8217;est pourquoi le journal n&#8217;affiche strictement aucune publicité. De même, sans publicité, nous ne nous soucions pas de l&#8217;opinion que pourrait avoir un annonceur de la teneur des informations publiées.</li></ul>\n" +
                    "<p>Pour ces raisons, <i>Reporterre</i> est un modèle rare dans le paysage médiatique. Le journal est composé d&#8217;une équipe de journalistes professionnels, qui produisent quotidiennement des articles, enquêtes et reportages sur les enjeux environnementaux et sociaux. Tout cela, nous le faisons car nous pensons qu’une information fiable, indépendante et transparente sur ces enjeux est une partie de la solution.</p>\n" +
                    "<p>Vous comprenez donc pourquoi nous sollicitons votre soutien. Des dizaines de milliers de personnes viennent chaque jour s&#8217;informer sur <i>Reporterre</i>, et de plus en plus de lecteurs comme vous soutiennent le journal, mais nos revenus ne sont toutefois pas assurés. Si toutes les personnes qui lisent et apprécient nos articles contribuent financièrement, le journal sera renforcé. <strong>Même pour 1 €, vous pouvez soutenir <i>Reporterre</i> &mdash; et cela ne prend qu’une minute. Merci.</strong></p>\n" +
                    "<center>\n" +
                    "<A HREF=\" https://reporterre.net/spip.php?page=don\" class=bouton_petitvert >Soutenir Reporterre</A>\n" +
                    "</center></div>"
            // TODO not in brèves et (pas info) et hors les murs
            if (!listOf("Brèves", "Hors les murs").contains(getSection()))
                select(DIV).getMatchAttr(ID, APPEL_DON).getIndex(0)?.append(appelDon)
            this
        }

    /**
     * Remove bottom logo.
     *
     * @return the article without bottom logo.
     */
    private fun Document?.removeBottomLogo(): Document? =
        this?.let {
            select(DIV).getMatchAttr(CLASS, NO_PRINT).remove()
            this
        }
}