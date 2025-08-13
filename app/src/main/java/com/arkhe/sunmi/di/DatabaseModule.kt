package com.arkhe.sunmi.di

import androidx.room.Room
import com.arkhe.sunmi.data.local.ScanHistoryDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            ScanHistoryDatabase::class.java,
            "scan_history.db"
        ).build()
    }

    single { get<ScanHistoryDatabase>().scanHistoryDao() }
}