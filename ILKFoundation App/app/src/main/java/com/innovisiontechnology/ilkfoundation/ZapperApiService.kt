package com.innovisiontechnology.ilkfoundation

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface ZapperApiService {
    @POST("sessions")
    fun createSession(
        @Header("merchantId") merchantId: String,
        @Header("merchantSiteId") merchantSiteId: String,
        @Header("x-api-key") apiKey: String,
        @Body request: SessionRequest
    ): Call<SessionResponse>
}
