package io.github.reyurnible.fitbit.api

import io.github.reyurnible.fitbit.FitbitConstants
import io.github.reyurnible.fitbit.entity.FitbitTimeSeriesActivity
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FitbitHeartRateApi {
    @GET("/${FitbitConstants.API_VERSION}/user/${FitbitConstants.CURRENT_USER_ID_PARAM}/activities/heart/date/{date}/{period}.json")
    fun getHeartRateActivities(@Path("date") date: String, @Path("period") period: String): Call<FitbitTimeSeriesActivity.HeartRate>

    @GET("/${FitbitConstants.API_VERSION}/user/${FitbitConstants.CURRENT_USER_ID_PARAM}/activities/heart/date/{baseDate}/{endDate}.json")
    fun getHeartRateActivitiesByDateRange(@Path("baseDate") baseDate: String, @Path("endDate") endDate: String): Call<FitbitTimeSeriesActivity.HeartRate>
}
