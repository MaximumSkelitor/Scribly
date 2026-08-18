package com.weberpackage.scribly.statistics.presentation.viewmodel

import androidx.lifecycle.viewModelScope
import com.weberpackage.scribly.common.presentation.base.BaseViewModel
import com.weberpackage.scribly.data.Subscription
import com.weberpackage.scribly.data.SubscriptionRepository
import com.weberpackage.scribly.statistics.presentation.contract.StatisticsContract
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: SubscriptionRepository
) : BaseViewModel<StatisticsContract.Event, StatisticsContract.State, StatisticsContract.Effect>() {

    init {
        observeSubscriptions()
    }

    override fun setInitialState() = StatisticsContract.State()

    override fun handleEvents(event: StatisticsContract.Event) {}

    private fun observeSubscriptions() {
        repository.allSubscriptions.onEach { subs ->
            val monthly = subs.sumOf { calculateMonthlyCost(it) }
            val yearly = subs.sumOf { calculateYearlyCost(it) }
            
            setState {
                copy(
                    subscriptions = subs,
                    monthlyTotal = monthly,
                    yearlyTotal = yearly
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
        return when (currencyCode) {
            "EUR" -> amount * 1.08
            "GBP" -> amount * 1.27
            "JPY" -> amount * 0.0067
            "CAD" -> amount * 0.74
            "AUD" -> amount * 0.65
            "INR" -> amount * 0.012
            "BRL" -> amount * 0.20
            else -> amount
        }
    }
}
