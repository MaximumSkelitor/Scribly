package com.weberpackage.scribly.common.presentation.utils

import com.weberpackage.scribly.data.Subscription
import java.util.Calendar

fun calculateLifetimeSpent(sub: Subscription): Double {
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
