package com.weberpackage.scribly.subscriptions.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.weberpackage.scribly.R
import com.weberpackage.scribly.add_edit.presentation.component.supportedCurrencies
import com.weberpackage.scribly.add_edit.presentation.model.getCategoryIcon
import com.weberpackage.scribly.common.presentation.components.ScriblyCard
import com.weberpackage.scribly.common.presentation.utils.ObserveAsEvents
import com.weberpackage.scribly.common.presentation.utils.calculateLifetimeSpent
import com.weberpackage.scribly.subscriptions.presentation.contract.SubscriptionsContract
import com.weberpackage.scribly.subscriptions.presentation.viewmodel.SubscriptionsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionsScreen(
    viewModel: SubscriptionsViewModel,
    contentPadding: PaddingValues,
    onNavigateToAddSubscription: () -> Unit,
    onNavigateToEditSubscription: (Long) -> Unit
) {
    val state = viewModel.viewState.value
    var showSortSheet by remember { mutableStateOf(false) }
    var showFilterSheet by remember { mutableStateOf(false) }

    ObserveAsEvents(flow = viewModel.effect) { effect ->
        when (effect) {
            is SubscriptionsContract.Effect.NavigateToAddSubscription -> onNavigateToAddSubscription()
            is SubscriptionsContract.Effect.NavigateToEditSubscription -> onNavigateToEditSubscription(effect.id)
            SubscriptionsContract.Effect.ShowSortSelector -> showSortSheet = true
            SubscriptionsContract.Effect.ShowFilterSelector -> showFilterSheet = true
        }
    }

    if (showSortSheet) {
        SortSelectorSheet(
            selectedOption = state.sortOption,
            onOptionSelected = {
                viewModel.setEvent(SubscriptionsContract.Event.OnSortOptionSelect(it))
                showSortSheet = false
            },
            onDismiss = { showSortSheet = false }
        )
    }

    if (showFilterSheet) {
        FilterSelectorSheet(
            selectedCategory = state.filterCategory,
            selectedCycle = state.filterBillingCycle,
            onlyFreeTrials = state.onlyFreeTrials,
            availableCategories = state.availableCategories,
            onCategorySelected = { viewModel.setEvent(SubscriptionsContract.Event.OnFilterCategorySelect(it)) },
            onCycleSelected = { viewModel.setEvent(SubscriptionsContract.Event.OnFilterBillingCycleSelect(it)) },
            onFreeTrialToggle = { viewModel.setEvent(SubscriptionsContract.Event.OnFilterFreeTrialToggle(it)) },
            onDismiss = { showFilterSheet = false }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 20.dp,
            top = contentPadding.calculateTopPadding() + 16.dp,
            end = 20.dp,
            bottom = contentPadding.calculateBottomPadding() + 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.subscriptions_title),
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = stringResource(R.string.subscriptions_count, state.subscriptions.size),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                }
                Row {
                    IconButton(onClick = { viewModel.setEvent(SubscriptionsContract.Event.OnFilterClick) }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = stringResource(R.string.filter_by_category),
                            tint = if (state.filterCategory != null || state.filterBillingCycle != null || state.onlyFreeTrials) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        item {
            TextField(
                value = state.searchQuery,
                onValueChange = { viewModel.setEvent(SubscriptionsContract.Event.OnSearchQueryChange(it)) },
                placeholder = { Text(stringResource(R.string.search_placeholder)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setEvent(SubscriptionsContract.Event.OnSearchQueryChange("")) }) {
                            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.clear))
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
        }

        if (state.subscriptions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val message = when {
                        state.searchQuery.isNotEmpty() -> "No results for \"${state.searchQuery}\""
                        state.filterCategory != null -> "No subscriptions in ${state.filterCategory}"
                        state.onlyFreeTrials -> "No active Free Trials found"
                        else -> stringResource(R.string.no_upcoming_renewals)
                    }
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            items(state.subscriptions, key = { it.id }) { sub ->
                SubscriptionListItem(
                    subscription = sub,
                    onEdit = { viewModel.setEvent(SubscriptionsContract.Event.OnEditSubscriptionClick(sub.id)) },
                    onDelete = { viewModel.setEvent(SubscriptionsContract.Event.OnDeleteSubscriptionClick(sub)) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(100.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortSelectorSheet(
    selectedOption: SubscriptionsContract.SortOption,
    onOptionSelected: (SubscriptionsContract.SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            Text(
                text = stringResource(R.string.sort_by),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(24.dp)
            )
            
            val options = listOf(
                SubscriptionsContract.SortOption.DATE_ASC to stringResource(R.string.sort_date_soon),
                SubscriptionsContract.SortOption.DATE_DESC to stringResource(R.string.sort_date_far),
                SubscriptionsContract.SortOption.NAME_ASC to stringResource(R.string.sort_name_az),
                SubscriptionsContract.SortOption.NAME_DESC to stringResource(R.string.sort_name_za),
                SubscriptionsContract.SortOption.PRICE_ASC to stringResource(R.string.sort_price_low),
                SubscriptionsContract.SortOption.PRICE_DESC to stringResource(R.string.sort_price_high),
            )
            
            options.forEach { (option, label) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOptionSelected(option) }
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = option == selectedOption,
                        onClick = null
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = label, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSelectorSheet(
    selectedCategory: String?,
    selectedCycle: String?,
    onlyFreeTrials: Boolean,
    availableCategories: List<String>,
    onCategorySelected: (String?) -> Unit,
    onCycleSelected: (String?) -> Unit,
    onFreeTrialToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(bottom = 32.dp)) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
            )
            
            // Trial Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show only Free Trials", style = MaterialTheme.typography.bodyLarge)
                Switch(checked = onlyFreeTrials, onCheckedChange = onFreeTrialToggle)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

            // Billing Cycle Filters
            Text(
                text = "Billing Cycle",
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )
            
            val cycles = listOf(
                null to "All",
                "Weekly" to "Weekly",
                "Monthly" to "Monthly",
                "Yearly" to "Yearly"
            )
            
            Row(
                modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                cycles.forEach { (cycle, label) ->
                    FilterChip(
                        selected = selectedCycle == cycle,
                        onClick = { onCycleSelected(cycle) },
                        label = { Text(label) }
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

            // Category Filters
            Text(
                text = stringResource(R.string.filter_by_category),
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.primary
            )
            
            LazyColumn(modifier = Modifier.heightIn(max = 250.dp)) {
                item {
                    CategoryFilterItem(label = stringResource(R.string.all_categories), selected = selectedCategory == null) {
                        onCategorySelected(null)
                    }
                }
                items(availableCategories) { category ->
                    CategoryFilterItem(label = category, selected = selectedCategory == category) {
                        onCategorySelected(category)
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryFilterItem(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SubscriptionListItem(
    subscription: com.weberpackage.scribly.data.Subscription,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd", LocalLocale.current.platformLocale)
    val dateStr = dateFormat.format(Date(subscription.nextPaymentDate))
    val lifetimeSpent = calculateLifetimeSpent(subscription)
    val currencySymbol = supportedCurrencies.find { it.code == subscription.currencyCode }?.symbol ?: "$"

    ScriblyCard(
        onClick = onEdit,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                if (subscription.icon.isNotBlank()) {
                    Text(text = subscription.icon, fontSize = 20.sp)
                } else {
                    Icon(
                        imageVector = getCategoryIcon(subscription.category),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = subscription.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    if (subscription.isFreeTrial) {
                        Spacer(Modifier.width(8.dp))
                        Surface(
                            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.free_trial),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
                Text(
                    text = stringResource(R.string.category_date_separator, subscription.category, dateStr),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                
                if (lifetimeSpent > 0) {
                    Text(
                        text = "Total spent: ${stringResource(R.string.price_format_simple, currencySymbol, String.format(Locale.US, "%.2f", lifetimeSpent))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                val displayPrice = if (subscription.sharingCount > 1) {
                    subscription.price / subscription.sharingCount
                } else {
                    subscription.price
                }
                
                Text(
                    text = stringResource(R.string.price_format_simple, currencySymbol, String.format(Locale.US, "%.2f", displayPrice)),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = stringResource(R.string.cycle_format, subscription.billingCycle.take(2).lowercase()),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.4f)
                )
            }
        }
    }
}
