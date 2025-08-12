package com.arkhe.sunmi.domain.model

data class ScanResult(
    val id: Long = 0,
    val content: String,
    val format: String,
    val timestamp: Long = System.currentTimeMillis()
)