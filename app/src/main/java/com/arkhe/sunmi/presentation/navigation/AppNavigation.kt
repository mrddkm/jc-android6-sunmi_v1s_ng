package com.arkhe.sunmi.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.arkhe.sunmi.presentation.screens.HomeScreen
import com.arkhe.sunmi.presentation.screens.PrintScreen
import com.arkhe.sunmi.presentation.screens.ScanScreen
import com.arkhe.sunmi.presentation.screens.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavigationDestinations.HOME
    ) {
        composable(NavigationDestinations.HOME) {
            HomeScreen(
                onNavigateToPrint = { navController.navigate(NavigationDestinations.PRINT) },
                onNavigateToScan = { navController.navigate(NavigationDestinations.SCAN) },
                onNavigateToSettings = { navController.navigate(NavigationDestinations.SETTINGS) }
            )
        }

        composable(NavigationDestinations.PRINT) {
            PrintScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavigationDestinations.SCAN) {
            ScanScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(NavigationDestinations.SETTINGS) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}