package com.udacity.project4.authentication

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.util.Log
import androidx.lifecycle.map
import com.firebase.ui.auth.AuthUI
import com.udacity.project4.base.BaseViewModel
import com.udacity.project4.base.NavigationCommand
import com.udacity.project4.utils.Constants.AuthenticationState
import com.udacity.project4.utils.Constants.SIGN_IN_REQUEST_CODE

class AuthenticationViewModel(app: Application) : BaseViewModel(app) {
    fun onLoginButtonClicked() {
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(), AuthUI.IdpConfig.GoogleBuilder().build()
        )
        val intent = AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setLockOrientation(true)
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .build()

        navigationCommand.value = NavigationCommand.StartActivity(
                intent,
                SIGN_IN_REQUEST_CODE
        )
    }

    fun onLoginResult(resultCode: Int) {
        if (resultCode == Activity.RESULT_CANCELED){
            navigationCommand.value = NavigationCommand.Back
            return
        }
    }
}