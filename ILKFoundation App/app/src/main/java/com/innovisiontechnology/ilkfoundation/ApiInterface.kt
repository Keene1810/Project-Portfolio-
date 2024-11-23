package com.innovisiontechnology.ilkfoundation

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    //Elastic Email HTTP API
    @FormUrlEncoded
    @POST("email/send")
    suspend fun sendEmail(
        @Field("apikey") apiKey: String,
        @Field("from") from: String,
        @Field("to") to: String,
        @Field("subject") subject: String,
        @Field("bodyHtml") bodyHtml: String
    ): Response<Void>
}