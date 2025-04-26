package eu.slickbot.caasi

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

fun createOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder().apply {
        connectTimeout(API_CONNECT_TIMEOUT, TimeUnit.SECONDS)
        readTimeout(API_READ_TIMEOUT, TimeUnit.SECONDS)
        writeTimeout(API_WRITE_TIMEOUT, TimeUnit.SECONDS)

//            if (BuildConfig.DEBUG) {
//                addNetworkInterceptor(HttpLoggingInterceptor().apply {
//                    level = HttpLoggingInterceptor.Level.BODY
//                })
//            }

        addInterceptor { chain ->
            chain.proceed(
                chain.request().newBuilder()
                    .header("Accept", "application/json")
                    .header("User-Agent", API_USER_AGENT)
                    .build()
            )
        }
    }.build()
}
