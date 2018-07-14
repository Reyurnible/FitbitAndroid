package io.github.reyurnible.fitbit

import android.app.Activity
import android.app.Fragment
import android.content.Context
import android.content.Intent
import io.github.reyurnible.fitbit.auth.FitbitAuthManager
import io.github.reyurnible.fitbit.auth.FitbitScope

class Fitbit(
    context: Context,
    clientId: String,
    clientSecret: String,
    redirectUrl: String,
    locale: FitbitLocale = FitbitLocale.UnitedStates
) {
    companion object {
        val instance: Fitbit
            get() = _instance ?: throw IllegalStateException("Fitbit client must be initialized")
        private var _instance: Fitbit? = null

        @JvmOverloads
        fun initialize(context: Context, clientId: String, clientSecret: String, redirectUrl: String, locale: FitbitLocale = FitbitLocale.UnitedStates) {
            _instance = Fitbit(context, clientId, clientSecret, redirectUrl, locale)
        }

        fun release() {
            _instance?.release()
            _instance = null
        }
    }

    val authManager: FitbitAuthManager = FitbitAuthManager(context, clientId, clientSecret, redirectUrl, locale)
    val client: FitbitClient = FitbitClient(context, authManager, locale)

    /**
     * Check Login
     * @return User login status.
     */
    fun isLogin(): Boolean =
        authManager.currentToken != null

    /**
     * Request login
     *
     * - [FitbitScope]
     * - [FitbitAuthManager.FitbitLoginCallback]
     *
     * @args scopes FitbitScope array
     */
    fun login(activity: Activity, scopes: Array<FitbitScope>, callback: FitbitAuthManager.FitbitLoginCallback?) {
        this.authManager.callback = callback
        activity.startActivity(Intent(Intent.ACTION_VIEW, authManager.createAuthorizationUri(scopes)))
    }

    /**
     * Request login
     *
     * - [FitbitScope]
     * - [FitbitAuthManager.FitbitLoginCallback]
     *
     * @args scopes FitbitScope array
     */
    fun login(fragment: Fragment, scopes: Array<FitbitScope>, callback: FitbitAuthManager.FitbitLoginCallback?) {
        this.authManager.callback = callback
        fragment.activity.startActivity(Intent(Intent.ACTION_VIEW, authManager.createAuthorizationUri(scopes)))
    }

    /**
     * Request logout
     */
    fun logout() {
        authManager.requestLogout()
    }

    private fun release() {
        authManager.release()
    }
}
