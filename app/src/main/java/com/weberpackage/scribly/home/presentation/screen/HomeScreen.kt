package com.weberpackage.scribly.home.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.weberpackage.scribly.R
import com.weberpackage.scribly.common.presentation.components.SectionHeader
import com.weberpackage.scribly.common.presentation.utils.ObserveAsEvents
import com.weberpackage.scribly.home.presentation.component.CategoryFolder
import com.weberpackage.scribly.home.presentation.component.GradientCard
import com.weberpackage.scribly.home.presentation.component.SubscriptionItem
import com.weberpackage.scribly.home.presentation.contract.HomeContract
import com.weberpackage.scribly.home.presentation.viewmodel.HomeViewModel
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.materials.ExperimentalHazeMaterialsApi
import java.util.Locale

@OptIn(ExperimentalHazeMaterialsApi::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    hazeState: HazeState,
    contentPadding: PaddingValues,
    onNavigateToAddSubscription: () -> Unit,
    onNavigateToEditSubscription: (Long) -> Unit
) {
    val state = viewModel.viewState.value

    ObserveAsEvents(flow = viewModel.effect) { effect ->
        when (effect) {
            is HomeContract.Effect.NavigateToAddSubscription -> onNavigateToAddSubscription()
            is HomeContract.Effect.NavigateToEditSubscription -> onNavigateToEditSubscription(effect.id)
        }
    }

    val groupedSubscriptions = remember(state.subscriptions) {
        state.subscriptions.groupBy { it.category }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = contentPadding.calculateTopPadding() + 24.dp,
            end = 20.dp,
            bottom = contentPadding.calculateBottomPadding() + 80.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GradientCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.monthly),
                    amount = stringResource(R.string.price_format, String.format(Locale.US, "%.2f", state.monthlyTotal)),
                    subtitle = stringResource(R.string.total_spent)
                )
                GradientCard(
                    modifier = Modifier.weight(1f),
                    title = stringResource(R.string.yearly),
                    amount = stringResource(R.string.price_format, String.format(Locale.US, "%.2f", state.yearlyTotal)),
                    subtitle = stringResource(R.string.projected)
                )
            }
        }

        item {
            SectionHeader(title = stringResource(R.string.upcoming))
        }

        if (state.upcoming.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.no_upcoming_renewals),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        } else {
            items(state.upcoming) { sub ->
                SubscriptionItem(sub) {
                    viewModel.setEvent(HomeContract.Event.OnSubscriptionClick(sub.id))
                }
            }
        }

        item {
            SectionHeader(title = stringResource(R.string.category_folders))
        }

        // Display folders in a Grid (2 columns)
        item {
            val sortedCategories = groupedSubscriptions.keys.toList()
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                sortedCategories.chunked(2).forEach { rowCategories ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        rowCategories.forEach { category ->
                            CategoryFolder(
                                modifier = Modifier.weight(1f),
                                categoryName = category,
                                subscriptions = groupedSubscriptions[category] ?: emptyList(),
                                hazeState = hazeState,
                                onSubscriptionClick = { id ->
                                    viewModel.setEvent(HomeContract.Event.OnSubscriptionClick(id))
                                }
                            )
                        }
                        if (rowCategories.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

object ScriblyCardDefaults {
    @Composable
    fun border() = androidx.compose.foundation.BorderStroke(
        width = 1.dp,
        color = Color.White.copy(alpha = 0.1f)
    )
}
