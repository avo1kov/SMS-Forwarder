package ru.smsforwarder.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class SendMessageRequest(
    @Json(name = "text") val text: String,
    @Json(name = "chat_id") val chatId: String,
    @Json(name = "parse_mode") val parseMode: String?
) {

    companion object {

        const val MARK_DOWN_V2 = "MarkdownV2"
    }
}
