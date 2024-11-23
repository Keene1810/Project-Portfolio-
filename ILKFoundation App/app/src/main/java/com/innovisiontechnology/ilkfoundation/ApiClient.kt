package com.innovisiontechnology.ilkfoundation

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://gateway.zapper.com/api/v3.1/"

    val zapperApiService: ZapperApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ZapperApiService::class.java)
    }
}
