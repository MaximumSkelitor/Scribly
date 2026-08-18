package com.weberpackage.scribly.statistics.presentation.contract

import com.weberpackage.scribly.common.presentation.base.ViewEvent
import com.weberpackage.scribly.common.presentation.base.ViewSideEffect
import com.weberpackage.scribly.common.presentation.base.ViewState
import com.weberpackage.scribly.data.Subscription

class StatisticsContract {
    sealed class Event : ViewEvent {
        data object Initialized : Event()
    }

    data class State(
        val monthlyTotal: Double = 0.0,
        val yearlyTotal: Double = 0.0,
        val subscriptions: List<Subscription> = emptyList()
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data object NoEffect : Effect()
    }
}
