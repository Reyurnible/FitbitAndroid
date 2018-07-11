package io.github.reyurnible.fitbit

import android.content.Context

class FitbitPreferenceImpl(
    override val context: Context
) : FitbitPreference {
    private object Keys {
        const val accessToken = "access_token"
        const val refreshToken = "refresh_token"
        const val expiresIn = "expires_in"
        const val userId = "user_id"
    }

    override val preferenceName: String
        get() = "fitbit_preference"

    override var token: FitbitAuthToken?
        get() = run {
            FitbitAuthToken(
                accessToken = readStringOrNull(Keys.accessToken) ?: return null,
                refreshToken = readStringOrNull(Keys.refreshToken) ?: return null,
                expiresIn = readIntOrNull(Keys.expiresIn) ?: return null,
                userId = readStringOrNull(Keys.userId) ?: return null
            )
        }
        set(value) {
            write {
                value?.run {
                    it.putString(Keys.accessToken, value.accessToken)
                    it.putString(Keys.refreshToken, value.refreshToken)
                    it.putInt(Keys.expiresIn, value.expiresIn)
                    it.putString(Keys.userId, value.userId)
                } ?: it.clear()
            }
        }
}
