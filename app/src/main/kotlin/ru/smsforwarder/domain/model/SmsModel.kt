package ru.smsforwarder.domain.model

data class SmsModel(
    val text: String,
    val address: String?,
    val timestamp: Long
)
