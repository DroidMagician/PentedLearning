package com.app.jeeves.retrofit

import okhttp3.MultipartBody
import okhttp3.Response
import retrofit2.Call
import retrofit2.http.*


interface  ApiInterface {
    //requesturl="https://maps.googleapis.com/maps/api/place/search/json?radius=500&sensor=false&key="+googleAPIKey+"&location="+latitude+","+longitude;
//    @GET("place/nearbysearch/json?")
//     fun doPlaces(@Query(value = "type", encoded = true) type: String, @Query(value = "location",
//            encoded = true) location: String, @Query(value = "name", encoded = true) name: String, @Query(
//            value = "opennow", encoded = true) opennow: Boolean, @Query(value = "rankby", encoded = true) rankby: String, @Query(
//            value = "key",
//            encoded = true) key: String): Call<PlacesPOJO.Root>
//
//     @GET("place/nearbysearch/json?")
//     fun getNearbyPlaces(@Query(value = "type", encoded = true) type: String,@Query(value = "radius", encoded = true) radius: String,@Query(value = "sensor", encoded = true) sensor: Boolean,@Query(value = "key", encoded = true) key: String,@Query(value = "location", encoded = true) location: String ): Call<PlacesPOJO.Root>

    //@GET("timezone/json?")
  //  fun getTimeZone(@Query(value = "location", encoded = true) location: String, @Query(value = "timestamp", encoded = true) timestamp: String, @Query(value = "key", encoded = true) key: String): Call<TimeZoneResponce>
}