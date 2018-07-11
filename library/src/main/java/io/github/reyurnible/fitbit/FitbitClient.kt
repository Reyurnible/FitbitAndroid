package io.github.reyurnible.fitbit

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Base64
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

class FitbitClient(
    context: Context,
    private val clientId: String,
    private val clientSecret: String,
    private val redirectUrl: String
) {
    companion object {
        const val API_HOST = "https://api.fitbit.com"
        const val AUTHORIZE_URL = "https://www.fitbit.com/oauth2/authorize"

        val instance: FitbitClient
            get() = _instance ?: throw IllegalStateException("Fitbit client must be initialized")
        private var _instance: FitbitClient? = null

        fun initialize(context: Context, clientId: String, clientSecret: String, redirectUrl: String) {
            _instance = FitbitClient(context, clientId, clientSecret, redirectUrl)
        }

        fun release() {
            _instance?.release()
            _instance = null
        }
    }

    object ResponseTypes {
        const val code = "code"
        const val token = "token"
    }

    private val fitbitPreference: FitbitPreference = FitbitPreferenceImpl(context)
    private val client: OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .addNetworkInterceptor(StethoInterceptor())
            .build()
    private val retrofit: Retrofit =
        Retrofit.Builder()
            .baseUrl(API_HOST)
            .client(client)
            .addConverterFactory(
                MoshiConverterFactory.create(Moshi.Builder().add(KotlinJsonAdapterFactory()).build())
            )
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    // Base64はNO_WRAPにしないと改行される(https://github.com/square/retrofit/issues/1153)
    private val basicAuthorization: String
        get() = "Basic ${Base64.encodeToString("$clientId:$clientSecret".toByteArray(), Base64.NO_WRAP)}"
    private val compositeDisposable = CompositeDisposable()
    var callback: FitbitLoginCallback? = null
    // Provide some apis
    val authApi: FitbitAuthApi
        get() = retrofit.create(FitbitAuthApi::class.java)

    /**
     * ログインしているかどうかを返す
     */
    fun isLogin(): Boolean = (fitbitPreference.token != null)

    /**
     * ログイン
     */
    fun login(scopes: Array<FitbitScope>): Intent = run {
        Intent(Intent.ACTION_VIEW, createAuthorizationUri(scopes))
    }

    /**
     * ログアウト
     */
    fun logout() {
        // Tokenの値をClearする
        fitbitPreference.clear()
    }

    /**
     * ログインページのURL作成
     */
    fun createAuthorizationUri(scopes: Array<FitbitScope>, responseType: String = ResponseTypes.code): Uri =
        Uri.parse(AUTHORIZE_URL)
            .buildUpon()
            .appendQueryParameter("client_id", clientId)
            .appendQueryParameter("response_type", responseType)
            .appendQueryParameter("scope", FitbitScope.generateUrlString(scopes))
            .appendQueryParameter("redirect_uri", redirectUrl)
            .build()

    fun handleRedirectUrl(uri: Uri?, handlingCallback: HandlingCallback? = null) {
        // 前の処理を一度Clearする
        compositeDisposable.clear()
        uri ?: let {
            handlingCallback?.onComplete()
            callback?.onLoginErrored(IllegalArgumentException("Callback uri is null."))
            return
        }
        if (uri.queryParameterNames.contains(ResponseTypes.code)) {
            // Code Flow
            // Parsing uri format : "{redirect_url}?code=~~~~~#_=_
            val code = uri.getQueryParameter(ResponseTypes.code)
            authApi.createAccessToken(
                basicAuthorization,
                mapOf(
                    Pair("client_id", clientId),
                    Pair("grant_type", "authorization_code"),
                    Pair("code", code),
                    Pair("redirect_uri", redirectUrl)
                ))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    // 保存
                    fitbitPreference.token = it
                    handlingCallback?.onComplete()
                    callback?.onLoginSuccessed(it)
                }, {
                    handlingCallback?.onComplete()
                    callback?.onLoginErrored(it)
                })
                .addTo(compositeDisposable)
        } else {
            handlingCallback?.onComplete()
            TODO("Not supporting authorization type.")
            // Grant Flow
            // Parsing uri format : "{redirect_url}#access_token=~~~&user_id=6QV3DH&scope=~~~&token_type=Bearer&expires_in=86400
            /*val fragmentQueries =
                uri.fragment.split("&")
                    .map {
                        it.split("=")
                            .takeIf { it.size == 2 }
                            ?.let {
                                Pair(it[0], it[1])
                            }
                    }
                    .filterNotNull()
                    .toMap()
            val accessToken: String = fragmentQueries.get("access_token")
                ?: throw IllegalArgumentException("")*/
        }
    }

    fun release() {
        callback = null
        compositeDisposable.dispose()
    }

    interface FitbitLoginCallback {
        fun onLoginSuccessed(token: FitbitAuthToken)
        fun onLoginErrored(error: Throwable)
    }

    interface HandlingCallback {
        fun onComplete()
    }

}
