package com.dj.core.data.run

import com.dj.core.database.dao.RunPendingSyncDao
import com.dj.core.database.mappers.toRun
import com.dj.core.domain.SessionStorage
import com.dj.core.domain.run.LocalRunDataSource
import com.dj.core.domain.run.RemoteRunDataSource
import com.dj.core.domain.run.Run
import com.dj.core.domain.run.RunId
import com.dj.core.domain.run.RunRepository
import com.dj.core.domain.util.DataError
import com.dj.core.domain.util.EmptyResult
import com.dj.core.domain.util.Result
import com.dj.core.domain.util.asEmptyDataResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class OfflineFirstRunRepository(
    private val localRunDataSource: LocalRunDataSource,
    private val remoteRunDataSource: RemoteRunDataSource,
    private val applicationScope: CoroutineScope,
    private val runPendingSyncDao: RunPendingSyncDao,
    private val sessionStorage: SessionStorage
) : RunRepository {
    override fun getRuns(): Flow<List<Run>> {
        return localRunDataSource.getRuns()
    }

    override suspend fun fetchRuns(): EmptyResult<DataError> {
        return when (val result = remoteRunDataSource.getRuns()) {
            is Result.Error -> result.asEmptyDataResult()
            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsertRuns(result.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun upsertRun(run: Run, mapPicture: ByteArray): EmptyResult<DataError> {
        val localResult = localRunDataSource.upsertRun(run)
        if (localResult !is Result.Success) {
            return localResult.asEmptyDataResult()
        }
        val runWithId = run.copy(id = localResult.data)
        val remoteResult = remoteRunDataSource.postRun(
            run = runWithId,
            mapPicture = mapPicture
        )
        return when (remoteResult) {
            is Result.Error -> {
                Result.Success(Unit)
            }

            is Result.Success -> {
                applicationScope.async {
                    localRunDataSource.upsertRun(remoteResult.data).asEmptyDataResult()
                }.await()
            }
        }
    }

    override suspend fun deleteRun(id: RunId) {
        localRunDataSource.deleteRun(id)

        // Edge case where the run is created in offline-mode,
        // and then deleted in offline-mode as well
        // in that case, we don't need to sync anything.
        val isPendingSync = runPendingSyncDao.getRunPendingSyncEntity(runId = id) != null
        if(isPendingSync){
            runPendingSyncDao.deleteRunPendingSyncEntity(runId = id)
            return
        }

        val remoteResult = applicationScope.async {
            remoteRunDataSource.deleteRun(id = id)
        }.await()
    }

    override suspend fun syncPendingRuns() {
        withContext(Dispatchers.IO) {
            val userId = sessionStorage.get()?.userId ?: return@withContext

            val createdRuns = async {
                runPendingSyncDao.getAllRunPendingSyncEntities(userId = userId)
            }
            val deletedRuns = async {
                runPendingSyncDao.getAllDeletedRunSyncEntities(userId = userId)
            }

            val createdJobs = createdRuns.await()
                .map {
                    launch {
                        val run = it.run.toRun()
                        when (remoteRunDataSource.postRun(
                            run = run,
                            mapPicture = it.mapPictureBytes
                        )) {
                            is Result.Error -> Unit // will retry later
                            is Result.Success -> {
                                // success so we can remove from our local db
                                applicationScope.launch {
                                    runPendingSyncDao.deleteRunPendingSyncEntity(it.runId)
                                }.join() // sequentially
                            }
                        }
                    }
                }

            val deletedJobs = deletedRuns
                .await()
                .map {
                    launch {
                        when (remoteRunDataSource.deleteRun(
                            id = it.runId
                        )) {
                            is Result.Error -> Unit // will retry later
                            is Result.Success -> {
                                // success so we can remove from our local db
                                applicationScope.launch {
                                    runPendingSyncDao.deleteDeletedRunSyncEntity(runId = it.runId)
                                }.join() // sequentially
                            }
                        }
                    }
                }

            createdJobs.forEach{
                it.join()
            }
            deletedJobs.forEach{
                it.join()
            }
        }
    }
}