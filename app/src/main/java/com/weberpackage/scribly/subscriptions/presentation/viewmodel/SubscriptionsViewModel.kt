package com.weberpackage.scribly.subscriptions.presentation.viewmodel

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import com.weberpackage.scribly.common.presentation.base.BaseViewModel
import com.weberpackage.scribly.data.Subscription
import com.weberpackage.scribly.data.SubscriptionRepository
import com.weberpackage.scribly.subscriptions.presentation.contract.SubscriptionsContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SubscriptionsViewModel @Inject constructor(
    private val repository: SubscriptionRepository
) : BaseViewModel<SubscriptionsContract.Event, SubscriptionsContract.State, SubscriptionsContract.Effect>() {

    init {
        observeSubscriptions()
    }

    override fun setInitialState() = SubscriptionsContract.State()

    override fun handleEvents(event: SubscriptionsContract.Event) {
        when (event) {
            is SubscriptionsContract.Event.OnAddSubscriptionClick -> {
                setEffect { SubscriptionsContract.Effect.NavigateToAddSubscription }
            }
            is SubscriptionsContract.Event.OnEditSubscriptionClick -> {
                setEffect { SubscriptionsContract.Effect.NavigateToEditSubscription(event.id) }
            }
            is SubscriptionsContract.Event.OnDeleteSubscriptionClick -> {
                viewModelScope.launch {
                    repository.delete(event.subscription)
                }
            }
            is SubscriptionsContract.Event.OnSearchQueryChange -> {
                setState { copy(searchQuery = event.query) }
            }
            is SubscriptionsContract.Event.OnSortOptionSelect -> {
                setState { copy(sortOption = event.option) }
            }
            is SubscriptionsContract.Event.OnFilterCategorySelect -> {
                setState { copy(filterCategory = event.category) }
            }
            is SubscriptionsContract.Event.OnFilterBillingCycleSelect -> {
                setState { copy(filterBillingCycle = event.cycle) }
            }
            is SubscriptionsContract.Event.OnFilterFreeTrialToggle -> {
                setState { copy(onlyFreeTrials = event.onlyFreeTrials) }
            }
            SubscriptionsContract.Event.OnSortClick -> {
                setEffect { SubscriptionsContract.Effect.ShowSortSelector }
            }
            SubscriptionsContract.Event.OnFilterClick -> {
                setEffect { SubscriptionsContract.Effect.ShowFilterSelector }
            }
        }
    }

    private fun observeSubscriptions() {
        val stateFlow = snapshotFlow { viewState.value }
            .distinctUntilChanged { old, new ->
                old.searchQuery == new.searchQuery &&
                old.sortOption == new.sortOption &&
                old.filterCategory == new.filterCategory &&
                old.filterBillingCycle == new.filterBillingCycle &&
                old.onlyFreeTrials == new.onlyFreeTrials
            }

        combine(
            repository.allSubscriptions,
            stateFlow
        ) { allSubs, state ->
            val categories = allSubs.map { it.category }.distinct().sorted()
            
            var filtered = allSubs.filter { sub ->
                val matchesSearch = sub.name.contains(state.searchQuery, ignoreCase = true)
                val matchesCategory = state.filterCategory == null || sub.category == state.filterCategory
                val matchesCycle = state.filterBillingCycle == null || sub.billingCycle == state.filterBillingCycle
                val matchesTrial = !state.onlyFreeTrials || sub.isFreeTrial
                
                matchesSearch && matchesCategory && matchesCycle && matchesTrial
            }

            filtered = when (state.sortOption) {
                SubscriptionsContract.SortOption.NAME_ASC -> filtered.sortedBy { it.name.lowercase() }
                SubscriptionsContract.SortOption.NAME_DESC -> filtered.sortedByDescending { it.name.lowercase() }
                SubscriptionsContract.SortOption.PRICE_ASC -> filtered.sortedBy { it.price }
                SubscriptionsContract.SortOption.PRICE_DESC -> filtered.sortedByDescending { it.price }
                SubscriptionsContract.SortOption.DATE_ASC -> filtered.sortedBy { it.nextPaymentDate }
                SubscriptionsContract.SortOption.DATE_DESC -> filtered.sortedByDescending { it.nextPaymentDate }
            }

            Triple(filtered, categories, state.filterCategory)
        }.onEach { (filtered, categories, currentCategory) ->
            setState { 
                copy(
                    subscriptions = filtered,
                    availableCategories = categories,
                    filterCategory = if (currentCategory != null && !categories.contains(currentCategory)) null else currentCategory
                ) 
            }
        }.launchIn(viewModelScope)
    }
}
