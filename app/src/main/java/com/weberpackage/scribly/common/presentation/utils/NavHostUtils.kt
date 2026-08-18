package com.weberpackage.scribly.common.presentation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavOptionsBuilder

fun <T> NavHostController.navBackWithResult(key: String, arg: T) {
    this.previousBackStackEntry?.savedStateHandle?.set(key, arg)
    this.popBackStack()
}

fun <T: Any> NavHostController.getBackStackArg(key: String): T? {
    return this.currentBackStackEntry?.savedStateHandle?.get(key)
}

fun <T: Any> NavHostController.clearBackStackArg(key: String): T? {
    return this.currentBackStackEntry?.savedStateHandle?.remove(key)
}

fun NavHostController.safeNavigate(route: Any, builder: NavOptionsBuilder.() -> Unit) {
    if (this.currentBackStackEntry?.lifecycleIsResumed() == true) {
        this.navigate(route, builder)
    }
}

fun NavHostController.safeNavigate(
    route: Any,
    popUpToRoute: Any? = null,
    inclusive: Boolean = true
) {
    if (this.currentBackStackEntry?.lifecycleIsResumed() == true) {
        this.navigate(route) {
            popUpToRoute?.let {
                popUpTo(it) {
                    this.inclusive = inclusive
                }
            }
        }
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED


@Composable
inline fun <reified T : ViewModel> NavBackStackEntry.sharedViewModel(
    navController: NavController
): T {
    val navGraphRoute = destination.parent?.route ?: return hiltViewModel()
    val parentEntry = remember(this) {
        navController.getBackStackEntry(navGraphRoute)
    }
    return hiltViewModel(parentEntry)
}
