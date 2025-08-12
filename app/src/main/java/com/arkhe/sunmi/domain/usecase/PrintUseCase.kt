package com.arkhe.sunmi.domain.usecase

import com.arkhe.sunmi.domain.model.PrintData
import com.arkhe.sunmi.domain.model.PrinterStatus
import com.arkhe.sunmi.domain.repository.PrinterRepository

class PrintUseCase(
    private val printerRepository: PrinterRepository
) {
    suspend fun execute(data: PrintData): Result<Unit> {
        return printerRepository.print(data)
    }

    suspend fun printReceipt(items: List<PrintData>): Result<Unit> {
        return printerRepository.printReceipt(items)
    }

    suspend fun checkPrinterStatus(): Result<PrinterStatus> {
        return printerRepository.checkPrinterStatus()
    }

    suspend fun initializePrinter(): Result<Unit> {
        return printerRepository.initializePrinter()
    }
}