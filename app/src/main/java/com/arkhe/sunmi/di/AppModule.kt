package com.arkhe.sunmi.di

import com.arkhe.sunmi.data.repository.PrinterRepositoryImpl
import com.arkhe.sunmi.data.repository.ScannerRepositoryImpl
import com.arkhe.sunmi.domain.repository.PrinterRepository
import com.arkhe.sunmi.domain.repository.ScannerRepository
import com.arkhe.sunmi.domain.usecase.PrintUseCase
import com.arkhe.sunmi.domain.usecase.ScanUseCase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {

    // Repositories
    single<PrinterRepository> {
        PrinterRepositoryImpl(androidContext())
    }

    single<ScannerRepository> {
        ScannerRepositoryImpl(get())
    }

    // Use Cases
    single { PrintUseCase(get()) }
    single { ScanUseCase(get()) }
}