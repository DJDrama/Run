package com.dj.analytics.data.di

import com.dj.analytics.data.RoomAnalyticsRepository
import com.dj.analytics.domain.AnalyticsRepository
import com.dj.core.database.RunDatabase
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val analyticsDataModule = module {
    singleOf(::RoomAnalyticsRepository).bind<AnalyticsRepository>()
    single {
        get<RunDatabase>().analyticsDao
    }
}