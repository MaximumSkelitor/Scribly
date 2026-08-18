package com.weberpackage.scribly.add_edit.presentation.contract

import com.weberpackage.scribly.common.presentation.base.ViewEvent
import com.weberpackage.scribly.common.presentation.base.ViewSideEffect
import com.weberpackage.scribly.common.presentation.base.ViewState

class AddEditContract {
    sealed class Event : ViewEvent {
        data class OnNameChange(val name: String) : Event()
        data class OnPriceChange(val price: String) : Event()
        data class OnBillingCycleChange(val cycle: String) : Event()
        data class OnCategoryChange(val category: String) : Event()
        data class OnPaymentDateChange(val paymentDate: String) : Event()
        data class OnStartDateChange(val startDate: String) : Event()
        data class OnIconChange(val icon: String) : Event()
        data class OnNotesChange(val notes: String) : Event()
        data class OnFreeTrialToggle(val isFreeTrial: Boolean) : Event()
        data class OnTrialDaysConfirm(val days: Int) : Event()
        data class OnCalendarSyncToggle(val enabled: Boolean) : Event()
        data class OnSharingCountChange(val count: String) : Event()
        data class OnCurrencyChange(val currencyCode: String) : Event()
        data object OnCategoryClick : Event()
        data object OnBillingCycleClick : Event()
        data object OnEmojiClick : Event()
        data object OnCurrencyClick : Event()
        data object OnSaveClick : Event()
        data object OnDeleteClick : Event()
        data object OnBackClick : Event()
    }

    data class State(
        val subId: Long = 0L,
        val name: String = "",
        val price: String = "",
        val billingCycle: String = "Monthly",
        val category: String = "Entertainment",
        val paymentDate: String = "",
        val startDate: String = "",
        val icon: String = "💳",
        val trialDays: Int = 7,
        val isPaymentDateInvalid: Boolean = false,
        val notes: String = "",
        val isFreeTrial: Boolean = false,
        val isCalendarSyncEnabled: Boolean = false,
        val sharingCount: String = "1",
        val currencyCode: String = "USD",
        val totalSpent: Double = 0.0,
        val isEditMode: Boolean = false
    ) : ViewState

    sealed class Effect : ViewSideEffect {
        data object NavigateBack : Effect()
        data object ShowCategorySelector : Effect()
        data object ShowBillingCycleSelector : Effect()
        data object ShowEmojiSelector : Effect()
        data object ShowTrialDurationPicker : Effect()
        data object ShowCurrencySelector : Effect()
        data class AddToCalendar(
            val name: String,
            val price: Double,
            val date: Long,
            val notes: String?
        ) : Effect()
    }
}
