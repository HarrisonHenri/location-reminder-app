package com.udacity.project4.base

import android.content.Intent
import androidx.navigation.NavDirections

/**
 * Sealed class used with the live data to navigate between the fragments
 */
sealed class NavigationCommand {
    /**
     * navigate to a direction
     */
    data class To(val directions: NavDirections) : NavigationCommand()

    /**
     * navigate back to the previous fragment
     */
    object Back : NavigationCommand()

    /**
     * navigate to a an external Intent
     */
    data class StartActivity(val intent: Intent, val requestCode: Int): NavigationCommand()

    /**
     * navigate back to a destination in the back stack
     */
    data class BackTo(val destinationId: Int) : NavigationCommand()
}