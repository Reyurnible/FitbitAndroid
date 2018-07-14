package io.github.reyurnible.fitbit.api

import io.github.reyurnible.fitbit.entity.FitbitUser
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface FitbitUserApi {
    @GET("/1/user/-/profile.json")
    fun getMe(): Call<FitbitUser>

    @GET("/1/user/{userId}/profile.json")
    fun getUser(@Path("userId") userId: String): Call<FitbitUser>
}
