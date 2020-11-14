package org.desperu.independentnews.utils

import org.desperu.independentnews.R
import org.desperu.independentnews.models.Source

// --- FOR MAIN ---

// Fragment identifier
// Main
const val NO_FRAG = -1
const val FRAG_TOP_STORY = 0
const val FRAG_CATEGORY = 1
const val FRAG_ALL_ARTICLES = 2
const val FRAG_ECOLOGY = 3
const val FRAG_SOCIAL = 4
const val FRAG_ENERGY = 5
const val FRAG_HEALTH = 6
const val FRAG_TODAY_ARTICLES = 7
// Sources
const val FRAG_SOURCES_LIST = 100
const val FRAG_SOURCES_DETAIL = 101

// View Pager
const val numberOfPage = 4

// Documentation url
const val DOCUMENTATION_URL = "https://github.com/desperu/IndependentNews/blob/master/Documentation/Documentation%20Fonctionnelle%20Independent%20News.pdf"


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
    url = BASTAMAG_BASE_URL,
    imageId = R.drawable.logo_bastamag,
    logoId = R.drawable.logo_mini_bastamag,
    backgroundColorId = R.color.bastamag_background
)
val REPORTERRE_SOURCE = Source(
    name = REPORTERRE,
    url = REPORTERRE_BASE_URL,
    imageId = R.drawable.logo_reporterre,
    logoId = R.drawable.logo_mini_reporterre,
    backgroundColorId = R.color.reporterre_background
)

// Source button link (to correct)
const val BASTA_EDITO = "Ligne éditoriale"


// --- FOR FILTERS ---

// Key for filter page
const val SOURCES = 0
const val THEMES = 1
const val SECTIONS = 2
const val DATES = 3
const val CATEGORIES = 4 // only used to apply filters, not in UI.


// --- FOR SETTINGS ACTIVITY ---

// For alert dialog
const val NOTIF_TIME_DIALOG = 0
const val TEXT_SIZE_DIALOG = 1
const val REFRESH_TIME_DIALOG = 2
const val STORE_DELAY_DIALOG = 3
const val RESET_DIALOG = 4


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

// Shared preferences keys
const val IS_FIRST_TIME = "isFirstTime"
const val NOTIFICATION_ENABLED = "notificationEnabled"
const val NOTIFICATION_TIME = "notificationTime"
const val TEXT_SIZE = "textSize"
const val REFRESH_ARTICLE_LIST = "refreshArticleList"
const val REFRESH_TIME = "refreshTime"
const val REFRESH_ONLY_WIFI = "refreshOnlyWifi"
const val STORE_DELAY = "storeDelay"

// Settings default value
const val NOTIFICATION_DEFAULT = true
const val NOTIFICATION_TIME_DEFAULT = 12
const val TEXT_SIZE_DEFAULT = 100
const val REFRESH_ARTICLE_LIST_DEFAULT = true
const val REFRESH_TIME_DEFAULT = 11
const val REFRESH_ONLY_WIFI_DEFAULT = false
const val STORE_DELAY_DEFAULT = 6