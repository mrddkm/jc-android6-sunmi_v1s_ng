package com.arkhe.sunmi.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

object PermissionUtils {

    fun hasPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun hasCameraPermission(context: Context): Boolean {
        return hasPermission(context, android.Manifest.permission.CAMERA)
    }

    fun hasStoragePermission(context: Context): Boolean {
        return hasPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}