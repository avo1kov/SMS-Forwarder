package ru.smsforwarder.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SendSmsResponse(
    @Json(name = "ok") val ok: Boolean
)
