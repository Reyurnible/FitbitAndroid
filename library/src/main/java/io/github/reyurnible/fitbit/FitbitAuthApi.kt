package io.github.reyurnible.fitbit

import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface FitbitAuthApi {

    @FormUrlEncoded
    @POST("/oauth2/token")
    fun createAccessToken(
        @Header("Authorization") authorization: String,
        @FieldMap params: Map<String, String>
    ): Call<FitbitAuthToken>

}
