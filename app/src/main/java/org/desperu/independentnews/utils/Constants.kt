package org.desperu.independentnews.utils

import org.desperu.independentnews.R
import org.desperu.independentnews.models.Source

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



// Section value
const val SEC_DECRYPTER = "Approfondir"
const val SEC_RESISTER = "Resister"
const val SEC_INVENTER = "Inventer"


// --- SOURCES ---

// Source name
const val BASTAMAG = "Bastamag"
const val REPORTERRE = "Reporterre"

// Source for database
val BASTAMAG_SOURCE = Source(
    name = BASTAMAG,
    url = BASTAMAG_BASE_URL,
    editorialUrl = BASTAMAG_EDITO_URL,
    imageId = R.drawable.logo_bastamag,
    logoId = R.drawable.logo_mini_bastamag
)
val REPORTERRE_SOURCE = Source(
    name = REPORTERRE,
    url = REPORTERRE_BASE_URL,
    editorialUrl = REPORTERRE_EDITO_URL,
    imageId = R.drawable.logo_reporterre,
    logoId = R.drawable.logo_mini_reporterre
)

// --- FOR UTILS ---

// To Convert Date
val monthNumber = arrayOf("janvier", "février", "mars", "avril", "mai", "juin",
    "juillet", "août", "septembre", "octobre", "novembre", "décembre")

// To parse author for Reporterre Html page
const val SOURCE = "Source"
const val PHOTO = "Photo"
const val PHOTOS = "Photos"


// --- FOR SERVICES ---

// For Receiver
const val UPDATE_DATA = 0
const val NOTIFICATION = 1


// --- FOR SHARED PREFERENCES ---


// File name
const val INDEPENDENT_NEWS_PREFS = "IndependentNewsPrefs"
// For keys
const val IS_FIRST_TIME = "isFirstTime"