package com.arkhe.sunmi.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.journeyapps.barcodescanner.CompoundBarcodeView

@Composable
fun ScannerView(
    onBarcodeViewCreated: (CompoundBarcodeView) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            CompoundBarcodeView(ctx).apply {
                onBarcodeViewCreated(this)
                resume()
            }
        },
        update = { view ->
            view.resume()
        }
    )
}