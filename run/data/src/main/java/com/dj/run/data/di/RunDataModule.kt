package com.dj.run.data.di

import com.dj.core.domain.run.SyncRunScheduler
import com.dj.run.data.CreateRunWorker
import com.dj.run.data.DeleteRunWorker
import com.dj.run.data.FetchRunsWorker
import com.dj.run.data.SyncRunWorkerScheduler
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::FetchRunsWorker)
    workerOf(::DeleteRunWorker)

    singleOf(::SyncRunWorkerScheduler).bind<SyncRunScheduler>()
}