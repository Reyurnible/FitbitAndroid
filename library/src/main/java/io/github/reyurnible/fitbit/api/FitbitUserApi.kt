package io.github.reyurnible.fitbit.api

import io.github.reyurnible.fitbit.FitbitConstants
import io.github.reyurnible.fitbit.entity.FitbitUser
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FitbitUserApi {
    @GET("/${FitbitConstants.API_VERSION}/user/${FitbitConstants.CURRENT_USER_ID_PARAM}/profile.json")
    fun getMe(): Call<FitbitUser>

    @GET("/${FitbitConstants.API_VERSION}/user/{userId}/profile.json")
    fun getUser(@Path("userId") userId: String): Call<FitbitUser>
}
