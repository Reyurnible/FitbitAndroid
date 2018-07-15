package io.github.reyurnible.fitbit

import android.app.Application
import io.github.reyurnible.fitbit.api.FitbitLocale

class SampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Fitbit.initialize(this,
            clientId = "Replace Fitbit Client Id",
            clientSecret = "Replace Fitbit Client Secret",
            redirectUrl = "Replace Fitbit Redirect Url",
            locale = FitbitLocale.Japan
        )
    }

}
