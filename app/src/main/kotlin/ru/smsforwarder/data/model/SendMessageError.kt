package ru.smsforwarder.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SendMessageError(
    @Json(name = "error_code") val errorCode: Int?,
    @Json(name = "description") val description: String?,
)
