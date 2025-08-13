package com.arkhe.sunmi.utils

import android.util.Log

object Logger {
    private const val TAG = "SunmiV1sNG"

    fun d(message: String, tag: String = TAG) {
        Log.d(tag, message)
    }

    fun i(message: String, tag: String = TAG) {
        Log.i(tag, message)
    }

    fun w(message: String, tag: String = TAG) {
        Log.w(tag, message)
    }

    fun e(message: String, throwable: Throwable? = null, tag: String = TAG) {
        Log.e(tag, message, throwable)
    }

    fun printInfo(action: String, data: String) {
        d("PRINT: $action - $data", "PrintAction")
    }

    fun scanInfo(action: String, data: String) {
        d("SCAN: $action - $data", "ScanAction")
    }
}