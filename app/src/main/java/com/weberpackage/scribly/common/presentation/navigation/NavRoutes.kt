package com.weberpackage.scribly.common.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.weberpackage.scribly.R
import kotlinx.serialization.Serializable

@Serializable
sealed interface NavRoutes {

    @Serializable
    data object HomeGraph : NavRoutes

    @Serializable
    data object HomeDest : NavRoutes

    @Serializable
    data object SubscriptionsDest : NavRoutes

    @Serializable
    data object StatisticsDest : NavRoutes

    @Serializable
    data object SettingsDest : NavRoutes

    @Serializable
    data object NotificationCenterDest : NavRoutes

    @Serializable
    data class AddEditDest(val subId: Long = 0L) : NavRoutes
}

data class BottomNavItem(
    val route: Any,
    val title: String,
    val icon: ImageVector,
    val topBarTitle: String? = null,
    val topBarDesc: String? = null,
)

@Composable
fun buildBottomNavItems(): List<BottomNavItem> {
    return listOf(
        BottomNavItem(
            NavRoutes.HomeDest,
            stringResource(R.string.home),
            Icons.Default.Home,
        ),
        BottomNavItem(
            NavRoutes.SubscriptionsDest,
            stringResource(R.string.list),
            Icons.AutoMirrored.Filled.List,
            stringResource(R.string.subscriptions_title),
            stringResource(R.string.subscriptions_count)
        ),
        BottomNavItem(
            NavRoutes.StatisticsDest,
            stringResource(R.string.stats),
            Icons.Default.BarChart,
            stringResource(R.string.statistics)
        ),
        BottomNavItem(
            NavRoutes.SettingsDest,
            stringResource(R.string.profile),
            Icons.Default.Person,
        )
    )
}
