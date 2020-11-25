package org.desperu.independentnews.utils

import android.content.pm.ActivityInfo
import android.view.View

// --- FLAGS ---
// System Ui

// Show full system ui flag.
const val SYS_UI_VISIBLE = View.SYSTEM_UI_FLAG_VISIBLE


// Hide system ui flags.
const val SYS_UI_HIDE = (
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE // Should stabilize the system ui (not really test)
                or View.SYSTEM_UI_FLAG_FULLSCREEN // Hide status bar
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Layout draw under status bar
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Re hide status and/or navigation bar
                or View.SYSTEM_UI_FLAG_LOW_PROFILE // Alpha 0.5 the navigation bar
        )

// Hide full system ui flags.
const val SYS_UI_FULL_HIDE = (
        View.SYSTEM_UI_FLAG_LAYOUT_STABLE // Should stabilize the system ui (not really test)
                or View.SYSTEM_UI_FLAG_FULLSCREEN // Hide status bar
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Layout draw under status bar
//                    or View.SYSTEM_UI_FLAG_IMMERSIVE // Hide status and/or navigation once
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY // Re hide status and/or navigation bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // Hide navigation bar
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION // Layout draw under navigation bar
//                    or View.SYSTEM_UI_FLAG_LOW_PROFILE // Alpha 0.5 the navigation bar
        )


// Low nav and status bar flag.
const val LOW_NAV_AND_STATUS_BAR = (
        View.SYSTEM_UI_FLAG_LOW_PROFILE // Alpha 0.5 the navigation bar
                or View.SYSTEM_UI_FLAG_FULLSCREEN
        )

// System Orientation

// Orientation landscape
const val LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

// Orientation full user
const val FULL_USER = ActivityInfo.SCREEN_ORIENTATION_FULL_USER