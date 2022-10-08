package ru.smsforwarder.workmanager

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import ru.smsforwarder.domain.SendSmsUseCase
import ru.smsforwarder.domain.model.SmsModel
import ru.smsforwarder.domain.model.SmsSendResult

@HiltWorker
class SendSmsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val sendSmsUseCase: SendSmsUseCase,
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        runCatching {
            val smsModel = getSmsInfo()
            sendSmsUseCase.sendSmsToTelegram(smsModel)
        }.fold(
            onSuccess = { result ->
                return when (result) {
                    SmsSendResult.Success -> Result.success()
                    SmsSendResult.Retry -> Result.retry()
                    SmsSendResult.Failure -> Result.failure()
                }
            },
            onFailure = {
                it.printStackTrace()
                Log.e(TAG, "FAIL with: $it")
                return Result.retry()
            }
        )
    }

    private fun getSmsInfo(): SmsModel {
        val text = inputData.getString(Constants.SMS_TEXT_KEY).orEmpty()
        val sender = inputData.getString(Constants.SMS_SENDER_KEY).orEmpty()
        val timestamp = inputData.getLong(Constants.SMS_TIMESTAMP_KEY, -1)

        return SmsModel(text, sender, timestamp)
    }

    companion object {

        private const val TAG = "SendSmsWorker"
        private val CONSTRAINTS = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        fun newRequest(smsModel: SmsModel): OneTimeWorkRequest =
            OneTimeWorkRequestBuilder<SendSmsWorker>().apply {
                val smsData = workDataOf(
                    Constants.SMS_TEXT_KEY to smsModel.text,
                    Constants.SMS_SENDER_KEY to smsModel.address,
                    Constants.SMS_TIMESTAMP_KEY to smsModel.timestamp,
                )
                setInputData(smsData)
                setConstraints(CONSTRAINTS)
            }.build()
    }
}
