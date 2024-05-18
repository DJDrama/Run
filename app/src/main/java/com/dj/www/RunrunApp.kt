package com.dj.www

import android.app.Application
import com.dj.auth.data.di.authDataModule
import com.dj.auth.presentation.di.authViewModelModule
import com.dj.core.data.di.coreDataModule
import com.dj.core.database.di.databaseModule
import com.dj.run.data.di.runDataModule
import com.dj.run.location.di.locationModule
import com.dj.run.network.di.networkModule
import com.dj.run.presentation.di.runPresentationModule
import com.dj.www.di.appModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.workmanager.koin.workManagerFactory
import org.koin.core.context.startKoin
import timber.log.Timber

class RunrunApp : Application() {
    val applicationScope = CoroutineScope(SupervisorJob())
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(tree = Timber.DebugTree())
        }

        startKoin {
            androidLogger()
            androidContext(androidContext = this@RunrunApp)
            workManagerFactory()

            modules(
                authDataModule,
                authViewModelModule,
                appModule,
                coreDataModule,
                runPresentationModule,
                locationModule,
                databaseModule,
                networkModule,
                runDataModule
            )
        }
    }
}