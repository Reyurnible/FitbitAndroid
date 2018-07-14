package io.github.reyurnible.fitbit.auth

import io.github.reyurnible.fitbit.util.Preference

/**
 * Fitbit用のPreference
 */
internal interface FitbitAuthPreference : Preference {
    var token: FitbitAuthToken?
}
