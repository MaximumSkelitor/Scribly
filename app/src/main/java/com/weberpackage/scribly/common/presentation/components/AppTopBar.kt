package com.weberpackage.scribly.common.presentation.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.weberpackage.scribly.common.presentation.base.hazeAppBarStyle
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HazeAppBar(
    title: @Composable () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior,
    hazeState: HazeState,
    style: HazeStyle = hazeAppBarStyle(),
    showNavigationIcon: Boolean = true,
    navigationIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    navigationIconDescription: String? = null,
    actions: @Composable RowScope.() -> Unit = {},
    onNavigate: (() -> Unit)? = null,
) {
    TopAppBar(
        title = title,
        scrollBehavior = scrollBehavior,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            scrolledContainerColor = Color.Transparent,
        ),
        modifier = Modifier
            .hazeEffect(
                state = hazeState,
                style = style
            )
            .fillMaxWidth(),
        actions = actions,
        navigationIcon = {
            if (onNavigate != null && showNavigationIcon) {
                IconButton(onClick = onNavigate) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconDescription
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardAppBar(
    title: @Composable () -> Unit = {},
    showNavigationIcon: Boolean = true,
    navigationIcon: ImageVector = Icons.AutoMirrored.Filled.ArrowBack,
    navigationIconDescription: String? = null,
    onNavigate: (() -> Unit)? = null,
) {
    TopAppBar(
        title = title,
        modifier = Modifier.fillMaxWidth(),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent
        ),
        navigationIcon = {
            if (onNavigate != null && showNavigationIcon) {
                IconButton(onClick = onNavigate) {
                    Icon(
                        imageVector = navigationIcon,
                        contentDescription = navigationIconDescription
                    )
                }
            }
        }
    )
}
