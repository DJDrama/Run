package com.dj.core.data.di

import com.dj.core.data.networking.HttpClientFactory
import org.koin.dsl.module

val coreDataModule = module {
    single {
        HttpClientFactory().build()
    }
}