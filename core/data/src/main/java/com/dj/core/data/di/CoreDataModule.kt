package com.dj.core.data.di

import com.dj.core.data.auth.EncryptedSessionStorage
import com.dj.core.data.networking.HttpClientFactory
import com.dj.core.data.run.OfflineFirstRunRepository
import com.dj.core.domain.SessionStorage
import com.dj.core.domain.run.RunRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory(sessionStorage = get()).build()
    }
    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()

    singleOf(::OfflineFirstRunRepository).bind<RunRepository>()
}