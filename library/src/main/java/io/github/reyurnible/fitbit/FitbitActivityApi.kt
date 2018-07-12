package io.github.reyurnible.fitbit

import retrofit2.Call
import retrofit2.http.GET

interface FitbitActivityApi {
    @GET("/1/user/-/activities/recent.json")
    fun getRecentActivities(): Call<List<FitbitActivity>>

    @GET("/1/user/-/activities/frequent.json")
    fun getFrequentActivities(): Call<List<FitbitActivity>>
}
