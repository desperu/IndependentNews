package org.desperu.independentnews.utils

// --- FOR MAIN ---

// Fragment identifier
const val NO_FRAG = -1
const val FRAG_TOP_STORY = 0
const val FRAG_CATEGORY = 1
const val FRAG_ALL_ARTICLES = 2
const val FRAG_DECRYPTER = 3
const val FRAG_RESISTER = 4
const val FRAG_INVENTER = 5


// --- FOR RETROFIT REQUEST ---

// Key for response type
const val XML = 0
const val HTML = 1

// Base url for retrofit request
const val BASTAMAG_BASE_URL = "https://www.bastamag.net/"

// Source value
const val BASTAMAG = "Bastamag"
const val REPORTERRE = "Reporterre"

// Category value
const val CAT_DECRYPTER = "Approfondir"
const val CAT_RESISTER = "Resister"
const val CAT_INVENTER = "Inventer"