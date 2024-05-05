package com.dj.core.data.di

import android.content.SharedPreferences
import com.dj.core.data.auth.EncryptedSessionStorage
import com.dj.core.data.networking.HttpClientFactory
import com.dj.core.domain.SessionStorage
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory().build()
    }
    singleOf(::EncryptedSessionStorage).bind<SessionStorage>()
}