package io.github.reyurnible.fitbit.api

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.squareup.moshi.JsonAdapter
import io.github.reyurnible.fitbit.BuildConfig
import io.github.reyurnible.fitbit.FitbitConstants
import io.github.reyurnible.fitbit.auth.FitbitAuthManager
import io.github.reyurnible.fitbit.entity.FitbitActivity
import io.github.reyurnible.fitbit.entity.FitbitDateActivity
import io.github.reyurnible.fitbit.entity.FitbitTimeSeriesActivity
import io.github.reyurnible.fitbit.entity.FitbitUser
import io.github.reyurnible.fitbit.util.MoshiCreator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class FitbitClient(
    private val authManager: FitbitAuthManager,
    private val locale: FitbitLocale
) {
    companion object {
        const val ExpiredTokenResponseCode = 401
    }

    private val client: OkHttpClient =
        OkHttpClient.Builder()
            .apply {
                if (BuildConfig.DEBUG) {
                    addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                    addNetworkInterceptor(StethoInterceptor())
                }
            }
            .addInterceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .header("Accept-Locale", locale.param)
                    .header("Authorization", "Bearer ${authManager.currentToken?.accessToken}")
                    .build()
                chain.proceed(request)
            }
            .build()
    private val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(FitbitConstants.API_ENDPOINT)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(MoshiCreator.create()))
            .build()
    private val errorResponseAdapter: JsonAdapter<FitbitErrorResponse> =
        MoshiCreator.create().adapter(FitbitErrorResponse::class.java)

    val userApi: FitbitUserApi
        get() = retrofit.create(FitbitUserApi::class.java)
    val activityApi: FitbitActivityApi
        get() = retrofit.create(FitbitActivityApi::class.java)
    val heartRateApi: FitbitHeartRateApi
        get() = retrofit.create(FitbitHeartRateApi::class.java)
    val sleepApi: FitbitSleepApi
        get() = retrofit.create(FitbitSleepApi::class.java)

    // User
    fun getMe(callback: FitbitApiCallback<FitbitUser>) =
        requestOnErrorRefreshToken({ userApi.getMe() }, callback)

    fun getUser(userId: String, callback: FitbitApiCallback<FitbitUser>) =
        requestOnErrorRefreshToken({ userApi.getUser(userId) }, callback)

    // Activities
    fun getDateActivities(date: String, callback: FitbitApiCallback<FitbitDateActivity>) =
        requestOnErrorRefreshToken({ activityApi.getDateActivities(date) }, callback)

    fun getRecentActivities(callback: FitbitApiCallback<List<FitbitActivity>>) =
        requestOnErrorRefreshToken({ activityApi.getRecentActivities() }, callback)

    fun getFrequentActivities(callback: FitbitApiCallback<List<FitbitActivity>>) =
        requestOnErrorRefreshToken({ activityApi.getFrequentActivities() }, callback)

    // HeartRate
    fun getHeartRateActivities(date: String, period: String, callback: FitbitApiCallback<FitbitTimeSeriesActivity.HeartRate>) =
        requestOnErrorRefreshToken({ heartRateApi.getHeartRateActivities(date, period) }, callback)

    fun getHeartRateActivitiesByDateRange(baseDate: String, endDate: String, callback: FitbitApiCallback<FitbitTimeSeriesActivity.HeartRate>) =
        requestOnErrorRefreshToken({ heartRateApi.getHeartRateActivitiesByDateRange(baseDate, endDate) }, callback)

    // Sleep
    fun getSleepActivities(date: String, callback: FitbitApiCallback<FitbitTimeSeriesActivity.Sleep>) =
        requestOnErrorRefreshToken({ sleepApi.getSleepActivities(date) }, callback)

    fun getSleepActivitiesByDateRange(startDate: String, endDate: String, callback: FitbitApiCallback<FitbitTimeSeriesActivity.Sleep>) =
        requestOnErrorRefreshToken({ sleepApi.getSleepActivitiesByDateRange(startDate, endDate) }, callback)

    private fun <T> requestOnErrorRefreshToken(
        requestCreator: () -> Call<T>,
        callback: FitbitApiCallback<T>,
        isRefreshable: Boolean = true
    ) {
        val call = requestCreator.invoke()
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>?, response: Response<T>?) {
                response?.body()?.let { callback.onResponse(it) }
            }

            override fun onFailure(call: Call<T>?, _error: Throwable?) {
                val error = (_error as? HttpException) ?: return
                val code = error.code()
                if (isRefreshable && code == ExpiredTokenResponseCode) {
                    authManager.refreshToken(object : FitbitAuthManager.RefreshTokenCallback {
                        override fun onRefreshed() {
                            // Not call refresh token twice request
                            requestOnErrorRefreshToken(requestCreator, callback, isRefreshable = false)
                        }

                        override fun onError(error: Throwable) {
                            (error as? HttpException)?.let {
                                callback.onError(it.code(), parseFitbitErrorResponse(it)?.errors)
                            }
                        }
                    })
                } else {
                    callback.onError(code, parseFitbitErrorResponse(error)?.errors)
                }
            }
        })
    }

    private fun parseFitbitErrorResponse(error: HttpException): FitbitErrorResponse? =
        error.response().errorBody()?.string()?.let {
            errorResponseAdapter.fromJson(it)
        }

    interface FitbitApiCallback<T> {
        fun onResponse(response: T?)
        fun onError(statusCode: Int, errors: List<FitbitErrorResponse.FitbitError>?)
    }

}
