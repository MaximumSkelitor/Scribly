package com.weberpackage.scribly.home.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.weberpackage.scribly.common.presentation.base.BaseViewModel
import com.weberpackage.scribly.data.Subscription
import com.weberpackage.scribly.data.SubscriptionRepository
import com.weberpackage.scribly.home.presentation.contract.HomeContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: SubscriptionRepository
) : BaseViewModel<HomeContract.Event, HomeContract.State, HomeContract.Effect>() {

    init {
        observeSubscriptions()
    }

    override fun setInitialState() = HomeContract.State()

    override fun handleEvents(event: HomeContract.Event) {
        when (event) {
            is HomeContract.Event.OnAddSubscriptionClick -> {
                setEffect { HomeContract.Effect.NavigateToAddSubscription }
            }
            is HomeContract.Event.OnSubscriptionClick -> {
                setEffect { HomeContract.Effect.NavigateToEditSubscription(event.id) }
            }
        }
    }

    private fun observeSubscriptions() {
        repository.allSubscriptions.onEach { subs ->
            val monthly = subs.sumOf { calculateMonthlyCost(it) }
            val yearly = subs.sumOf { calculateYearlyCost(it) }
            val upcoming = subs.sortedBy { it.nextPaymentDate }.take(3)
            
            setState {
                copy(
                    subscriptions = subs,
                    monthlyTotal = monthly,
                    yearlyTotal = yearly,
                    upcoming = upcoming
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun calculateMonthlyCost(sub: Subscription): Double {
        val sharedPrice = sub.price / sub.sharingCount.coerceAtLeast(1)
        val convertedPrice = convertToUsd(sharedPrice, sub.currencyCode)
        return when (sub.billingCycle.lowercase()) {
            "weekly" -> convertedPrice * 4.33
            "monthly" -> convertedPrice
            "yearly" -> convertedPrice / 12.0
            else -> convertedPrice
        }
    }

    private fun calculateYearlyCost(sub: Subscription): Double {
        val sharedPrice = sub.price / sub.sharingCount.coerceAtLeast(1)
        val convertedPrice = convertToUsd(sharedPrice, sub.currencyCode)
        return when (sub.billingCycle.lowercase()) {
            "weekly" -> convertedPrice * 52.0
            "monthly" -> convertedPrice * 12.0
            "yearly" -> convertedPrice
            else -> convertedPrice * 12.0
        }
    }

    private fun convertToUsd(amount: Double, currencyCode: String): Double {
        // Simplified conversion rates for now
        return when (currencyCode) {
            "EUR" -> amount * 1.08
            "GBP" -> amount * 1.27
            "JPY" -> amount * 0.0067
            "CAD" -> amount * 0.74
            "AUD" -> amount * 0.65
            "INR" -> amount * 0.012
            "BRL" -> amount * 0.20
            else -> amount // USD or unknown
        }
    }
}
