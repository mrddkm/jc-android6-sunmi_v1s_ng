package com.arkhe.sunmi.domain.model

data class PrinterStatus(
    val isConnected: Boolean,
    val paperStatus: PaperStatus,
    val temperature: TemperatureStatus
)

enum class PaperStatus { OK, LOW, EMPTY }
enum class TemperatureStatus { NORMAL, HIGH, OVERHEATED }