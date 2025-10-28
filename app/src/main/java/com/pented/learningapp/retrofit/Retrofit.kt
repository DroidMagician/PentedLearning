package com.pented.learningapp.retrofit

import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.pented.learningapp.BuildConfig
import com.pented.learningapp.MyApplication
import com.pented.learningapp.helper.Constants
import com.pented.learningapp.helper.SharedPrefs

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class Retrofit {

    companion object Singleton {
        private lateinit var mRetrofit: Retrofit
        private lateinit var mRetrofitStripe: Retrofit



        fun init() {

            val httpClient = OkHttpClient.Builder()

            httpClient.readTimeout(10, TimeUnit.MINUTES)
            httpClient.connectTimeout(5, TimeUnit.MINUTES)
            httpClient.writeTimeout(10, TimeUnit.MINUTES)

            /* httpClient.addInterceptor { chain ->
                 val original = chain.request()
                 val builder = original.newBuilder()
                 builder.header("Accept", "application/json")
                 builder.header("api_key", SharedPrefs.getToken(MyApplication.getInstance()) ?: null)
                 builder.method(original.method(), original.body())
                 chain.proceed(builder.build())
             }*/

            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val builder = original.newBuilder()
                builder.header("Accept", "application/json")
                builder.header("platform_type", "android")
                builder.header("header-language-id", Constants.headerlanguageid ?: "")
                builder.header("header-standard-id",  Constants.headerstandardid ?: "")
                builder.header("header-app-version",  Constants.headerappversion ?: "")
                builder.header("header-device-model",  Constants.headerdevicemodel ?: "")
                builder.header("header-device-UUID",   Constants.headerdeviceUUID ?: "")
                builder.header("platform_type", "android")
                builder.header(
                    "Authorization",
                    "bearer ${SharedPrefs.getToken(MyApplication.getInstance())}"
                )
                builder.method(original.method(), original.body())
                chain.proceed(builder.build())
            }

            val interceptor = HttpLoggingInterceptor()
            if (BuildConfig.DEBUG)
                interceptor.level = HttpLoggingInterceptor.Level.BODY
            else
                interceptor.level = HttpLoggingInterceptor.Level.NONE

            httpClient.addInterceptor(interceptor)

            val client = httpClient.build()
            client.dispatcher().maxRequests = Integer.MAX_VALUE
            val gson = GsonBuilder()
                .setLenient()
                .create()

//            mRetrofitStatic = retrofit2.Retrofit.Builder()
//                .baseUrl("https://stripe-issuing-europe.dev.jeev.es/v1/")
//                .client(client)
//                .addConverterFactory(GsonConverterFactory.create(gson))
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .build()

            var finalUrl: String? = null
//            if (API.BASE_URL != null && API.BASE_URL != "") {
//                if (API.BASE_URL?.contains("V1", true) == true) {
//                    finalUrl = "${API.BASE_URL}"
//                } else {
//                    finalUrl = "${API.BASE_URL}/v1/"
//                }
//
//            } else {
//                finalUrl = "https://prod.jeev.es/v1/"
//            }

            mRetrofit = retrofit2.Retrofit.Builder()
                  .baseUrl(API.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
            mRetrofitStripe = retrofit2.Retrofit.Builder()
                .baseUrl(API.STRIPE_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()

        }

        fun getRetrofit(): Retrofit {
            return mRetrofit
        }


        fun getRetrofitStripe(): Retrofit {
            return mRetrofitStripe
        }
    }
}