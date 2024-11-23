package com.innovisiontechnology.ilkfoundation

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetroIns {

    //For the Spoonacular API
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.elasticemail.com/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    //Pull the interface
    val someInterface by lazy {
        retrofit.create(ApiInterface::class.java)
    }

}