package com.weberpackage.scribly.home_pager.presentation.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.weberpackage.scribly.common.presentation.navigation.buildBottomNavItems
import com.weberpackage.scribly.home.presentation.viewmodel.HomeViewModel
import com.weberpackage.scribly.home_pager.presentation.component.ScriblyHazeTopAppBar
import com.weberpackage.scribly.settings.presentation.screen.SettingsScreen
import com.weberpackage.scribly.settings.presentation.viewmodel.SettingsViewModel
import com.weberpackage.scribly.statistics.presentation.screen.StatisticsScreen
import com.weberpackage.scribly.statistics.presentation.viewmodel.StatisticsViewModel
import com.weberpackage.scribly.subscriptions.presentation.screen.SubscriptionsScreen
import com.weberpackage.scribly.subscriptions.presentation.viewmodel.SubscriptionsViewModel
import dev.chrisbanes.haze.hazeSource
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.launch
import com.weberpackage.scribly.home.presentation.screen.HomeScreen as DashboardScreen

@Composable
fun HomePagerScreen(
    onNavigateToAddSubscription: () -> Unit,
    onNavigateToEditSubscription: (Long) -> Unit,
    onNavigateToNotificationCenter: () -> Unit
) {
    val bottomNavItems = buildBottomNavItems()
    val pagerState = rememberPagerState(pageCount = { bottomNavItems.size })
    val coroutineScope = rememberCoroutineScope()
    val hazeState = rememberHazeState()
    val homeViewModel: HomeViewModel = hiltViewModel()
    val subscriptionsViewModel: SubscriptionsViewModel = hiltViewModel()
    val statisticsViewModel: StatisticsViewModel = hiltViewModel()
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val selectedPage = pagerState.currentPage
    val selectedItem = bottomNavItems[selectedPage]

    Scaffold(
        topBar = {
            ScriblyHazeTopAppBar(
                title = selectedItem.title,
                subtitle = null,
                hazeState = hazeState
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.fillMaxWidth(),
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp
            ) {
                bottomNavItems.take(2).forEachIndexed { index, item ->
                    PagerNavigationItem(
                        title = item.title,
                        icon = item.icon,
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        }
                    )
                }

                // Center "nav item" slot for the FAB
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(80.dp),
                    contentAlignment = Center
                ) {
                    FloatingActionButton(
                        onClick = onNavigateToAddSubscription,
                        modifier = Modifier.size(56.dp), // Slightly larger as in the image
                        shape = CircleShape,
                        containerColor = MaterialTheme.colorScheme.primary, // Blue color from theme
                        contentColor = Color.White,
                        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 0.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add subscription")
                    }
                }

                bottomNavItems.drop(2).forEachIndexed { offset, item ->
                    val index = offset + 2
                    PagerNavigationItem(
                        title = item.title,
                        icon = item.icon,
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                        }
                    )
                }
            }
        }
    ) { contentPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .hazeSource(state = hazeState, zIndex = 0f)
        ) { page ->
            when (page) {
                0 -> DashboardScreen(
                    viewModel = homeViewModel,
                    hazeState = hazeState,
                    contentPadding = contentPadding,
                    onNavigateToAddSubscription = onNavigateToAddSubscription,
                    onNavigateToEditSubscription = onNavigateToEditSubscription
                )

                1 -> SubscriptionsScreen(
                    viewModel = subscriptionsViewModel,
                    contentPadding = contentPadding,
                    onNavigateToAddSubscription = onNavigateToAddSubscription,
                    onNavigateToEditSubscription = onNavigateToEditSubscription
                )

                2 -> StatisticsScreen(
                    viewModel = statisticsViewModel,
                    contentPadding = contentPadding
                )

                3 -> SettingsScreen(
                    viewModel = settingsViewModel,
                    contentPadding = contentPadding,
                    onNavigateToNotificationCenter = onNavigateToNotificationCenter
                )
            }
        }
    }
}

@Composable
private fun RowScope.PagerNavigationItem(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = title) },
        label = { Text(title) }
    )
}
