package org.desperu.independentnews.utils

import org.desperu.independentnews.models.database.Source
import org.desperu.independentnews.ui.showArticle.webClient.JS_INTERFACE_NAME

// --- FOR RETROFIT REQUEST ---

// Key for response type
const val XML = 0
const val HTML = 1

// Base url for retrofit request
const val BASTAMAG_BASE_URL = "https://www.bastamag.net/"
const val REPORTERRE_BASE_URL = "https://reporterre.net/"
const val MULTINATIONALES_BASE_URL = "https://multinationales.org/"
// Editorial
const val BASTAMAG_EDITO_URL = "Qui-sommes-nous"
const val REPORTERRE_EDITO_URL = "quisommesnous"
const val MULTINATIONALES_EDITO_URL = "A-propos"

// Section value
const val BASTA_SEC_DECRYPTER = "Approfondir"
const val BASTA_SEC_RESISTER = "Resister"
const val BASTA_SEC_INVENTER = "Inventer"

const val REPORT_SEC_DECRYPTER = "Enquete"
const val REPORT_SEC_RESISTER = "Reportage"
const val REPORT_SEC_INVENTER = "Alternatives"

const val MULTINATIONALES_SEC_ENQUETE = "Enquetes"


// --- SOURCES ---

// Source name
const val BASTAMAG = "Basta !"
const val REPORTERRE = "Reporterre"
const val MULTINATIONALES = "Observatoire des Multinationales"

// Source data type (for snackbar message)
const val RSS = " (RSS)"
const val CATEGORY = " (Categories)"

// Source for database
val SOURCE_LIST: List<Source>
    get() = listOf(BASTAMAG_SOURCE, REPORTERRE_SOURCE, MULTINATIONALES_SOURCE)
val BASTAMAG_SOURCE = Source(
    name = BASTAMAG,
    url = BASTAMAG_BASE_URL,
    isEnabled = true
)
val REPORTERRE_SOURCE = Source(
    name = REPORTERRE,
    url = REPORTERRE_BASE_URL,
    isEnabled = true
)
val MULTINATIONALES_SOURCE = Source(
    name = MULTINATIONALES,
    url = MULTINATIONALES_BASE_URL,
    isEnabled = true
)


// --- FOR WEB VIEW ---

// For text zoom
// Alter media (Bastamag and Multinationales)
const val ALTER_MEDIA_TEXT_ZOOM = 30


// --- PARSE HTML ---

// Source button link (to correct)
const val BASTA_EDITO = "Ligne éditoriale"

// To parse author for Reporterre Html page
const val SOURCE = "Source"
const val PHOTO = "Photo"
const val PHOTOS = "Photos"

// Donate call for Reporterre
val NO_DONATE_CALL = listOf("Brèves", "Hors les murs", "On en parle")
const val DONATE_CALL =
    "<div class=\"encart_gris\">\n" +
            "<p>C&#8217;est maintenant que tout se joue…</p>\n" +
            "<p>La communauté scientifique ne cesse d&#8217;alerter sur le désastre environnemental" +
            " qui s&#8217;accélère et s&#8217;aggrave, la population est de plus en plus préoccupée," +
            " et pourtant, le sujet reste secondaire dans le paysage médiatique. Ce bouleversement" +
            " étant le problème fondamental de ce siècle, nous estimons qu&#8217;il doit occuper" +
            " une place centrale dans le traitement de l&#8217;actualité.<br class='autobr' />\n" +
            "Contrairement à de nombreux autres médias, nous avons fait des choix drastiques :</p>\n" +
            "<ul class=\"spip\"><li> celui de l&#8217;indépendance éditoriale, ne laissant aucune" +
            " prise aux influences de pouvoirs. Le journal n&#8217;appartient pas à un milliardaire" +
            " ou à une entreprise<small class=\"fine d-inline\"> </small>; <i>Reporterre</i>" +
            " est géré par une association d&#8217;intérêt général, à but non lucratif. Nous" +
            " pensons qu&#8217;un média doit informer, et non être un outil d&#8217;influence de" +
            " l&#8217;opinion au profit d&#8217;intérêts particuliers.</li><li> celui de" +
            " l&#8217;ouverture : tous nos articles sont en libre accès, sans aucune restriction." +
            " Nous considérons que s&#8217;informer est un droit essentiel, nécessaire à la" +
            " compréhension du monde et de ses enjeux. Ce droit ne doit pas être conditionné par" +
            " les ressources financières de chacun.</li><li> celui de la cohérence : " +
            "<i>Reporterre</i> traite des bouleversements environnementaux, causés entre autres" +
            " par la surconsommation, elle-même encouragée par la publicité. Le journal" +
            " n&#8217;affiche donc strictement aucune publicité. Cela garantit l&#8217;absence" +
            " de lien financier avec des entreprises, et renforce d&#8217;autant plus" +
            " l&#8217;indépendance de la rédaction.</li></ul>\n" +
            "<p>En résumé, <i>Reporterre</i> est un exemple rare dans le paysage médiatique : " +
            "totalement indépendant, à but non lucratif, en accès libre, et sans publicité. " +
            "<br class='autobr' />\n" +
            "Le journal emploie une équipe de journalistes professionnels, qui produisent chaque" +
            " jour des articles, enquêtes et reportages sur les enjeux environnementaux et" +
            " sociaux. Nous faisons cela car nous pensons que la publication d&#8217;informations" +
            " fiables, transparentes et accessibles à tous sur ces questions est une partie" +
            " de la solution.</p>\n" +
            "<p>Vous comprenez donc pourquoi nous sollicitons votre soutien. Des dizaines de" +
            " milliers de personnes viennent chaque jour s&#8217;informer sur <i>Reporterre</i>, " +
            "et de plus en plus de lecteurs comme vous soutiennent le journal. Les dons de nos" +
            " lecteurs représentent plus de 98% de nos ressources. Si toutes les personnes qui" +
            " lisent et apprécient nos articles contribuent financièrement, le journal sera " +
            "renforcé. <strong>Même pour 1 €, vous pouvez soutenir <i>Reporterre</i> &mdash; " +
            "et cela ne prend qu’une minute. Merci.</strong></p>\n" +
            "<p><A HREF=\"/dons\" class=\"bouton_petitvert bouton_noir\">Soutenir Reporterre</A></p>\n" +
            "</div>"

