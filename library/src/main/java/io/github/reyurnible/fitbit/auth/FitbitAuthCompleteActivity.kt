package io.github.reyurnible.fitbit.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import io.github.reyurnible.fitbit.Fitbit

class FitbitAuthCompleteActivity : Activity(), FitbitAuthManager.HandlingCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No UI
        // Parse Fitbit Auth Callback
        Fitbit.instance.authManager.handleRedirectUrl(intent?.data, this@FitbitAuthCompleteActivity)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Parse Fitbit Auth Callback
        Fitbit.instance.authManager.handleRedirectUrl(intent?.data, this@FitbitAuthCompleteActivity)
    }

    override fun onComplete() {
        finish()
    }

}
