package io.github.reyurnible.fitbit.api

import io.github.reyurnible.fitbit.entity.FitbitActivity
import retrofit2.Call
import retrofit2.http.GET

interface FitbitActivityApi {
    @GET("/1/user/-/activities/recent.json")
    fun getRecentActivities(): Call<List<FitbitActivity>>

    @GET("/1/user/-/activities/frequent.json")
    fun getFrequentActivities(): Call<List<FitbitActivity>>
}
