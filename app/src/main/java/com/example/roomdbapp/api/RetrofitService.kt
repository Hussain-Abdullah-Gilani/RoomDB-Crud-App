package com.example.roomdbapp.api

import android.util.Log
import com.example.roomdbapp.db.UserEntity
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST

interface RetrofitService {

    @GET("users")
    suspend fun getUsers(): Response<List<UserEntity>>

    @POST("users")
    suspend fun createUsers():Response<List<UserEntity>>





    companion object{

        var retrofitService:RetrofitService?=null

        fun getInstance():RetrofitService
        {

            if(retrofitService== null) {
                Log.d("TAG","RETROFITSERVICE GETUSERS()")
                val retrofit = Retrofit.Builder().baseUrl("https://gorest.co.in/public/v2/").addConverterFactory(
                    GsonConverterFactory.create()).build()
                retrofitService=retrofit.create(RetrofitService::class.java)

            }
            return retrofitService!!
        }
    }















}