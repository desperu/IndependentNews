package org.desperu.independentnews.utils

// --- FOR MAIN ---

// Fragment identifier
const val NO_FRAG = -1
const val FRAG_TOP_STORY = 0
const val FRAG_CATEGORY = 1
const val FRAG_ALL_ARTICLES = 2
const val FRAG_SANTE = 3
const val FRAG_SOCIAL = 4
const val FRAG_CLIMAT = 5

// Categories View Pager
const val numberOfPage = 3
const val CAT_SANTE = "santé" // TODO use index???
const val CAT_SOCIAL = "social"
const val CAT_CLIMAT = "climat"


// --- FOR RETROFIT REQUEST ---

// Key for response type
const val XML = 0
const val HTML = 1

// Base url for retrofit request
const val BASTAMAG_BASE_URL = "https://www.bastamag.net/"
const val REPORTERRE_BASE_URL = "https://reporterre.net/"
// Editorial
const val BASTAMAG_EDITO_URL = "Site-d-informations-independant-sur-les-enjeux-sociaux-et-environnementaux"
const val REPORTERRE_EDITO_URL = "Qui-sommes-nous-8"

// Source value
const val BASTAMAG = "Bastamag"
const val REPORTERRE = "Reporterre"

// Section value
const val SEC_DECRYPTER = "Approfondir"
const val SEC_RESISTER = "Resister"
const val SEC_INVENTER = "Inventer"


// --- FOR UTILS ---

// To Convert Date
val monthNumber = arrayOf("Janvier", "Février", "Mars", "Avril", "Mai", "Juin",
    "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre")

// To parse author for Reporterre Html page
const val SOURCE = "Source"
const val PHOTO = "Photo"
const val PHOTOS = "Photos"