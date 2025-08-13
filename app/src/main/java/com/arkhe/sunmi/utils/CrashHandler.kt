package com.arkhe.sunmi.utils

import android.util.Log
import kotlin.system.exitProcess

class CrashHandler private constructor() : Thread.UncaughtExceptionHandler {

    companion object {
        private const val TAG = "CrashHandler"

        fun install() {
            val handler = CrashHandler()
            Thread.setDefaultUncaughtExceptionHandler(handler)
            Log.d(TAG, "CrashHandler installed")
        }
    }

    override fun uncaughtException(thread: Thread, exception: Throwable) {
        Log.e(TAG, "=== UNCAUGHT EXCEPTION ===")
        Log.e(TAG, "Thread: ${thread.name}")
        Log.e(TAG, "Exception: ${exception::class.java.simpleName}")
        Log.e(TAG, "Message: ${exception.message}")
        Log.e(TAG, "Stack trace:")

        exception.printStackTrace()

        // Log the full stack trace
        val stackTrace = Log.getStackTraceString(exception)
        Log.e(TAG, stackTrace)

        Log.e(TAG, "=== END UNCAUGHT EXCEPTION ===")

        // Let the system handle it normally
        exitProcess(1)
    }
}