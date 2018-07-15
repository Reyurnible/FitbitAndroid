package io.github.reyurnible.fitbit

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.squareup.moshi.JsonAdapter
import io.github.reyurnible.fitbit.api.FitbitActivityApi
import io.github.reyurnible.fitbit.api.FitbitErrorResponse
import io.github.reyurnible.fitbit.api.FitbitLocale
import io.github.reyurnible.fitbit.api.FitbitUserApi
import io.github.reyurnible.fitbit.auth.FitbitAuthManager
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

    private val activityApi: FitbitActivityApi
        get() = retrofit.create(FitbitActivityApi::class.java)
    private val userApi: FitbitUserApi
        get() = retrofit.create(FitbitUserApi::class.java)

    fun getMe(callback: FitbitApiCallback<FitbitUser>) =
        requestOnErrorRefreshToken({ userApi.getMe() }, callback)

    fun getUser(userId: String, callback: FitbitApiCallback<FitbitUser>) =
        requestOnErrorRefreshToken({ userApi.getUser(userId) }, callback)


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
