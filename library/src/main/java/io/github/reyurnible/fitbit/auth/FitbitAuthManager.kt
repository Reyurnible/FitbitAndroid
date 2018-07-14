package io.github.reyurnible.fitbit.auth

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.facebook.stetho.okhttp3.StethoInterceptor
import io.github.reyurnible.fitbit.BuildConfig
import io.github.reyurnible.fitbit.FitbitConstants
import io.github.reyurnible.fitbit.FitbitLocale
import io.github.reyurnible.fitbit.api.FitbitAuthApi
import io.github.reyurnible.fitbit.util.MoshiCreator
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class FitbitAuthManager(
    context: Context,
    private val clientId: String,
    private val clientSecret: String,
    private val redirectUrl: String,
    private val locale: FitbitLocale
) {
    companion object {
        const val AUTHORIZE_URL = "https://www.fitbit.com/oauth2/authorize"
    }

    private object RequestGrantTypes {
        const val authorization = "authorization_code"
        const val refreshToken = "refresh_token"
    }
    private object ResponseTypes {
        const val code = "code"
        private const val token = "token"
    }

    // Callback
    var callback: FitbitLoginCallback? = null
    var expiresIn: Int? = null

    private val preference: FitbitAuthPreference = FitbitAuthPreferenceImpl(context)
    // Base64 setting to NO_WRAP (link: https://github.com/square/retrofit/issues/1153)
    private val basicAuthorization: String
        get() = "Basic ${Base64.encodeToString("$clientId:$clientSecret".toByteArray(), Base64.NO_WRAP)}"
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
                    .build()
                chain.proceed(request)
            }
            .build()
    private val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(FitbitConstants.API_HOST)
            .client(client)
            .addConverterFactory(MoshiConverterFactory.create(MoshiCreator.create()))
            .build()
    // Auth apis
    private val authApi: FitbitAuthApi
        get() = retrofit.create(FitbitAuthApi::class.java)
    private var accessTokenRequestCall: Call<FitbitAuthToken>? = null

    /**
     * Get current token
     */
    val currentToken: FitbitAuthToken?
        get() = preference.token

    /**
     * Request logout
     */
    fun requestLogout() {
        // Clear pref token
        preference.clear()
    }

    /**
     * Create authorization url
     * Please preset callback
     *
     * - [FitbitAuthManager.FitbitLoginCallback]
     *
     * @args scopes FitbitScope array
     * @return
     */
    fun createAuthorizationUri(scopes: Array<FitbitScope>, responseType: String = ResponseTypes.code): Uri =
        Uri.parse(AUTHORIZE_URL)
            .buildUpon()
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("response_type", responseType)
            .appendQueryParameter("scope", FitbitScope.generateUrlString(scopes))
            .appendQueryParameter("redirect_uri", redirectUrl)
            .build()

    /**
     * Handle Redirect Url
     * For create custom complete activity
     *
     * - [FitbitAuthManager.HandlingCallback]
     *
     * @args uri
     * @args handlingCallback
     */
    fun handleRedirectUrl(uri: Uri?, handlingCallback: HandlingCallback? = null) {
        clearCurrentRequest()
        uri ?: let {
            handlingCallback?.onComplete()
            callback?.onLoginErrored(IllegalArgumentException("Callback uri is null."))
            return
        }
        if (uri.queryParameterNames.contains(ResponseTypes.code)) {
            // Code Flow
            // Parsing uri format : "{redirect_url}?code=~~~~~#_=_
            val code = uri.getQueryParameter(ResponseTypes.code)
            accessTokenRequestCall = createTokenRequest(code, handlingCallback)
            accessTokenRequestCall?.enqueue(object : Callback<FitbitAuthToken> {
                override fun onResponse(call: Call<FitbitAuthToken>?, response: Response<FitbitAuthToken>?) {
                    val body = response?.body() ?: return
                    // Save AuthToken
                    preference.token = body
                    handlingCallback?.onComplete()
                    callback?.onLoginSuccessed(body)
                    accessTokenRequestCall = null
                }

                override fun onFailure(call: Call<FitbitAuthToken>?, _error: Throwable?) {
                    val error = _error ?: return
                    handlingCallback?.onComplete()
                    callback?.onLoginErrored(error)
                    accessTokenRequestCall = null
                }
            })
        }
    }

    /**
     * Release callback function
     */
    fun release() {
        callback = null
        if (accessTokenRequestCall?.isCanceled == false) {
            accessTokenRequestCall?.cancel()
        }
        accessTokenRequestCall = null
    }

    fun refreshToken(callback: RefreshTokenCallback) {
        clearCurrentRequest()
        accessTokenRequestCall = createRefreshTokenRequest()
        accessTokenRequestCall?.enqueue(object : Callback<FitbitAuthToken> {
            override fun onResponse(call: Call<FitbitAuthToken>?, response: Response<FitbitAuthToken>?) {
                val body = response?.body() ?: return
                // Save AuthToken
                preference.token = body
                callback.onRefreshed()
                accessTokenRequestCall = null
            }

            override fun onFailure(call: Call<FitbitAuthToken>?, _error: Throwable?) {
                val error = _error ?: return
                callback.onError(error)
                accessTokenRequestCall = null
            }
        })
    }

    fun createRefreshTokenRequest(): Call<FitbitAuthToken>? = run {
        val refreshToken = preference.token?.refreshToken ?: return@run null
        authApi.createAccessToken(
            basicAuthorization,
            mutableMapOf<String, Any>(
                Pair("grant_type", RequestGrantTypes.refreshToken),
                Pair("refresh_token", refreshToken)
            ).apply {
                expiresIn?.let {
                    put("expires_in", it)
                }
            })
    }

    private fun createTokenRequest(code: String, handlingCallback: HandlingCallback?): Call<FitbitAuthToken> =
        authApi.createAccessToken(
            basicAuthorization,
            mutableMapOf<String, Any>(
                Pair("client_id", clientId),
                Pair("grant_type", RequestGrantTypes.authorization),
                Pair("code", code),
                Pair("redirect_uri", redirectUrl)
            ).apply {
                expiresIn?.let {
                    put("expires_in", it)
                }
            })

    private fun clearCurrentRequest() {
        // Cancel already request
        if (accessTokenRequestCall?.isExecuted == true && accessTokenRequestCall?.isCanceled == false) {
            accessTokenRequestCall?.cancel()
        }
        accessTokenRequestCall = null
    }

    interface FitbitLoginCallback {
        fun onLoginSuccessed(token: FitbitAuthToken)
        fun onLoginErrored(error: Throwable)
    }

    interface HandlingCallback {
        fun onComplete()
    }

    interface RefreshTokenCallback {
        fun onRefreshed()
        fun onError(error: Throwable)
    }

}
