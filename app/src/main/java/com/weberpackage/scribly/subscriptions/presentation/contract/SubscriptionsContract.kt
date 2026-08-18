package com.weberpackage.scribly.subscriptions.presentation.contract

import com.weberpackage.scribly.common.presentation.base.ViewEvent
import com.weberpackage.scribly.common.presentation.base.ViewSideEffect
import com.weberpackage.scribly.common.presentation.base.ViewState
import com.weberpackage.scribly.data.Subscription

class SubscriptionsContract {
    sealed class Event : ViewEvent {
        data object OnAddSubscriptionClick : Event()
        data class OnEditSubscriptionClick(val id: Long) : Event()
        data class OnDeleteSubscriptionClick(val subscription: Subscription) : Event()
        data class OnSearchQueryChange(val query: String) : Event()
        data class OnSortOptionSelect(val option: SortOption) : Event()
        data class OnFilterCategorySelect(val category: String?) : Event()
        data class OnFilterBillingCycleSelect(val cycle: String?) : Event()
        data class OnFilterFreeTrialToggle(val onlyFreeTrials: Boolean) : Event()
        data object OnSortClick : Event()
        data object OnFilterClick : Event()
    }

    data class State(
        val subscriptions: List<Subscription> = emptyList(),
        val searchQuery: String = "",
        val sortOption: SortOption = SortOption.DATE_ASC,
        val filterCategory: String? = null,
        val filterBillingCycle: String? = null,
        val onlyFreeTrials: Boolean = false,
        val availableCategories: List<String> = emptyList()
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data object NavigateToAddSubscription : Effect()
        data class NavigateToEditSubscription(val id: Long) : Effect()
        data object ShowSortSelector : Effect()
        data object ShowFilterSelector : Effect()
    }

    enum class SortOption {
        NAME_ASC, NAME_DESC,
        PRICE_ASC, PRICE_DESC,
        DATE_ASC, DATE_DESC
    }
}
