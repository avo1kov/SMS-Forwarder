package ru.smsforwarder.domain

import ru.smsforwarder.BuildConfig
import ru.smsforwarder.data.model.UserRequestParams

class UserInfoProvider {

    fun provideRequestParams(): UserRequestParams {
        return UserRequestParams(
            telegramBotToken = BuildConfig.TELEGRAM_BOT_TOKEN,
            chatId = BuildConfig.CHAT_ID
        )
    }
}
