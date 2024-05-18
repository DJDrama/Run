package com.dj.run.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.dj.core.domain.run.RunRepository
import com.dj.core.domain.util.DataError

class FetchRunsWorker(
    context: Context,
    params: WorkerParameters,
    private val runRepository: RunRepository
) : CoroutineWorker(appContext = context, params = params) {
    override suspend fun doWork(): Result {
        if (runAttemptCount >= 5) { // retry only 5 times
            return Result.failure()
        }
        return when (val result = runRepository.fetchRuns()) {
            is com.dj.core.domain.util.Result.Error -> {
               result.error.toWorkerResult()
            }

            is com.dj.core.domain.util.Result.Success -> Result.success()
        }
    }

}