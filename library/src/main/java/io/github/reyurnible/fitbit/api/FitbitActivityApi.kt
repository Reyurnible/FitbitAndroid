package io.github.reyurnible.fitbit.api

import io.github.reyurnible.fitbit.FitbitConstants
import io.github.reyurnible.fitbit.entity.FitbitActivity
import io.github.reyurnible.fitbit.entity.FitbitDateActivity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FitbitActivityApi {
    private object Constants {
        const val API_VERSION = 1
    }

    @GET("/${Constants.API_VERSION}/user/${FitbitConstants.CURRENT_USER_ID_PARAM}/activities/date/{date}.json")
    fun getDateActivities(@Path("date") date: String): Call<FitbitDateActivity>

    @GET("/${Constants.API_VERSION}/user/${FitbitConstants.CURRENT_USER_ID_PARAM}/activities/recent.json")
    fun getRecentActivities(): Call<List<FitbitActivity>>

    @GET("/${Constants.API_VERSION}/user/${FitbitConstants.CURRENT_USER_ID_PARAM}/activities/frequent.json")
    fun getFrequentActivities(): Call<List<FitbitActivity>>
}
