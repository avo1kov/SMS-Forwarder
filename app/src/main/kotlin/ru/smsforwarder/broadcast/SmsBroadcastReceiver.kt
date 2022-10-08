package ru.smsforwarder.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import ru.smsforwarder.domain.model.SmsModel
import ru.smsforwarder.workmanager.Constants
import ru.smsforwarder.workmanager.SendSmsWorker

class SmsBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (!intent.action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) return

        val extractMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        val sms = extractMessages.map { smsMessage ->
            with(smsMessage) {
                SmsModel(
                    text = displayMessageBody,
                    address = displayOriginatingAddress,
                    timestamp = timestampMillis
                )
            }
        }

        sms.forEach { sendSms(context, it) }
    }

    private fun sendSms(context: Context, smsModel: SmsModel) {
        val request = SendSmsWorker.newRequest(smsModel)

        WorkManager.getInstance(context)
            .beginUniqueWork(
                Constants.SMS_MANIPULATION_WORK_NAME,
                ExistingWorkPolicy.APPEND,
                request
            ).enqueue()
    }
}
