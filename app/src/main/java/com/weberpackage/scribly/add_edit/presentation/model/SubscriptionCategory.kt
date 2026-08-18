package com.weberpackage.scribly.add_edit.presentation.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Style
import androidx.compose.ui.graphics.vector.ImageVector

data class SubscriptionCategory(
    val name: String,
    val icon: ImageVector
)

val subscriptionCategories = listOf(
    SubscriptionCategory("Entertainment", Icons.Default.Movie),
    SubscriptionCategory("Music & Audio", Icons.Default.MusicNote),
    SubscriptionCategory("Gaming", Icons.Default.Gamepad),
    SubscriptionCategory("Productivity", Icons.Default.QueryStats),
    SubscriptionCategory("Software", Icons.Default.Code),
    SubscriptionCategory("Cloud Storage", Icons.Default.Cloud),
    SubscriptionCategory("News & Reading", Icons.AutoMirrored.Filled.MenuBook),
    SubscriptionCategory("Education", Icons.Default.School),
    SubscriptionCategory("Health & Fitness", Icons.Default.FitnessCenter),
    SubscriptionCategory("Shopping", Icons.Default.ShoppingBag),
    SubscriptionCategory("Food & Drink", Icons.Default.Restaurant),
    SubscriptionCategory("Lifestyle", Icons.Default.Style),
    SubscriptionCategory("Utilities", Icons.Default.Home),
    SubscriptionCategory("Business", Icons.Default.BusinessCenter),
    SubscriptionCategory("Finance", Icons.Default.Security),
    SubscriptionCategory("Transportation", Icons.Default.DirectionsCar),
    SubscriptionCategory("Health", Icons.Default.LocalHospital),
    SubscriptionCategory("Other", Icons.Default.Style),
)

fun getCategoryIcon(name: String): ImageVector {
    return subscriptionCategories.find { it.name == name }?.icon ?: Icons.Default.Style
}
