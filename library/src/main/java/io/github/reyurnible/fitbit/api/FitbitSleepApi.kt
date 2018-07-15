package io.github.reyurnible.fitbit.api

import io.github.reyurnible.fitbit.FitbitConstants
import io.github.reyurnible.fitbit.entity.FitbitTimeSeriesActivity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FitbitSleepApi {
    private object Constants {
        const val API_VERSION = 1.2
    }

    @GET("/${Constants.API_VERSION}/user/${FitbitConstants.CURRENT_USER_ID_PARAM}/sleep/date/{date}.json")
    fun getSleepActivities(@Path("date") date: String): Call<FitbitTimeSeriesActivity.Sleep>

    @GET("/${Constants.API_VERSION}/user/${FitbitConstants.CURRENT_USER_ID_PARAM}/sleep/date/{startDate}/{endDate}.json")
    fun getSleepActivitiesByDateRange(@Path("startDate") startDate: String, @Path("endDate") endDate: String): Call<FitbitTimeSeriesActivity.Sleep>
}
