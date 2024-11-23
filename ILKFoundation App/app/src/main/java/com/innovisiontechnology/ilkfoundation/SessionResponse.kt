package com.innovisiontechnology.ilkfoundation

import com.google.gson.annotations.SerializedName

data class SessionResponse(
    @SerializedName("sessionId") val sessionId: String,
    @SerializedName("redirectUrl") val redirectUrl: String,
    @SerializedName("status") val status: String,
    @SerializedName("errors") val errors: List<String>?
)