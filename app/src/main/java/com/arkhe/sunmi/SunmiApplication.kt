package com.arkhe.sunmi

import android.app.Application
import android.util.Log
import com.arkhe.sunmi.di.appModule
import com.arkhe.sunmi.di.databaseModule
import com.arkhe.sunmi.di.viewModelModule
import com.arkhe.sunmi.utils.CrashHandler
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

class SunmiApplication : Application() {

    companion object {
        private const val TAG = "SunmiApplication"
    }

    override fun onCreate() {
        super.onCreate()

        CrashHandler.install()

        Log.d(TAG, "Application onCreate started")

        try {
            startKoin {
                androidLogger(Level.DEBUG)
                androidContext(this@SunmiApplication)
                modules(
                    appModule,
                    databaseModule,
                    viewModelModule
                )
            }
            Log.d(TAG, "Koin initialization completed successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing Koin", e)
            throw e
        }
    }
}