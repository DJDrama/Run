package com.dj.auth.data.di

import com.dj.auth.data.AuthRepositoryImpl
import com.dj.auth.data.EmailAndroidPatternValidator
import com.dj.auth.domain.AuthRepository
import com.dj.auth.domain.PatternValidator
import com.dj.auth.domain.UserDataValidator
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val authDataModule = module {
    single<PatternValidator> {
        EmailAndroidPatternValidator
    }
    singleOf(::UserDataValidator)
    singleOf(::AuthRepositoryImpl).bind<AuthRepository>()
}