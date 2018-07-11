package io.github.reyurnible.fitbit

/**
 * Fitbit用のPreference
 */
interface FitbitPreference : Preference {
    var token: FitbitAuthToken?
}
