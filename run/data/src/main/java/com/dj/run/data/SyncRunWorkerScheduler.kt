package com.dj.run.data

import android.content.Context
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.await
import com.dj.core.database.dao.RunPendingSyncDao
import com.dj.core.database.entity.DeletedRunSyncEntity
import com.dj.core.database.entity.RunPendingSyncEntity
import com.dj.core.database.mappers.toRunEntity
import com.dj.core.domain.SessionStorage
import com.dj.core.domain.run.Run
import com.dj.core.domain.run.RunId
import com.dj.core.domain.run.SyncRunScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import kotlin.time.Duration
import kotlin.time.toJavaDuration

class SyncRunWorkerScheduler(
    private val context: Context,
    private val pendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage,
    private val applicationScope: CoroutineScope
) : SyncRunScheduler {

    private val workManager = WorkManager.getInstance(context)
    override suspend fun scheduleSync(type: SyncRunScheduler.SyncType) {
        when (type) {
            is SyncRunScheduler.SyncType.FetchRuns -> scheduleFetchRunsWorker(interval = type.interval)
            is SyncRunScheduler.SyncType.CreateRun -> scheduleCreateRunWorker(
                run = type.run,
                mapPictureBytes = type.mapPictureBytes
            )

            is SyncRunScheduler.SyncType.DeleteRun -> scheduleDeleteRunWorker(runId = type.runId)
        }
    }

    private suspend fun scheduleDeleteRunWorker(runId: RunId) {
        val userId = sessionStorage.get()?.userId ?: return
        val entity = DeletedRunSyncEntity(
            runId = runId,
            userId = userId,
        )
        pendingSyncDao.upsertDeletedRunSyncEntity(entity)

        // run once
        val workRequest = OneTimeWorkRequestBuilder<CreateRunWorker>()
            .setConstraints(
                // Internet connection
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                // EXPONENTIAL: if work fails, wait after 2 seconds, second will 4, third will 8, 16 ...
                // giving more time for worker
                // LINEAR: every 2 seconds
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                inputData = Data.Builder()
                    .putString(DeleteRunWorker.RUN_ID, entity.runId)
                    .build()
            )
            .addTag("delete_work")
            .build()

        applicationScope.launch {
            workManager.enqueue(workRequest).await()
        }.join()
    }

    private suspend fun scheduleCreateRunWorker(run: Run, mapPictureBytes: ByteArray) {
        val userId = sessionStorage.get()?.userId ?: return
        val pendingRun = RunPendingSyncEntity(
            run = run.toRunEntity(),
            mapPictureBytes = mapPictureBytes,
            userId = userId
        )
        pendingSyncDao.upsertRunPendingSyncEntity(pendingRun)

        // run once
        val workRequest = OneTimeWorkRequestBuilder<CreateRunWorker>()
            .setConstraints(
                // Internet connection
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .setBackoffCriteria(
                // EXPONENTIAL: if work fails, wait after 2 seconds, second will 4, third will 8, 16 ...
                // giving more time for worker
                // LINEAR: every 2 seconds
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInputData(
                inputData = Data.Builder()
                    .putString(CreateRunWorker.RUN_ID, pendingRun.runId)
                    .build()
            )
            .addTag("create_work")
            .build()

        applicationScope.launch {
            workManager.enqueue(workRequest).await()
        }.join()
    }

    private suspend fun scheduleFetchRunsWorker(interval: Duration) {
        val isSyncScheduled = withContext(Dispatchers.IO) {
            workManager.getWorkInfosByTag("sync_work")
                .get()
                .isNotEmpty()
        }
        if (isSyncScheduled) {
            return
        }
        val workRequest = PeriodicWorkRequestBuilder<FetchRunsWorker>(
            repeatInterval = interval.toJavaDuration()
        ).setConstraints(
            // Internet connection
            Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
        )
            .setBackoffCriteria(
                // EXPONENTIAL: if work fails, wait after 2 seconds, second will 4, third will 8, 16 ...
                // giving more time for worker
                // LINEAR: every 2 seconds
                backoffPolicy = BackoffPolicy.EXPONENTIAL,
                backoffDelay = 2000L,
                timeUnit = TimeUnit.MILLISECONDS
            )
            .setInitialDelay(
                duration = 30,
                timeUnit = TimeUnit.MINUTES
            )
            .addTag("sync_work")
            .build()

        workManager.enqueue(workRequest).await()
    }

    override suspend fun cancelAllSyncs() {
        workManager
            .cancelAllWork()
            .await()
    }
}