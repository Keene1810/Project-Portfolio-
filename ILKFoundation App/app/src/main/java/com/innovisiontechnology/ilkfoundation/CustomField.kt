package com.innovisiontechnology.ilkfoundation

import com.google.gson.annotations.SerializedName

data class CustomField(
    @SerializedName("key") val key: String,
    @SerializedName("value") val value: String
)
