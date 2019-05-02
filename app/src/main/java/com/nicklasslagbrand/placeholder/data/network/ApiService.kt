package com.nicklasslagbrand.placeholder.data.network

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.nicklasslagbrand.placeholder.Constants
import com.nicklasslagbrand.placeholder.domain.model.AttractionCategory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

fun createPlaceholderApi(
    debug: Boolean = false,
    baseUrl: String = Constants.API_BASE_URL
): PlaceholderApi {
    return Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(createOkHttpClient(addPlaceholerAuthHeader = true, debug = debug))
        .addConverterFactory(GsonConverterFactory.create(createNetworkResponseGson()))
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(PlaceholderApi::class.java)
}

private fun createOkHttpClient(addPlaceholerAuthHeader: Boolean = false, debug: Boolean): OkHttpClient {
    return OkHttpClient.Builder().apply {
        if (debug) {
            val loggingInterceptor = HttpLoggingInterceptor {
                println(it)
            }
                .apply {
                    level = HttpLoggingInterceptor.Level.BASIC
                }
            addInterceptor(loggingInterceptor)
        }

        if (addPlaceholerAuthHeader) {
            addNetworkInterceptor(BasicAuthRequestInterceptor())
        }
        addNetworkInterceptor(JsonRequestInterceptor())
    }.build()
}

private fun createNetworkResponseGson(): Gson {
    return GsonBuilder()
        .registerTypeAdapter(
            AttractionCategory::class.java,
            AttractionCategoryDeserializer()
        )
        .setLenient()
        .create()
}

inline fun <reified T> Gson.fromJson(json: String?): T = this.fromJson<T>(json, object : TypeToken<T>() {}.type)
