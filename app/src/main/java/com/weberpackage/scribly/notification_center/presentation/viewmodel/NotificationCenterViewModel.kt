package com.weberpackage.scribly.notification_center.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.weberpackage.scribly.common.presentation.base.BaseViewModel
import com.weberpackage.scribly.core.prefs.Pref
import com.weberpackage.scribly.core.prefs.Prefs
import com.weberpackage.scribly.notification_center.presentation.contract.NotificationCenterContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationCenterViewModel @Inject constructor(
    private val prefs: Prefs
) : BaseViewModel<NotificationCenterContract.Event, NotificationCenterContract.State, NotificationCenterContract.Effect>() {

    init {
        observePrefs()
    }

    override fun setInitialState() = NotificationCenterContract.State()

    override fun handleEvents(event: NotificationCenterContract.Event) {
        when (event) {
            is NotificationCenterContract.Event.OnUpcomingPaymentsToggle -> prefs.set(Pref.UpcomingPaymentsEnabled, event.enabled)
            is NotificationCenterContract.Event.OnSubscriptionEndingToggle -> prefs.set(Pref.SubscriptionEndingEnabled, event.enabled)
            is NotificationCenterContract.Event.OnFreeTrialEndingToggle -> prefs.set(Pref.FreeTrialEndingEnabled, event.enabled)
            is NotificationCenterContract.Event.OnFreeTrialEndedToggle -> prefs.set(Pref.FreeTrialEndedEnabled, event.enabled)
            is NotificationCenterContract.Event.OnWeeklyDigestToggle -> prefs.set(Pref.WeeklyDigestEnabled, event.enabled)
            is NotificationCenterContract.Event.OnCalendarSyncToggle -> prefs.set(Pref.CalendarSyncEnabled, event.enabled)
            NotificationCenterContract.Event.OnBackClick -> setEffect { NotificationCenterContract.Effect.NavigateBack }
        }
    }

    private fun observePrefs() {
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.UpcomingPaymentsEnabled).collect { enabled ->
                setState { copy(upcomingPaymentsEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.SubscriptionEndingEnabled).collect { enabled ->
                setState { copy(subscriptionEndingEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.FreeTrialEndingEnabled).collect { enabled ->
                setState { copy(freeTrialEndingEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.FreeTrialEndedEnabled).collect { enabled ->
                setState { copy(freeTrialEndedEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.WeeklyDigestEnabled).collect { enabled ->
                setState { copy(weeklyDigestEnabled = enabled) }
            }
        }
        viewModelScope.launch {
            prefs.collectPrefsFlow(Pref.CalendarSyncEnabled).collect { enabled ->
                setState { copy(calendarSyncEnabled = enabled) }
            }
        }
    }
}
