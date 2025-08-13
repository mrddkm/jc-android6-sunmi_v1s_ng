package com.arkhe.sunmi.utils

import android.content.Context
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

// Context Extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

// Bitmap Extensions
fun Bitmap.resize(maxWidth: Int, maxHeight: Int): Bitmap {
    return BitmapUtils.resizeBitmap(this, maxWidth, maxHeight)
}

fun Bitmap.isValidForPrint(): Boolean {
    return width > 0 && height > 0 && !isRecycled
}

// String Extensions
fun String.isValidBarcode(): Boolean {
    return this.isNotBlank() && this.all { it.isDigit() || it.isLetter() }
}

fun String.isValidQRContent(): Boolean {
    return this.isNotBlank() && this.length <= 2000
}

// Compose Extensions
@Composable
fun Dp.toPx(): Float {
    return with(LocalDensity.current) { this@toPx.toPx() }
}

@Composable
fun Float.toDp(): Dp {
    return with(LocalDensity.current) { this@toDp.toDp() }
}