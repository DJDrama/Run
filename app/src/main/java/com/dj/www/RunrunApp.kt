package com.dj.www

import android.app.Application
import com.dj.auth.data.di.authDataModule
import com.dj.auth.presentation.di.authViewModelModule
import com.dj.core.data.di.coreDataModule
import com.dj.www.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class RunrunApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(tree = Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(androidContext = this@RunrunApp)

            modules(
                authDataModule,
                authViewModelModule,
                appModule,
                coreDataModule
            )
        }
    }
}