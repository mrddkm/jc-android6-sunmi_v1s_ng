package com.arkhe.sunmi.presentation.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkhe.sunmi.presentation.viewmodel.MainViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceiptTextOnlyScreen(
    onNavigateBack: () -> Unit,
    viewModel: MainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackBarHostState = remember { SnackbarHostState() }

    val spacerDp = 8.dp
    val fontSp = 8.sp

    val textHeader = "Transaction Receipt"
    val textHeaderDate = "11/08/2025, 18:08"
    val textHeaderLocation = "Gaenta S.S. BDG"
    val textHeaderTransactionId = "2-1491879_12773952_1490626"

    val textPayment = "Payment"
    val textPaymentDetails = "0.4 - V-Power"
    val textPaymentAmount = "Rp12.345"
    val textPaymentDetailsLitres = "3.52 Litres x Rp13.050 / Litre"

    val textService = "0.9 - Service"
    val textServiceAmount = "Rp6.789.000"
    val textServiceDetailsCVC = "CVC x Rp900.999"
    val textServiceDetailsHead = "Head x Rp50.889"

    val textTotalPaid = "Total Paid"
    val textTotalPaidAmount = "Rp999.450.936"

    val textPaymentMethod = "Payment Method"
    val textPaymentMethodDetails = "Credit Card"

    val textThankYou = "Thank you for your purchase!"
    val textMoreInfo = "© Gaenta S.S. ― gaenta.id"

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackBarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Receipt Text Only",
                        fontSize = fontSp * 2
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Card {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = textHeader,
                        fontSize = fontSp * 3,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )

                    Text(
                        text = textHeaderDate,
                        fontSize = fontSp * 2
                    )

                    Text(
                        text = textHeaderLocation,
                        fontSize = fontSp * 2,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    Text(
                        text = textHeaderTransactionId,
                        fontSize = fontSp * 2
                    )

                    Spacer(modifier = Modifier.height(spacerDp))
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                    Spacer(modifier = Modifier.height(spacerDp * 2))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = textPayment,
                            fontSize = fontSp * 2,
                            fontWeight = FontWeight.ExtraLight
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = textPaymentDetails,
                            fontSize = fontSp * 2,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = textPaymentAmount,
                            fontSize = fontSp * 2,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = textPaymentDetailsLitres,
                            fontSize = fontSp * 1.5f
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = textService,
                            fontSize = fontSp * 2,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = textServiceAmount,
                            fontSize = fontSp * 2,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = textServiceDetailsCVC,
                            fontSize = fontSp * 1.5f
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Text(
                            text = textServiceDetailsHead,
                            fontSize = fontSp * 1.5f
                        )
                    }
                    Spacer(modifier = Modifier.height(spacerDp))
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                    Spacer(modifier = Modifier.height(spacerDp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = textTotalPaid,
                            fontSize = fontSp * 2,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = textTotalPaidAmount,
                            fontSize = fontSp * 2,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(spacerDp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = textPaymentMethod,
                            fontSize = fontSp * 2,
                            fontWeight = FontWeight.ExtraLight
                        )
                        Text(
                            text = textPaymentMethodDetails,
                            fontSize = fontSp * 2,
                            fontWeight = FontWeight.ExtraLight
                        )
                    }

                    Spacer(modifier = Modifier.height(spacerDp))
                    HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)
                    Spacer(modifier = Modifier.height(spacerDp * 2))

                    Text(
                        text = textThankYou,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    Text(
                        text = textMoreInfo,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(spacerDp * 3))

                Button(
                    onClick = {
                        viewModel.printSampleReceiptTextOnly(
                            textHeader,
                            textHeaderDate,
                            textHeaderLocation,
                            textHeaderTransactionId,
                            textPayment,
                            textPaymentDetails,
                            textPaymentAmount,
                            textPaymentDetailsLitres,
                            textService,
                            textServiceAmount,
                            textServiceDetailsCVC,
                            textServiceDetailsHead,
                            textTotalPaid,
                            textTotalPaidAmount,
                            textPaymentMethod,
                            textPaymentMethodDetails,
                            textThankYou,
                            textMoreInfo
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !uiState.isLoading
                ) {
                    Text("Print", fontSize = 10.sp)
                }
            }
        }
    }
}