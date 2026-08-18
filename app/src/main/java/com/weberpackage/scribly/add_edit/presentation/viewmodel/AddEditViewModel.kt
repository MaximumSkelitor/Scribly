package com.weberpackage.scribly.add_edit.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.weberpackage.scribly.add_edit.presentation.contract.AddEditContract
import com.weberpackage.scribly.common.presentation.base.BaseViewModel
import com.weberpackage.scribly.common.presentation.navigation.NavRoutes
import com.weberpackage.scribly.core.prefs.Pref
import com.weberpackage.scribly.core.prefs.Prefs
import com.weberpackage.scribly.data.Subscription
import com.weberpackage.scribly.data.SubscriptionRepository
import com.weberpackage.scribly.widget.utils.WidgetUpdater
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

@HiltViewModel
class AddEditViewModel @Inject constructor(
    private val repository: SubscriptionRepository,
    private val prefs: Prefs,
    private val widgetUpdater: WidgetUpdater,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<AddEditContract.Event, AddEditContract.State, AddEditContract.Effect>() {

    private val navArgs = savedStateHandle.toRoute<NavRoutes.AddEditDest>()

    init {
        val subId = navArgs.subId
        if (subId != 0L) {
            loadSubscription(subId)
        } else {
            setState { 
                copy(
                    startDate = dateFormater.format(Date()),
                    paymentDate = dateFormater.format(Date(System.currentTimeMillis() + THIRTY_DAYS_MS)),
                    isCalendarSyncEnabled = prefs.get(Pref.CalendarSyncEnabled)
                )
            }
        }
    }

    override fun setInitialState() = AddEditContract.State(subId = navArgs.subId)

    override fun handleEvents(event: AddEditContract.Event) {
        when (event) {
            is AddEditContract.Event.OnNameChange -> setState { copy(name = event.name) }
            is AddEditContract.Event.OnPriceChange -> {
                setState { copy(price = event.price) }
                updateLifetimeSpent()
            }
            is AddEditContract.Event.OnBillingCycleChange -> {
                setState { copy(billingCycle = event.cycle) }
                updateLifetimeSpent()
            }
            is AddEditContract.Event.OnCategoryChange -> setState { copy(category = event.category) }
            is AddEditContract.Event.OnPaymentDateChange -> setState {
                copy(paymentDate = event.paymentDate, isPaymentDateInvalid = false)
            }
            is AddEditContract.Event.OnStartDateChange -> {
                setState { copy(startDate = event.startDate) }
                updateLifetimeSpent()
            }
            is AddEditContract.Event.OnIconChange -> setState { copy(icon = event.icon) }
            is AddEditContract.Event.OnNotesChange -> setState { copy(notes = event.notes) }
            is AddEditContract.Event.OnSharingCountChange -> {
                setState { copy(sharingCount = event.count) }
                updateLifetimeSpent()
            }
            is AddEditContract.Event.OnCurrencyChange -> setState { copy(currencyCode = event.currencyCode) }
            is AddEditContract.Event.OnCalendarSyncToggle -> {
                prefs.set(Pref.CalendarSyncEnabled, event.enabled)
                setState { copy(isCalendarSyncEnabled = event.enabled) }
            }
            is AddEditContract.Event.OnFreeTrialToggle -> {
                if (event.isFreeTrial) {
                    setEffect { AddEditContract.Effect.ShowTrialDurationPicker }
                } else {
                    setState { copy(isFreeTrial = false) }
                }
            }
            is AddEditContract.Event.OnTrialDaysConfirm -> {
                val startTs = parseDate(viewState.value.startDate) ?: System.currentTimeMillis()
                val nextTs = startTs + (event.days.toLong() * 24 * 60 * 60 * 1000)
                setState { 
                    copy(
                        isFreeTrial = true, 
                        trialDays = event.days,
                        paymentDate = dateFormater.format(Date(nextTs))
                    ) 
                }
            }
            AddEditContract.Event.OnCategoryClick -> setEffect { AddEditContract.Effect.ShowCategorySelector }
            AddEditContract.Event.OnBillingCycleClick -> setEffect { AddEditContract.Effect.ShowBillingCycleSelector }
            AddEditContract.Event.OnEmojiClick -> setEffect { AddEditContract.Effect.ShowEmojiSelector }
            AddEditContract.Event.OnCurrencyClick -> setEffect { AddEditContract.Effect.ShowCurrencySelector }
            is AddEditContract.Event.OnBackClick -> setEffect { AddEditContract.Effect.NavigateBack }
            is AddEditContract.Event.OnSaveClick -> saveSubscription()
            is AddEditContract.Event.OnDeleteClick -> deleteSubscription()
        }
    }

    private fun loadSubscription(id: Long) {
        viewModelScope.launch {
            repository.getSubscriptionById(id)?.let { sub ->
                setState {
                    copy(
                        subId = sub.id,
                        name = sub.name,
                        price = sub.price.toString(),
                        billingCycle = sub.billingCycle,
                        category = sub.category,
                        startDate = dateFormater.format(Date(sub.startDate)),
                        paymentDate = dateFormater.format(Date(sub.nextPaymentDate)),
                        icon = sub.icon,
                        notes = sub.notes ?: "",
                        isFreeTrial = sub.isFreeTrial,
                        sharingCount = sub.sharingCount.toString(),
                        currencyCode = sub.currencyCode,
                        totalSpent = calculateLifetimeSpent(sub),
                        isEditMode = true,
                        isCalendarSyncEnabled = prefs.get(Pref.CalendarSyncEnabled)
                    )
                }
            }
        }
    }

    private fun saveSubscription() {
        val currentState = viewState.value
        if (currentState.name.isNotBlank() && currentState.price.toDoubleOrNull() != null) {
            val startTs = parseDate(currentState.startDate) ?: System.currentTimeMillis()
            val nextTs = parseDate(currentState.paymentDate) ?: (startTs + THIRTY_DAYS_MS)

            viewModelScope.launch {
                val sub = Subscription(
                    id = currentState.subId,
                    name = currentState.name,
                    price = currentState.price.toDouble(),
                    billingCycle = currentState.billingCycle,
                    startDate = startTs,
                    nextPaymentDate = nextTs,
                    category = currentState.category,
                    icon = currentState.icon,
                    isFreeTrial = currentState.isFreeTrial,
                    notes = if (currentState.notes.isBlank()) null else currentState.notes,
                    sharingCount = currentState.sharingCount.toIntOrNull() ?: 1,
                    currencyCode = currentState.currencyCode
                )
                if (currentState.isEditMode) {
                    repository.update(sub)
                } else {
                    repository.insert(sub)
                }
                
                widgetUpdater.updateAllWidgets()

                if (currentState.isCalendarSyncEnabled) {
                    setEffect {
                        AddEditContract.Effect.AddToCalendar(
                            name = sub.name,
                            price = sub.price,
                            date = sub.nextPaymentDate,
                            notes = sub.notes
                        )
                    }
                }
                
                setEffect { AddEditContract.Effect.NavigateBack }
            }
        }
    }

    private fun deleteSubscription() {
        viewModelScope.launch {
            val currentState = viewState.value
            repository.getSubscriptionById(currentState.subId)?.let {
                repository.delete(it)
                setEffect { AddEditContract.Effect.NavigateBack }
            }
        }
    }

    private fun updateLifetimeSpent() {
        val currentState = viewState.value
        val sub = Subscription(
            name = currentState.name,
            price = currentState.price.toDoubleOrNull() ?: 0.0,
            billingCycle = currentState.billingCycle,
            startDate = parseDate(currentState.startDate) ?: System.currentTimeMillis(),
            nextPaymentDate = parseDate(currentState.paymentDate) ?: System.currentTimeMillis(),
            category = currentState.category,
            sharingCount = currentState.sharingCount.toIntOrNull() ?: 1,
            currencyCode = currentState.currencyCode
        )
        setState { copy(totalSpent = com.weberpackage.scribly.common.presentation.utils.calculateLifetimeSpent(sub)) }
    }

    private fun parseDate(value: String): Long? {
        return runCatching { 
            dateFormater.parse(value)?.time 
        }.getOrNull()
    }

    private fun calculateLifetimeSpent(sub: Subscription): Double {
        val now = System.currentTimeMillis()
        if (sub.startDate >= now) return 0.0
        
        val diffMs = now - sub.startDate
        val occurrences = when (sub.billingCycle.lowercase()) {
            "weekly" -> (diffMs / (7L * 24 * 60 * 60 * 1000)).toInt() + 1
            "monthly" -> {
                val startCal = Calendar.getInstance().apply { timeInMillis = sub.startDate }
                val nowCal = Calendar.getInstance()
                val monthDiff = (nowCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR)) * 12 + 
                        (nowCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH))
                monthDiff + 1
            }
            "yearly" -> {
                val startCal = Calendar.getInstance().apply { timeInMillis = sub.startDate }
                val nowCal = Calendar.getInstance()
                val yearDiff = nowCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR)
                yearDiff + 1
            }
            else -> 1
        }
        
        val myPrice = sub.price / sub.sharingCount.coerceAtLeast(1)
        return occurrences * myPrice
    }

    private companion object {
        const val THIRTY_DAYS_MS = 30L * 24 * 60 * 60 * 1000
        val dateFormater = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
}
