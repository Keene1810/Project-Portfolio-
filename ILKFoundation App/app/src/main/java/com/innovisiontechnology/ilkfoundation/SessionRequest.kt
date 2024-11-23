package com.innovisiontechnology.ilkfoundation

import com.google.gson.annotations.SerializedName

data class SessionRequest(
    @SerializedName("requestId") val requestId: String,
    @SerializedName("merchantOrderId") val merchantOrderId: String,
    @SerializedName("amount") val amount: Int,
    @SerializedName("currencyISOCode") val currencyISOCode: String,
    @SerializedName("notificationUrl") val notificationUrl: String,
    @SerializedName("returnUrl") val returnUrl: String,
    @SerializedName("cancelUrl") val cancelUrl: String,
    @SerializedName("origin") val origin: String,
    @SerializedName("customFields") val customFields: List<CustomField> = listOf()
)


