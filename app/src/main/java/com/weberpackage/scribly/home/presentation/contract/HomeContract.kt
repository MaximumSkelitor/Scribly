package com.weberpackage.scribly.home.presentation.contract

import com.weberpackage.scribly.common.presentation.base.ViewEvent
import com.weberpackage.scribly.common.presentation.base.ViewSideEffect
import com.weberpackage.scribly.common.presentation.base.ViewState
import com.weberpackage.scribly.data.Subscription

class HomeContract {
    sealed class Event : ViewEvent {
        data object OnAddSubscriptionClick : Event()
        data class OnSubscriptionClick(val id: Long) : Event()
    }

    data class State(
        val subscriptions: List<Subscription> = emptyList(),
        val monthlyTotal: Double = 0.0,
        val yearlyTotal: Double = 0.0,
        val upcoming: List<Subscription> = emptyList()
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data object NavigateToAddSubscription : Effect()
        data class NavigateToEditSubscription(val id: Long) : Effect()
    }
}
