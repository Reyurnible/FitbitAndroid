package io.github.reyurnible.fitbit

import com.squareup.moshi.Json

data class FitbitAuthToken(
    @Json(name = "access_token")
    val accessToken : String,
    @Json(name = "refresh_token")
    val refreshToken : String,
    @Json(name = "expires_in")
    val expiresIn : Int,
    @Json(name = "user_id")
    val userId: String
)
