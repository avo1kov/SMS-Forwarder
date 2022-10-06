package ru.smsforwarder.data.model

data class UserRequestParams(
    val telegramBotToken: String,
    val chatId: String
)
