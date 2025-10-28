package com.pented.learningapp.retrofit

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class PlacesRetrofitClient {
 //   val GOOGLE_PLACE_API_KEY = "AIzaSyC9OJE-EeP4QJUOGG4PFrUFcIG2mC44Bfs"
    companion object{
        private var retrofit: Retrofit? = null
        var base_url = "https://maps.googleapis.com/maps/api/"
        open fun getClient(): Retrofit {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor).build()
            retrofit = null
            retrofit = Retrofit.Builder()
                .baseUrl(base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit as Retrofit
        }
    }
}