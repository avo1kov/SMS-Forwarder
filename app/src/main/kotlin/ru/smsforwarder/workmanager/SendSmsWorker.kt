package ru.smsforwarder.workmanager

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import ru.smsforwarder.model.SmsModel

class SendSmsWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        runCatching {
            val args = inputData
            val smsText = args.getString(Constants.SMS_TEXT_KEY)
            val smsSender = args.getString(Constants.SMS_SENDER_KEY)
            Log.d(TAG, "SEND TO TELEGRAM: $smsText $smsSender")
        }.fold(
            onSuccess = { return Result.success() },
            onFailure = {
                Log.e(TAG, "FAIL with: $it")
                return Result.retry()
            }
        )
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
                )
                setInputData(smsData)
                setConstraints(CONSTRAINTS)
            }.build()
    }
}
