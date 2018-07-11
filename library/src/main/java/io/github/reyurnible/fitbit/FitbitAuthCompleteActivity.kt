package io.github.reyurnible.fitbit

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class FitbitAuthCompleteActivity : Activity(), FitbitClient.HandlingCallback {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // No UI
        // Parse Fitbit Auth Callback
        FitbitClient.instance.handleRedirectUrl(intent?.data, this@FitbitAuthCompleteActivity)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        // Parse Fitbit Auth Callback
        FitbitClient.instance.handleRedirectUrl(intent?.data, this@FitbitAuthCompleteActivity)
    }

    override fun onComplete() {
        finish()
    }

}
