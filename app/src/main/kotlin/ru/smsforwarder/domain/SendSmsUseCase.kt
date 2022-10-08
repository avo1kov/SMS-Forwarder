package ru.smsforwarder.domain

import android.util.Log
import com.slack.eithernet.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.smsforwarder.data.model.SendMessageRequest
import ru.smsforwarder.data.model.UserRequestParams
import ru.smsforwarder.data.service.TelegramSendSmsService
import ru.smsforwarder.domain.model.SmsModel
import ru.smsforwarder.domain.model.SmsSendResult
import ru.smsforwarder.util.escapeMdV2
import ru.smsforwarder.util.highlightNumberSequence
import javax.inject.Inject

class SendSmsUseCase @Inject constructor(
    private val smsService: TelegramSendSmsService,
    private val userInfoProvider: UserInfoProvider
) {

    suspend fun sendSmsToTelegram(smsModel: SmsModel): SmsSendResult {
        val requestParams = userInfoProvider.provideRequestParams()

        val result = withContext(Dispatchers.IO) {
            val request = formatSendRequest(smsModel, requestParams)

            Log.v("SendSmsUseCase", "send sms with MD: $request")
            val result = smsService.sendSms(
                telegramBotToken = requestParams.telegramBotToken,
                body = request,
            )
            Log.v("SendSmsUseCase", "RESULT: $result")

            result
        }

        return when (result) {
            is ApiResult.Success -> SmsSendResult.Success
            is ApiResult.Failure.UnknownFailure -> SmsSendResult.Failure
            is ApiResult.Failure.NetworkFailure -> SmsSendResult.Retry
            is ApiResult.Failure.HttpFailure,
            is ApiResult.Failure.ApiFailure -> sendSmsWithoutMd(smsModel, requestParams)
        }
    }

    private suspend fun sendSmsWithoutMd(
        smsModel: SmsModel,
        requestParams: UserRequestParams
    ): SmsSendResult {
        Log.d("SendSmsUseCase", "MODEL: $smsModel")
        val request = formatSendRequest(
            smsModel = smsModel,
            requestParams = requestParams,
            tryToUseMd = false
        )
        Log.v("SendSmsUseCase", "send sms without MD: $request")
        val result = smsService.sendSms(
            telegramBotToken = requestParams.telegramBotToken,
            body = request,
        )
        Log.v("SendSmsUseCase", "RESULT: $result")

        return when (result) {
            is ApiResult.Success -> SmsSendResult.Success
            is ApiResult.Failure.UnknownFailure -> {
                result.error.printStackTrace()
                Log.d("SendSmsUseCase", "sendSmsWithoutMd: ${result.error}")
                SmsSendResult.Failure
            }
            is ApiResult.Failure.NetworkFailure -> SmsSendResult.Retry
            is ApiResult.Failure.HttpFailure,
            is ApiResult.Failure.ApiFailure -> SmsSendResult.Failure
        }
    }

    private fun formatSendRequest(
        smsModel: SmsModel,
        requestParams: UserRequestParams,
        tryToUseMd: Boolean = true
    ): SendMessageRequest {
        val text = when (tryToUseMd) {
            true -> formatTelegramMessageMarkdown(smsModel)
            false -> formatTelegramMessage(smsModel)
        }

        val parseMode = when (tryToUseMd) {
            true -> SendMessageRequest.MARK_DOWN_V2
            false -> null
        }
        return SendMessageRequest(
            text = text,
            chatId = requestParams.chatId,
            parseMode = parseMode
        )
    }

    private fun formatTelegramMessage(smsModel: SmsModel): String = with(smsModel) {
        """
            $text
            From:$address
        """.trimIndent()
    }

    private fun formatTelegramMessageMarkdown(smsModel: SmsModel): String = with(smsModel) {
        """
            ${text.escapeMdV2().highlightNumberSequence()}
            
            *From:* ${address?.escapeMdV2()}
        """.trimIndent()
    }
}
