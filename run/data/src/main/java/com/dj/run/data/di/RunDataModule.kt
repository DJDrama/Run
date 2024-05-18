package com.dj.run.data.di

import com.dj.run.data.CreateRunWorker
import com.dj.run.data.DeleteRunWorker
import com.dj.run.data.FetchRunsWorker
import org.koin.androidx.workmanager.dsl.workerOf
import org.koin.dsl.module

val runDataModule = module {
    workerOf(::CreateRunWorker)
    workerOf(::FetchRunsWorker)
    workerOf(::DeleteRunWorker)

}