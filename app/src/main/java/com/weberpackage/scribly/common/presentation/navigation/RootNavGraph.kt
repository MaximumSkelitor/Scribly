package com.weberpackage.scribly.common.presentation.navigation

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.weberpackage.scribly.add_edit.presentation.screen.AddEditSubscriptionScreen
import com.weberpackage.scribly.add_edit.presentation.viewmodel.AddEditViewModel
import com.weberpackage.scribly.home_pager.presentation.screens.HomePagerScreen
import com.weberpackage.scribly.notification_center.presentation.screen.NotificationCenterScreen
import com.weberpackage.scribly.notification_center.presentation.viewmodel.NotificationCenterViewModel

@Composable
fun RootNavGraph(
    navController: NavHostController = rememberNavController(),
    startWithAddScreen: Boolean = false
) {
    LaunchedEffect(startWithAddScreen) {
        if (startWithAddScreen) {
            navController.navigate(NavRoutes.AddEditDest(0L))
        }
    }

    Surface(
        modifier = androidx.compose.ui.Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        NavHost(
            navController = navController,
            startDestination = NavRoutes.HomeDest,
            enterTransition = { fadeIn() },
            exitTransition = { fadeOut() },
            popEnterTransition = { fadeIn() },
            popExitTransition = { fadeOut() }
        ) {
            composable<NavRoutes.HomeDest> {
                HomePagerScreen(
                    onNavigateToAddSubscription = {
                        navController.navigate(NavRoutes.AddEditDest(0L))
                    },
                    onNavigateToEditSubscription = { id ->
                        navController.navigate(NavRoutes.AddEditDest(id))
                    },
                    onNavigateToNotificationCenter = {
                        navController.navigate(NavRoutes.NotificationCenterDest)
                    }
                )
            }
            composable<NavRoutes.AddEditDest> {
                val viewModel: AddEditViewModel = hiltViewModel()
                AddEditSubscriptionScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable<NavRoutes.NotificationCenterDest> {
                val viewModel: NotificationCenterViewModel = hiltViewModel()
                NotificationCenterScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
