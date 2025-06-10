package com.sumkim.pretest.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.kurly.android.mockserver.MockInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASEURL = "https://kurly.com/"

    fun <T> createService(
        context: Context,
        serviceClass: Class<T>
    ): T {
        val okHttp = OkHttpClient.Builder()
            .addInterceptor(MockInterceptor(context))

        val client = Retrofit.Builder()
            .baseUrl(BASEURL)
            .client(okHttp.build())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()

        return client.create(serviceClass)
    }
}