// Note Redirect Script
// To use for note redirect, need to be set in the original html code
// to work as JavascriptInterface
const val NOTE_REDIRECT =
    "function scrollToElement(id) {\n" +
            "    var elem = document.getElementById(id);\n" +
            "    var y = 0;\n" +
            "\n" +
            "    while (elem != null) {\n" +
            "        y += elem.offsetTop;\n" +
            "        elem = elem.offsetParent;\n" +
            "    }\n" +
            "\n" +
            "    $JS_INTERFACE_NAME.webScrollTo(y);\n" + // Call Javascript interface function
            "}"

// Page listener used to received callback event from web view,
// when the page is loaded and show.
const val PAGE_LISTENER =
    "function onPageShow() {\n" +
            "    $JS_INTERFACE_NAME.onPageShow();\n" + // Call Javascript interface function
            "}"

// --- Parse Css Style ---

const val BASTA_ADD_CSS = // Used for Multinationales too
    "div[itemprop=\"description\"]{font-weight:bold}" + // Description/chapo bold
            "div.notes{font-size:1em;color:black}" + // Notes text style correction
            ".notes > div p{margin-bottom:10px;font-size:normal;line-height:normal;font-weight:normal}" +
            ".notes > h2{margin-bottom:10px;font-weight:bold}"
const val REPORTERRE_ADD_CSS = "div.chapo{font-weight:bold}"
const val MULTI_ADD_CSS = "$BASTA_ADD_CSS .content{text-align:left}" // Replacement below seems to bug
const val MULTI_ORIG_CSS_BODY = "body{margin:0;padding:0;background:#FFF;text-align:center}"
const val MULTI_NEW_CSS_BODY = "body{margin:0;padding:0;background:#FFF;text-align:left}" // Align text left


// --- HTML PAGE TO PARSE ---

internal val WHITE_LIST = listOf(

    // Basta !

    // Reporterre
    "https://reporterre.net/quisommesnous",

    // Multinationales
    "https://multinationales.org/A-propos"
)

internal val BLACK_LIST = listOf(

    // Basta !

    // Reporterre
    "https://reporterre.net/La-vie-de-Reporterre-10",
    "https://reporterre.net/Les-femmes-et-les-hommes-de-Reporterre-72",
    "https://reporterre.net/Hors-les-murs",
    "https://reporterre.net/Pres-de-chez-vous",
    "https://reporterre.net/Tribune-15",
    "https://reporterre.net/Soutenir",
    "https://reporterre.net/Culture-et-idees",
    "https://reporterre.net/Hors-les-murs",
    "https://reporterre.net/rubrique-de-plus",
    "https://reporterre.net/Une-minute-Une-question-21",
    "https://reporterre.net/Les-femmes-et-les-hommes-de-Reporterre-72"

    // Multinationales
)