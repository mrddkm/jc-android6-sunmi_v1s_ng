package com.arkhe.sunmi.utils

object Constants {

    object Print {
        const val DEFAULT_FONT_SIZE = 24
        const val DEFAULT_QR_SIZE = 200
        const val DEFAULT_BARCODE_WIDTH = 300
        const val DEFAULT_BARCODE_HEIGHT = 100
        const val MAX_PRINT_WIDTH = 384 // pixels for 58mm paper
    }

    object Scanner {
        const val SCAN_TIMEOUT_MS = 30000L
        const val AUTO_FOCUS_INTERVAL_MS = 2000L
    }

    object Database {
        const val NAME = "sunmi_v1s_db"
        const val VERSION = 1
    }

    object Permissions {
        const val CAMERA = android.Manifest.permission.CAMERA
        const val BLUETOOTH = android.Manifest.permission.BLUETOOTH
        const val BLUETOOTH_ADMIN = android.Manifest.permission.BLUETOOTH_ADMIN
        const val SUNMI_PRINTER = "com.sunmi.permission.PRINTER"
    }
}