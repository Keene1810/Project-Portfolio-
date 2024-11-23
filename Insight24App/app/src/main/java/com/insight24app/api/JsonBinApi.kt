package com.insight24app.api

import com.insight24app.model.InsightResponse
import retrofit2.Call
import retrofit2.http.GET

interface JsonBinApi {
    @GET("b/6719eee2ad19ca34f8bdad5a") // Replace with your actual JSONBin ID
    fun getHeadlines(): Call<InsightResponse>
}