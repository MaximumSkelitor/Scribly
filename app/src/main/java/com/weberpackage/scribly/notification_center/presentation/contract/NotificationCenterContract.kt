package com.weberpackage.scribly.notification_center.presentation.contract

import com.weberpackage.scribly.common.presentation.base.ViewEvent
import com.weberpackage.scribly.common.presentation.base.ViewSideEffect
import com.weberpackage.scribly.common.presentation.base.ViewState

class NotificationCenterContract {
    sealed class Event : ViewEvent {
        data class OnUpcomingPaymentsToggle(val enabled: Boolean) : Event()
        data class OnSubscriptionEndingToggle(val enabled: Boolean) : Event()
        data class OnFreeTrialEndingToggle(val enabled: Boolean) : Event()
        data class OnFreeTrialEndedToggle(val enabled: Boolean) : Event()
        data class OnWeeklyDigestToggle(val enabled: Boolean) : Event()
        data class OnCalendarSyncToggle(val enabled: Boolean) : Event()
        data object OnBackClick : Event()
    }

    data class State(
        val upcomingPaymentsEnabled: Boolean = true,
        val subscriptionEndingEnabled: Boolean = true,
        val freeTrialEndingEnabled: Boolean = true,
        val freeTrialEndedEnabled: Boolean = true,
        val weeklyDigestEnabled: Boolean = false,
        val calendarSyncEnabled: Boolean = false
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
    }
}
