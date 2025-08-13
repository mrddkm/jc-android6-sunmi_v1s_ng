package com.arkhe.sunmi.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkhe.sunmi.presentation.components.ScannerView
import com.arkhe.sunmi.presentation.viewmodel.MainViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ScanScreen(
    onNavigateBack: () -> Unit,
    viewModel: MainViewModel = koinViewModel()
) {
    val scanResults by viewModel.scanResults.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val cameraPermissionState = rememberPermissionState(android.Manifest.permission.CAMERA)

    DisposableEffect(Unit) {
        onDispose {
            if (uiState.isScanning) {
                viewModel.stopScanning()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Scanner Functions",
                        fontSize = 16.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (scanResults.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearScanHistory() }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear History",
                                modifier = Modifier.size(20.dp) // Smaller icon
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (cameraPermissionState.status.isGranted) {
                FloatingActionButton(
                    onClick = {
                        if (uiState.isScanning) {
                            viewModel.stopScanning()
                        } else {
                            viewModel.startScanning()
                        }
                    },
                    modifier = Modifier.size(48.dp) // Smaller FAB
                ) {
                    Icon(
                        imageVector = if (uiState.isScanning) Icons.Default.Stop else Icons.Default.PlayArrow,
                        contentDescription = if (uiState.isScanning) "Stop Scanning" else "Start Scanning",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(8.dp) // Reduced padding
        ) {

            // Camera Permission Check
            if (!cameraPermissionState.status.isGranted) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Camera Permission Required",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = "Grant camera permission to use scanner",
                            modifier = Modifier.padding(vertical = 4.dp),
                            fontSize = 12.sp
                        )

                        Button(
                            onClick = { cameraPermissionState.launchPermissionRequest() }
                        ) {
                            Text("Grant Permission", fontSize = 12.sp)
                        }
                    }
                }
            } else {
                // Camera View - Reduced height for small screen
                if (uiState.isScanning) {
                    ScannerView(
                        onBarcodeViewCreated = { /* Configure with repository if needed */ },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp) // Reduced from 300dp
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card {
                            Text(
                                text = "Press play to start scanning",
                                modifier = Modifier.padding(16.dp),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            // Scan Results - More compact
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(1f) // Take remaining space
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Results (${scanResults.size})",
                            style = MaterialTheme.typography.titleMedium
                        )

                        if (uiState.isScanning) {
                            Text(
                                text = "Scanning...",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    if (scanResults.isEmpty()) {
                        Text(
                            text = "No scans yet",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp) // Reduced spacing
                        ) {
                            items(scanResults) { result ->
                                Card(
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                                    )
                                ) {
                                    Column(
                                        modifier = Modifier.padding(8.dp) // Reduced padding
                                    ) {
                                        Text(
                                            text = result.content,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontSize = 12.sp,
                                            maxLines = 2 // Limit lines for compact display
                                        )

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = result.format,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 10.sp
                                            )

                                            Text(
                                                text = SimpleDateFormat(
                                                    "HH:mm:ss",
                                                    Locale.getDefault()
                                                ).format(Date(result.timestamp)),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}