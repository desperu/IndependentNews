package org.desperu.independentnews.utils

import org.desperu.independentnews.R

// --- FOR UI ---

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

// Filters View Pager
const val numberOfPage = 4

// Documentation url
const val DOCUMENTATION_URL = "https://github.com/desperu/IndependentNews/blob/master/Documentation/Documentation%20Fonctionnelle%20Independent%20News.pdf"

// Who owns what image list // TODO add divided images for better view
internal val WHO_OWNS_WHAT = arrayListOf(R.drawable.who_owns_what_2019, R.drawable.who_owns_what_2016)

// SnackBar Keys
const val SEARCH = 0
const val FIND = 1
const val FETCH = 2
const val ERROR = 3
const val END_FIND = 4
const val END_NOT_FIND = 5
const val END_ERROR = 6

// Alert Dialog Key
const val ABOUT = 0
const val CONNEXION = 1
const val CONNEXION_START = 10
const val FIRST_START_ERROR = 20


// FOR REQUEST CODE
const val RC_FIRST_START = 1000 // First start activity
const val RC_SOURCE = 2000 // Source activity
const val RC_PERMS = 10000 // For permissions


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
internal val monthNumber = arrayOf("janvier", "février", "mars", "avril", "mai", "juin",
    "juillet", "août", "septembre", "octobre", "novembre", "décembre")
internal val imageSuffix = listOf(".png", ".jpg", ".jpeg")

// For compare result
const val EQUALS = 0
const val NOT_EQUALS = 1


// --- FOR SERVICES ---

// For Receiver
const val UPDATE_DATA = 0
const val NOTIFICATION = 1


// --- FOR SHARED PREFERENCES ---

// File name
const val INDEPENDENT_NEWS_PREFS = "IndependentNewsPrefs"

// Shared preferences keys
// global use
const val IS_FIRST_TIME = "isFirstTime"
// settings
const val NOTIFICATION_ENABLED = "notificationEnabled"
const val NOTIFICATION_TIME = "notificationTime"
const val TEXT_SIZE = "textSize"
const val REFRESH_ARTICLE_LIST = "refreshArticleList"
const val REFRESH_TIME = "refreshTime"
const val REFRESH_ONLY_WIFI = "refreshOnlyWifi"
const val STORE_DELAY = "storeDelay"

// Settings default value
// global use
const val FIRST_TIME_DEFAULT = true
// settings
const val NOTIFICATION_DEFAULT = true
const val NOTIFICATION_TIME_DEFAULT = 12
const val TEXT_SIZE_DEFAULT = 100
const val REFRESH_ARTICLE_LIST_DEFAULT = true
const val REFRESH_TIME_DEFAULT = 11
const val REFRESH_ONLY_WIFI_DEFAULT = false
const val STORE_DELAY_DEFAULT = 6