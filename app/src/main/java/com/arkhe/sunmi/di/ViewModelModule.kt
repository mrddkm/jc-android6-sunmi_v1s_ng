@file:Suppress("DEPRECATION")

package com.arkhe.sunmi.di

import com.arkhe.sunmi.presentation.viewmodel.MainViewModel
import com.arkhe.sunmi.presentation.viewmodel.PrintViewModel
import com.arkhe.sunmi.presentation.viewmodel.ScanViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { MainViewModel(get(), get()) }
    viewModel { PrintViewModel(get()) }
    viewModel { ScanViewModel(get()) }
}