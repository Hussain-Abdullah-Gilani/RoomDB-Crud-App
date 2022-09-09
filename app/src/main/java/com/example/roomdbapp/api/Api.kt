package com.example.roomdbapp.api

import com.example.roomdbapp.db.UserEntity
import retrofit2.Call
import retrofit2.http.*

interface Api {
    @GET("users")
    fun listUsers(): Call<List<UserEntity>>

    @GET("users")
    fun listCountries(@Query("name") searchtext: CharSequence): Call<ArrayList<UserEntity>>

    @POST("users")
    @Headers("Accept:application/json", "Content-Type:application/json", "Authorization: Bearer 93a893a16099d11c237943d7c7cf2c56b3ebdb3e9329e80e12bd9d425ef98dda")
    fun addUser(@Query("name") name: String,
                @Query("email") email:String,
                @Query("gender") gender:String,
                @Query("status") status:String,
    ): Call<UserEntity>

    @PUT("users/8")
    @Headers("Accept:application/json", "Content-Type:application/json", "Authorization: Bearer 93a893a16099d11c237943d7c7cf2c56b3ebdb3e9329e80e12bd9d425ef98dda")
    fun putUser(@Query("name") name: String,
                @Query("email") email:String,

                ): Call<UserEntity>

    @DELETE("users/8")
    @Headers("Accept:application/json", "Content-Type:application/json", "Authorization: Bearer 93a893a16099d11c237943d7c7cf2c56b3ebdb3e9329e80e12bd9d425ef98dda")
    fun DelUser(): Call<UserEntity>
}