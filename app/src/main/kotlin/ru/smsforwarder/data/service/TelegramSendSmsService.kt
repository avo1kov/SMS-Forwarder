package ru.smsforwarder.data.service

import com.slack.eithernet.ApiResult
import com.slack.eithernet.DecodeErrorBody
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path
import ru.smsforwarder.data.model.SendMessageError
import ru.smsforwarder.data.model.SendMessageRequest
import ru.smsforwarder.data.model.SendSmsResponse

interface TelegramSendSmsService {

    @DecodeErrorBody
    @POST("/bot{telegramBotToken}/sendMessage")
    suspend fun sendSms(
        @Path("telegramBotToken") telegramBotToken: String,
        @Body body: SendMessageRequest
    ): ApiResult<SendSmsResponse, SendMessageError>

    companion object {

        const val BASE_URL = "https://api.telegram.org/"
    }
}
