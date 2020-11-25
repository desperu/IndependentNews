package org.desperu.independentnews.utils

import org.desperu.independentnews.models.Source

// --- FOR RETROFIT REQUEST ---

// Key for response type
const val XML = 0
const val HTML = 1

// Base url for retrofit request
const val BASTAMAG_BASE_URL = "https://www.bastamag.net/"
const val REPORTERRE_BASE_URL = "https://reporterre.net/"
// Editorial
const val BASTAMAG_EDITO_URL = "Qui-sommes-nous"
const val REPORTERRE_EDITO_URL = "Qui-sommes-nous-8"

// Section value
const val BASTA_SEC_DECRYPTER = "Approfondir"
const val BASTA_SEC_RESISTER = "Resister"
const val BASTA_SEC_INVENTER = "Inventer"

const val REPORT_SEC_DECRYPTER = "Enquete"
const val REPORT_SEC_RESISTER = "Reportage"
const val REPORT_SEC_INVENTER = "Alternatives"


// --- SOURCES ---

// Source name
const val BASTAMAG = "Basta !"
const val REPORTERRE = "Reporterre"

// Source for database
val BASTAMAG_SOURCE = Source(
    name = BASTAMAG,
    url = BASTAMAG_BASE_URL
)
val REPORTERRE_SOURCE = Source(
    name = REPORTERRE,
    url = REPORTERRE_BASE_URL
)


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
    "<div  id=\"appel-dons\"><p><strong>Puisque vous êtes ici…</strong></p>\n" +
            "<p>... nous avons une faveur à vous demander. Le désastre environnemental" +
            " s&#8217;accélère et s&#8217;aggrave, les citoyens sont de plus en plus concernés," +
            " et pourtant, le sujet reste secondaire dans le paysage médiatique." +
            " Ce bouleversement étant le problème fondamental de ce siècle, nous estimons" +
            " qu&#8217;il doit occuper une place centrale dans le traitement de l&#8217;actualité." +
            "<br class='autobr' />\n" +
            "Contrairement à de nombreux autres médias, nous avons fait des choix drastiques :</p>\n" +
            "<ul class=\"spip\"><li> celui de l&#8217;indépendance éditoriale, ne laissant aucune" +
            " prise aux influences de pouvoirs. Le journal n&#8217;appartient à aucun milliardaire" +
            " ou entreprise<small class=\"fine d-inline\"> </small>; <i>Reporterre</i> est géré par" +
            " une association à but non lucratif. Nous pensons que l&#8217;information ne doit pas" +
            " être un levier d&#8217;influence de l&#8217;opinion au profit d&#8217;intérêts" +
            " particuliers.</li><li> celui de l&#8217;ouverture : tous nos articles sont en libre" +
            " consultation, sans aucune restriction. Nous considérons que l&#8217;accès à" +
            " information est essentiel à la compréhension du monde et de ses enjeux, et ne doit" +
            " pas être dépendant des ressources financières de chacun.</li><li> celui de la" +
            " cohérence : <i>Reporterre</i> traite des bouleversements environnementaux," +
            " causés entre autres par la surconsommation. C&#8217;est pourquoi le journal" +
            " n&#8217;affiche strictement aucune publicité. De même, sans publicité, nous ne" +
            " nous soucions pas de l&#8217;opinion que pourrait avoir un annonceur de la teneur" +
            " des informations publiées.</li></ul>\n" +
            "<p>Pour ces raisons, <i>Reporterre</i> est un modèle rare dans le paysage médiatique." +
            " Le journal est composé d&#8217;une équipe de journalistes professionnels, qui" +
            " produisent quotidiennement des articles, enquêtes et reportages sur les enjeux" +
            " environnementaux et sociaux. Tout cela, nous le faisons car nous pensons qu’une" +
            " information fiable, indépendante et transparente sur ces enjeux est une partie" +
            " de la solution.</p>\n" +
            "<p>Vous comprenez donc pourquoi nous sollicitons votre soutien. Des dizaines de" +
            " milliers de personnes viennent chaque jour s&#8217;informer sur <i>Reporterre</i>," +
            " et de plus en plus de lecteurs comme vous soutiennent le journal, mais nos revenus" +
            " ne sont toutefois pas assurés. Si toutes les personnes qui lisent et apprécient nos" +
            " articles contribuent financièrement, le journal sera renforcé. <strong>Même pour 1 €," +
            " vous pouvez soutenir <i>Reporterre</i> &mdash; et cela ne prend qu’une minute." +
            " Merci.</strong></p>\n" +
            "<center>\n" +
            "<A HREF=\" https://reporterre.net/spip.php?page=don\" class=bouton_petitvert >Soutenir Reporterre</A>\n" +
            "</center></div>"